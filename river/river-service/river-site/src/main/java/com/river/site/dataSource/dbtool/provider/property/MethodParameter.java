package com.river.site.dataSource.dbtool.provider.property;

import com.river.site.dataSource.dbtool.util.ClassHelper;
import com.river.site.dataSource.dbtool.util.IOHelper;
import com.river.site.dataSource.dbtool.util.StringHelper;
import com.river.site.dataSource.dbtool.util.paranamer.AdaptiveParanamer;
import com.river.site.dataSource.dbtool.util.paranamer.BytecodeReadingParanamer;
import com.river.site.dataSource.dbtool.util.paranamer.CachingParanamer;
import com.river.site.dataSource.dbtool.util.paranamer.DefaultParanamer;
import com.river.site.dataSource.dbtool.util.paranamer.JavaSourceParanamer;
import com.river.site.dataSource.dbtool.util.paranamer.Paranamer;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MethodParameter
{
  int paramIndex = -1;
  String paramName;
  JavaClass paramClass;
  JavaMethod method;
  public static Paranamer paranamer = setParanamer(ClassHelper.getDefaultClassLoader());

  public MethodParameter(int paramIndex, JavaMethod method, JavaClass paramClazz)
  {
    this.method = method;
    this.paramIndex = paramIndex;
    this.paramClass = paramClazz;
  }

  public JavaMethod getMethod() {
    return this.method;
  }

  public String getName() {
    if (this.paramIndex < 0) {
      return null;
    }
    String[] parameterNames = lookupParameterNamesByParanamer();
    if ((parameterNames == null) || (parameterNames.length == 0)) {
      if (StringHelper.isNotBlank(this.paramName)) {
        return this.paramName;
      }
      if ((this.paramClass.getClazz().isPrimitive()) || (this.paramClass.getClazz().getName().startsWith("java."))) {
        return "param" + this.paramIndex;
      }
      return StringHelper.uncapitalize(this.paramClass.getClassName());
    }

    return parameterNames[(this.paramIndex - 1)];
  }

  public static Paranamer setParanamer(ClassLoader classLoader)
  {
    paranamer = new CachingParanamer(new AdaptiveParanamer(new Paranamer[] { new DefaultParanamer(), new BytecodeReadingParanamer(), new JavaSourceParanamer(classLoader) }));
    return paranamer;
  }

  private String[] lookupParameterNamesByParanamer() {
    return paranamer.lookupParameterNames(this.method.method, false);
  }

  public int getParamIndex() {
    return this.paramIndex;
  }

  public String getAsType() {
    return this.paramClass.getAsType();
  }

  public String getClassName() {
    return this.paramClass.getClassName();
  }

  public String getJavaType() {
    return this.paramClass.getJavaType();
  }

  public String getPackageName() {
    return this.paramClass.getPackageName();
  }

  public String getPackagePath() {
    return this.paramClass.getPackagePath();
  }

  public String getParentPackageName() {
    return this.paramClass.getParentPackageName();
  }

  public String getParentPackagePath() {
    return this.paramClass.getParentPackagePath();
  }

  public boolean isArray() {
    return this.paramClass.isArray();
  }

  public String getCanonicalName() {
    return this.paramClass.getCanonicalName();
  }

  public List<JavaField> getFields() {
    return this.paramClass.getFields();
  }

  public JavaMethod[] getMethods() {
    return this.paramClass.getMethods();
  }

  public boolean isAnnotation() {
    return this.paramClass.isAnnotation();
  }

  public boolean isAnonymousClass() {
    return this.paramClass.isAnonymousClass();
  }

  public boolean isEnum() {
    return this.paramClass.isEnum();
  }

  public boolean isInterface() {
    return this.paramClass.isInterface();
  }

  public boolean isLocalClass() {
    return this.paramClass.isLocalClass();
  }

  public boolean isMemberClass() {
    return this.paramClass.isMemberClass();
  }

  public boolean isPrimitive() {
    return this.paramClass.isPrimitive();
  }

  public boolean isSynthetic() {
    return this.paramClass.isSynthetic();
  }

  public JavaProperty[] getProperties() throws Exception {
    return this.paramClass.getProperties();
  }

  public String getSuperclassName() {
    return this.paramClass.getSuperclassName();
  }

  public JavaClass getParamClass() {
    return this.paramClass;
  }

  public static String[] parseJavaFileForParamNames(Method method, File javaFile) throws IOException {
    String content = IOHelper.readFile(javaFile);
    return new JavaSourceFileMethodParametersParser().parseJavaFileForParamNames(method, content);
  }

  @Override
  public String toString()
  {
    return "MethodParameter:" + getName() + "=" + getJavaType();
  }

  public static class JavaSourceFileMethodParametersParser
  {
    public String[] parseJavaFileForParamNames(Method method, String content)
    {
      Pattern methodPattern = Pattern.compile("(?s)" + method.getName() + "\\s*\\(" + getParamsPattern(method) + "\\)\\s*\\{");
      Matcher m = methodPattern.matcher(content);
      List paramNames = new ArrayList();
      if (m.find()) {
        for (int i = 1; i <= method.getParameterTypes().length; i++) {
          paramNames.add(m.group(i));
        }
        return (String[])paramNames.toArray(new String[0]);
      }
      return null;
    }

    public static String getParamsPattern(Method method) {
      List paramPatterns = new ArrayList();
      for (int i = 0; i < method.getParameterTypes().length; i++) {
        Class type = method.getParameterTypes()[i];
        String classType = type.getSimpleName().replace("[", "\\[").replace("]", "\\]");
        String paramPattern = ".*" + classType + ".*\\s+(\\w+).*";
        paramPatterns.add(paramPattern);
      }
      return StringHelper.join(paramPatterns, ",");
    }

    public static String getSimpleParamsPattern(Method method) {
      List paramPatterns = new ArrayList();
      for (int i = 0; i < method.getParameterTypes().length; i++) {
        Class type = method.getParameterTypes()[i];
        String classType = type.getSimpleName().replace("[", "\\[").replace("]", "\\]");
        String paramPattern = classType + ".*";
        paramPatterns.add(paramPattern);
      }
      return ".*" + StringHelper.join(paramPatterns, "");
    }
  }
}

