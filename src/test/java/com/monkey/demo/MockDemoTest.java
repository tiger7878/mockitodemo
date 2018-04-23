package com.monkey.demo;

import org.junit.Test;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

/**
 * mock示例
 * User: monkey
 * Date: 2018-04-23 9:52
 */
public class MockDemoTest {

    //验证行为
    @Test
    public void verify_behaviour(){
        //模拟创建一个List对象
        List list=mock(List.class);
        //使用mock的对象
        list.add(1);
        list.clear();
        //验证add(1)和clear()行为是否发生
        verify(list).add(1);
        verify(list).clear();
    }
}
