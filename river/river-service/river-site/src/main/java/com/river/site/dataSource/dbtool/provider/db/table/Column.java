package com.river.site.dataSource.dbtool.provider.db.table;

import com.river.site.dataSource.dbtool.GeneratorProperties;
import com.river.site.dataSource.dbtool.util.BuilderLogger;
import com.river.site.dataSource.dbtool.util.ColumnHelper;
import com.river.site.dataSource.dbtool.util.StringHelper;
import com.river.site.dataSource.dbtool.util.TestDataGenerator;
import com.river.site.dataSource.dbtool.util.typemapping.ActionScriptDataTypesUtils;
import com.river.site.dataSource.dbtool.util.typemapping.DatabaseDataTypesUtils;
import com.river.site.dataSource.dbtool.util.typemapping.JavaPrimitiveTypeMapping;
import com.river.site.dataSource.dbtool.util.typemapping.JdbcType;
import java.io.Serializable;
import java.util.List;

public class Column
  implements Serializable, Cloneable
{
  private Table _table;
  private int _sqlType;
  private String _sqlTypeName;
  private String _sqlName;
  private boolean _isPk;
  private boolean _isFk;
  private int _size;
  private int _decimalDigits;
  private boolean _isNullable;
  private boolean _isIndexed;
  private boolean _isUnique;
  private String _defaultValue;
  private String _remarks;
  private ForeignKey.ReferenceKey hasOne;
  private ForeignKey.ReferenceKey hasMany = null;

  private String enumString = "";
  private String javaType;
  private String columnAlias;
  private String columnName;
  private String asType;
  private String enumClassName;
  private boolean updatable = true;
  private boolean insertable = true;
  private String hibernateValidatorExprssion;

  public Column(Table table, int sqlType, String sqlTypeName, String sqlName, int size, int decimalDigits, boolean isPk, boolean isNullable, boolean isIndexed, boolean isUnique, String defaultValue, String remarks)
  {
    if (sqlName == null) {
      throw new NullPointerException("sqlName must be not null");
    }
    this._table = table;
    this._sqlType = sqlType;
    this._sqlName = sqlName;
    this._sqlTypeName = sqlTypeName;
    this._size = size;
    this._decimalDigits = decimalDigits;
    this._isPk = isPk;
    this._isNullable = isNullable;
    this._isIndexed = isIndexed;
    this._isUnique = isUnique;
    this._defaultValue = defaultValue;
    this._remarks = remarks;

    BuilderLogger.trace(sqlName + " isPk -> " + this._isPk);

    initOtherProperties();
  }

  public Column(Column c) {
    this(c.getTable(), c
      .getSqlType(), c
      .getSqlTypeName(), c
      .getSqlName(), c
      .getSize(), c
      .getDecimalDigits(), c
      .isPk(), c
      .isNullable(), c
      .isIndexed(), c
      .isUnique(), c
      .getDefaultValue(), c
      .getRemarks());
  }

  public Column()
  {
  }

  public int getSqlType()
  {
    return this._sqlType;
  }

  public Table getTable()
  {
    return this._table;
  }

  public int getSize()
  {
    return this._size;
  }

  public int getDecimalDigits()
  {
    return this._decimalDigits;
  }

  public String getSqlTypeName()
  {
    return this._sqlTypeName;
  }

  public String getSqlName()
  {
    if (this._sqlName == null) {
      throw new NullPointerException();
    }
    return this._sqlName;
  }

  public void setSqlName(String v) {
    if (StringHelper.isBlank(v)) {
      throw new IllegalArgumentException("sqlName must be not blank");
    }
    if (!v.equalsIgnoreCase(this._sqlName)) {
      throw new IllegalArgumentException("cannot change property:sqlName value");
    }
    this._sqlName = v;
  }

  public boolean isPk()
  {
    return this._isPk;
  }

  public boolean isFk()
  {
    return this._isFk;
  }

  public boolean isNullable()
  {
    return this._isNullable;
  }

  public boolean isIndexed()
  {
    return this._isIndexed;
  }

  public boolean isUnique()
  {
    return this._isUnique;
  }

  public String getDefaultValue()
  {
    return this._defaultValue;
  }

  public String getRemarks()
  {
    return this._remarks;
  }

  public void setUpdatable(boolean updatable) {
    this.updatable = updatable;
  }

  public void setInsertable(boolean insertable) {
    this.insertable = insertable;
  }

  public void setNullable(boolean v) {
    this._isNullable = v;
  }

  public void setUnique(boolean unique) {
    this._isUnique = unique;
  }

  public void setPk(boolean v) {
    this._isPk = v;
  }

  @Override
  public int hashCode()
  {
    if (getTable() != null) {
      return (getTable().getSqlName() + "#" + getSqlName()).hashCode();
    }
    return getSqlName().hashCode();
  }

  @Override
  public boolean equals(Object o)
  {
    if (this == o) {
      return true;
    }
    if ((o instanceof Column)) {
      Column other = (Column)o;
      if (getSqlName().equals(other.getSqlName())) {
        return true;
      }
    }
    return false;
  }

  @Override
  public String toString()
  {
    return getSqlName();
  }

  @Override
  public Object clone() {
    try {
      return super.clone();
    } catch (CloneNotSupportedException e) {
    }
    return null;
  }

  protected String prefsPrefix()
  {
    return "tables/" + getTable().getSqlName() + "/columns/" + getSqlName();
  }

  void setFk(boolean flag)
  {
    this._isFk = flag;
  }

  public String getUnderscoreName() {
    return getSqlName().toLowerCase();
  }

  public String getColumnName()
  {
    return this.columnName;
  }

  public String getColumnNameFirstLower()
  {
    return StringHelper.uncapitalize(getColumnName());
  }

  public String getColumnNameLowerCase()
  {
    return getColumnName().toLowerCase();
  }

  /** @deprecated */
  public String getColumnNameLower()
  {
    return getColumnNameFirstLower();
  }

  public String getJdbcSqlTypeName()
  {
    return getJdbcType();
  }

  public String getJdbcType()
  {
    String result = JdbcType.getJdbcSqlTypeName(getSqlType());
    return result;
  }

  public String getColumnAlias()
  {
    return this.columnAlias;
  }

  public String getConstantName()
  {
    return StringHelper.toUnderscoreName(getColumnName()).toUpperCase();
  }

  /** @deprecated */
  public boolean getIsNotIdOrVersionField()
  {
    return !isPk();
  }

  public String getValidateString()
  {
    return "required " + getNoRequiredValidateString();
  }

  public String getNoRequiredValidateString()
  {
    return ColumnHelper.getRapidValidation(this);
  }

  public String[] getHibernateValidatorConstraintNames()
  {
    return ColumnHelper.removeHibernateValidatorSpecialTags(getHibernateValidatorExprssion());
  }

  public String getHibernateValidatorExprssion()
  {
    return this.hibernateValidatorExprssion;
  }

  public void setHibernateValidatorExprssion(String v) {
    this.hibernateValidatorExprssion = v;
  }

  public boolean getIsStringColumn()
  {
    return DatabaseDataTypesUtils.isString(getJavaType());
  }

  public boolean getIsDateTimeColumn()
  {
    return DatabaseDataTypesUtils.isDate(getJavaType());
  }

  public boolean getIsNumberColumn()
  {
    return (DatabaseDataTypesUtils.isFloatNumber(getJavaType())) || 
      (DatabaseDataTypesUtils.isIntegerNumber(getJavaType()));
  }

  public boolean contains(String keywords)
  {
    if (keywords == null) {
      throw new IllegalArgumentException("'keywords' must be not null");
    }
    return StringHelper.contains(getSqlName(), keywords.split(","));
  }

  public boolean isHtmlHidden() {
    return (isPk()) && (this._table.isSingleId());
  }

  public String getJavaType()
  {
    return this.javaType;
  }

  public String getSimpleJavaType()
  {
    return StringHelper.getJavaClassSimpleName(getJavaType());
  }

  public String getPossibleShortJavaType()
  {
    if (getJavaType().startsWith("java.lang.")) {
      return getSimpleJavaType();
    }
    return getJavaType();
  }

  public boolean isPrimitive()
  {
    return JavaPrimitiveTypeMapping.getWrapperTypeOrNull(getJavaType()) != null;
  }

  public String getPrimitiveJavaType()
  {
    return JavaPrimitiveTypeMapping.getPrimitiveType(getSimpleJavaType());
  }

  public String getAsType()
  {
    return this.asType;
  }

  public String getTestData()
  {
    return new TestDataGenerator().getDBUnitTestData(getColumnName(), getJavaType(), getSize());
  }

  public boolean isUpdatable()
  {
    return this.updatable;
  }

  public boolean isInsertable()
  {
    return this.insertable;
  }

  public String getEnumClassName()
  {
    return this.enumClassName;
  }

  public void setEnumString(String str)
  {
    this.enumString = str;
  }

  public String getEnumString() {
    return this.enumString;
  }

  public List<EnumMetaDada> getEnumList() {
    return StringHelper.string2EnumMetaData(getEnumString());
  }

  public boolean isEnumColumn() {
    return (getEnumList() != null) && (!getEnumList().isEmpty());
  }

  public void setJavaType(String javaType) {
    this.javaType = javaType;
  }

  public void setColumnAlias(String columnAlias) {
    this.columnAlias = columnAlias;
  }

  public void setColumnName(String columnName) {
    this.columnName = columnName;
  }

  public void setAsType(String asType) {
    this.asType = asType;
  }

  public void setEnumClassName(String enumClassName) {
    this.enumClassName = enumClassName;
  }

  public String getHasOne()
  {
    return ForeignKey.ReferenceKey.toString(this.hasOne);
  }

  public String getNullValue()
  {
    return JavaPrimitiveTypeMapping.getDefaultValue(getJavaType());
  }

  public boolean isHasNullValue() {
    return JavaPrimitiveTypeMapping.getWrapperTypeOrNull(getJavaType()) != null;
  }

  public void setHasOne(String foreignKey)
  {
    this.hasOne = ForeignKey.ReferenceKey.fromString(foreignKey);
    if ((this.hasOne != null) && (this._table != null))
    {
      this._table.getImportedKeys().addForeignKey(this.hasOne.tableName, this.hasOne.columnSqlName, getSqlName(), Integer.valueOf(this.hasOne.columnSqlName.toLowerCase().hashCode()));
    }
  }

  public String getHasMany()
  {
    return ForeignKey.ReferenceKey.toString(this.hasMany);
  }

  public void setHasMany(String foreignKey)
  {
    this.hasMany = ForeignKey.ReferenceKey.fromString(foreignKey);
    if ((this.hasMany != null) && (this._table != null))
    {
      this._table.getExportedKeys().addForeignKey(this.hasMany.tableName, this.hasMany.columnSqlName, getSqlName(), Integer.valueOf(this.hasMany.columnSqlName.toLowerCase().hashCode()));
    }
  }

  private void initOtherProperties() {
    String normalJdbcJavaType = DatabaseDataTypesUtils.getPreferredJavaType(getSqlType(), getSize(), getDecimalDigits());
    this.javaType = GeneratorProperties.getProperty("java_typemapping." + normalJdbcJavaType, normalJdbcJavaType).trim();
    this.columnName = StringHelper.makeAllWordFirstLetterUpperCase(StringHelper.toUnderscoreName(getSqlName()));
    this.enumClassName = (getColumnName() + "Enum");
    this.asType = ActionScriptDataTypesUtils.getPreferredAsType(getJavaType());
    this.columnAlias = StringHelper.removeCrlf(StringHelper.defaultIfEmpty(getRemarks(), getColumnNameFirstLower()));
    setHibernateValidatorExprssion(ColumnHelper.getHibernateValidatorExpression(this));
  }

  public static String removeAggregationColumnChars(String columSqlName)
  {
    return columSqlName.replace('(', '_').replace(")", "").replace("*", "");
  }

  public static class EnumMetaDada
  {
    private String enumAlias;
    private String enumKey;
    private String enumDesc;

    public EnumMetaDada(String enumAlias, String enumKey, String enumDesc)
    {
      this.enumAlias = enumAlias;
      this.enumKey = enumKey;
      this.enumDesc = enumDesc;
    }

    public String getEnumAlias() {
      return this.enumAlias;
    }
    public String getEnumKey() {
      return this.enumKey;
    }
    public String getEnumDesc() {
      return this.enumDesc;
    }
  }
}

