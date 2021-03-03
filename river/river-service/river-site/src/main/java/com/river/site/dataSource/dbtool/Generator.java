package com.river.site.dataSource.dbtool;

import com.river.site.dataSource.dbtool.util.*;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.*;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.*;

public class Generator
{
  private static final String GENERATOR_INSERT_LOCATION = "generator-insert-location";
  private ArrayList<File> templateRootDirs = new ArrayList();
  private String outRootDir;
  private boolean ignoreTemplateGenerateException = true;
  private String removeExtensions = GeneratorProperties.getProperty(GeneratorConstants.GENERATOR_REMOVE_EXTENSIONS);
  private boolean isCopyBinaryFile = true;

  private String includes = GeneratorProperties.getProperty(GeneratorConstants.GENERATOR_INCLUDES);
  private String excludes = GeneratorProperties.getProperty(GeneratorConstants.GENERATOR_EXCLUDES);
  private String sourceEncoding = GeneratorProperties.getProperty(GeneratorConstants.GENERATOR_SOURCE_ENCODING);
  private String outputEncoding = GeneratorProperties.getProperty(GeneratorConstants.GENERATOR_OUTPUT_ENCODING);

  public void setTemplateRootDir(File templateRootDir)
  {
    setTemplateRootDirs(new File[] { templateRootDir });
  }

  public void setTemplateRootDir(String templateRootDir)
  {
    setTemplateRootDirs(StringHelper.tokenizeToStringArray(templateRootDir, ","));
  }

  public void setTemplateRootDirs(File[] templateRootDirs) {
    this.templateRootDirs = new ArrayList(Arrays.asList(templateRootDirs));
  }

  public void setTemplateRootDirs(String[] templateRootDirs) {
    ArrayList tempDirs = new ArrayList();
    for (String dir : templateRootDirs) {
      tempDirs.add(FileHelper.getFile(dir));
    }
    this.templateRootDirs = tempDirs;
  }

  public void addTemplateRootDir(File file) {
    this.templateRootDirs.add(file);
  }

  public void addTemplateRootDir(String file) {
    this.templateRootDirs.add(FileHelper.getFile(file));
  }

  public boolean isIgnoreTemplateGenerateException() {
    return this.ignoreTemplateGenerateException;
  }

  public void setIgnoreTemplateGenerateException(boolean ignoreTemplateGenerateException) {
    this.ignoreTemplateGenerateException = ignoreTemplateGenerateException;
  }

  public boolean isCopyBinaryFile() {
    return this.isCopyBinaryFile;
  }

  public void setCopyBinaryFile(boolean isCopyBinaryFile) {
    this.isCopyBinaryFile = isCopyBinaryFile;
  }

  public String getSourceEncoding() {
    return this.sourceEncoding;
  }

  public void setSourceEncoding(String sourceEncoding) {
    if (StringHelper.isBlank(sourceEncoding)) {
      throw new IllegalArgumentException("sourceEncoding must be not empty");
    }
    this.sourceEncoding = sourceEncoding;
  }

  public String getOutputEncoding() {
    return this.outputEncoding;
  }

  public void setOutputEncoding(String outputEncoding) {
    if (StringHelper.isBlank(outputEncoding)) {
      throw new IllegalArgumentException("outputEncoding must be not empty");
    }
    this.outputEncoding = outputEncoding;
  }

  public void setIncludes(String includes) {
    this.includes = includes;
  }

  public void setExcludes(String excludes) {
    this.excludes = excludes;
  }

  public void setOutRootDir(String rootDir) {
    if (rootDir == null) {
      throw new IllegalArgumentException("outRootDir must be not null");
    }
    this.outRootDir = rootDir;
  }

  public String getOutRootDir()
  {
    return this.outRootDir;
  }

  public void setRemoveExtensions(String removeExtensions) {
    this.removeExtensions = removeExtensions;
  }

  public void deleteOutRootDir() throws IOException {
    if (StringHelper.isBlank(getOutRootDir())) {
      throw new IllegalStateException("'outRootDir' property must be not null.");
    }
    BuilderLogger.println("[delete dir]    " + getOutRootDir());
    FileHelper.deleteDirectory(new File(getOutRootDir()));
  }

