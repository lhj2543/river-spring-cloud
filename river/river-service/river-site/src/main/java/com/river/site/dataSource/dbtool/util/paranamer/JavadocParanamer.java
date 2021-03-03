package com.river.site.dataSource.dbtool.util.paranamer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class JavadocParanamer
  implements Paranamer
{
  private static final String IE = "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 2.0.50727)";
  private static final ParameterNamesNotFoundException CLASS_NOT_SUPPORTED = new ParameterNamesNotFoundException("class not supported");

  private String base = null;
  private final boolean isArchive;
  private final boolean isDirectory;
  private final boolean isURI;
  private final URI location;
  private final Set<String> packages = new HashSet();

  public JavadocParanamer(File archiveOrDirectory)
    throws IOException
  {
    if (archiveOrDirectory == null) {
      throw new NullPointerException();
    }
    if (!archiveOrDirectory.exists())
    {
      throw new FileNotFoundException(archiveOrDirectory
        .getAbsolutePath());
    }
    this.isURI = false;
    this.location = archiveOrDirectory.toURI();

    if (archiveOrDirectory.isDirectory())
    {
      this.isArchive = false;
      this.isDirectory = true;

      File dir = archiveOrDirectory;

      File packageList = new File(dir
        .getAbsolutePath() + "/package-list");
      if (!packageList.isFile())
      {
        throw new FileNotFoundException("No package-list found at " + dir
          .getAbsolutePath() + ". Not a valid Javadoc directory.");
      }

      FileInputStream input = new FileInputStream(packageList);
      try {
        String packageListString = streamToString(input);
        parsePackageList(packageListString);
      } finally {
        input.close();
      }
    } else if (archiveOrDirectory.isFile())
    {
      this.isArchive = true;
      this.isDirectory = false;
      File archive = archiveOrDirectory;
      if (!archive.getAbsolutePath().toLowerCase().endsWith(".zip")) {
        throw new IllegalArgumentException(archive.getAbsolutePath() + " is not a zip file.");
      }

      ZipFile zip = new ZipFile(archive);
      try
      {
        Enumeration entries = zip.entries();

        SortedMap packageLists = new TreeMap();
        while (entries.hasMoreElements()) {
          ZipEntry entry = (ZipEntry)entries.nextElement();
          String name = entry.getName();
          if (name.endsWith("package-list")) {
            Long size = Long.valueOf(entry.getSize());
            packageLists.put(size, entry);
          }
        }
        if (packageLists.size() == 0) {
          throw new FileNotFoundException("no package-list found in archive");
        }

        ZipEntry entry = (ZipEntry)packageLists.get(packageLists.lastKey());
        String name = entry.getName();
        this.base = name
          .substring(0, name
          .length() - "package-list"
          .length());
        InputStream input = zip.getInputStream(entry);
        try {
          String packageListString = streamToString(input);
          parsePackageList(packageListString);
        } finally {
        }
      }
      finally {
        zip.close();
      }
    }
    else {
      throw new IllegalArgumentException(archiveOrDirectory
        .getAbsolutePath() + " is neither a directory nor a file.");
    }
  }

  public JavadocParanamer(URL url)
    throws IOException
  {
    if (url == null) {
      throw new NullPointerException();
    }
    this.isArchive = false;
    this.isDirectory = false;
    this.isURI = true;
    try {
      this.location = new URI(url.toString());
    } catch (URISyntaxException e) {
      throw new IOException(e.getMessage());
    }

    URL packageListURL = new URL(url.toString() + "/package-list");
    InputStream input = urlToInputStream(packageListURL);
    try {
      String packageList = streamToString(input);
      parsePackageList(packageList);
    } finally {
      input.close();
    }
  }

  @Override
  public String[] lookupParameterNames(AccessibleObject methodOrConstructor) {
    return lookupParameterNames(methodOrConstructor, true);
  }

  @Override
  public String[] lookupParameterNames(AccessibleObject methodOrConstructor, boolean throwExceptionIfMissing) {
    if (methodOrConstructor == null) {
      throw new NullPointerException();
    }
    Class[] types;
    String name;
    Class klass;
    if ((methodOrConstructor instanceof Constructor)) {
      Constructor constructor = (Constructor)methodOrConstructor;
      klass = constructor.getDeclaringClass();
      name = constructor.getName();
      types = constructor.getParameterTypes();
    }
    else
    {
      if ((methodOrConstructor instanceof Method)) {
        Method method = (Method)methodOrConstructor;
        klass = method.getDeclaringClass();
        name = method.getName();
        types = method.getParameterTypes();
      } else {
        throw new IllegalArgumentException();
      }
    }

    if (!this.packages.contains(klass.getPackage().getName())) {
      throw CLASS_NOT_SUPPORTED;
    }
    try
    {
      String[] names = getParameterNames(klass, name, types);
      if (names == null) {
        if (throwExceptionIfMissing)
        {
          throw new ParameterNamesNotFoundException(methodOrConstructor
            .toString());
        }
        return Paranamer.EMPTY_NAMES;
      }

      return names;
    } catch (IOException e) {
      if (throwExceptionIfMissing)
      {
        throw new ParameterNamesNotFoundException(methodOrConstructor
          .toString() + " due to an I/O error: " + e
          .getMessage());
      }
    }
    return Paranamer.EMPTY_NAMES;
  }

  private String[] getParameterNames(Class<?> klass, String constructorOrMethodName, Class<?>[] types)
    throws IOException
  {
    if ((types != null) && (types.length == 0)) {
      return new String[0];
    }
    String path = getCanonicalName(klass).replace('.', '/');
    if (this.isArchive) {
      ZipFile archive = new ZipFile(new File(this.location));
      ZipEntry entry = archive.getEntry(this.base + path + ".html");
      if (entry == null) {
        throw CLASS_NOT_SUPPORTED;
      }
      InputStream input = archive.getInputStream(entry);
      return getParameterNames2(input, constructorOrMethodName, types);
    }if (this.isDirectory) {
      File file = new File(this.location.getPath() + "/" + path + ".html");
      if (!file.isFile()) {
        throw CLASS_NOT_SUPPORTED;
      }
      FileInputStream input = new FileInputStream(file);
      return getParameterNames2(input, constructorOrMethodName, types);
    }if (this.isURI) {
      try {
        URL url = new URL(this.location.toString() + "/" + path + ".html");
        InputStream input = urlToInputStream(url);
        return getParameterNames2(input, constructorOrMethodName, types);
      } catch (FileNotFoundException e) {
        throw CLASS_NOT_SUPPORTED;
      }
    }
    throw new RuntimeException("bug in JavadocParanamer. Should not reach here.");
  }

  private String[] getParameterNames2(InputStream input, String constructorOrMethodName, Class<?>[] types)
    throws IOException
  {
    String javadoc = streamToString(input);
    input.close();

    StringBuffer regex = new StringBuffer();
    regex.append("NAME=\"");
    regex.append(constructorOrMethodName);

    regex.append("\\(\\Q");
    for (int i = 0; i < types.length; i++) {
      if (i != 0) {
        regex.append(", ");
      }
      regex.append(getCanonicalName(types[i]));
    }
    regex.append("\\E\\)\"");

    Pattern pattern = Pattern.compile(regex.toString());
    Matcher matcher = pattern.matcher(javadoc);
    if (!matcher.find())
    {
      return Paranamer.EMPTY_NAMES;
    }

    String[] names = new String[types.length];

    String regexParams = "<DD><CODE>([^<]*)</CODE>";
    Pattern patternParams = Pattern.compile(regexParams);
    int start = matcher.end();
    Matcher matcherParams = patternParams.matcher(javadoc);
    for (int i = 0; i < types.length; i++) {
      boolean find = matcherParams.find(start);
      if (!find) {
        return Paranamer.EMPTY_NAMES;
      }
      start = matcherParams.end();
      names[i] = matcherParams.group(1);
    }
    return names;
  }

  private String getCanonicalName(Class<?> klass)
  {
    if (klass.isArray()) {
      return getCanonicalName(klass.getComponentType()) + "[]";
    }
    return klass.getName();
  }

  private void parsePackageList(String packageList) throws IOException {
    StringReader reader = new StringReader(packageList);
    BufferedReader breader = new BufferedReader(reader);
    String line;
    while ((line = breader.readLine()) != null){
      this.packages.add(line);
    }
  }

  private String streamToString(InputStream input) throws IOException
  {
    InputStreamReader reader;
    try
    {
      reader = new InputStreamReader(input, "UTF-8");
    }
    catch (UnsupportedEncodingException e)
    {
      reader = new InputStreamReader(input);
    }
    BufferedReader breader = new BufferedReader(reader);

    StringBuffer builder = new StringBuffer();
    String line;
    while ((line = breader.readLine()) != null) {
      builder.append(line);
      builder.append("\n");
    }
    return builder.toString();
  }

  private InputStream urlToInputStream(URL url) throws IOException {
    URLConnection conn = url.openConnection();

    conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 2.0.50727)");

    conn.setRequestProperty("Accept-Encoding", "gzip, deflate");
    conn.connect();
    String encoding = conn.getContentEncoding();
    if ((encoding != null) && (encoding.equalsIgnoreCase("gzip"))) {
      return new GZIPInputStream(conn.getInputStream());
    }
    if ((encoding != null) && (encoding.equalsIgnoreCase("deflate"))) {
      return new InflaterInputStream(conn.getInputStream(), new Inflater(true));
    }

    return conn.getInputStream();
  }
}

