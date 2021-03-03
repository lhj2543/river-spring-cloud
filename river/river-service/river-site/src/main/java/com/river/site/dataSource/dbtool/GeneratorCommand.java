package com.river.site.dataSource.dbtool;

import com.river.site.dataSource.dbtool.util.ArrayHelper;
import com.river.site.dataSource.dbtool.util.StringHelper;
import com.river.site.dataSource.dbtool.util.SystemHelper;

import java.io.File;
import java.io.PrintStream;
import java.util.Scanner;

public class GeneratorCommand
{
  public void startProcess()
    throws Exception
  {
    Scanner sc = new Scanner(System.in);
    System.out.println("templateRootDir:" + new File(getTemplateRootDir()).getAbsolutePath());
    printUsages();
    while (sc.hasNextLine()) {
      try {
        processLine(sc);
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        Thread.sleep(700L);
        printUsages();
      }
    }
  }

  private void processLine(Scanner sc) throws Exception
  {
    GeneratorFacade facade = new GeneratorFacade();
    String cmd = sc.next();
    if ("gen".equals(cmd)) {
      String[] args = nextArguments(sc);
      if (args.length == 0) {
        return;
      }
      facade.getGenerator().setIncludes(getIncludes(args, 1));
      facade.getGenerator().addTemplateRootDir(new File(getTemplateRootDir()));
      facade.generateByTable(new String[] { args[0] });
      if (SystemHelper.isWindowsOS) {
        Runtime.getRuntime().exec("cmd.exe /c start " + GeneratorProperties.getRequiredProperty("outRoot").replace('/', '\\'));
      }
    }
    else if ("del".equals(cmd)) {
      String[] args = nextArguments(sc);
      if (args.length == 0) {
        return;
      }
      facade.getGenerator().setIncludes(getIncludes(args, 1));
      facade.getGenerator().addTemplateRootDir(new File(getTemplateRootDir()));
      facade.deleteByTable(new String[] { args[0] });
    } else if ("quit".equals(cmd)) {
      System.exit(0);
    } else {
      System.err.println(" [ERROR] unknow command:" + cmd);
    }
  }

  private String getIncludes(String[] args, int i) {
    String includes = ArrayHelper.getValue(args, i);
    if (includes == null) {
      return null;
    }
    return includes + "/**";
  }

  private String getTemplateRootDir() {
    return System.getProperty("templateRootDir", "template");
  }

  private void printUsages() {
    System.out.println("Usage:");
    System.out.println("\tgen table_name [include_path]: generate files by table_name");
    System.out.println("\tdel table_name [include_path]: delete files by table_name");
    System.out.println("\tgen * [include_path]: search database all tables and generate files");
    System.out.println("\tdel * [include_path]: search database all tables and delete files");
    System.out.println("\tquit : quit");
    System.out.println("\t[include_path] subdir of templateRootDir,example: 1. dao  2. dao/**,service/**");
    System.out.print("please input command:");
  }

  private String[] nextArguments(Scanner sc) {
    return StringHelper.tokenizeToStringArray(sc.nextLine(), " ");
  }
}

