package com.river.learn.java.base;

/**
 * String & StringBuffer & StringBuilder 使用场景和区别 & 常用方法
 *
 * String 字符串常量（不可变对象，没次修改都返回一个新的对象）
 * StringBuffer 字符串变量（线程安全）
 * StringBuilder 字符串变量（非线程安全）
 *
 *
 * String 类型和 StringBuffer 类型的主要性能区别其实在于 String 是不可变的对象, 因此在每次对 String 类型进行改变的时候其实都等同于生成了一个新的 String 对象，然后将指针指向新的 String 对象，
 * 所以经常改变内容的字符串最好不要用 String ，因为每次生成对象都会对系统性能产生影响，特别当内存中无引用对象多了以后， JVM 的 GC 就会开始工作，那速度是一定会相当慢的。
 *
 * StringBuffer 类则结果就不一样了，每次结果都会对 StringBuffer 对象本身进行操作，而不是生成新的对象，再改变对象引用。所以在一般情况下我们推荐使用 StringBuffer ，特别是字符串对象经常改变的情况下。
 * 而在某些特别情况下， String 对象的字符串拼接其实是被 JVM 解释成了 StringBuffer 对象的拼接，所以这些时候 String 对象的速度并不会比 StringBuffer 对象慢，
 * 而特别是以下的字符串对象生成中， String 效率是远要比 StringBuffer 快的：
 *  String S1 = “This is only a” + “ simple” + “ test”;
 *  StringBuffer Sb = new StringBuilder(“This is only a”).append(“ simple”).append(“ test”);
 * 你会很惊讶的发现，生成 String S1 对象的速度简直太快了，而这个时候 StringBuffer 居然速度上根本一点都不占优势。其实这是 JVM 的一个把戏，在 JVM 眼里，这个
 * String S1 = “This is only a” + “ simple” + “test”; 其实就是：
 * String S1 = “This is only a simple test”; 所以当然不需要太多的时间了。但大家这里要注意的是，如果你的字符串是来自另外的 String 对象的话，速度就没那么快了，譬如：
 * String S2 = “This is only a”;
 * String S3 = “ simple”;
 * String S4 = “ test”;
 * String S1 = S2 +S3 + S4;
 *
 * StringBuilde 是线程不安全类，所有性能比StringBuffer 好，一般单线程场合 StringBuilder 更适合
 *
 * @author 17822
 */
public class StringAndStringBufferAndStringBuilder {

    public static void main(String[] args) {
        reverse();
    }

    /**
     * String 常用的一些方法
     */
    public void StringBaseMethod(){

        String str = "test";

        //获取字符串str长度
        int i = str.length();
        //根据位置（index)获取字符
        char  c = str.charAt(0);
        //获取字符在字符串中的位置
        //int i =str.indexOf(char ch);  获取的是第一次出现的位置
        //int i =str.indexOf(char ch ,int index);  //从位置index后获取ch出现的第一次的位置
        //int  i =str.indexOf(str1) ;// 获取str1 在str 第一次出现的位置
        //int i=str.indexOf(str1, index0);//获取从index位置后str第一次出现的位置
        // int i = str.lastIndexOf(ch或者 str1)  //获取ch或者str1最后出现的位置

        //判断是否以指定字符串str1开头/结尾
        boolean b = str.startsWith("");
        boolean b2 = str.endsWith("");
        //判断是否包含某一子串
        boolean b3 = str.contains("");
        //判断字符串是否有内容
        boolean isEmpty = str.isEmpty();
        //忽略大小写判断字符串是否相同
        boolean b4 = str.equalsIgnoreCase("ABC");

        //将字符数组 -char[] ch- 转化成字符串
        /*String str =new String(ch); //将整个数组变成字符串
        String str =new String(ch,offset,count);*/
        //将字符数组中的offset位置之后的count个元素转换成字符串
        /*String str =String.valueOf(ch);
        String str =String.copyValueOf(ch,offset,count);
        String str =String.copyValueOf(ch);*/

        //将字符串转化为字符数组
        char[] ch = str.toCharArray();
        //将字节数组转换为字符串
        //同上1) 传入类型变为Byte[];
        //将字符串转换为字节数组
        byte[] bs = str.getBytes();
        //将基本数据类型装换成字符串
        String str2 = String.valueOf(1431);


        //替换   replace();
        //将str里oldchar变为newchar
        str.replace("oldchar","newchar");

        //切割   split();
        //将str用 ","分割成String数组
        String[]  str1 = str.split(",");

        // s 为 str 从begin位置到最后的字符串
        String s = str.substring(2);
        //s 是 str 从begin 位置到end 位置的字符串
        String s2 = str.substring(0,4);


        //转换大小写：
        //将str变成大写字母
        String toUpperCase = str. toUpperCase();
        //将str变成小写字母
        String toLowerCase = str. toLowerCase();

        //除去空格：
        String trim =str.trim();

        //比较：
        int aa = str.compareTo("aa");
    }

    /**
     * 字符串反转
     */
    public static void  reverse(){

        String str = "kjien";
        char[] chars = str.toCharArray();

        int head = 0;
        int fooder = chars.length -1;
        char temp;
        for(int i = head;i<=fooder;i++){
            temp  = chars[i];
            chars[head] = chars[fooder];
            chars[fooder] = temp;
            head++;
            fooder--;
        }
        System.out.println(String.valueOf(chars));

    }
    /**
     * StringBuffer 常用方法
     */
    public void StringBuffer(){
        //append(); //将指定数据加在容器末尾,返回值也是StringBuffer
        StringBuffer sb = new StringBuffer("aaaa");
        sb.reverse();
        //数据可以任何基本数据类型
        //注：此时sb == sb1他们是同一对象，意思是可以不用新建sb1直接 sb.append(数据) 使用时之后接使用sb
        StringBuffer sb1=sb.append("数据");

        //insert();// 插入
        sb.insert(0 ,"数据2");
        //删除
        //删除start到end的字符内容
        sb.delete(0 ,10);
        ////删除指定位置的字符
        //注意：这里的所有包含index的操作都是含头不含尾的
        sb.deleteCharAt(2);
        //清空StringBuffer缓冲区
        sb=new StringBuffer();
        sb.delete(0,sb.length());

        //获取index上的字符
        char c = sb.charAt(3);
        //获取char字符出现的第一次位置
        int i = sb.indexOf("数据");

        //4.修改
        //将从start开始到end的字符串替换为string；
        sb =sb.replace(0,2,"string");
        //将index位置的字符变为新的char
        sb.setCharAt(2 ,c);

        //反转 //将sb倒序
        sb.reverse();

        //将StringBuffer缓冲区中的指定数据存储到指定数组中
        char[] ch = new char[10];
        sb.getChars(0,10,ch,2);

    }

}
