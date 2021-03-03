package com.river.site.dataSource.dbtool.util.typemapping;

import com.river.site.dataSource.dbtool.provider.property.JavaClass;
import com.river.site.dataSource.dbtool.util.StringHelper;
import java.lang.reflect.Modifier;
import java.util.Set;
import java.util.TreeSet;

public class JavaImport
{
  TreeSet<String> imports = new TreeSet();

  public void addImport(String javaType) {
    if (isNeedImport(javaType)) {
      this.imports.add(javaType.replace("$", "."));
    }
  }

  public void addImport(JavaImport javaImport)
  {
    if (javaImport != null) {
      this.imports.addAll(javaImport.getImports());
    }
  }

  public TreeSet<String> getImports() {
    return this.imports;
  }

  public static void addImportClass(Set<JavaClass> set, Class[] clazzes) {
    if (clazzes == null) {
      return;
    }
    for (Class c : clazzes) {
      if ((c != null) &&
        (!c.getName().startsWith("java.lang.")) &&
        (!c.isPrimitive()) &&
        (!"void".equals(c.getName())) &&
        (!c.isAnonymousClass()) &&
        (Modifier.isPublic(c.getModifiers())) &&
        (isNeedImport(c.getName()))) {
        set.add(new JavaClass(c));
      }
    }
  }

  public static boolean isNeedImport(String type)
  {
    if (StringHelper.isBlank(type)) {
      return false;
    }
    if ("void".equals(type)) {
      return false;
    }

    if (type.startsWith("java.lang.")) {
      return false;
    }

    if (JavaPrimitiveTypeMapping.getPrimitiveTypeOrNull(type) != null) {
      return false;
    }

    if ((type.indexOf(".") < 0) || (Character.isLowerCase(StringHelper.getJavaClassSimpleName(type).charAt(0)))) {
      return false;
    }

    return true;
  }
}

