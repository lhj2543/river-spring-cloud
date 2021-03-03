package com.river.site.dataSource.dbtool.util.paranamer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class BytecodeReadingParanamer
  implements Paranamer
{
  private static final Map<String, String> primitives = new HashMap() { } ;

  @Override
  public String[] lookupParameterNames(AccessibleObject methodOrConstructor)
  {
    return lookupParameterNames(methodOrConstructor, true);
  }

  @Override
  public String[] lookupParameterNames(AccessibleObject methodOrCtor, boolean throwExceptionIfMissing)
  {
    Class[] types = null;
    Class declaringClass = null;
    String name = null;
    if ((methodOrCtor instanceof Method)) {
      Method method = (Method)methodOrCtor;
      types = method.getParameterTypes();
      name = method.getName();
      declaringClass = method.getDeclaringClass();
    } else {
      Constructor constructor = (Constructor)methodOrCtor;
      types = constructor.getParameterTypes();
      declaringClass = constructor.getDeclaringClass();
      name = "<init>";
    }

    if (types.length == 0) {
      return EMPTY_NAMES;
    }
    InputStream byteCodeStream = getClassAsStream(declaringClass);
    if (byteCodeStream == null) {
      if (throwExceptionIfMissing) {
        throw new ParameterNamesNotFoundException("Unable to get class bytes");
      }
      return Paranamer.EMPTY_NAMES;
    }
    try
    {
      ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
      byte[] buff = new byte[1024];
      int rc = 0;
      while ((rc = byteCodeStream.read(buff, 0, 1024)) > 0) {
        swapStream.write(buff, 0, rc);
      }
      byte[] byt = swapStream.toByteArray();

      ClassReader reader = new ClassReader(byt, 0);
      TypeCollector visitor = new TypeCollector(name, types, throwExceptionIfMissing);
      reader.accept(visitor);
      String[] parameterNamesForMethod = visitor.getParameterNamesForMethod();
      try {
        byteCodeStream.close();
      } catch (IOException e) {
      }
      return parameterNamesForMethod;
    } catch (IOException e) {
      if (throwExceptionIfMissing) {
        throw new ParameterNamesNotFoundException("IoException while reading class bytes", e);
      }
    }
    return Paranamer.EMPTY_NAMES;
  }

  private InputStream getClassAsStream(Class<?> clazz)
  {
    ClassLoader classLoader = clazz.getClassLoader();
    if (classLoader == null) {
      classLoader = ClassLoader.getSystemClassLoader();
    }
    return getClassAsStream(classLoader, clazz.getName());
  }

  private InputStream getClassAsStream(ClassLoader classLoader, String className) {
    String name = className.replace('.', '/') + ".class";

    InputStream asStream = classLoader.getResourceAsStream(name);
    if (asStream == null) {
      asStream = BytecodeReadingParanamer.class.getResourceAsStream(name);
    }
    return asStream;
  }

  private static class Type
  {
    private static final int VOID = 0;
    private static final int BOOLEAN = 1;
    private static final int CHAR = 2;
    private static final int BYTE = 3;
    private static final int SHORT = 4;
    private static final int INT = 5;
    private static final int FLOAT = 6;
    private static final int LONG = 7;
    private static final int DOUBLE = 8;
    private static final int ARRAY = 9;
    private static final int OBJECT = 10;
    private static final Type VOID_TYPE = new Type(0, null, 1443168256, 1);

    private static final Type BOOLEAN_TYPE = new Type(1, null, 1509950721, 1);

    private static final Type CHAR_TYPE = new Type(2, null, 1124075009, 1);

    private static final Type BYTE_TYPE = new Type(3, null, 1107297537, 1);

    private static final Type SHORT_TYPE = new Type(4, null, 1392510721, 1);

    private static final Type INT_TYPE = new Type(5, null, 1224736769, 1);

    private static final Type FLOAT_TYPE = new Type(6, null, 1174536705, 1);

    private static final Type LONG_TYPE = new Type(7, null, 1241579778, 1);

    private static final Type DOUBLE_TYPE = new Type(8, null, 1141048066, 1);
    private final int sort;
    private char[] buf;
    private int off;
    private final int len;

    private Type(int sort)
    {
      this.sort = sort;
      this.len = 1;
    }

    private Type(int sort, char[] buf, int off, int len)
    {
      this.sort = sort;
      this.buf = buf;
      this.off = off;
      this.len = len;
    }

    private static Type[] getArgumentTypes(String methodDescriptor)
    {
      char[] buf = methodDescriptor.toCharArray();
      int off = 1;
      int size = 0;
      while (true) {
        char car = buf[(off++)];
        if (car == ')') {
          break;
        }
        if (car == 'L') {
          while (buf[(off++)] != ';'){};
          size++;
        } else if (car != '[') {
          size++;
        }
      }

      Type[] args = new Type[size];
      off = 1;
      size = 0;
      while (buf[off] != ')') {
        args[size] = getType(buf, off);
        off += args[size].len + (args[size].sort == 10 ? 2 : 0);
        size++;
      }
      return args;
    }

    private static Type getType(char[] buf, int off)
    {
      switch (buf[off]) {
      case 'V':
        return VOID_TYPE;
      case 'Z':
        return BOOLEAN_TYPE;
      case 'C':
        return CHAR_TYPE;
      case 'B':
        return BYTE_TYPE;
      case 'S':
        return SHORT_TYPE;
      case 'I':
        return INT_TYPE;
      case 'F':
        return FLOAT_TYPE;
      case 'J':
        return LONG_TYPE;
      case 'D':
        return DOUBLE_TYPE;
      case '[':
        int len = 1;
        while (buf[(off + len)] == '[') {
          len++;
        }
        if (buf[(off + len)] == 'L') {
          len++;
          while (buf[(off + len)] != ';') {
            len++;
          }
        }
        return new Type(9, buf, off, len + 1);
      case 'E':
      case 'G':
      case 'H':
      case 'K':
      case 'L':
      case 'M':
      case 'N':
      case 'O':
      case 'P':
      case 'Q':
      case 'R':
      case 'T':
      case 'U':
      case 'W':
      case 'X':
      case 'Y': } int len = 1;
      while (buf[(off + len)] != ';') {
        len++;
      }
      return new Type(10, buf, off + 1, len - 1);
    }

    private int getDimensions()
    {
      int i = 1;
      while (this.buf[(this.off + i)] == '[') {
        i++;
      }
      return i;
    }

    private Type getElementType()
    {
      return getType(this.buf, this.off + getDimensions());
    }

    private String getClassName()
    {
      switch (this.sort) {
      case 0:
        return "void";
      case 1:
        return "boolean";
      case 2:
        return "char";
      case 3:
        return "byte";
      case 4:
        return "short";
      case 5:
        return "int";
      case 6:
        return "float";
      case 7:
        return "long";
      case 8:
        return "double";
      case 9:
        StringBuffer b = new StringBuffer(getElementType().getClassName());
        for (int i = getDimensions(); i > 0; i--) {
          b.append("[]");
        }
        return b.toString();
      }

      return new String(this.buf, this.off, this.len).replace('/', '.');
    }
  }

  private static class ClassReader
  {
    public final byte[] b;
    private final int[] items;
    private final String[] strings;
    private final int maxStringLength;
    public final int header;
    static final int FIELD = 9;
    static final int METH = 10;
    static final int IMETH = 11;
    static final int INT = 3;
    static final int FLOAT = 4;
    static final int LONG = 5;
    static final int DOUBLE = 6;
    static final int NAME_TYPE = 12;
    static final int UTF8 = 1;

    private ClassReader(byte[] b)
    {
      this(b, 0);
    }

    private ClassReader(byte[] b, int off)
    {
      this.b = b;

      this.items = new int[readUnsignedShort(off + 8)];
      int n = this.items.length;
      this.strings = new String[n];
      int max = 0;
      int index = off + 10;
      for (int i = 1; i < n; i++) {
        this.items[i] = (index + 1);
        int size;
        switch (b[index]) {
        case 3:
        case 4:
        case 9:
        case 10:
        case 11:
        case 12:
          size = 5;
          break;
        case 5:
        case 6:
          size = 9;
          i++;
          break;
        case 1:
           size = 3 + readUnsignedShort(index + 1);
          if (size > max) {
            max = size;
          }
          break;
        case 2:
        case 7:
        case 8:
        default:
          size = 3;
        }

        index += size;
      }
      this.maxStringLength = max;

      this.header = index;
    }

    private ClassReader(InputStream is)
      throws IOException
    {
      this(readClass(is));
    }

    private static byte[] readClass(InputStream is)
      throws IOException
    {
      if (is == null) {
        throw new IOException("Class not found");
      }
      byte[] b = new byte[is.available()];
      int len = 0;
      while (true) {
        int n = is.read(b, len, b.length - len);
        if (n == -1) {
          if (len < b.length) {
            byte[] c = new byte[len];
            System.arraycopy(b, 0, c, 0, len);
            b = c;
          }
          return b;
        }
        len += n;
        if (len == b.length) {
          int last = is.read();
          if (last < 0) {
            return b;
          }
          byte[] c = new byte[b.length + 1000];
          System.arraycopy(b, 0, c, 0, len);
          c[(len++)] = ((byte)last);
          b = c;
        }
      }
    }

    private void accept(TypeCollector classVisitor)
    {
      char[] c = new char[this.maxStringLength];

      int anns = 0;
      int ianns = 0;

      int u = this.header;
      int v = this.items[readUnsignedShort(u + 4)];
      int len = readUnsignedShort(u + 6);
      int w = 0;
      u += 8;
      for (int i = 0; i < len; i++) {
        u += 2;
      }
      v = u;
      int i = readUnsignedShort(v);
      v += 2;
      for (; i > 0; i--) {
        int j = readUnsignedShort(v + 6);
        v += 8;
        for (; j > 0; j--) {
          v += 6 + readInt(v + 2);
        }
      }
      i = readUnsignedShort(v);
      v += 2;
      for (; i > 0; i--) {
        int j = readUnsignedShort(v + 6);
        v += 8;
        for (; j > 0; j--) {
          v += 6 + readInt(v + 2);
        }
      }

      i = readUnsignedShort(v);
      v += 2;
      for (; i > 0; i--) {
        v += 6 + readInt(v + 2);
      }

      i = readUnsignedShort(u);
      u += 2;
      for (; i > 0; i--) {
        int j = readUnsignedShort(u + 6);
        u += 8;
        for (; j > 0; j--) {
          u += 6 + readInt(u + 2);
        }

      }

      i = readUnsignedShort(u);
      u += 2;
      for (; i > 0; i--)
      {
        u = readMethod(classVisitor, c, u);
      }
    }

    private int readMethod(TypeCollector classVisitor, char[] c, int u)
    {
      int access = readUnsignedShort(u);
      String name = readUTF8(u + 2, c);
      String desc = readUTF8(u + 4, c);
      int v = 0;
      int w = 0;

      int j = readUnsignedShort(u + 6);
      u += 8;
      for (; j > 0; j--) {
        String attrName = readUTF8(u, c);
        int attrSize = readInt(u + 2);
        u += 6;

        if (attrName.equals("Code")) {
          v = u;
        }
        u += attrSize;
      }

      if (w != 0)
      {
        w += 2;
        for (j = 0; j < readUnsignedShort(w); j++) {
          w += 2;
        }

      }

      MethodCollector mv = classVisitor.visitMethod(access, name, desc);

      if ((mv != null) && (v != 0)) {
        int codeLength = readInt(v + 4);
        v += 8;

        int codeStart = v;
        int codeEnd = v + codeLength;
        v = codeEnd;

        j = readUnsignedShort(v);
        v += 2;
        for (; j > 0; j--) {
          v += 8;
        }

        int varTable = 0;
        int varTypeTable = 0;
        j = readUnsignedShort(v);
        v += 2;
        for (; j > 0; j--) {
          String attrName = readUTF8(v, c);
          if (attrName.equals("LocalVariableTable")) {
            varTable = v + 6;
          } else if (attrName.equals("LocalVariableTypeTable")) {
            varTypeTable = v + 6;
          }
          v += 6 + readInt(v + 2);
        }

        v = codeStart;

        if (varTable != 0) {
          if (varTypeTable != 0) {
            int k = readUnsignedShort(varTypeTable) * 3;
            w = varTypeTable + 2;
            int[] typeTable = new int[k];
            while (k > 0) {
              typeTable[(--k)] = (w + 6);
              typeTable[(--k)] = readUnsignedShort(w + 8);
              typeTable[(--k)] = readUnsignedShort(w);
              w += 10;
            }
          }
          int k = readUnsignedShort(varTable);
          w = varTable + 2;
          for (; k > 0; k--) {
            int index = readUnsignedShort(w + 8);
            mv.visitLocalVariable(readUTF8(w + 4, c), index);
            w += 10;
          }
        }
      }
      return u;
    }

    private int readUnsignedShort(int index)
    {
      byte[] b = this.b;
      return (b[index] & 0xFF) << 8 | b[(index + 1)] & 0xFF;
    }

    private int readInt(int index)
    {
      byte[] b = this.b;
      return (b[index] & 0xFF) << 24 | (b[(index + 1)] & 0xFF) << 16 | (b[(index + 2)] & 0xFF) << 8 | b[(index + 3)] & 0xFF;
    }

    private String readUTF8(int index, char[] buf)
    {
      int item = readUnsignedShort(index);
      String s = this.strings[item];
      if (s != null) {
        return s;
      }
      index = this.items[item];
      this.strings[item] = readUTF(index + 2, readUnsignedShort(index), buf);
      return readUTF(index + 2, readUnsignedShort(index), buf);
    }

    private String readUTF(int index, int utfLen, char[] buf)
    {
      int endIndex = index + utfLen;
      byte[] b = this.b;
      int strLen = 0;

      int st = 0;
      char cc = '\000';
      while (index < endIndex) {
        int c = b[(index++)];
        switch (st) {
        case 0:
          c &= 255;
          if (c < 128) {
            buf[(strLen++)] = ((char)c);
          } else if ((c < 224) && (c > 191)) {
            cc = (char)(c & 0x1F);
            st = 1;
          } else {
            cc = (char)(c & 0xF);
            st = 2;
          }
          break;
        case 1:
          buf[(strLen++)] = ((char)(cc << '\006' | c & 0x3F));
          st = 0;
          break;
        case 2:
          cc = (char)(cc << '\006' | c & 0x3F);
          st = 1;
        }
      }

      return new String(buf, 0, strLen);
    }
  }

  private static class MethodCollector
  {
    private final int paramCount;
    private final int ignoreCount;
    private int currentParameter;
    private final StringBuffer result;
    private boolean debugInfoPresent;

    private MethodCollector(int ignoreCount, int paramCount)
    {
      this.ignoreCount = ignoreCount;
      this.paramCount = paramCount;
      this.result = new StringBuffer();
      this.currentParameter = 0;

      this.debugInfoPresent = (paramCount == 0);
    }

    public void visitLocalVariable(String name, int index) {
      if ((index >= this.ignoreCount) && (index < this.ignoreCount + this.paramCount)) {
        if (!name.equals("arg" + this.currentParameter)) {
          this.debugInfoPresent = true;
        }
        this.result.append(',');
        this.result.append(name);
        this.currentParameter += 1;
      }
    }

    private String getResult() {
      return this.result.length() != 0 ? this.result.substring(1) : "";
    }

    private boolean isDebugInfoPresent() {
      return this.debugInfoPresent;
    }
  }

  private static class TypeCollector
  {
    private static final String COMMA = ",";
    private final String methodName;
    private final Class<?>[] parameterTypes;
    private final boolean throwExceptionIfMissing;
    private MethodCollector collector;

    private TypeCollector(String methodName, Class<?>[] parameterTypes, boolean throwExceptionIfMissing)
    {
      this.methodName = methodName;
      this.parameterTypes = parameterTypes;
      this.throwExceptionIfMissing = throwExceptionIfMissing;
      this.collector = null;
    }

    public MethodCollector visitMethod(int access, String name, String desc)
    {
      if (this.collector != null) {
        return null;
      }

      if (!name.equals(this.methodName)) {
        return null;
      }
      Type[] argumentTypes = Type.getArgumentTypes(desc);
      int longOrDoubleQuantity = 0;
      for (Type t : argumentTypes) {
        if ((t.getClassName().equals("long")) || 
          (t
          .getClassName().equals("double"))) {
          longOrDoubleQuantity++;
        }
      }
      int paramCount = argumentTypes.length;

      if (paramCount != this.parameterTypes.length) {
        return null;
      }
      for (int i = 0; i < argumentTypes.length; i++) {
        if (!correctTypeName(argumentTypes, i).equals(this.parameterTypes[i]
          .getName())) {
          return null;
        }
      }
      this.collector = new MethodCollector(Modifier.isStatic(access) ? 0 : 1, argumentTypes.length + longOrDoubleQuantity);

      return this.collector;
    }

    private String correctTypeName(Type[] argumentTypes, int i) {
      String s = argumentTypes[i].getClassName();

      if (s.endsWith("[]")) {
        String prefix = s.substring(0, s.length() - 2);
        if (BytecodeReadingParanamer.primitives.containsKey(prefix)) {
          s = "[" + (String)BytecodeReadingParanamer.primitives.get(prefix);
        } else {
          s = "[L" + prefix + ";";
        }
      }
      return s;
    }

    private String[] getParameterNamesForMethod() {
      if (this.collector == null) {
        return Paranamer.EMPTY_NAMES;
      }
      if (!this.collector.isDebugInfoPresent()) {
        if (this.throwExceptionIfMissing) {
          throw new ParameterNamesNotFoundException("Parameter names not found for " + this.methodName);
        }
        return Paranamer.EMPTY_NAMES;
      }

      return this.collector.getResult().split(",");
    }
  }
}

