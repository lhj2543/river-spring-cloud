package com.river.site.dataSource.dbtool.util;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;

public class AntPathMatcher
{
  public static final String DEFAULT_PATH_SEPARATOR = "/";
  private String pathSeparator;

  public AntPathMatcher()
  {
    this.pathSeparator = "/";
  }

  public void setPathSeparator(String pathSeparator) {
    this.pathSeparator = (pathSeparator != null ? pathSeparator : "/");
  }

  public boolean isPattern(String path) {
    return (path.indexOf('*') != -1) || (path.indexOf('?') != -1);
  }

  public boolean match(String pattern, String path) {
    return doMatch(pattern, path, true, null);
  }

  public boolean matchStart(String pattern, String path) {
    return doMatch(pattern, path, false, null);
  }

  protected boolean doMatch(String pattern, String path, boolean fullMatch, Map<String, String> uriTemplateVariables)
  {
    if (path.startsWith(this.pathSeparator) != pattern.startsWith(this.pathSeparator)) {
      return false;
    }

    String[] pattDirs = StringHelper.tokenizeToStringArray(pattern, this.pathSeparator);
    String[] pathDirs = StringHelper.tokenizeToStringArray(path, this.pathSeparator);

    int pattIdxStart = 0;
    int pattIdxEnd = pattDirs.length - 1;
    int pathIdxStart = 0;
    int pathIdxEnd = pathDirs.length - 1;

    while ((pattIdxStart <= pattIdxEnd) && (pathIdxStart <= pathIdxEnd)) {
      String patDir = pattDirs[pattIdxStart];
      if ("**".equals(patDir)) {
        break;
      }
      if (!matchStrings(patDir, pathDirs[pathIdxStart], uriTemplateVariables)) {
        return false;
      }
      pattIdxStart++;
      pathIdxStart++;
    }

    if (pathIdxStart > pathIdxEnd)
    {
      if (pattIdxStart > pattIdxEnd)
      {
        return !path
          .endsWith(this.pathSeparator) ? 
          true : pattern.endsWith(this.pathSeparator) ? path.endsWith(this.pathSeparator) : 
          false;
      }
      if (!fullMatch) {
        return true;
      }
      if ((pattIdxStart == pattIdxEnd) && (pattDirs[pattIdxStart].equals("*")) && (path.endsWith(this.pathSeparator))) {
        return true;
      }
      for (int i = pattIdxStart; i <= pattIdxEnd; i++) {
        if (!pattDirs[i].equals("**")) {
          return false;
        }
      }
      return true;
    }
    if (pattIdxStart > pattIdxEnd)
    {
      return false;
    }
    if ((!fullMatch) && ("**".equals(pattDirs[pattIdxStart])))
    {
      return true;
    }

    while ((pattIdxStart <= pattIdxEnd) && (pathIdxStart <= pathIdxEnd)) {
      String patDir = pattDirs[pattIdxEnd];
      if (patDir.equals("**")) {
        break;
      }
      if (!matchStrings(patDir, pathDirs[pathIdxEnd], uriTemplateVariables)) {
        return false;
      }
      pattIdxEnd--;
      pathIdxEnd--;
    }
    if (pathIdxStart > pathIdxEnd)
    {
      for (int i = pattIdxStart; i <= pattIdxEnd; i++) {
        if (!pattDirs[i].equals("**")) {
          return false;
        }
      }
      return true;
    }

    while ((pattIdxStart != pattIdxEnd) && (pathIdxStart <= pathIdxEnd)) {
      int patIdxTmp = -1;
      for (int i = pattIdxStart + 1; i <= pattIdxEnd; i++) {
        if (pattDirs[i].equals("**")) {
          patIdxTmp = i;
          break;
        }
      }
      if (patIdxTmp == pattIdxStart + 1)
      {
        pattIdxStart++;
      }
      else
      {
        int patLength = patIdxTmp - pattIdxStart - 1;
        int strLength = pathIdxEnd - pathIdxStart + 1;
        int foundIdx = -1;

        label550: for (int i = 0; i <= strLength - patLength; i++) {
          for (int j = 0; j < patLength; j++) {
            String subPat = pattDirs[(pattIdxStart + j + 1)];
            String subStr = pathDirs[(pathIdxStart + i + j)];
            if (!matchStrings(subPat, subStr, uriTemplateVariables)) {
              break label550;
            }
          }
          foundIdx = pathIdxStart + i;
          break;
        }

        if (foundIdx == -1) {
          return false;
        }

        pattIdxStart = patIdxTmp;
        pathIdxStart = foundIdx + patLength;
      }
    }
    for (int i = pattIdxStart; i <= pattIdxEnd; i++) {
      if (!pattDirs[i].equals("**")) {
        return false;
      }
    }

    return true;
  }

  private boolean matchStrings(String pattern, String str, Map<String, String> uriTemplateVariables)
  {
    AntPathStringMatcher matcher = new AntPathStringMatcher(pattern, str, uriTemplateVariables);
    return matcher.matchStrings();
  }

