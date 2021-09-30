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
        Random random = new Random();
        ArrayList<String> baba = new ArrayList<>();
        baba.add("cbaba");
        baba.add("edbf");
        baba.add("abed");
        baba.add("ace");
        baba.add("aee");
        List<String> str = baba.stream().filter(it->it.startsWith("a")).collect(Collectors.toList());
        System.out.println(str);
        Map<Integer,List<String>> t = baba.stream().collect(Collectors.groupingBy(String::length));
        System.out.println(t);
        for (int i = 0; i < 1_000_0000; i++) {
            if(random.nextBoolean()){
                baba.add("a"+random.nextDouble());
            }else {
                baba.add("c"+random.nextDouble());
            }
        }
        long start = System.currentTimeMillis();
        unstream(baba);
        System.out.println(System.currentTimeMillis() - start);
        start = System.currentTimeMillis();
        stream(baba);
        System.out.println(System.currentTimeMillis() - start);
        start = System.currentTimeMillis();
        parallelStream(baba);
        System.out.println(System.currentTimeMillis() - start);
    }

    private void unstream(List<String> str){
        ArrayList<String> result = new ArrayList<>();
        for (String st : str) {
            if (st.startsWith("a")){
                result.add(st);
            }
        }
    }
    private void stream(List<String> str){
        str.stream().filter(it->it.startsWith("a")).collect(Collectors.toList());
    }
    private void parallelStream(List<String> str){
        str.parallelStream().filter(it->it.startsWith("a")).collect(Collectors.toList());
    }
}
