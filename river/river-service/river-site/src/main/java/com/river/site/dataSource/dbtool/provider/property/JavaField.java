package com.river.site.dataSource.dbtool.provider.property;

import com.river.site.dataSource.dbtool.util.typemapping.ActionScriptDataTypesUtils;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class JavaField
{
  private Field field;
  private JavaClass clazz;

  public JavaField(Field field, JavaClass clazz)
  {
    this.field = field;
    this.clazz = clazz;
  }

  public String getFieldName() {
    return this.field.getName();
  }

  public JavaClass getType() {
    return new JavaClass(this.field.getType());
  }

  public boolean isAccessible() {
    return this.field.isAccessible();
  }

  public boolean isEnumConstant() {
    return this.field.isEnumConstant();
  }

  public String toGenericString() {
    return this.field.toGenericString();
  }

  public JavaClass getClazz() {
    return this.clazz;
  }

  public String getJavaType() {
    return this.field.getType().getName();
  }

  public String getAsType() {
    return ActionScriptDataTypesUtils.getPreferredAsType(getJavaType());
  }

  public Annotation[] getAnnotations() {
    return this.field.getAnnotations();
  }

  public boolean getIsDateTimeField()
  {
    return (getJavaType().equalsIgnoreCase("java.util.Date")) || 
      (getJavaType().equalsIgnoreCase("java.sql.Date")) || 
      (getJavaType().equalsIgnoreCase("java.sql.Timestamp")) || 
      (getJavaType().equalsIgnoreCase("java.sql.Time"));
  }

  @Override
  public int hashCode() {
    int prime = 31;
    int result = 1;
    result = 31 * result + (this.field == null ? 0 : this.field.hashCode());
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
    JavaField other = (JavaField)obj;
    if (this.field == null) {
      if (other.field != null)
        return false;
    } else if (!this.field.equals(other.field))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "JavaClass:" + this.clazz + " JavaField:" + getFieldName();
  }
}

