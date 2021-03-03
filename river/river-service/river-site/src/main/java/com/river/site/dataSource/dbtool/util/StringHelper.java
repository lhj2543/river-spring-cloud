package com.river.site.dataSource.dbtool.util;

import com.river.site.dataSource.dbtool.GeneratorConstants;
import com.river.site.dataSource.dbtool.GeneratorProperties;
import com.river.site.dataSource.dbtool.provider.db.table.Column;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringHelper
{
  private static final String FOLDER_SEPARATOR = "/";
  private static final String WINDOWS_FOLDER_SEPARATOR = "\\";
  private static final String TOP_PATH = "..";
  private static final String CURRENT_PATH = ".";
  private static final char EXTENSION_SEPARATOR = '.';
  private static final Map<String, String> XML = new HashMap();

  private static final Random RANDOM = new Random();

  static Pattern three = Pattern.compile("(.*)\\((.*),(.*)\\)");
  static Pattern two = Pattern.compile("(.*)\\((.*)\\)");

  public static String removeCrlf(String str)
  {
    if (str == null) {
      return null;
    }
    return join(tokenizeToStringArray(str, "\t\n\r\f"), " ");
  }

  public static String unescapeXml(String str)
  {
    if (str == null) {
      return null;
    }
    for (String key : XML.keySet()) {
      String value = (String)XML.get(key);
      str = replace(str, new StringBuilder().append("&").append(key).append(";").toString(), value);
    }
    return str;
  }

  public static String escapeXml(String str) {
    if (str == null) {
      return null;
    }
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < str.length(); i++) {
      char c = str.charAt(i);
      String escapedStr = getEscapedStringByChar(c);
      if (escapedStr == null) {
        sb.append(c);
      } else {
        sb.append(escapedStr);
      }
    }
    return sb.toString();
  }

  public static String escapeXml(String str, String escapeChars) {
    if (str == null) {
      return null;
    }
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < str.length(); i++) {
      char c = str.charAt(i);
      if (escapeChars.indexOf(c) < 0) {
        sb.append(c);
      }
      else {
        String escapedStr = getEscapedStringByChar(c);
        if (escapedStr == null) {
          sb.append(c);
        } else {
          sb.append(escapedStr);
        }
      }
    }
    return sb.toString();
  }

  private static String getEscapedStringByChar(char c) {
    String escapedStr = null;
    for (String key : XML.keySet()) {
      String value = (String)XML.get(key);
      if (c == value.charAt(0)) {
        escapedStr = new StringBuilder().append("&").append(key).append(";").toString();
      }
    }
    return escapedStr;
  }

  public static String removePrefix(String str, String prefix) {
    return removePrefix(str, prefix, false);
  }

  public static String removePrefix(String str, String prefix, boolean ignoreCase) {
    if (str == null) {
      return null;
    }
    if (prefix == null) {
      return str;
    }
    if (ignoreCase) {
      if (str.toLowerCase().startsWith(prefix.toLowerCase())) {
        return str.substring(prefix.length());
      }
    }
    else if (str.startsWith(prefix)) {
      return str.substring(prefix.length());
    }

    return str;
  }

  public static boolean isBlank(String str) {
    return (str == null) || (str.trim().length() == 0);
  }

  public static boolean isNotBlank(String str) {
    return !isBlank(str);
  }

  public static boolean isEmpty(String str) {
    return (str == null) || (str.length() == 0);
  }

  public static boolean isNotEmpty(String str) {
    return !isEmpty(str);
  }

  public static String getExtension(String str) {
    if (str == null) {
      return null;
    }
    int i = str.lastIndexOf(46);
    if (i >= 0) {
      return str.substring(i + 1);
    }
    return null;
  }

  public static String insertBefore(String content, String compareToken, String insertString) {
    if (content.indexOf(insertString) >= 0) {
      return content;
    }
    int index = content.indexOf(compareToken);
    if (index >= 0) {
      return new StringBuffer(content).insert(index, insertString).toString();
    }
    throw new IllegalArgumentException(new StringBuilder().append("not found insert location by compareToken:").append(compareToken).append(" content:").append(content).toString());
  }

  public static String insertAfter(String content, String compareToken, String insertString)
  {
    if (content.indexOf(insertString) >= 0) {
      return content;
    }
    int index = content.indexOf(compareToken);
    if (index >= 0) {
      return new StringBuffer(content).insert(index + compareToken.length(), insertString).toString();
    }
    throw new IllegalArgumentException(new StringBuilder().append("not found insert location by compareToken:").append(compareToken).append(" content:").append(content).toString());
  }

  public static int countOccurrencesOf(String str, String sub)
  {
    if ((str == null) || (sub == null) || (str.length() == 0) || (sub.length() == 0)) {
      return 0;
    }
    int count = 0;
    int pos = 0;
    int idx;
    while ((idx = str.indexOf(sub, pos)) != -1) {
      count++;
      pos = idx + sub.length();
    }
    return count;
  }

  public static boolean contains(String str, String[] keywords) {
    if (str == null) {
      return false;
    }
    if (keywords == null) {
      throw new IllegalArgumentException("'keywords' must be not null");
    }

    for (String keyword : keywords) {
      if (str.contains(keyword.toLowerCase())) {
        return true;
      }
    }
    return false;
  }

  public static String defaultString(Object value) {
    if (value == null) {
      return "";
    }
    return value.toString();
  }

  public static String defaultIfEmpty(Object value, String defaultValue) {
    if ((value == null) || ("".equals(value))) {
      return defaultValue;
    }
    return value.toString();
  }

  public static String makeAllWordFirstLetterUpperCase(String sqlName) {
    String[] strs = sqlName.toLowerCase().split("_");
    String result = "";
    String preStr = "";
    for (int i = 0; i < strs.length; i++) {
      if (preStr.length() == 1) {
        result = new StringBuilder().append(result).append(strs[i]).toString();
      } else {
        result = new StringBuilder().append(result).append(capitalize(strs[i])).toString();
      }
      preStr = strs[i];
    }
    return result;
  }

  public static int indexOfByRegex(String input, String regex) {
    Pattern p = Pattern.compile(regex);
    Matcher m = p.matcher(input);
    if (m.find()) {
      return m.start();
    }
    return -1;
  }

  public static String toJavaVariableName(String str) {
    return uncapitalize(toJavaClassName(str));
  }

  public static String toJavaClassName(String sqlName)
  {
    return toJavaClassName(sqlName, GeneratorProperties.getBoolean(GeneratorConstants.TABLE_NAME_SINGULARIZE));
  }

  public static String toJavaClassName(String sqlName, boolean singularize)
  {
    String processedSqlName = removeTableSqlNamePrefix(sqlName);
    if (singularize) {
      processedSqlName = singularize(processedSqlName);
    }
    return makeAllWordFirstLetterUpperCase(toUnderscoreName(processedSqlName));
  }

  public static String removeTableSqlNamePrefix(String sqlName) {
    String[] prefixs = GeneratorProperties.getStringArray(GeneratorConstants.TABLE_REMOVE_PREFIXES);
    for (String prefix : prefixs) {
      String removedPrefixSqlName = removePrefix(sqlName, prefix, true);
      if (!removedPrefixSqlName.equals(sqlName)) {
        return removedPrefixSqlName;
      }
    }
    return sqlName;
  }

  public static String getJavaClassSimpleName(String clazz) {
    if (clazz == null) {
      return null;
    }
    if (clazz.lastIndexOf(".") >= 0) {
      return clazz.substring(clazz.lastIndexOf(".") + 1);
    }
    return clazz;
  }

  public static String removeMany(String inString, String[] keywords) {
    if (inString == null) {
      return null;
    }
    for (String k : keywords) {
      inString = replace(inString, k, "");
    }
    return inString;
  }

  public static void appendReplacement(Matcher m, StringBuffer sb, String replacement) {
    replacement = replace(replacement, "$", "\\$");
    m.appendReplacement(sb, replacement);
  }

  public static String replace(String inString, String oldPattern, String newPattern) {
    if (inString == null) {
      return null;
    }
    if ((oldPattern == null) || (newPattern == null)) {
      return inString;
    }

    StringBuffer sbuf = new StringBuffer();

    int pos = 0;
    int index = inString.indexOf(oldPattern);

    int patLen = oldPattern.length();
    while (index >= 0) {
      sbuf.append(inString.substring(pos, index));
      sbuf.append(newPattern);
      pos = index + patLen;
      index = inString.indexOf(oldPattern, pos);
    }
    sbuf.append(inString.substring(pos));

    return sbuf.toString();
  }

  public static String capitalize(String str) {
    return changeFirstCharacterCase(str, true);
  }

  public static String uncapitalize(String str)
  {
    return changeFirstCharacterCase(str, false);
  }

  private static String changeFirstCharacterCase(String str, boolean capitalize) {
    if ((str == null) || (str.length() == 0)) {
      return str;
    }
    StringBuffer buf = new StringBuffer(str.length());
    if (capitalize) {
      buf.append(Character.toUpperCase(str.charAt(0)));
    }
    else {
      buf.append(Character.toLowerCase(str.charAt(0)));
    }
    buf.append(str.substring(1));
    return buf.toString();
  }

  public static String randomNumeric(int count)
  {
    return random(count, false, true);
  }

  public static String random(int count, boolean letters, boolean numbers) {
    return random(count, 0, 0, letters, numbers);
  }

  public static String random(int count, int start, int end, boolean letters, boolean numbers) {
    return random(count, start, end, letters, numbers, null, RANDOM);
  }

  public static String random(int count, int start, int end, boolean letters, boolean numbers, char[] chars, Random random)
  {
    if (count == 0) {
      return "";
    }
    if (count < 0) {
      throw new IllegalArgumentException(new StringBuilder().append("Requested random string length ").append(count).append(" is less than 0.").toString());
    }

    if ((start == 0) && (end == 0)) {
      end = 123;
      start = 32;
      if ((!letters) && (!numbers)) {
        start = 0;
        end = 2147483647;
      }
    }

    char[] buffer = new char[count];
    int gap = end - start;

    while (count-- != 0)
    {
      char ch;
      if (chars == null) {
        ch = (char)(random.nextInt(gap) + start);
      } else {
        ch = chars[(random.nextInt(gap) + start)];
      }
      if (((letters) && (Character.isLetter(ch))) || ((numbers) && 
        (Character.isDigit(ch))) || (
        (!letters) && (!numbers)))
      {
        if ((ch >= 56320) && (ch <= 57343)) {
          if (count == 0) {
            count++;
          }
          else
          {
            buffer[count] = ch;
            count--;
            buffer[count] = ((char)(55296 + random.nextInt(128)));
          }
        } else if ((ch >= 55296) && (ch <= 56191)) {
          if (count == 0) {
            count++;
          }
          else
          {
            buffer[count] = ((char)(56320 + random.nextInt(128)));
            count--;
            buffer[count] = ch;
          }
        } else if ((ch >= 56192) && (ch <= 56319))
        {
          count++;
        }
        else {
          buffer[count] = ch;
        }
      }
      else {
        count++;
      }
    }
    return new String(buffer);
  }

  public static String toUnderscoreName(String name)
  {
    if (name == null) {
      return null;
    }

    String filteredName = name;
    if ((filteredName.indexOf("_") >= 0) && (filteredName.equals(filteredName.toUpperCase()))) {
      filteredName = filteredName.toLowerCase();
    }
    if ((filteredName.indexOf("_") == -1) && (filteredName.equals(filteredName.toUpperCase()))) {
      filteredName = filteredName.toLowerCase();
    }

    StringBuffer result = new StringBuffer();
    if ((filteredName != null) && (filteredName.length() > 0)) {
      result.append(filteredName.substring(0, 1).toLowerCase());
      for (int i = 1; i < filteredName.length(); i++) {
        String preChart = filteredName.substring(i - 1, i);
        String c = filteredName.substring(i, i + 1);
        if (c.equals("_")) {
          result.append("_");
        }
        else if (preChart.equals("_")) {
          result.append(c.toLowerCase());
        }
        else if (c.matches("\\d")) {
          result.append(c);
        } else if (c.equals(c.toUpperCase())) {
          result.append("_");
          result.append(c.toLowerCase());
        }
        else {
          result.append(c);
        }
      }
    }
    return result.toString();
  }

  public static String removeEndWiths(String inputString, String[] endWiths) {
    for (String endWith : endWiths) {
      if (inputString.endsWith(endWith)) {
        return inputString.substring(0, inputString.length() - endWith.length());
      }
    }
    return inputString;
  }

  public static List<Column.EnumMetaDada> string2EnumMetaData(String data)
  {
    if ((data == null) || (data.trim().length() == 0)) {
      return new ArrayList();
    }

    List list = new ArrayList();
    Pattern p = Pattern.compile("\\w+\\(.*?\\)");
    Matcher m = p.matcher(data);
    while (m.find()) {
      String str = m.group();
      Matcher three_m = three.matcher(str);
      if (three_m.find()) {
        list.add(new Column.EnumMetaDada(three_m.group(1), three_m.group(2), three_m.group(3)));
      }
      else {
        Matcher two_m = two.matcher(str);
        if (two_m.find()) {
          list.add(new Column.EnumMetaDada(two_m.group(1), two_m.group(1), two_m.group(2)));
        }
        else {
          throw new IllegalArgumentException(new StringBuilder().append("error enumString format:").append(data).append(" expected format:F(1,Female);M(0,Male) or F(Female);M(Male)").toString());
        }
      }
    }
    return list;
  }

  public static boolean substringMatch(CharSequence str, int index, CharSequence substring)
  {
    for (int j = 0; j < substring.length(); j++) {
      int i = index + j;
      if ((i >= str.length()) || (str.charAt(i) != substring.charAt(j))) {
        return false;
      }
    }
    return true;
  }

  public static String[] tokenizeToStringArray(String str, String seperators) {
    if (str == null) {
      return new String[0];
    }
    StringTokenizer tokenlizer = new StringTokenizer(str, seperators);
    List result = new ArrayList();

    while (tokenlizer.hasMoreElements()) {
      Object s = tokenlizer.nextElement();
      result.add(s);
    }
    return (String[])result.toArray(new String[result.size()]);
  }
  public static String join(List list, String seperator) {
    return join(list.toArray(new Object[0]), seperator);
  }

  public static String replace(int start, int end, String str, String replacement) {
    String before = str.substring(0, start);
    String after = str.substring(end);
    return new StringBuilder().append(before).append(replacement).append(after).toString();
  }

  public static String join(Object[] array, String seperator) {
    if (array == null) {
      return null;
    }
    StringBuffer result = new StringBuffer();
    for (int i = 0; i < array.length; i++) {
      result.append(array[i]);
      if (i != array.length - 1) {
        result.append(seperator);
      }
    }
    return result.toString();
  }
  public static int containsCount(String string, String keyword) {
    if (string == null) {
      return 0;
    }
    int count = 0;
    for (int i = 0; i < string.length(); i++) {
      int indexOf = string.indexOf(keyword, i);
      if (indexOf < 0) {
        break;
      }
      count++;
      i = indexOf;
    }
    return count;
  }

  public static String getByRegex(String str, String regex) {
    return getByRegex(str, regex, 0);
  }

  public static String getByRegex(String str, String regex, int group) {
    if (regex == null) {
      throw new NullPointerException();
    }
    if (group < 0) {
      throw new IllegalArgumentException();
    }
    if (str == null) {
      return null;
    }
    Pattern p = Pattern.compile(regex);
    Matcher m = p.matcher(str);
    if (m.find()) {
      return m.group(group);
    }
    return null;
  }

  public static String removeIbatisOrderBy(String sql)
  {
    String orderByRemovedSql = sql.replaceAll("(?si)<\\w+[^>]*?>\\s*order\\s+by\\s+[^<]+?</\\w+>", "")
      .replaceAll("(?i)<\\w+[\\w\\s='\"]+prepend[\\w\\s='\"]*?order\\s+by\\s*['\"][^>]*?>[^<]+</\\w+>", "")
      .replaceAll("(?i)\\s*order\\s+by\\s+.*", "");

    return removeXmlTagIfBodyEmpty(removeXmlTagIfBodyEmpty(removeXmlTagIfBodyEmpty(removeXmlTagIfBodyEmpty(orderByRemovedSql))));
  }

  public static String removeXMLCdataTag(String str) {
    if (str == null) {
      return null;
    }
    str = replace(str, "<![CDATA[", "");
    str = replace(str, "]]>", "");
    return str;
  }

  public static String insertTokenIntoSelectSql(String str, String insertValue)
  {
    String token = "select\\s";
    int selectBeginPos = indexOfByRegex(str, new StringBuilder().append("(?si)").append(token).toString());
    if (selectBeginPos == -1) {
      return str;
    }
    return new StringBuffer(str).insert(selectBeginPos + "select ".length(), insertValue).toString();
  }

  public static String removeXmlTagIfBodyEmpty(String sql) {
    return sql.replaceAll("<\\w+[^>]*?>\\s+</\\w+>", "");
  }

  public static String columnNameToClassName(String dbName) {
    throw new UnsupportedOperationException();
  }

  public static String tableNameToClassName(String dbName)
  {
    return makeAllWordFirstLetterUpperCase(toUnderscoreName(dbName));
  }

  public static String pluralize(String word)
  {
    return Inflector.getInstance().pluralize(word);
  }

  public static String singularize(String word)
  {
    return Inflector.getInstance().singularize(word);
  }

  public static String cleanPath(String path)
  {
    if (path == null) {
      return null;
    }
    String pathToUse = replace(path, "\\", "/");

    int prefixIndex = pathToUse.indexOf(":");
    String prefix = "";
    if (prefixIndex != -1) {
      prefix = pathToUse.substring(0, prefixIndex + 1);
      pathToUse = pathToUse.substring(prefixIndex + 1);
    }
    if (pathToUse.startsWith("/")) {
      prefix = new StringBuilder().append(prefix).append("/").toString();
      pathToUse = pathToUse.substring(1);
    }

    String[] pathArray = delimitedListToStringArray(pathToUse, "/");
    List pathElements = new LinkedList();
    int tops = 0;

    for (int i = pathArray.length - 1; i >= 0; i--) {
      String element = pathArray[i];
      if (!".".equals(element))
      {
        if ("..".equals(element))
        {
          tops++;
        }
        else if (tops > 0)
        {
          tops--;
        }
        else
        {
          pathElements.add(0, element);
        }
      }

    }

    for (int i = 0; i < tops; i++) {
      pathElements.add(0, "..");
    }

    return new StringBuilder().append(prefix).append(collectionToDelimitedString(pathElements, "/")).toString();
  }

  public static String collectionToDelimitedString(Collection coll, String delim)
  {
    return collectionToDelimitedString(coll, delim, "", "");
  }

  public static String[] commaDelimitedListToStringArray(String str)
  {
    return delimitedListToStringArray(str, ",");
  }

  public static String[] delimitedListToStringArray(String str, String delimiter)
  {
    return delimitedListToStringArray(str, delimiter, null);
  }

  public static String collectionToDelimitedString(Collection coll, String delim, String prefix, String suffix)
  {
    if ((coll == null) || (coll.isEmpty())) {
      return "";
    }
    StringBuilder sb = new StringBuilder();
    Iterator it = coll.iterator();
    while (it.hasNext()) {
      sb.append(prefix).append(it.next()).append(suffix);
      if (it.hasNext()) {
        sb.append(delim);
      }
    }
    return sb.toString();
  }

  public static String[] delimitedListToStringArray(String str, String delimiter, String charsToDelete)
  {
    if (str == null) {
      return new String[0];
    }
    if (delimiter == null) {
      return new String[] { str };
    }
    List result = new ArrayList();
    if ("".equals(delimiter)) {
      for (int i = 0; i < str.length(); i++) {
        result.add(deleteAny(str.substring(i, i + 1), charsToDelete));
      }
    }
    else
    {
      int pos = 0;
      int delPos;
      while ((delPos = str.indexOf(delimiter, pos)) != -1) {
        result.add(deleteAny(str.substring(pos, delPos), charsToDelete));
        pos = delPos + delimiter.length();
      }
      if ((str.length() > 0) && (pos <= str.length()))
      {
        result.add(deleteAny(str.substring(pos), charsToDelete));
      }
    }
    return toStringArray(result);
  }

  public static String[] toStringArray(Collection<String> collection)
  {
    if (collection == null) {
      return null;
    }
    return (String[])collection.toArray(new String[collection.size()]);
  }

  public static String deleteAny(String inString, String charsToDelete)
  {
    if ((!hasLength(inString)) || (!hasLength(charsToDelete))) {
      return inString;
    }
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < inString.length(); i++) {
      char c = inString.charAt(i);
      if (charsToDelete.indexOf(c) == -1) {
        sb.append(c);
      }
    }
    return sb.toString();
  }

  public static boolean hasLength(CharSequence str)
  {
    return (str != null) && (str.length() > 0);
  }

  public static boolean hasLength(String str)
  {
    return hasLength(str);
  }

  static
  {
    XML.put("apos", "'");
    XML.put("quot", "\"");
    XML.put("amp", "&");
    XML.put("lt", "<");
    XML.put("gt", ">");
  }
}

