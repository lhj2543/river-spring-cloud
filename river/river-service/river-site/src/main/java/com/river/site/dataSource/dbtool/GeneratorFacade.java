package com.river.site.dataSource.dbtool;

import com.river.site.dataSource.dbtool.provider.db.sql.Sql;
import com.river.site.dataSource.dbtool.provider.db.table.Table;
import com.river.site.dataSource.dbtool.provider.db.table.TableFactory;
import com.river.site.dataSource.dbtool.provider.property.JavaClass;
import com.river.site.dataSource.dbtool.util.*;

import java.io.*;
import java.util.*;

public class GeneratorFacade
{
  private Generator generator = new Generator();

  public GeneratorFacade() {
    if (StringHelper.isNotBlank(GeneratorProperties.getProperty("outRoot"))) {
      this.generator.setOutRootDir(GeneratorProperties.getProperty("outRoot"));
    }
  }

  public void printAllTableNames() throws Exception
  {
    PrintUtils.printAllTableNames(TableFactory.getInstance().getAllTables());
  }

  public void deleteOutRootDir() throws IOException {
    this.generator.deleteOutRootDir();
  }

  public void generateByMap(Map[] maps)
    throws Exception
  {
    for (Map map : maps) {
      new ProcessUtils().processByMap(map, false);
    }
  }

  public void deleteByMap(Map[] maps)
    throws Exception
  {
    for (Map map : maps)
      new ProcessUtils().processByMap(map, true);
  }

  public void generateBy(Generator.GeneratorModel[] models)
    throws Exception
  {
    for (Generator.GeneratorModel model : models)
      new ProcessUtils().processByGeneratorModel(model, false);
  }

  public void deleteBy(Generator.GeneratorModel[] models)
    throws Exception
  {
    for (Generator.GeneratorModel model : models)
      new ProcessUtils().processByGeneratorModel(model, true);
  }

  public void generateByAllTable()
    throws Exception
  {
    new ProcessUtils().processByAllTable(false);
  }

  public void deleteByAllTable()
    throws Exception
  {
    new ProcessUtils().processByAllTable(true);
  }

  public void generateByTable(String[] tableNames)
    throws Exception
  {
    for (String tableName : tableNames)
      new ProcessUtils().processByTable(tableName, false);
  }

  public void deleteByTable(String[] tableNames)
    throws Exception
  {
    for (String tableName : tableNames)
      new ProcessUtils().processByTable(tableName, true);
  }

  public void generateByClass(Class[] clazzes)
    throws Exception
  {
    for (Class clazz : clazzes)
      new ProcessUtils().processByClass(clazz, false);
  }

  public void deleteByClass(Class[] clazzes)
    throws Exception
  {
    for (Class clazz : clazzes)
      new ProcessUtils().processByClass(clazz, true);
  }

  public void generateBySql(Sql[] sqls)
    throws Exception
  {
    for (Sql sql : sqls)
      new ProcessUtils().processBySql(sql, false);
  }

  public void deleteBySql(Sql[] sqls)
    throws Exception
  {
    for (Sql sql : sqls)
      new ProcessUtils().processBySql(sql, true);
  }

  public Generator getGenerator()
  {
    return this.generator;
  }

  public void setGenerator(Generator generator) {
    this.generator = generator;
  }

  private static class PrintUtils
  {
    private static void printExceptionsSumary(String msg, String outRoot, List<Exception> exceptions)
      throws FileNotFoundException
    {
      File errorFile = new File(outRoot, "generator_error.log");
      if ((exceptions != null) && (exceptions.size() > 0)) {
        System.err.println(new StringBuilder().append("[Generate Error Summary] : ").append(msg).toString());
        errorFile.getParentFile().mkdirs();
        PrintStream output = new PrintStream(new FileOutputStream(errorFile));
        for (int i = 0; i < exceptions.size(); i++) {
          Exception e = (Exception)exceptions.get(i);
          System.err.println(new StringBuilder().append("[GENERATE ERROR]:").append(e).toString());
          if (i == 0) e.printStackTrace();
          e.printStackTrace(output);
        }
        output.close();
        System.err.println("***************************************************************");
        System.err.println("* * 输出目录已经生成generator_error.log用于查看错误 ");
        System.err.println("***************************************************************");
      }
    }

    private static void printBeginProcess(String displayText, boolean isDatele) {
      BuilderLogger.println("***************************************************************");
      BuilderLogger.println(new StringBuilder().append("* BEGIN ").append(isDatele ? " delete by " : " generate by ").append(displayText).toString());
      BuilderLogger.println("***************************************************************");
    }

    public static void printAllTableNames(List<Table> tables) throws Exception {
      BuilderLogger.println("\n----All TableNames BEGIN----");
      for (int i = 0; i < tables.size(); i++) {
        String sqlName = ((Table)tables.get(i)).getSqlName();
        BuilderLogger.println(new StringBuilder().append("g.generateTable(\"").append(sqlName).append("\");").toString());
      }
      BuilderLogger.println("----All TableNames END----");
    }
  }

  public static class GeneratorModelUtils
  {
    public static Generator.GeneratorModel newGeneratorModel(String key, Object valueObject)
    {
      Generator.GeneratorModel gm = newDefaultGeneratorModel();
      gm.templateModel.put(key, BeanHelper.describe(valueObject, new String[0]));
      gm.filePathModel.putAll(BeanHelper.describe(valueObject, new String[0]));
      return gm;
    }

    public static Generator.GeneratorModel newFromMap(Map params) {
      Generator.GeneratorModel gm = newDefaultGeneratorModel();
      gm.templateModel.putAll(params);
      gm.filePathModel.putAll(params);
      return gm;
    }

