package me.rabbitmq.demo;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    void listTest(){
        var random = new Random();
        var baba = new ArrayList<String>();
        baba.add("cbaba");
        baba.add("edbf");
        baba.add("abed");
        baba.add("ace");
        baba.add("aee");
        var str = baba.stream().filter(it->it.startsWith("a")).toList();
        System.out.println(str);
        var t = baba.stream().collect(Collectors.groupingBy(String::length));
        System.out.println(t);
        for (var i = 0; i < 1_000_0000; i++) {
            if(random.nextBoolean()){
                baba.add("a"+random.nextDouble());
            }else {
                baba.add("c"+random.nextDouble());
            }
        }
        var start = System.currentTimeMillis();
        var _ =unStream(baba);
        System.out.println(System.currentTimeMillis() - start);
        start = System.currentTimeMillis();
        var _ =stream(baba);
        System.out.println(System.currentTimeMillis() - start);
        start = System.currentTimeMillis();
        var _ =parallelStream(baba);
        System.out.println(System.currentTimeMillis() - start);
    }

    private List<String> unStream(List<String> str){
        var result = new ArrayList<String>();
        for (String st : str) {
            if (st.startsWith("a")){
                result.add(st);
            }
        }
        return result;
    }
    private List<String> stream(List<String> str){
        return str.stream().filter(it->it.startsWith("a")).toList();
    }
    private List<String> parallelStream(List<String> str){
        return str.parallelStream().filter(it->it.startsWith("a")).toList();
    }
}
