package com.monkey.demo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

/**
 * 使用注解来快速模拟
 * User: monkey
 * Date: 2018-04-25 16:44
 */
@RunWith(MockitoJUnitRunner.class)
public class MockitoExample2 {

    @Mock
    private List mockList;

    @Test
    public void shorhand(){
        mockList.add(1);
        verify(mockList).add(1);
    }
}
