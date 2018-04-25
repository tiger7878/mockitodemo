package com.monkey.demo;

import org.junit.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Iterator;
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

    //模拟我们所期望的结果
    @Test
    public void when_thenReturn(){
        //mock一个Iterator类
        Iterator iterator=mock(Iterator.class);
        //预设当iterator调用next()时第一次返回hello，第n次都返回world
        when(iterator.next()).thenReturn("hello").thenReturn("world");
        //使用mock的对象
        String result=iterator.next()+" "+iterator.next()+" "+iterator.next();
        //验证结果
        assertEquals("hello world world",result);
    }

    @Test(expected = IOException.class)
    public void when_thenThrow()throws IOException{
        OutputStream outputStream=mock(OutputStream.class);
        OutputStreamWriter writer=new OutputStreamWriter(outputStream);
        //预设当流关闭时抛出异常
        doThrow(new IOException()).when(outputStream).close();
        outputStream.close();
    }

    @Test
    public void returnsSmartNullsTest(){
        //在创建mock对象时，有的方法我们没有进行stubbing，所以调用时会放回Null这样在进行操作是很可能抛出NullPointerException。
        // 如果通过RETURNS_SMART_NULLS参数创建的mock对象在没有调用stubbed方法时会返回SmartNull。
        // 例如：返回类型是String，会返回"";是int，会返回0；是List，会返回空的List。另外，在控制台窗口中可以看到SmartNull的友好提示。
        List list=mock(List.class,RETURNS_SMART_NULLS);
        System.out.println("get0："+list.get(0));

        //使用RETURNS_SMART_NULLS参数创建的mock对象，不会抛出NullPointerException异常。
        // 另外控制台窗口会提示信息“SmartNull returned by unstubbed get() method on mock”
        System.out.println("length："+list.toArray().length);
    }

    //RETURNS_DEEP_STUBS也是创建mock对象时的备选参数
    //RETURNS_DEEP_STUBS参数程序会自动进行mock所需的对象，方法deepstubsTest和deepstubsTest2是等价的
    @Test
    public void deepstubsTest(){
        Account account=mock(Account.class,RETURNS_DEEP_STUBS);
//        Account account=mock(Account.class);
        //当方法执行时返回“Beijing”
        when(account.getRailwayTicket().getDestination()).thenReturn("Beijing");
        account.getRailwayTicket().getDestination();
        verify(account.getRailwayTicket()).getDestination();//验证方法执行
        //判断预期结果
        assertEquals("Beijing",account.getRailwayTicket().getDestination());
    }

    @Test
    public void deepstubsTest2(){
        Account account=mock(Account.class);
        RailwayTicket railwayTicket=mock(RailwayTicket.class);
        when(account.getRailwayTicket()).thenReturn(railwayTicket);
        when(railwayTicket.getDestination()).thenReturn("Beijing");

        account.getRailwayTicket().getDestination();
        verify(account.getRailwayTicket()).getDestination();
        assertEquals("Beijing",account.getRailwayTicket().getDestination());
    }



}