  public Generator generateBy(Map templateModel, Map filePathModel)
    throws Exception
  {
    processTemplateRootDirs(templateModel, filePathModel, false);
    return this;
  }

  public Generator deleteBy(Map templateModel, Map filePathModel)
    throws Exception
  {
    processTemplateRootDirs(templateModel, filePathModel, true);
    return this;
  }

  private void processTemplateRootDirs(Map templateModel, Map filePathModel, boolean isDelete) throws Exception
  {
    if (StringHelper.isBlank(getOutRootDir())) {
      throw new IllegalStateException("'outRootDir' property must be not empty.");
    }
    if ((this.templateRootDirs == null) || (this.templateRootDirs.size() == 0)) {
      throw new IllegalStateException("'templateRootDirs'  must be not empty");
    }

    BuilderLogger.debug("******* Template reference variables *********", templateModel);
    BuilderLogger.debug("\n\n******* FilePath reference variables *********", filePathModel);

    templateModel.putAll(GeneratorHelper.getDirValuesMap(templateModel));
    filePathModel.putAll(GeneratorHelper.getDirValuesMap(filePathModel));

    GeneratorException ge = new GeneratorException("generator occer error, Generator BeanInfo:" + BeanHelper.describe(this, new String[0]));
    List processedTemplateRootDirs = processTemplateRootDirs();

    for (int i = 0; i < processedTemplateRootDirs.size(); i++) {
      File templateRootDir = (File)processedTemplateRootDirs.get(i);
      List exceptions = scanTemplatesAndProcess(templateRootDir, processedTemplateRootDirs, templateModel, filePathModel, isDelete);
      ge.addAll(exceptions);
    }
    if (!ge.exceptions.isEmpty()) {
      throw ge;
    }
  }

  protected List<File> processTemplateRootDirs()
    throws Exception
  {
    return unzipIfTemplateRootDirIsZipFile();
  }

  private List<File> unzipIfTemplateRootDirIsZipFile()
    throws MalformedURLException
  {
    List unzipIfTemplateRootDirIsZipFile = new ArrayList();
    for (int i = 0; i < this.templateRootDirs.size(); i++) {
      File file = (File)this.templateRootDirs.get(i);
      String templateRootDir = FileHelper.toFilePathIfIsURL(file);

      String subFolder = "";
      int zipFileSeperatorIndexOf = templateRootDir.indexOf("!");
      if (zipFileSeperatorIndexOf >= 0) {
        subFolder = templateRootDir.substring(zipFileSeperatorIndexOf + 1);
        templateRootDir = templateRootDir.substring(0, zipFileSeperatorIndexOf);
      }

      if (new File(templateRootDir).isFile()) {
        File tempDir = ZipUtils.unzip2TempDir(new File(templateRootDir), "tmp_generator_template_folder_for_zipfile");
        unzipIfTemplateRootDirIsZipFile.add(new File(tempDir, subFolder));
      } else {
        unzipIfTemplateRootDirIsZipFile.add(new File(templateRootDir, subFolder));
      }
    }
    return unzipIfTemplateRootDirIsZipFile;
  }

  private List<Exception> scanTemplatesAndProcess(File templateRootDir, List<File> templateRootDirs, Map templateModel, Map filePathModel, boolean isDelete)
    throws Exception
  {
    if (templateRootDir == null) {
      throw new IllegalStateException("'templateRootDir' must be not null");
    }
    BuilderLogger.println("-------------------load template from templateRootDir = '" + templateRootDir.getAbsolutePath() + "' outRootDir:" + new File(this.outRootDir).getAbsolutePath());

    List srcFiles = FileHelper.searchAllNotIgnoreFile(templateRootDir);

    List exceptions = new ArrayList();
    for (int i = 0; i < srcFiles.size(); i++) {
      File srcFile = (File)srcFiles.get(i);
      try {
        if (isDelete) {
          new TemplateProcessor(templateRootDirs).executeDelete(templateRootDir, templateModel, filePathModel, srcFile);
        } else {
          long start = System.currentTimeMillis();
          new TemplateProcessor(templateRootDirs).executeGenerate(templateRootDir, templateModel, filePathModel, srcFile);
          BuilderLogger.perf("genereate by tempate cost time:" + (System.currentTimeMillis() - start) + "ms");
        }
      } catch (Exception e) {
        if (this.ignoreTemplateGenerateException) {
          BuilderLogger.warn("iggnore generate error,template is:" + srcFile + " cause:" + e);
          exceptions.add(e);
        } else {
          throw e;
        }
      }
    }
    return exceptions;
  }

