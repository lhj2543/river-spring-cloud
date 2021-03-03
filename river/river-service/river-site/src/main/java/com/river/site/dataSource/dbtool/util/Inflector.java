package com.river.site.dataSource.dbtool.util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Inflector
{
  private static transient Inflector instance = null;

  private List plurals = new LinkedList();

  private List singulars = new ArrayList();

  private List uncountables = new LinkedList();

  private Inflector()
  {
    addPlural("$", "s", false);
    addPlural("(.*)$", "\\1s");
    addPlural("(.*)(ax|test)is$", "\\1\\2es");
    addPlural("(.*)(octop|vir)us$", "\\1\\2i");
    addPlural("(.*)(alias|status)$", "\\1\\2es");
    addPlural("(.*)(bu)s$", "\\1\\2ses");
    addPlural("(.*)(buffal|tomat)o$", "\\1\\2oes");
    addPlural("(.*)([ti])um$", "\\1\\2a");
    addPlural("(.*)sis$", "\\1ses");
    addPlural("(.*)(?:([^f])fe|([lr])f)$", "\\1\\3ves");
    addPlural("(.*)(hive)$", "\\1\\2s");
    addPlural("(.*)(tive)$", "\\1\\2s");
    addPlural("(.*)([^aeiouy]|qu)y$", "\\1\\2ies");
    addPlural("(.*)(series)$", "\\1\\2");
    addPlural("(.*)(movie)$", "\\1\\2s");
    addPlural("(.*)(x|ch|ss|sh)$", "\\1\\2es");
    addPlural("(.*)(matr|vert|ind)ix|ex$", "\\1\\2ices");
    addPlural("(.*)(o)$", "\\1\\2es");
    addPlural("(.*)(shoe)$", "\\1\\2s");
    addPlural("(.*)([m|l])ouse$", "\\1\\2ice");
    addPlural("^(ox)$", "\\1en");
    addPlural("(.*)(vert|ind)ex$", "\\1\\2ices");
    addPlural("(.*)(matr)ix$", "\\1\\2ices");
    addPlural("(.*)(quiz)$", "\\1\\2zes");

    addSingular("(.*)s$", "\\1");
    addSingular("(.*)(n)ews$", "\\1\\2ews");
    addSingular("(.*)([ti])a$", "\\1\\2um");
    addSingular("(.*)((a)naly|(b)a|(d)iagno|(p)arenthe|(p)rogno|(s)ynop|(t)he)ses$", "\\1\\2sis");
    addSingular("(.*)(^analy)ses$", "\\1\\2sis");
    addSingular("(.*)([^f])ves$", "\\1\\2fe");
    addSingular("(.*)(hive)s$", "\\1\\2");
    addSingular("(.*)(tive)s$", "\\1\\2");
    addSingular("(.*)([lr])ves$", "\\1\\2f");
    addSingular("(.*)([^aeiouy]|qu)ies$", "\\1\\2y");
    addSingular("(.*)(s)eries$", "\\1\\2eries");
    addSingular("(.*)(m)ovies$", "\\1\\2ovie");
    addSingular("(.*)(x|ch|ss|sh)es$", "\\1\\2");
    addSingular("(.*)([m|l])ice$", "\\1\\2ouse");
    addSingular("(.*)(bus)es$", "\\1\\2");
    addSingular("(.*)(o)es$", "\\1\\2");
    addSingular("(.*)(shoe)s$", "\\1\\2");
    addSingular("(.*)(cris|ax|test)es$", "\\1\\2is");
    addSingular("(.*)(octop|vir)i$", "\\1\\2us");
    addSingular("(.*)(alias|status)es$", "\\1\\2");
    addSingular("^(ox)en", "\\1");
    addSingular("(.*)(vert|ind)ices$", "\\1\\2ex");
    addSingular("(.*)(matr)ices$", "\\1\\2ix");
    addSingular("(.*)(quiz)zes$", "\\1\\2");

    addIrregular("child", "children");
    addIrregular("man", "men");
    addIrregular("move", "moves");
    addIrregular("person", "people");
    addIrregular("sex", "sexes");

    addUncountable("equipment");
    addUncountable("fish");
    addUncountable("information");
    addUncountable("money");
    addUncountable("rice");
    addUncountable("series");
    addUncountable("sheep");
    addUncountable("species");
  }

  public static Inflector getInstance()
  {
    if (instance == null) {
      instance = new Inflector();
    }
    return instance;
  }

  public String camelize(String word)
  {
    return camelize(word, false);
  }

  public String camelize(String word, boolean flag)
  {
    if (word.length() == 0) {
      return word;
    }

    StringBuffer sb = new StringBuffer(word.length());
    if (flag) {
      sb.append(Character.toLowerCase(word.charAt(0)));
    } else {
      sb.append(Character.toUpperCase(word.charAt(0)));
    }
    boolean capitalize = false;
    for (int i = 1; i < word.length(); i++) {
      char ch = word.charAt(i);
      if (capitalize) {
        sb.append(Character.toUpperCase(ch));
        capitalize = false;
      } else if (ch == '_') {
        capitalize = true;
      } else if (ch == '/') {
        capitalize = true;
        sb.append('.');
      } else {
        sb.append(ch);
      }
    }
    return sb.toString();
  }

  public String classify(String tableName)
  {
    int period = tableName.lastIndexOf('.');
    if (period >= 0) {
      tableName = tableName.substring(period + 1);
    }
    return camelize(singularize(tableName));
  }

  public String dasherize(String word)
  {
    return word.replace('_', '-');
  }

  public String demodulize(String className)
  {
    int period = className.lastIndexOf('.');
    if (period >= 0) {
      return className.substring(period + 1);
    }
    return className;
  }

  public String foreignKey(String className)
  {
    return foreignKey(className, true);
  }

  public String foreignKey(String className, boolean underscore)
  {
    return underscore(new StringBuilder().append(demodulize(className)).append(underscore ? "_id" : "id").toString());
  }

  public String humanize(String words)
  {
    if (words.endsWith("_id")) {
      words = words.substring(0, words.length() - 3);
    }
    StringBuffer sb = new StringBuffer(words.length());
    sb.append(Character.toUpperCase(words.charAt(0)));
    for (int i = 1; i < words.length(); i++) {
      char ch = words.charAt(i);
      if (ch == '_') {
        sb.append(' ');
      } else {
        sb.append(ch);
      }
    }
    return sb.toString();
  }

  public String ordinalize(int number)
  {
    int modulo = number % 100;
    if ((modulo >= 11) && (modulo <= 13)) {
      return new StringBuilder().append("").append(number).append("th").toString();
    }
    switch (number % 10) { case 1:
      return new StringBuilder().append("").append(number).append("st").toString();
    case 2:
      return new StringBuilder().append("").append(number).append("nd").toString();
    case 3:
      return new StringBuilder().append("").append(number).append("rd").toString(); }
    return new StringBuilder().append("").append(number).append("th").toString();
  }

  public String pluralize(String word)
  {
    for (int i = 0; i < this.uncountables.size(); i++) {
      if (this.uncountables.get(i).equals(word)) {
        return word;
      }

    }

    for (int i = 0; i < this.plurals.size(); i++) {
      Replacer replacer = (Replacer)this.plurals.get(i);
      if (replacer.matches(word)) {
        return replacer.replacement();
      }

    }

    return word;
  }

  public String singularize(String word)
  {
    for (int i = 0; i < this.uncountables.size(); i++) {
      if (this.uncountables.get(i).equals(word)) {
        return word;
      }

    }

    for (int i = 0; i < this.singulars.size(); i++) {
      Replacer replacer = (Replacer)this.singulars.get(i);
      if (replacer.matches(word)) {
        return replacer.replacement();
      }

    }

    return word;
  }

  public String tableize(String className)
  {
    return pluralize(underscore(className));
  }

  public String titleize(String words)
  {
    StringBuffer sb = new StringBuffer(words.length());
    boolean capitalize = true;
    for (int i = 0; i < words.length(); i++) {
      char ch = words.charAt(i);
      if (Character.isWhitespace(ch)) {
        sb.append(' ');
        capitalize = true;
      } else if (ch == '-') {
        sb.append(' ');
        capitalize = true;
      } else if (capitalize) {
        sb.append(Character.toUpperCase(ch));
        capitalize = false;
      } else {
        sb.append(ch);
      }
    }
    return sb.toString();
  }

  public String underscore(String word)
  {
    StringBuffer sb = new StringBuffer(word.length() + 5);
    boolean uncapitalize = false;
    for (int i = 0; i < word.length(); i++) {
      char ch = word.charAt(i);
      if (uncapitalize) {
        sb.append(Character.toLowerCase(ch));
        uncapitalize = false;
      } else if (ch == '.') {
        sb.append('/');
        uncapitalize = true;
      } else if (Character.isUpperCase(ch)) {
        if (i > 0) {
          sb.append('_');
        }
        sb.append(Character.toLowerCase(ch));
      } else {
        sb.append(ch);
      }
    }
    return sb.toString();
  }

  public void addIrregular(String singular, String plural)
  {
    addPlural(new StringBuilder().append("(.*)(").append(singular.substring(0, 1)).append(")").append(singular.substring(1)).append("$").toString(), new StringBuilder().append("\\1\\2")
      .append(plural
      .substring(1))
      .toString());
    addSingular(new StringBuilder().append("(.*)(").append(plural.substring(0, 1)).append(")").append(plural.substring(1)).append("$").toString(), new StringBuilder().append("\\1\\2")
      .append(singular
      .substring(1))
      .toString());
  }

  public void addPlural(String match, String rule)
  {
    addPlural(match, rule, true);
  }

  public void addPlural(String match, String rule, boolean insensitive)
  {
    this.plurals.add(0, new Replacer(match, rule, insensitive));
  }

  public void addSingular(String match, String rule)
  {
    addSingular(match, rule, true);
  }

  public void addSingular(String match, String rule, boolean insensitive)
  {
    this.singulars.add(0, new Replacer(match, rule, insensitive));
  }

  public void addUncountable(String word)
  {
    this.uncountables.add(0, word.toLowerCase());
  }

  private class Replacer
  {
    private String input = null;
    private Matcher matcher = null;
    private Pattern pattern = null;
    private String rule = null;

    public Replacer(String match, String rule, boolean insensitive)
    {
      this.pattern = Pattern.compile(match, insensitive ? 2 : 0);

      this.rule = rule;
    }

    public boolean matches(String input)
    {
      this.matcher = this.pattern.matcher(input);
      if (this.matcher.matches()) {
        this.input = input;
        return true;
      }
      this.input = null;
      this.matcher = null;
      return false;
    }

    public String replacement()
    {
      StringBuffer sb = new StringBuffer();
      boolean group = false;
      for (int i = 0; i < this.rule.length(); i++) {
        char ch = this.rule.charAt(i);
        if (group) {
          sb.append(this.matcher.group(Character.digit(ch, 10)));
          group = false;
        } else if (ch == '\\') {
          group = true;
        } else {
          sb.append(ch);
        }
      }
      return sb.toString();
    }
  }
}