  public String extractPathWithinPattern(String pattern, String path)
  {
    String[] patternParts = StringHelper.tokenizeToStringArray(pattern, this.pathSeparator);
    String[] pathParts = StringHelper.tokenizeToStringArray(path, this.pathSeparator);

    StringBuilder builder = new StringBuilder();

    int puts = 0;
    for (int i = 0; i < patternParts.length; i++) {
      String patternPart = patternParts[i];
      if (((patternPart.indexOf('*') > -1) || (patternPart.indexOf('?') > -1)) && (pathParts.length >= i + 1)) {
        if ((puts > 0) || ((i == 0) && (!pattern.startsWith(this.pathSeparator)))) {
          builder.append(this.pathSeparator);
        }
        builder.append(pathParts[i]);
        puts++;
      }

    }

    for (int i = patternParts.length; i < pathParts.length; i++) {
      if ((puts > 0) || (i > 0)) {
        builder.append(this.pathSeparator);
      }
      builder.append(pathParts[i]);
    }

    return builder.toString();
  }

  public Map<String, String> extractUriTemplateVariables(String pattern, String path) {
    Map variables = new LinkedHashMap();
    boolean result = doMatch(pattern, path, true, variables);
    if (!result) {
      throw new IllegalStateException(new StringBuilder().append("Pattern \"").append(pattern).append("\" is not a match for \"").append(path).append("\"").toString());
    }
    return variables;
  }

  public String combine(String pattern1, String pattern2)
  {
    if ((StringHelper.isBlank(pattern1)) && (StringHelper.isBlank(pattern2))) {
      return "";
    }
    if (StringHelper.isBlank(pattern1)) {
      return pattern2;
    }
    if (StringHelper.isBlank(pattern2)) {
      return pattern1;
    }
    if (match(pattern1, pattern2)) {
      return pattern2;
    }
    if (pattern1.endsWith("/*")) {
      if (pattern2.startsWith("/"))
      {
        return new StringBuilder().append(pattern1.substring(0, pattern1.length() - 1)).append(pattern2.substring(1)).toString();
      }

      return new StringBuilder().append(pattern1.substring(0, pattern1.length() - 1)).append(pattern2).toString();
    }

    if (pattern1.endsWith("/**")) {
      if (pattern2.startsWith("/"))
      {
        return new StringBuilder().append(pattern1).append(pattern2).toString();
      }

      return new StringBuilder().append(pattern1).append("/").append(pattern2).toString();
    }

    int dotPos1 = pattern1.indexOf('.');
    if (dotPos1 == -1)
    {
      if ((pattern1.endsWith("/")) || (pattern2.startsWith("/"))) {
        return new StringBuilder().append(pattern1).append(pattern2).toString();
      }

      return new StringBuilder().append(pattern1).append("/").append(pattern2).toString();
    }

    String fileName1 = pattern1.substring(0, dotPos1);
    String extension1 = pattern1.substring(dotPos1);

    int dotPos2 = pattern2.indexOf('.');
    String fileName2;
    String extension2;
    if (dotPos2 != -1) {
      fileName2 = pattern2.substring(0, dotPos2);
      extension2 = pattern2.substring(dotPos2);
    }
    else {
      fileName2 = pattern2;
      extension2 = "";
    }
    String fileName = fileName1.endsWith("*") ? fileName2 : fileName1;
    String extension = extension1.startsWith("*") ? extension2 : extension1;

    return new StringBuilder().append(fileName).append(extension).toString();
  }

  public Comparator<String> getPatternComparator(String path)
  {
    return new AntPatternComparator(path);
  }

  private static class AntPatternComparator implements Comparator<String>
  {
    private final String path;

    private AntPatternComparator(String path) {
      this.path = path;
    }

    @Override
    public int compare(String pattern1, String pattern2) {
      if ((pattern1 == null) && (pattern2 == null)) {
        return 0;
      }
      if (pattern1 == null) {
        return 1;
      }
      if (pattern2 == null) {
        return -1;
      }
      boolean pattern1EqualsPath = pattern1.equals(this.path);
      boolean pattern2EqualsPath = pattern2.equals(this.path);
      if ((pattern1EqualsPath) && (pattern2EqualsPath)) {
        return 0;
      }
      if (pattern1EqualsPath) {
        return -1;
      }
      if (pattern2EqualsPath) {
        return 1;
      }
      int wildCardCount1 = StringHelper.countOccurrencesOf(pattern1, "*");
      int wildCardCount2 = StringHelper.countOccurrencesOf(pattern2, "*");
      if (wildCardCount1 < wildCardCount2) {
        return -1;
      }
      if (wildCardCount2 < wildCardCount1) {
        return 1;
      }
      int bracketCount1 = StringHelper.countOccurrencesOf(pattern1, "{");
      int bracketCount2 = StringHelper.countOccurrencesOf(pattern2, "{");
      if (bracketCount1 < bracketCount2) {
        return -1;
      }
      if (bracketCount2 < bracketCount1) {
        return 1;
      }
      return 0;
    }
  }
}