    public static Generator.GeneratorModel newDefaultGeneratorModel() {
      Map templateModel = new HashMap();
      templateModel.putAll(getShareVars());

      Map filePathModel = new HashMap();
      filePathModel.putAll(getShareVars());
      return new Generator.GeneratorModel(templateModel, filePathModel);
    }

    public static Map getShareVars() {
      Map templateModel = new HashMap();
      templateModel.putAll(System.getProperties());
      templateModel.putAll(GeneratorProperties.getProperties());
      templateModel.put("env", System.getenv());
      templateModel.put("now", new Date());
      templateModel.put(GeneratorConstants.DATABASE_TYPE.code, GeneratorProperties.getDatabaseType(GeneratorConstants.DATABASE_TYPE.code));
      templateModel.putAll(GeneratorContext.getContext());
      templateModel.putAll(getToolsMap());
      return templateModel;
    }

    private static Map getToolsMap()
    {
      Map toolsMap = new HashMap();
      String[] tools = GeneratorProperties.getStringArray(GeneratorConstants.GENERATOR_TOOLS_CLASS);
      for (String className : tools) {
        try {
          Object instance = ClassHelper.newInstance(className);
          toolsMap.put(Class.forName(className).getSimpleName(), instance);
          BuilderLogger.debug("put tools class:" + className + " with key:" + Class.forName(className).getSimpleName());
        } catch (Exception e) {
          BuilderLogger.error("cannot load tools by className:" + className + " cause:" + e);
        }
      }
      return toolsMap;
    }
  }

  public class ProcessUtils
  {
    public ProcessUtils()
    {
    }

    public void processByGeneratorModel(Generator.GeneratorModel model, boolean isDelete)
      throws Exception, FileNotFoundException
    {
      Generator g = GeneratorFacade.this.getGenerator();

      Generator.GeneratorModel targetModel = GeneratorModelUtils.newDefaultGeneratorModel();
      targetModel.filePathModel.putAll(model.filePathModel);
      targetModel.templateModel.putAll(model.templateModel);
      processByGeneratorModel(isDelete, g, targetModel);
    }

    public void processByMap(Map params, boolean isDelete) throws Exception, FileNotFoundException {
      Generator g = GeneratorFacade.this.getGenerator();
      Generator.GeneratorModel m = GeneratorModelUtils.newFromMap(params);
      processByGeneratorModel(isDelete, g, m);
    }

    public void processBySql(Sql sql, boolean isDelete) throws Exception {
      Generator g = GeneratorFacade.this.getGenerator();
      Generator.GeneratorModel m = GeneratorModelUtils.newGeneratorModel("sql", sql);
      PrintUtils.printBeginProcess("sql:" + sql.getSourceSql(), isDelete);
      processByGeneratorModel(isDelete, g, m);
    }

    public void processByClass(Class clazz, boolean isDelete) throws Exception, FileNotFoundException {
      Generator g = GeneratorFacade.this.getGenerator();
      Generator.GeneratorModel m = GeneratorModelUtils.newGeneratorModel("clazz", new JavaClass(clazz));
      PrintUtils.printBeginProcess("JavaClass:" + clazz.getSimpleName(), isDelete);
      processByGeneratorModel(isDelete, g, m);
    }

    private void processByGeneratorModel(boolean isDelete, Generator g, Generator.GeneratorModel m)
      throws Exception, FileNotFoundException
    {
      try
      {
        if (isDelete)
          g.deleteBy(m.templateModel, m.filePathModel);
        else
          g.generateBy(m.templateModel, m.filePathModel);
      } catch (GeneratorException ge) {
        PrintUtils.printExceptionsSumary(ge.getMessage(), GeneratorFacade.this.getGenerator().getOutRootDir(), ge.getExceptions());
        throw ge;
      }
    }

    public void processByTable(String tableName, boolean isDelete) throws Exception {
      if ("*".equals(tableName)) {
        if (isDelete)
          GeneratorFacade.this.deleteByAllTable();
        else
          GeneratorFacade.this.generateByAllTable();
        return;
      }
      Generator g = GeneratorFacade.this.getGenerator();
      Table table = TableFactory.getInstance().getTable(tableName);
      try {
        processByTable(g, table, isDelete);
      } catch (GeneratorException ge) {
        PrintUtils.printExceptionsSumary(ge.getMessage(), GeneratorFacade.this.getGenerator().getOutRootDir(), ge.getExceptions());
        throw ge;
      }
    }

    public void processByAllTable(boolean isDelete) throws Exception {
      List tables = TableFactory.getInstance().getAllTables();
      List exceptions = new ArrayList();
      for (int i = 0; i < tables.size(); i++) {
        try {
          processByTable(GeneratorFacade.this.getGenerator(), (Table)tables.get(i), isDelete);
        } catch (GeneratorException ge) {
          exceptions.addAll(ge.getExceptions());
        }
      }
      PrintUtils.printExceptionsSumary("", GeneratorFacade.this.getGenerator().getOutRootDir(), exceptions);
      if (!exceptions.isEmpty())
        throw new GeneratorException("batch generate by all table occer error", exceptions);
    }

    public void processByTable(Generator g, Table table, boolean isDelete)
      throws Exception
    {
      Generator.GeneratorModel m = GeneratorModelUtils.newGeneratorModel("table", table);
      PrintUtils.printBeginProcess(table.getSqlName() + " => " + table.getClassName(), isDelete);
      if (isDelete)
        g.deleteBy(m.templateModel, m.filePathModel);
      else
        g.generateBy(m.templateModel, m.filePathModel);
    }
  }
}

