package com.river.site.dataSource.dbtool.provider.property;

import com.river.site.dataSource.dbtool.util.BuilderLogger;
import com.river.site.dataSource.dbtool.util.StringHelper;
import com.river.site.dataSource.dbtool.util.typemapping.JavaImport;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JavaMethod
{
  Method method;
  private JavaClass clazz;

  public JavaMethod(Method method, JavaClass clazz)
  {
    if (method == null) throw new IllegalArgumentException("method must be not null");
    if (clazz == null) throw new IllegalArgumentException("clazz must be not null");
    this.method = method;
    this.clazz = clazz;
  }

  public JavaClass getClazz() {
    return this.clazz;
  }

  public String getMethodName() {
    return this.method.getName();
  }

  public JavaClass getReturnType() {
    return new JavaClass(this.method.getReturnType());
  }

  public Annotation[] getAnnotations() {
    return this.method.getAnnotations();
  }

  public boolean isBridge() {
    return this.method.isBridge();
  }

  public List<JavaClass> getExceptionTypes() {
    List result = new ArrayList();
    for (Class c : this.method.getExceptionTypes()) {
      result.add(new JavaClass(c));
    }
    return result;
  }

  public boolean isSynthetic() {
    return this.method.isSynthetic();
  }

  public boolean isVarArgs() {
    return this.method.isVarArgs();
  }

  public Set<JavaClass> getImportClasses() {
    Set set = new LinkedHashSet();
    JavaImport.addImportClass(set, this.method.getParameterTypes());
    JavaImport.addImportClass(set, this.method.getExceptionTypes());
    JavaImport.addImportClass(set, new Class[] { this.method.getReturnType() });
    return set;
  }

  public List<MethodParameter> getParameters() {
    Class[] parameters = this.method.getParameterTypes();
    List results = new ArrayList();
    for (int i = 0; i < parameters.length; i++) {
      results.add(new MethodParameter(i + 1, this, new JavaClass(parameters[i])));
    }
    return results;
  }

  public String getMethodNameUpper() {
    return StringHelper.capitalize(getMethodName());
  }

  @Override
  public int hashCode() {
    int prime = 31;
    int result = 1;
    result = 31 * result + (this.method == null ? 0 : this.method.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    JavaMethod other = (JavaMethod)obj;
    if (this.method == null) {
      if (other.method != null)
        return false;
    } else if (!this.method.equals(other.method))
      return false;
    return true;
  }

  public boolean isPropertyMethod() {
    if ((getMethodName().startsWith("set")) || (getMethodName().startsWith("get")) || ((getMethodName().startsWith("is")) && (getReturnType().isBooleanType()))) {
      return true;
    }
    return false;
  }

  public List<FieldMethodInvocation> getFieldMethodInvocationSequences()
  {
    if (StringHelper.isNotBlank(this.clazz.getMavenJavaSourceFileContent())) {
      try {
        JavaMethodInvokeSequencesParser cmd = new JavaMethodInvokeSequencesParser(this, this.clazz.getMavenJavaSourceFileContent());
        cmd.execute();
        return cmd.getMethodInvokeSequences();
      } catch (Exception e) {
        BuilderLogger.warn("getFieldMethodInvocationSequences() occer error on method:" + this.method.toString(), e);
        return new ArrayList(0);
      }
    }
    return new ArrayList(0);
  }

  @Override
  public String toString()
  {
    return this.clazz.getJavaType() + "." + getMethodName() + "()";
  }

  public static class JavaMethodInvokeSequencesParser
  {
    public static String fieldMethodInvokeRegex = "(\\w+)\\.(\\w+)\\(";
    private JavaMethod method;
    private String javaSourceContent;
    private JavaClass clazz;
    boolean executed = false;

    private List<FieldMethodInvocation> methodInvokeFlows = new ArrayList();

    public JavaMethodInvokeSequencesParser(JavaMethod method, String javaSourceContent)
    {
      if (StringHelper.isBlank(javaSourceContent)) {
        throw new IllegalArgumentException("'javaSourceContent' must be not blank");
      }

      this.method = method;
      this.javaSourceContent = javaSourceContent;
      this.clazz = method.getClazz();
    }

    public List<FieldMethodInvocation> getMethodInvokeSequences()
    {
      if (this.executed) {
        return this.methodInvokeFlows;
      }
      throw new IllegalStateException("please invoke execute() method before getMethodInvokeFlows()");
    }

    public void execute()
    {
      this.executed = true;

      if (!declaredMethodsContains()) {
        return;
      }

      if (this.method.getMethodName().indexOf("$") >= 0) {
        return;
      }

      String javaSourceContent = removeSomeThings();
      String methodBody = getMethodBody(javaSourceContent);

      Pattern p = Pattern.compile(fieldMethodInvokeRegex);
      Matcher m = p.matcher(methodBody);
      while (m.find()) {
        String field = m.group(1);
        String methodName = m.group(2);
        addFieldMethodInvocation(field, methodName);
      }
    }

    private boolean declaredMethodsContains() {
      for (Method m : this.clazz.getClazz().getDeclaredMethods()) {
        if (m.equals(this.method.method)) {
          return true;
        }
      }
      return false;
    }

    private void addFieldMethodInvocation(String field, String methodName) {
      try {
        JavaField javaField = this.clazz.getField(field);
        JavaClass fieldType = javaField.getType();
        JavaMethod method = fieldType.getMethod(methodName);
        if (method != null)
          this.methodInvokeFlows.add(new FieldMethodInvocation(javaField, method));
      }
      catch (NoSuchFieldException e)
      {
      }
    }

    public static String modifierToString(int mod) {
      if ((mod & 0x1) != 0) return "public";
      if ((mod & 0x4) != 0) return "protected";
      if ((mod & 0x2) != 0) return "private";
      return "";
    }

    private String getMethodBody(String javaSourceContent)
    {
      String methodStartPattern = "(?s)\\s+" + this.method.getMethodName() + "\\s*\\(" + MethodParameter.JavaSourceFileMethodParametersParser.getSimpleParamsPattern(this.method.method) + "\\)\\s*";

      int methodStart = StringHelper.indexOfByRegex(javaSourceContent, methodStartPattern);
      if (methodStart == -1) throw new IllegalArgumentException("cannot get method body by pattern:" + methodStartPattern + " methodName:" + this.method.getMethodName() + "\n javaSource:" + javaSourceContent);
      try
      {
        String methodEnd = javaSourceContent.substring(methodStart);
        int[] beginAndEnd = findWrapCharEndLocation(methodEnd, '{', '}');
        if (beginAndEnd == null) return "";

        return methodEnd.substring(beginAndEnd[0], beginAndEnd[1]);
      }
      catch (RuntimeException e) {
        throw new IllegalArgumentException("cannot get method body by pattern:" + methodStartPattern + "\n javaSource:" + javaSourceContent, e);
      }
    }

    private String removeSomeThings() {
      String javaSourceContent = removeJavaComments(this.javaSourceContent);

      javaSourceContent = replaceString2EmptyString(javaSourceContent);
      return javaSourceContent;
    }

    public static String replaceString2EmptyString(String str) {
      if (str == null) return null;
      str = str.replaceAll("\".*?\"", "");
      return str;
    }

    public static String removeJavaComments(String str) {
      if (str == null) return null;
      str = str.replaceAll("//.*", "");
      str = str.replaceAll("(?s)/\\*.*?\\*/", "");
      return str;
    }

    public static int[] findWrapCharEndLocation(String str, char begin, char end)
    {
      int count = 0;
      boolean foundEnd = false;
      boolean foundBegin = false;
      int[] beginAndEnd = new int[2];
      for (int i = 0; i < str.length(); i++) {
        char c = str.charAt(i);
        if (c == begin) {
          if (!foundBegin) {
            beginAndEnd[0] = i;
          }
          foundBegin = true;
          count++;
        }
        if (c == end) {
          foundEnd = true;
          count--;
        }
        if ((count == 0) && (foundBegin) && (foundEnd)) {
          beginAndEnd[1] = i;
          return beginAndEnd;
        }
      }
      return null;
    }
  }

  public static class FieldMethodInvocation
  {
    JavaField field;
    JavaMethod method;

    public FieldMethodInvocation(JavaField field, JavaMethod method)
    {
      this.field = field;
      this.method = method;
    }
    public JavaField getField() {
      return this.field;
    }
    public void setField(JavaField field) {
      this.field = field;
    }
    public JavaMethod getMethod() {
      return this.method;
    }
    public void setMethod(JavaMethod method) {
      this.method = method;
    }
    @Override
    public boolean equals(Object obj) {
      if (obj == null) return false;
      if (!(obj instanceof FieldMethodInvocation)) return false;
      FieldMethodInvocation other = (FieldMethodInvocation)obj;
      return (this.field.equals(other.field)) && (this.method.equals(other.method));
    }
    @Override
    public int hashCode() {
      return this.field.hashCode() + this.method.hashCode();
    }
    @Override
    public String toString() {
      return this.field.getFieldName() + "." + this.method.getMethodName() + "()";
    }
  }
}