  public static class GeneratorModel
    implements Serializable
  {
    private static final long serialVersionUID = -6430787906037836995L;
    public Map templateModel = new HashMap();

    public Map filePathModel = new HashMap();

    public GeneratorModel() {
    }
    public GeneratorModel(Map templateModel, Map filePathModel) {
      this.templateModel = templateModel;
      this.filePathModel = filePathModel;
    }
  }

  static class GeneratorHelper
  {
    public static Map getDirValuesMap(Map map)
    {
      Map dirValues = new HashMap();
      Set keys = map.keySet();
      for (Iterator localIterator = keys.iterator(); localIterator.hasNext(); ) { Object key = localIterator.next();
        Object value = map.get(key);
        if (((key instanceof String)) && ((value instanceof String))) {
          String dirKey = key + "_dir";
          String dirValue = value.toString().replace('.', '/');
          dirValues.put(dirKey, dirValue);
        }
      }
      return dirValues;
    }

    public static boolean isIgnoreTemplateProcess(File srcFile, String templateFile, String includes, String excludes) {
      if ((srcFile.isDirectory()) || (srcFile.isHidden())) {
        return true;
      }
      if (templateFile.trim().equals("")) {
        return true;
      }
      if (srcFile.getName().toLowerCase().endsWith(".include")) {
        BuilderLogger.println("[skip]\t\t endsWith '.include' template:" + templateFile);
        return true;
      }
      templateFile = templateFile.replace('\\', '/');
      for (String exclude : StringHelper.tokenizeToStringArray(excludes, ",")) {
        if (new AntPathMatcher().match(exclude.replace('\\', '/'), templateFile)) {
          return true;
        }
      }
      if (StringHelper.isBlank(includes)) {
        return false;
      }
      for (String include : StringHelper.tokenizeToStringArray(includes, ",")) {
        if (new AntPathMatcher().match(include.replace('\\', '/'), templateFile)) {
          return false;
        }
      }
      return true;
    }

    private static boolean isFoundInsertLocation(GeneratorControl gg, Template template, Map model, File outputFile, StringWriter newFileContent) throws IOException, TemplateException {
      LineNumberReader reader = new LineNumberReader(new FileReader(outputFile));
      String line = null;
      boolean isFoundInsertLocation = false;

      PrintWriter writer = new PrintWriter(newFileContent);
      while ((line = reader.readLine()) != null) {
        writer.println(line);

        if ((!isFoundInsertLocation) && (line.indexOf(gg.getMergeLocation()) >= 0)) {
          template.process(model, writer);
          writer.println();
          isFoundInsertLocation = true;
        }
      }

      writer.close();
      reader.close();
      return isFoundInsertLocation;
    }
    public static Configuration newFreeMarkerConfiguration(List<File> templateRootDirs, String defaultEncoding, String templateName) throws IOException {
      Configuration conf = new Configuration();

      FileTemplateLoader[] templateLoaders = new FileTemplateLoader[templateRootDirs.size()];
      for (int i = 0; i < templateRootDirs.size(); i++) {
        templateLoaders[i] = new FileTemplateLoader((File)templateRootDirs.get(i));
      }
      MultiTemplateLoader multiTemplateLoader = new MultiTemplateLoader(templateLoaders);

      conf.setTemplateLoader(multiTemplateLoader);
      conf.setNumberFormat("###############");
      conf.setBooleanFormat("true,false");
      conf.setDefaultEncoding(defaultEncoding);

      List autoIncludes = getParentPaths(templateName, "macro.include");
      List availableAutoInclude = FreemarkerHelper.getAvailableAutoInclude(conf, autoIncludes);
      conf.setAutoIncludes(availableAutoInclude);
      BuilderLogger.trace("set Freemarker.autoIncludes:" + availableAutoInclude + " for templateName:" + templateName + " autoIncludes:" + autoIncludes);
      return conf;
    }

