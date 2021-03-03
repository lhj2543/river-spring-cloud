package com.river.site.dataSource.dbtool.provider.db.sql;

import com.river.site.dataSource.dbtool.provider.db.table.Column;
import com.river.site.dataSource.dbtool.util.BeanHelper;
import com.river.site.dataSource.dbtool.util.StringHelper;
import com.river.site.dataSource.dbtool.util.typemapping.JavaPrimitiveTypeMapping;

public class SqlParameter extends Column
{
  String parameterClass;
  String paramName;
  boolean isListParam = false;

  public SqlParameter() {
  }
  public SqlParameter(Column param) {
    super(param);
    BeanHelper.copyProperties(this, param);
  }

  public SqlParameter(SqlParameter param) {
    super(param);
    this.isListParam = param.isListParam;
    this.paramName = param.paramName;
  }

  public String getParameterClass()
  {
    if (StringHelper.isNotBlank(this.parameterClass)) {
      return this.parameterClass;
    }
    return getPossibleShortJavaType();
  }

  public void setParameterClass(String parameterClass) {
    this.parameterClass = parameterClass;
  }

  public String getPreferredParameterJavaType() {
    return toListParam(getParameterClass());
  }

  String toListParam(String parameterClassName) {
    if (this.isListParam) {
      if (parameterClassName.indexOf("[]") >= 0) {
        return parameterClassName;
      }
      if (parameterClassName.indexOf("List") >= 0) {
        return parameterClassName;
      }
      if (parameterClassName.indexOf("Set") >= 0) {
        return parameterClassName;
      }
      return "java.util.List<" + JavaPrimitiveTypeMapping.getWrapperType(parameterClassName) + ">";
    }

    return JavaPrimitiveTypeMapping.getWrapperType(parameterClassName);
  }

  public String getParamName()
  {
    return this.paramName;
  }
  public void setParamName(String paramName) {
    this.paramName = paramName;
  }

  public boolean isListParam()
  {
    return this.isListParam;
  }
  public void setListParam(boolean isListParam) {
    this.isListParam = isListParam;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if ((obj instanceof SqlParameter)) {
      SqlParameter other = (SqlParameter)obj;
      return this.paramName.equals(other.getParamName());
    }
    return false;
  }

  @Override
  public int hashCode() {
    return this.paramName.hashCode();
  }
  @Override
  public String toString() {
    return "paramName:" + this.paramName + " preferredParameterJavaType:" + getPreferredParameterJavaType();
  }
}

