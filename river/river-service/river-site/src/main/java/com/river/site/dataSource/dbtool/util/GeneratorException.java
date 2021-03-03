package com.river.site.dataSource.dbtool.util;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class GeneratorException extends RuntimeException
{
  public List<Exception> exceptions = new ArrayList();

  public GeneratorException()
  {
  }

  public GeneratorException(String message, Throwable cause) {
    super(message, cause);
  }

  public GeneratorException(String message) {
    super(message);
  }

  public GeneratorException(String message, List<Exception> exceptions) {
    super(message);
    this.exceptions = exceptions;
  }

  public GeneratorException(Throwable cause) {
    super(cause);
  }

  public List<Exception> getExceptions()
  {
    return this.exceptions;
  }

  public void setExceptions(List<Exception> exceptions) {
    if (exceptions == null) {
      throw new NullPointerException("'exceptions' must be not null");
    }
    this.exceptions = exceptions;
  }

  public GeneratorException add(Exception e) {
    this.exceptions.add(e);
    return this;
  }

  public GeneratorException addAll(List<Exception> excetpions) {
    this.exceptions.addAll(excetpions);
    return this;
  }

  @Override
  public void printStackTrace()
  {
    printStackTrace(System.err);
  }

  @Override
  public void printStackTrace(PrintStream s)
  {
    s.println("GeneratorException:" + getMessage());
    for (Exception e : this.exceptions) {
      e.printStackTrace(s);
      s.println("--------------------------------------------------------------------------------------------------------------------------------");
    }
  }

  @Override
  public void printStackTrace(PrintWriter s)
  {
    s.println("GeneratorException:" + getMessage());
    for (Exception e : this.exceptions) {
      e.printStackTrace(s);
      s.println("--------------------------------------------------------------------------------------------------------------------------------");
    }
  }

  @Override
  public String toString() {
    StringWriter out = new StringWriter();
    for (Exception e : this.exceptions) {
      out.append(e.toString() + "\n");
    }
    return out.toString();
  }
}

