package com.super_m.uid.generator;

import com.super_m.uid.factory.UidGeneratorFactory;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SnowflakeUidGeneratorTests {
    @Test
    public void id() {
        Map<Long, Long> map = new HashMap<>();
        int maxCount = 1;
        SnowflakeUidGenerator idWorker = (SnowflakeUidGenerator) UidGeneratorFactory.createUidGenerator(SnowflakeUidGenerator.class);
        idWorker.setWorkerId(1);
        idWorker.setDatacenterId(1);
        for (int i = 0; i < maxCount; i++) {
            long id = (long) idWorker.nextId();
            System.out.println("--------------------------------------------------------------");
            System.out.println(id + " ---> id长整型");
            System.out.println(Long.toBinaryString(id) + " ---> id二进制");
            System.out.println(Long.toBinaryString((-1L ^ (-1L << 12))) + " ---> 序列号最大值,也就是4095");
            System.out.println(Long.toBinaryString((-1l)) + " ---> 64位,负数的最大值");
            System.out.println(Long.toBinaryString((-1l << 5l)) + " ---> 按位左移5位");
            System.out.println(Long.toBinaryString(~(-1l << 5l)) + "---> 取反,获得5位二进制的最大值,也就是31");
            System.out.println(Long.toBinaryString((id)) + " ---> id ,左移0位,定位到workerId");
            System.out.println(Long.toBinaryString((id >> 12)) + " ---> id ,左移12位,定位到workerId");
            System.out.println(Long.toBinaryString((id >> 17)) + " ---> id ,左移17位,定位到datacenterId");
            System.out.println(Long.toBinaryString((id >> 22)) + " ---> id ,左移22位,定位到timestamp");
            System.out.println(Long.parseLong(Long.toBinaryString((id) & ~(-1L << 12L)), 2) + " ---> & 运算 , 解析sequence");
            System.out.println(Long.parseLong(Long.toBinaryString((id >> 12) & ~(-1L << 10L)), 2) + " ---> & 运算 , 解析dwId");
            System.out.println(Long.parseLong(Long.toBinaryString((id >> 12) & ~(-1L << 5L)), 2) + " ---> & 运算 , 解析workerId");
            System.out.println(Long.parseLong(Long.toBinaryString((id >> 17) & ~(-1L << 5L)), 2) + " ---> & 运算 , 解析datacenterId");
            System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS").format(new Date(Long.parseLong(Long.toBinaryString((id >> 22) + 1288834974657l), 2))) + " ---> & 运算 , 解析timestamp");
            if (map.containsKey(id)) {
                System.out.println("重复:" + id);
            }
            map.put(id, id);
        }
        long time = System.currentTimeMillis();
        System.out.println(Long.toBinaryString(time));
        long dwId = time & ~(-1L << (5L + 5L));
        System.out.println(Long.toBinaryString(dwId));
        //0 - 0000000000 0000000000 0000000000 0000000000 0 - 00000 - 00000 - 000000000000
        //       1100100 0101110001 1111010110 0111000000 1 - 00001 - 00001 - 000000000000 ---> 1 ,1
        //       1100100 0101110010 0110110000 1110100110 1 - 00010 - 00010 - 000000000000 ---> 2 ,2
        //       1100100 0101110010 1100010011 1000001000 0 - 11111 - 11111 - 000000000000 ---> 31 , 31
    }

}
