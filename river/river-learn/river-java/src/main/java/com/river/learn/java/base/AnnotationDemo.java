package com.river.learn.java.base;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * https://www.runoob.com/w3cnote/java-annotation.html
 * https://www.jianshu.com/p/9471d6bcf4cf
 * 注解
 * Java 定义了一套注解，共有 7 个，3 个在 java.lang 中，剩下 4 个在 java.lang.annotation 中。
 * @Override - 检查该方法是否是重写方法。如果发现其父类，或者是引用的接口中并没有该方法时，会报编译错误。
 * @Deprecated - 标记过时方法。如果使用该方法，会报编译警告。
 * @SuppressWarnings - 指示编译器去忽略注解中声明的警告。
 * @Retention - 标识这个注解怎么保存，是只在代码中，还是编入class文件中，或者是在运行时可以通过反射访问。
 * @Documented - 标记这些注解是否包含在用户文档中。
 * @Target - 标记这个注解应该是哪种 Java 成员。
 * @Inherited - 标记这个注解是继承于哪个注解类(默认 注解并没有继承于任何子
 *
 * @interface
 * 使用 @interface 定义注解时，意味着它实现了 java.lang.annotation.Annotation 接口，即该注解就是一个Annotation。
 * 定义 Annotation 时，@interface 是必须的。
 *
 * 注意：它和我们通常的 implemented 实现接口的方法不同。Annotation 接口的实现细节都由编译器完成。通过 @interface 定义注解后，该注解不能继承其他的注解或接口。
 *
 * java 常用的 Annotation
 * @Deprecated  -- @Deprecated 所标注内容，不再被建议使用。
 * @Override    -- @Override 只能标注方法，表示该方法覆盖父类中的方法。
 * @Documented  -- @Documented 所标注内容，可以出现在javadoc中。
 * @Inherited   -- @Inherited只能被用来标注“Annotation类型”，它所标注的Annotation具有继承性。
 * @Retention   -- @Retention只能被用来标注“Annotation类型”，而且它被用来指定Annotation的RetentionPolicy属性。
 * @Target      -- @Target只能被用来标注“Annotation类型”，而且它被用来指定Annotation的ElementType属性。
 * @SuppressWarnings -- @SuppressWarnings 所标注内容产生的警告，编译器会对这些警告保持静默。
 *
 *  @interface -- 它的用来修饰 SuppressWarnings，意味着 SuppressWarnings 实现了 java.lang.annotation.Annotation 接口；即 SuppressWarnings 就是一个注解。
 *
 * (02) @Retention(RetentionPolicy.SOURCE) -- 它的作用是指定 SuppressWarnings 的策略是 RetentionPolicy.SOURCE。这就意味着，SuppressWarnings 信息仅存在于编译器处理期间，编译器处理完之后 SuppressWarnings 就没有作用了。
 *
 * (03) @Target({TYPE, FIELD, METHOD, PARAMETER, CONSTRUCTOR, LOCAL_VARIABLE}) -- 它的作用是指定 SuppressWarnings 的类型同时包括TYPE, FIELD, METHOD, PARAMETER, CONSTRUCTOR, LOCAL_VARIABLE。
 *
 *     TYPE 意味着，它能标注"类、接口（包括注释类型）或枚举声明"。
 *     FIELD 意味着，它能标注"字段声明"。
 *     METHOD 意味着，它能标注"方法"。
 *     PARAMETER 意味着，它能标注"参数"。
 *     CONSTRUCTOR 意味着，它能标注"构造方法"。
 *     LOCAL_VARIABLE 意味着，它能标注"局部变量"。
 *
 * (04) String[] value(); 意味着，SuppressWarnings 能指定参数
 *
 * (05) SuppressWarnings 的作用是，让编译器对"它所标注的内容"的某些警告保持静默。例如，"@SuppressWarnings(value={"deprecation", "unchecked"})"
 * 表示对"它所标注的内容"中的 "SuppressWarnings 不再建议使用警告"和"未检查的转换时的警告"保持沉默。示例如下：
 *
 * @author 17822
 */
@CustomAnnotation()
public class AnnotationDemo {

    public static void main(String[] args) {

    }

}

/**
 * 自定义注解
 * @author 17822
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.CONSTRUCTOR, ElementType.LOCAL_VARIABLE})
@interface CustomAnnotation{

    String[] value() default "aaa";

}
