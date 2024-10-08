package me.rabbitmq.demo;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * me.rabbitmq.demo.StreamTest
 *
 * @author Zhuang Jiabin
 * @version V1.0.0
 * @since 2020/12/18 10:45
 */
public class StreamTest {

    @Test
    void listTest() {
        // 创建一个随机数生成器
        var random = new Random();
        // 初始化一个字符串列表
        var baba = new ArrayList<String>();
        // 向列表中添加元素
        baba.add("cbaba");
        baba.add("edbf");
        baba.add("abed");
        baba.add("ace");
        baba.add("aee");
        // 过滤出以"a"开头的字符串并转换为列表
        var str = baba.stream().filter(it -> it.startsWith("a")).toList();
        System.out.println(str);
        // 按字符串长度分组
        var t = baba.stream().collect(Collectors.groupingBy(String::length));
        System.out.println(t);
        // 向列表中添加1000万个元素，元素以"a"或"c"开头，后跟一个随机数
        for (var i = 0; i < 1_000_0000; i++) {
            if (random.nextBoolean()) {
                baba.add("a" + random.nextDouble());
            } else {
                baba.add("c" + random.nextDouble());
            }
        }
        // 测试unStream方法的执行时间
        var start = System.currentTimeMillis();
        var _ = unStream(baba);
        System.out.println(System.currentTimeMillis() - start);
        // 测试stream方法的执行时间
        start = System.currentTimeMillis();
        var _ = stream(baba);
        System.out.println(System.currentTimeMillis() - start);
        // 测试parallelStream方法的执行时间
        start = System.currentTimeMillis();
        var _ = parallelStream(baba);
        System.out.println(System.currentTimeMillis() - start);
    }

    /**
     * 根据特定条件处理字符串列表
     * 本函数主要演示如何通过流（Stream）的过滤机制来筛选出特定前缀的字符串
     *
     * @param str 输入的字符串列表
     * @return 返回筛选后具有特定前缀的字符串列表
     */
    private List<String> unStream(List<String> str) {
        // 创建一个新的字符串列表，用于存储符合条件的元素
        var result = new ArrayList<String>();

        // 遍历输入的字符串列表
        for (String st : str) {
            // 检查字符串是否以字母"a"开头
            if (st.startsWith("a")) {
                // 如果是，则添加到结果列表中
                result.add(st);
            }
        }
        // 返回处理后的结果列表
        return result;
    }

    /**
     * 过滤列表中以"a"开头的字符串
     * 使用Java 8的Stream API对列表进行筛选，只保留那些以"a"开头的字符串
     *
     * @param str 输入的字符串列表，预期包含以"a"开头的字符串
     * @return 返回一个新的列表，包含输入列表中所有以"a"开头的字符串
     */
    private List<String> stream(List<String> str) {
        return str.stream().filter(it -> it.startsWith("a")).toList();
    }

    /**
     * 使用并行流过滤列表中以"a"开头的字符串
     * <p>
     * 本方法演示如何利用Java的并行流(parallelStream)来提升处理大量数据的效率
     * 它将输入的字符串列表转换为一个并行流，然后过滤出所有以"a"开头的字符串
     * 最后，将过滤后的结果收集到一个新的列表中返回
     *
     * @param str 输入的字符串列表，预计列表中的字符串元素可能很多
     * @return 返回一个新列表，包含输入列表中所有以"a"开头的字符串
     */
    private List<String> parallelStream(List<String> str) {
        // 使用parallelStream将列表转换为并行流，以提升过滤操作的执行效率
        // filter操作过滤出以"a"开头的字符串
        // toList将过滤后的结果收集到一个新的列表中
        return str.parallelStream().filter(it -> it.startsWith("a")).toList();
    }
}
