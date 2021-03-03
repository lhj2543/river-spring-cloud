package com.river.learn.java.java8;

import lombok.Data;
import lombok.experimental.Accessors;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Stream 可以指对集合进行操作，可以执行复杂的查找，过滤，映射数据等操作。Stream API一种高效且易于使用的处理数据的方式。
 * Stream 自己不会存储元素
 * Stream 不会改变源对象，它会返回一个持有结果的新Stream
 * Stream 操作是延迟执行的，所以会等到需要结果的时候才执行
 *
 * Stream 流程：创建Stream（一个数据源：如集合，数组） -> 中间操作（对数据源的数据处理）->终止操作（执行中间操作链，产生结果）
 *
 * @author 17822
 */
public class StreamDemo {

    /**
     * 创建Stream
     */
    @Test
    public void  test1(){
        //1:通过Collection 系列集合提供的stream() 或 paralleStream()
        List<String> list = new ArrayList<>();
        Stream<String> stream = list.stream();

        //2:通过Arrays 中静态方法stream() 获取数组流
        String[] strs = new String[10];
        Stream<String> stream1 = Arrays.stream(strs);

        //3：通过Stream 类中静态方法of() 获取流
        ArrayList<User> users = new ArrayList<>();
        Stream<ArrayList<User>> arrayListStream = Stream.of(users);
        Stream<Integer> integerStream = Stream.of(10, 20);

        //4：创建无限流
        //从0开始，每次加1
        Stream<Integer> iterate = Stream.iterate(0, (x) -> x + 1);
        //只生产10个数
        iterate.limit(10).forEach(System.out::println);

        //生成 无限流
        Stream.generate(()->Math.random())
                .limit(10)
                .forEach((row)->Optional.of(row).ifPresent(System.out::println));

    }

    /**
     * 中间操作（对数据源的数据处理） 注：中间操作不会执行任何操作，只有等有流的终止操作才一并执行
     * filter 从流中过滤元素
     * limit 使其元素不超过给定数量
     * slip(n) 跳过元素，返回一个扔掉了前n个元素的流，若流中元素不足n个，则返回一个空流，与limit组合使用，分页等
     * distinct 筛选，通过流所生成元素的hashCode() 和equals() 去除重复元素
     *
     * 映射
     * map 接收Lambda ,将元素转换成其他形式或提取信息，接收一个函数作为参数，该函数会被应用到每个元素上，并将其映射成一个新的元素
     * flatMap 接收一个函数作为参赛，将流中的每一个值替换成另一个流，然后把所有的流接成一个流
     *
     * 排序
     * sorted() 自然排序(Comparable)
     * sorted(Comparator comparator) 自定义排序
     */
    @Test
    public void  test2(){

        List<User> users = getUsers();
        users.stream()
                .skip(0)
                .limit(5)
                //.filter((user)-> user.getAge()>10)
                .distinct()
                .forEach((user -> Optional.of(user.toString()).ifPresent(System.out::println)));
        System.out.println("===================================================================================");

        users.stream()
                .map(User::getName)
                .forEach(System.out::println);

        System.out.println("-----------------------------------------------");

        users.stream()
                .flatMap((user) -> StreamDemo.getUsers(user.getName()))
                .sorted()
                .forEach(System.out::println);

        System.out.println("===================================================================================");
        users.stream()
                .sorted((o1,o2)->{
                    if(o1.getAge().intValue() == o2.getAge().intValue()){
                        return  o1.getName().compareTo(o2.getName());
                    }else {
                        return  o1.getAge().compareTo(o2.getAge());
                    }
                }).forEach(user->Optional.of(user.toString()).ifPresent(System.out::println));

    }

    /**
     * 查找与匹配
     * allMatch 是否所有元素都匹配
     * anyMatch 是否至少一个匹配
     * noneMatch 是否所有元素都不匹配
     * findFirst 返回第一个元素
     * findAny 返回当前流中任意一个元素
     * count 返回流中元素总个数
     * manx 返回流中最大值
     * min 返回流中最小值
     */
    @Test
    public void test3(){

        List<User> users = getUsers();
        Optional<User> any = users.stream().findAny();
        System.out.println(any.get());

        long count = users.stream().count();
        System.out.println(count);

        users.stream().max((a1,a2)->a1.getAge().compareTo(a2.getAge())).ifPresent(System.out::println);
    }

    /**
     * 归约 :可将流中的元数据反复结合起来，得到一个值
     * reduce(T identity,BinaryOperator)
     * reduce(BinaryOperator)
     *
     * 收集 :将流转换成其他形式，接收一个Collector 接口的实现，用于Stream中元数据汇总的方法
     * coollect
     */
    @Test
    public void test4(){

        List<User> users = getUsers();
        Integer reduce = users.stream()
                .map(User::getAge)
                .reduce(0, (x, y) -> x + y);
        System.out.println(reduce);

        List<String> list = users.stream()
                .map(User::getName)
                .collect(Collectors.toList());
        Optional.of(list).ifPresent(System.out::println);

        HashSet<String> set = users.stream()
                .map(User::getName)
                .collect(Collectors.toCollection(HashSet::new));
        System.out.println(set);

        //平均值
        Double collect = users.stream()
                .collect(Collectors.averagingDouble(User::getAge));
        System.out.println(collect);

        //总和
        DoubleSummaryStatistics collect1 = users.stream()
                .collect(Collectors.summarizingDouble(User::getAge));
        System.out.println(collect1);
        
        //分组
        Map<State, List<User>> collect2 = users.stream()
                .collect(Collectors.groupingBy(User::getState));
        System.out.println(collect2);

        //多级分组
        Map<State, Map<String, List<User>>> collect3 = users.stream()
                .collect(Collectors.groupingBy(User::getState, Collectors.groupingBy(User::getName)));
        System.out.println(collect3);


        //转换成map
        Map<String, State> map = users.stream()
                .collect(Collectors.toMap(User::getName, User::getState,(last,next)->next));
        Optional.of(map).ifPresent(System.out::println);

    }

    /**
     * 并行流
     *
     */
    @Test
    public void test5(){
        List<User> users = getUsers();
        users.stream()
                .parallel();

        users.parallelStream();
    }

    public static Stream<Character> getUsers(String name){
        ArrayList<Character> list = new ArrayList<Character>();
        for (char c:name.toCharArray()){
            list.add(c);
        }
        return list.stream();
    }


    public List<User> getUsers(){
        ArrayList<User> list = new ArrayList(30);

        State[] states = new State[]{State.DISABLE,State.NORMAL,State.ERROE};
        Random random = new Random(System.currentTimeMillis());


        Stream.iterate(1,(i)->i+1)
            .limit(30)
            .forEach(i->{
                User u = new User();
                if(i==2 || i==3){
                    u.setAge(12);
                    u.setName("张三");
                }else {
                    u.setAge((int)(Math.random()*100));
                    u.setName(String.format("张三%s",i));
                }
                u.setState(states[random.nextInt(3)]);
                u.setCreateDate(new Date());
                list.add(u);
            });
        return list;
    }

}

@Data
@Accessors(chain = true)//链式
class User{
    private String name;

    private Integer age;

    private State state;

    private Date createDate;

    /*@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        return age == user.age &&
                name.equals(user.name) &&
                createDate.equals(user.createDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, age, createDate);
    }*/

}

enum State{
    DISABLE,NORMAL,ERROE;
}