    public static List<String> getParentPaths(String templateName, String suffix) {
      String[] array = StringHelper.tokenizeToStringArray(templateName, "\\/");
      List list = new ArrayList();
      list.add(suffix);
      list.add(File.separator + suffix);
      String path = "";
      for (int i = 0; i < array.length; i++) {
        path = path + File.separator + array[i];
        list.add(path + File.separator + suffix);
      }
      return list;
    }
  }

  private class TemplateProcessor
  {
    private GeneratorControl gg = new GeneratorControl();
    private List<File> templateRootDirs = new ArrayList();

    public TemplateProcessor(List<File> templateRootDirs)
    {
      this.templateRootDirs = templateRootDirs;
    }

    private void executeGenerate(File templateRootDir, Map templateModel, Map filePathModel, File srcFile) throws SQLException, IOException, TemplateException {
      String templateFile = FileHelper.getRelativePath(templateRootDir, srcFile);
      if (GeneratorHelper.isIgnoreTemplateProcess(srcFile, templateFile, Generator.this.includes, Generator.this.excludes)) {
        return;
      }

      if ((Generator.this.isCopyBinaryFile) && (FileHelper.isBinaryFile(srcFile))) {
        String outputFilepath = proceeForOutputFilepath(filePathModel, templateFile);
        File outputFile = new File(Generator.this.getOutRootDir(), outputFilepath);
        BuilderLogger.println("[copy binary file by extention] from:" + srcFile + " => " + outputFile);
        FileHelper.parentMkdir(outputFile);
        IOHelper.copyAndClose(new FileInputStream(srcFile), new FileOutputStream(outputFile));
        return;
      }
      try
      {
        String outputFilepath = proceeForOutputFilepath(filePathModel, templateFile);

        initGeneratorControlProperties(srcFile, outputFilepath);
        processTemplateForGeneratorControl(templateModel, templateFile);

        if (this.gg.isIgnoreOutput()) {
          BuilderLogger.println("[not generate] by gg.isIgnoreOutput()=true on template:" + templateFile);
          return;
        }

        if (StringHelper.isNotBlank(this.gg.getOutputFile())) {
          generateNewFileOrInsertIntoFile(templateFile, this.gg.getOutputFile(), templateModel);
        }
      }
      catch (Exception e) {
        throw new RuntimeException("generate oucur error,templateFile is:" + templateFile + " => " + this.gg.getOutputFile() + " cause:" + e, e);
      }
    }

    private void executeDelete(File templateRootDir, Map templateModel, Map filePathModel, File srcFile) throws SQLException, IOException, TemplateException {
      String templateFile = FileHelper.getRelativePath(templateRootDir, srcFile);
      if (GeneratorHelper.isIgnoreTemplateProcess(srcFile, templateFile, Generator.this.includes, Generator.this.excludes)) {
        return;
      }
      String outputFilepath = proceeForOutputFilepath(filePathModel, templateFile);
      initGeneratorControlProperties(srcFile, outputFilepath);
      this.gg.deleteGeneratedFile = true;
      processTemplateForGeneratorControl(templateModel, templateFile);
      BuilderLogger.println("[delete file] file:" + new File(this.gg.getOutputFile()).getAbsolutePath());
      new File(this.gg.getOutputFile()).delete();
    }

