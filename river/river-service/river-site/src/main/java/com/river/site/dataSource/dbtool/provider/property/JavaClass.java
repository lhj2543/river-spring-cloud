package com.river.site.dataSource.dbtool.provider.property;

import com.river.site.dataSource.dbtool.util.IOHelper;
import com.river.site.dataSource.dbtool.util.StringHelper;
import com.river.site.dataSource.dbtool.util.typemapping.ActionScriptDataTypesUtils;
import com.river.site.dataSource.dbtool.util.typemapping.JavaImport;
import com.river.site.dataSource.dbtool.util.typemapping.JavaPrimitiveTypeMapping;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class JavaClass
{
  private Class clazz;
  private String mavenJavaSourceFileContent;

  public JavaClass(Class clazz)
  {
    this.clazz = clazz;
  }

  public String getClassName() {
    return getClassName(getSimpleJavaType());
  }

  static String getClassName(String simpleJavaType) {
    return simpleJavaType.indexOf('$') >= 0 ? simpleJavaType.substring(simpleJavaType.indexOf('$') + 1) : simpleJavaType;
  }

  public String getPackageName() {
    return this.clazz.getPackage().getName();
  }

  public String getLastPackageName() {
    return StringHelper.getExtension(getPackageName());
  }

  public String getLastPackageNameFirstUpper() {
    return getLastPackageName() == null ? "" : StringHelper.capitalize(getLastPackageName());
  }

  public boolean isHasDefaultConstructor() {
    if ((this.clazz.isInterface()) || (this.clazz.isAnnotation()) || (this.clazz.isEnum()) || (Modifier.isAbstract(this.clazz.getModifiers()))) {
      return false;
    }
    for (Constructor c : this.clazz.getConstructors()) {
      if ((Modifier.isPublic(c.getModifiers())) && 
        (c.getParameterTypes().length == 0)) {
        return true;
      }
    }

    return false;
  }

  public Set<JavaClass> getImportClasses()
  {
    Set set = new LinkedHashSet();
    for (Method m : this.clazz.getMethods()) {
      Class[] clazzes = { m.getReturnType() };
      JavaImport.addImportClass(set, clazzes);
      JavaImport.addImportClass(set, m.getParameterTypes());
      JavaImport.addImportClass(set, m.getExceptionTypes());
    }

    if (this.clazz.isMemberClass()) {
      Class[] clazzes = new Class[] { this.clazz };
      JavaImport.addImportClass(set, (Class[])clazzes);
    }
    for (Field f : this.clazz.getFields()) {
      Class[] clazzes = { f.getType() };
      JavaImport.addImportClass(set, clazzes);
    }
    for (Field f : this.clazz.getDeclaredFields()) {
      Class[] clazzes = { f.getType() };
      JavaImport.addImportClass(set, clazzes);
    }
    for (Constructor c : this.clazz.getDeclaredConstructors()) {
      JavaImport.addImportClass(set, c.getExceptionTypes());
      JavaImport.addImportClass(set, c.getParameterTypes());
    }
    for (Constructor c : this.clazz.getConstructors()) {
      JavaImport.addImportClass(set, c.getExceptionTypes());
      JavaImport.addImportClass(set, c.getParameterTypes());
    }
    for (Class c : this.clazz.getDeclaredClasses()) {
      JavaImport.addImportClass(set, new Class[] { c });
    }
    return set;
  }

  public Set<JavaClass> getPropertiesImportClasses() throws Exception {
    Set set = getImportClasses();
    for (JavaProperty prop : getProperties()) {
      set.addAll(prop.getPropertyType().getImportClasses());
    }
    return set;
  }

  public String getSuperclassName() {
    return this.clazz.getSuperclass() != null ? this.clazz.getSuperclass().getName() : null;
  }

  public JavaMethod[] getMethods() {
    return toJavaMethods(this.clazz.getDeclaredMethods());
  }

  public JavaMethod getMethod(String methodName) {
    for (Method m : this.clazz.getMethods()) {
      if (m.getName().equals(methodName)) {
        return new JavaMethod(m, this);
      }
    }
    return null;
  }

  public JavaMethod[] getPublicMethods() {
    Method[] methods = this.clazz.getDeclaredMethods();
    return toJavaMethods(filterByModifiers(methods, new int[] { 1 }));
  }

  public JavaMethod[] getPublicStaticMethods() {
    Method[] methods = this.clazz.getDeclaredMethods();
    return toJavaMethods(filterByModifiers(methods, new int[] { 1, 8 }));
  }

  public JavaMethod[] getPublicNotStaticMethods() {
    Method[] staticMethods = filterByModifiers(this.clazz.getDeclaredMethods(), new int[] { 8 });
    Method[] publicMethods = filterByModifiers(this.clazz.getDeclaredMethods(), new int[] { 1 });
    Method[] filtered = (Method[])exclude(publicMethods, staticMethods).toArray(new Method[0]);
    return toJavaMethods(filtered);
  }

  public JavaProperty[] getReadProperties() throws Exception {
    List result = new ArrayList();
    for (JavaProperty p : getProperties()) {
      if (p.isHasReadMethod()) {
        result.add(p);
      }
    }
    return (JavaProperty[])result.toArray(new JavaProperty[0]);
  }

  public JavaProperty[] getWriteProperties() throws Exception {
    List result = new ArrayList();
    for (JavaProperty p : getProperties()) {
      if (p.isHasWriteMethod()) {
        result.add(p);
      }
    }
    return (JavaProperty[])result.toArray(new JavaProperty[0]);
  }

  public JavaProperty[] getProperties() throws Exception {
    List result = new ArrayList();
    BeanInfo beanInfo = Introspector.getBeanInfo(this.clazz);
    PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
    for (PropertyDescriptor pd : pds) {
      if (!"class".equalsIgnoreCase(pd.getName())) {
        result.add(new JavaProperty(pd, this));
      }
    }
    return (JavaProperty[])result.toArray(new JavaProperty[0]);
  }

  public List<JavaField> getFields() {
    Field[] fields = this.clazz.getDeclaredFields();
    List result = new ArrayList();
    for (Field f : fields) {
      result.add(new JavaField(f, this));
    }
    return result;
  }

  public String getPackagePath()
  {
    return getPackageName().replace(".", "/");
  }

  public String getParentPackageName()
  {
    return getPackageName().substring(0, getPackageName().lastIndexOf("."));
  }

  public String getParentPackagePath()
  {
    return getParentPackageName().replace(".", "/");
  }

  public String getClassFile()
  {
    return this.clazz.getClassLoader().getResource(this.clazz.getName().replace('.', '/') + ".class").getFile();
  }

  public String getJavaSourceFile()
  {
    return this.clazz.getName().replace('.', '/') + ".java";
  }

  public String getMavenJavaTestSourceFile()
  {
    return MavenHelper.getMavenJavaTestSourceFile(getClassFile());
  }

  public String getMavenJavaSourceFile()
  {
    return MavenHelper.getMavenJavaSourceFile(getClassFile());
  }

  public String getMavenJavaSourceFileContent()
  {
    if ((this.mavenJavaSourceFileContent == null) && 
      (getMavenJavaSourceFile() != null)) {
      File file = new File(getMavenJavaSourceFile());
      if (file.exists()) {
        this.mavenJavaSourceFileContent = IOHelper.readFile(file);
      }

    }

    return this.mavenJavaSourceFileContent;
  }

  public String getLoadedClasspath()
  {
    return getClassFile().substring(0, getClassFile().length() - (this.clazz.getName() + ".class").length());
  }

  public String getAsType() {
    return ActionScriptDataTypesUtils.getPreferredAsType(getJavaType());
  }

  public String getJavaType() {
    if (isArray()) {
      return this.clazz.getComponentType().getName().replace("$", ".");
    }
    return this.clazz.getName().replace("$", ".");
  }

  public String getPrimitiveJavaType()
  {
    return JavaPrimitiveTypeMapping.getPrimitiveType(getJavaType());
  }

  public String getSimpleJavaType() {
    if (isArray()) {
      return this.clazz.getComponentType().getSimpleName();
    }
    return this.clazz.getSimpleName();
  }

  public String getNullValue()
  {
    return JavaPrimitiveTypeMapping.getDefaultValue(getJavaType());
  }

  public String getCanonicalName() {
    return this.clazz.getCanonicalName();
  }

  public JavaField getField(String name) throws NoSuchFieldException, SecurityException {
    return new JavaField(this.clazz.getDeclaredField(name), this);
  }

  public JavaClass getSuperclass() {
    return new JavaClass(this.clazz.getSuperclass());
  }

  public boolean isAnnotation() {
    return this.clazz.isAnnotation();
  }

  public boolean isAnonymousClass() {
    return this.clazz.isAnonymousClass();
  }

  public boolean isArray() {
    return this.clazz.isArray();
  }

  public boolean isBooleanType() {
    return ("boolean".equals(this.clazz.getName())) || ("Boolean".equals(this.clazz.getSimpleName()));
  }

  public boolean isEnum() {
    return this.clazz.isEnum();
  }

  public boolean isInstance(Object obj) {
    return this.clazz.isInstance(obj);
  }

  public boolean isInterface() {
    return this.clazz.isInterface();
  }

  public boolean isLocalClass() {
    return this.clazz.isLocalClass();
  }

  public boolean isMemberClass() {
    return this.clazz.isMemberClass();
  }

  public boolean isPrimitive() {
    return this.clazz.isPrimitive();
  }

  public boolean isSynthetic() {
    return this.clazz.isSynthetic();
  }

  public Class getClazz() {
    return this.clazz;
  }

  public Set<JavaMethod.FieldMethodInvocation> getFieldMethodInvocationSequences() {
    Set set = new LinkedHashSet();
    for (JavaMethod m : getMethods()) {
      set.addAll(m.getFieldMethodInvocationSequences());
    }
    return set;
  }

  private Method[] filterByModifiers(Method[] methods, int[] filteredModifiers) {
    List filtered = new ArrayList();
    for (int i = 0; i < methods.length; i++) {
      for (int j = 0; j < filteredModifiers.length; j++) {
        if ((filteredModifiers[j] & methods[i].getModifiers()) != 0) {
          filtered.add(methods[i]);
        }
      }
    }
    return (Method[])filtered.toArray(new Method[0]);
  }

  private JavaMethod[] toJavaMethods(Method[] declaredMethods) {
    JavaMethod[] methods = new JavaMethod[declaredMethods.length];
    for (int i = 0; i < declaredMethods.length; i++) {
      methods[i] = new JavaMethod(declaredMethods[i], this);
    }
    return methods;
  }

  private <T> List<T> exclude(T[] methods, T[] excludeMethods) {
    List result = new ArrayList();

    label68:
    for (int i = 0; i < methods.length; i++) {
      for (int j = 0; j < excludeMethods.length; j++) {
        if (methods[i].equals(excludeMethods[j])) {
          break label68;
        }
      }
      result.add(methods[i]);
    }
    return result;
  }

  @Override
  public int hashCode() {
    int prime = 31;
    int result = 1;
    result = 31 * result + (this.clazz == null ? 0 : this.clazz.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    JavaClass other = (JavaClass)obj;
    if (this.clazz == null) {
      if (other.clazz != null) {
        return false;
      }
    } else if (!this.clazz.equals(other.clazz)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return this.clazz.getName();
  }

  public static class MavenHelper
  {
    public static String getMavenJavaTestSourceFile(String clazzFile)
    {
      if (clazzFile == null) {
        return null;
      }
      clazzFile = clazzFile.replace('\\', '/');
      clazzFile = clazzFile.replaceAll("\\w+\\$", "");
      if (clazzFile.indexOf("target/classes") >= 0) {
        String result = StringHelper.replace(clazzFile, "target/classes", "src/test/java");
        return StringHelper.replace(result, ".class", "Test.java");
      }if (clazzFile.indexOf("target/test-classes") >= 0) {
        String result = StringHelper.replace(clazzFile, "target/test-classes", "src/test/java");
        return StringHelper.replace(result, ".class", "Test.java");
      }
      return null;
    }

    public static String getMavenJavaSourceFile(String clazzFile)
    {
      if (clazzFile == null) {
        return null;
      }
      clazzFile = clazzFile.replace('\\', '/');
      clazzFile = clazzFile.replaceAll("\\$\\w+", "");
      if (clazzFile.indexOf("target/classes") >= 0) {
        String result = StringHelper.replace(clazzFile, "target/classes", "src/main/java");
        return StringHelper.replace(result, ".class", ".java");
      }if (clazzFile.indexOf("target/test-classes") >= 0) {
        String result = StringHelper.replace(clazzFile, "target/test-classes", "src/test/java");
        return StringHelper.replace(result, ".class", ".java");
      }

      return null;
    }
  }
}