    private void initGeneratorControlProperties(File srcFile, String outputFile) throws SQLException {
      this.gg.setSourceFile(srcFile.getAbsolutePath());
      this.gg.setSourceFileName(srcFile.getName());
      this.gg.setSourceDir(srcFile.getParent());
      this.gg.setOutRoot(Generator.this.getOutRootDir());
      this.gg.setOutputEncoding(Generator.this.outputEncoding);
      this.gg.setSourceEncoding(Generator.this.sourceEncoding);
      this.gg.setMergeLocation("generator-insert-location");
      this.gg.setOutputFile(outputFile);
    }

    private void processTemplateForGeneratorControl(Map templateModel, String templateFile) throws IOException, TemplateException {
      templateModel.put("gg", this.gg);
      Template template = getFreeMarkerTemplate(templateFile);
      template.process(templateModel, IOHelper.NULL_WRITER);
    }

    private String proceeForOutputFilepath(Map filePathModel, String templateFile) throws IOException
    {
      String outputFilePath = templateFile;

      int testExpressionIndex = -1;
      if ((testExpressionIndex = templateFile.indexOf('@')) != -1) {
        outputFilePath = templateFile.substring(0, testExpressionIndex);
        String testExpressionKey = templateFile.substring(testExpressionIndex + 1);
        Object expressionValue = filePathModel.get(testExpressionKey);
        if (expressionValue == null) {
          System.err.println("[not-generate] WARN: test expression is null by key:[" + testExpressionKey + "] on template:[" + templateFile + "]");
          return null;
        }
        if (!"true".equals(String.valueOf(expressionValue))) {
          BuilderLogger.println("[not-generate]\t test expression '@" + testExpressionKey + "' is false,template:" + templateFile);
          return null;
        }
      }

      String[] testExpressionKey = Generator.this.removeExtensions.split(",");
      int expressionValue = testExpressionKey.length;
      for (int localObject1 = 0; localObject1 < expressionValue; localObject1++) { String removeExtension = testExpressionKey[localObject1];
        if (outputFilePath.endsWith(removeExtension)) {
          outputFilePath = outputFilePath.substring(0, outputFilePath.length() - removeExtension.length());
          break;
        }
      }
      Configuration conf = GeneratorHelper.newFreeMarkerConfiguration(this.templateRootDirs, Generator.this.sourceEncoding, "/filepath/processor/");

      outputFilePath = outputFilePath.replace('^', '?');
      return FreemarkerHelper.processTemplateString(outputFilePath, filePathModel, conf);
    }

    private Template getFreeMarkerTemplate(String templateName) throws IOException {
      return GeneratorHelper.newFreeMarkerConfiguration(this.templateRootDirs, Generator.this.sourceEncoding, templateName).getTemplate(templateName);
    }

    private void generateNewFileOrInsertIntoFile(String templateFile, String outputFilepath, Map templateModel) throws Exception {
      Template template = getFreeMarkerTemplate(templateFile);
      template.setOutputEncoding(this.gg.getOutputEncoding());

      File absoluteOutputFilePath = FileHelper.parentMkdir(outputFilepath);
      if (absoluteOutputFilePath.exists()) {
        StringWriter newFileContentCollector = new StringWriter();
        if (GeneratorHelper.isFoundInsertLocation(this.gg, template, templateModel, absoluteOutputFilePath, newFileContentCollector)) {
          BuilderLogger.println("[insert]\t generate content into:" + outputFilepath);
          IOHelper.saveFile(absoluteOutputFilePath, newFileContentCollector.toString(), this.gg.getOutputEncoding());
          return;
        }
      }

      if ((absoluteOutputFilePath.exists()) && (!this.gg.isOverride())) {
        BuilderLogger.println("[not generate]\t by gg.isOverride()=false and outputFile exist:" + outputFilepath);
        return;
      }

      if (absoluteOutputFilePath.exists()) {
        BuilderLogger.println("[override]\t template:" + templateFile + " ==> " + outputFilepath);
      } else {
        BuilderLogger.println("[generate]\t template:" + templateFile + " ==> " + outputFilepath);
      }
      FreemarkerHelper.processTemplate(template, templateModel, absoluteOutputFilePath, this.gg.getOutputEncoding());
    }
  }
}

