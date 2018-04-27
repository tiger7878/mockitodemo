package com.monkey.demo;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.*;

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
    public void verify_behaviour() {
        //模拟创建一个List对象
        List list = mock(List.class);
        //使用mock的对象
        list.add(1);
        list.clear();
        //验证add(1)和clear()行为是否发生
        verify(list).add(1);
        verify(list).clear();
    }

    //模拟我们所期望的结果
    @Test
    public void when_thenReturn() {
        //mock一个Iterator类
        Iterator iterator = mock(Iterator.class);
        //预设当iterator调用next()时第一次返回hello，第n次都返回world
        when(iterator.next()).thenReturn("hello").thenReturn("world");
        //使用mock的对象
        String result = iterator.next() + " " + iterator.next() + " " + iterator.next();
        //验证结果
        assertEquals("hello world world", result);
    }

    @Test(expected = IOException.class)
    public void when_thenThrow() throws IOException {
        OutputStream outputStream = mock(OutputStream.class);
        OutputStreamWriter writer = new OutputStreamWriter(outputStream);
        //预设当流关闭时抛出异常
        doThrow(new IOException()).when(outputStream).close();
        outputStream.close();
    }

    @Test
    public void returnsSmartNullsTest() {
        //在创建mock对象时，有的方法我们没有进行stubbing，所以调用时会放回Null这样在进行操作是很可能抛出NullPointerException。
        // 如果通过RETURNS_SMART_NULLS参数创建的mock对象在没有调用stubbed方法时会返回SmartNull。
        // 例如：返回类型是String，会返回"";是int，会返回0；是List，会返回空的List。另外，在控制台窗口中可以看到SmartNull的友好提示。
        List list = mock(List.class, RETURNS_SMART_NULLS);
        System.out.println("get0：" + list.get(0));

        //使用RETURNS_SMART_NULLS参数创建的mock对象，不会抛出NullPointerException异常。
        // 另外控制台窗口会提示信息“SmartNull returned by unstubbed get() method on mock”
        System.out.println("length：" + list.toArray().length);
    }

    //RETURNS_DEEP_STUBS也是创建mock对象时的备选参数
    //RETURNS_DEEP_STUBS参数程序会自动进行mock所需的对象，方法deepstubsTest和deepstubsTest2是等价的
    @Test
    public void deepstubsTest() {
        Account account = mock(Account.class, RETURNS_DEEP_STUBS);
//        Account account=mock(Account.class);
        //当方法执行时返回“Beijing”
        when(account.getRailwayTicket().getDestination()).thenReturn("Beijing");
        account.getRailwayTicket().getDestination();
        verify(account.getRailwayTicket()).getDestination();//验证方法执行
        //判断预期结果
        assertEquals("Beijing", account.getRailwayTicket().getDestination());
    }

    @Test
    public void deepstubsTest2() {
        Account account = mock(Account.class);
        RailwayTicket railwayTicket = mock(RailwayTicket.class);
        when(account.getRailwayTicket()).thenReturn(railwayTicket);
        when(railwayTicket.getDestination()).thenReturn("Beijing");

        account.getRailwayTicket().getDestination();
        verify(account.getRailwayTicket()).getDestination();
        assertEquals("Beijing", account.getRailwayTicket().getDestination());
    }

    //模拟方法体抛出异常
    @Test(expected = RuntimeException.class)
    public void doThrow_when() {
        List list = mock(List.class);
        doThrow(new RuntimeException()).when(list).add(1);
        list.add(1);
    }

    //参数匹配
    @Test
    public void with_arguments() {
        Comparable comparable = mock(Comparable.class);
        //预设根据不同的参数返回不同的结果
        when(comparable.compareTo("Test")).thenReturn(1);
        when(comparable.compareTo("Omg")).thenReturn(2);
        assertEquals(1, comparable.compareTo("Test"));
        assertEquals(2, comparable.compareTo("Omg"));
        //对于没有预设的情况会返回默认值
        assertEquals(0, comparable.compareTo("Not stub"));
    }

    //除了匹配制定参数外，还可以匹配自己想要的任意参数
    @Test
    public void with_unspecified_arguments() {
        List list = mock(List.class);
        //匹配任意参数
        when(list.get(anyInt())).thenReturn(1);
        when(list.contains(argThat(new IsValid()))).thenReturn(true);

        assertEquals(1, list.get(1));
        assertEquals(1, list.get(999));

        assertTrue(list.contains(1));
        assertTrue(!list.contains(3));
    }

    private class IsValid extends ArgumentMatcher<List> {
        @Override
        public boolean matches(Object o) {
            return o == 1 || o == 2;
        }
    }

    //注意：如果你使用了参数匹配，那么所有的参数都必须通过matchers来匹配，如下代码：
    @Test
    public void all_arguments_provided_by_matchers() {
        Comparator comparator = mock(Comparator.class);
        comparator.compare("nihao", "hello");
        //如果你使用了参数匹配，那么所有的参数都必须通过matchers来匹配
        verify(comparator).compare(anyString(), eq("hello"));
        //下面的为无效的参数匹配使用
        //verify(comparator).compare(anyString(),"hello");
    }

    //自定义参数匹配
    @Test
    public void argumentMatchersTest() {
        //创建mock对象
        List<String> list = mock(List.class);

        //argThat(Matches<T> matcher)方法用来应用自定义的规则，可以传入任何实现Matcher接口的实现类。
        when(list.addAll(argThat(new IsListofTwoElements()))).thenReturn(true);

//        list.addAll(Arrays.asList("one","two","three"));
        list.addAll(Arrays.asList("one", "two"));
        //IsListofTwoElements用来匹配size为2的List，因为例子传入List为三个元素，所以此时将失败。
        verify(list).addAll(argThat(new IsListofTwoElements()));
    }

    class IsListofTwoElements extends ArgumentMatcher<List> {
        @Override
        public boolean matches(Object list) {
            return ((List) list).size() == 2;
        }
    }

    //捕获参数来进一步断言
    //较复杂的参数匹配器会降低代码的可读性，有些地方使用参数捕获器更加合适。
    @Test
    public void capturing_args() {
        PersonDao personDao = mock(PersonDao.class);
        PersonService personService = new PersonService(personDao);

        ArgumentCaptor<Person> argument = ArgumentCaptor.forClass(Person.class);
        personService.update(1, "jack");
        verify(personDao).update(argument.capture());
        assertEquals(1, argument.getValue().getId());
        assertEquals("jack", argument.getValue().getName());
    }

    class Person {
        private int id;
        private String name;

        public Person(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }

    interface PersonDao {
        public void update(Person person);
    }

    class PersonService {
        private PersonDao personDao;

        public PersonService(PersonDao personDao) {
            this.personDao = personDao;
        }

        public void update(int id, String name) {
            personDao.update(new Person(id, name));
        }
    }

    //使用方法预期回调接口生成期望值（Answer结构）
    @Test
    public void answerTest() {
        //mockList不知道从哪里来的
//        when(mockList.get(anyInt()))
    }

    private class CustomerAnswer implements Answer<String> {
        @Override
        public String answer(InvocationOnMock invocationOnMock) throws Throwable {
            Object[] args = invocationOnMock.getArguments();
            return "hello world:" + args[0];
        }
    }

    //2.10 修改对未预设的调用返回默认期望
    @Test
    public void unstubbed_invocations() {
        //mock对象使用Answer来对未预设的调用返回默认期望值
        List list = mock(List.class, new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                return 999;
            }
        });
        //下面的get(1)没有预设，通常情况下会返回NULL，但是使用了Answer改变了默认期望值
        assertEquals(999, list.get(1));
        //下面的size()没有预设，通常情况下会返回0，但是使用了Answer改变了默认期望值
        assertEquals(999, list.size());
    }

    //2.11 用spy监控真实对象
    //Mock不是真实的对象，它只是用类型的class创建了一个虚拟对象，并可以设置对象行为
    //Spy是一个真实的对象，但它可以设置对象行为
    //InjectMocks创建这个类的对象并自动将标记@Mock、@Spy等注解的属性值注入到这个中
    @Test(expected = IndexOutOfBoundsException.class)
    public void spy_on_real_objects() {
        List list = new LinkedList();
        List spy = spy(list);
        //下面预设的spy.get(0)会报错，因为会调用真实对象的get(0)，所以会抛出越界异常
        when(spy.get(0)).thenReturn(3);

        //使用doReturn-when可以避免when-thenReturn调用真实对象api
        doReturn(999).when(spy).get(999);
        //预设size()期望值
        when(spy.size()).thenReturn(100);
        //调用真实对象的api
        spy.add(1);
        spy.add(2);
        assertEquals(100, spy.size());
        assertEquals(1, spy.get(0));
        assertEquals(2, spy.get(1));

        verify(spy).add(1);
        verify(spy).add(2);
        assertEquals(999, spy.get(999));
        spy.get(2);
    }

    //2.12 真实的部分mock
    @Test
    public void real_partial_mock() {
        //通过spy来调用真实的api
        List list = spy(new ArrayList());
        assertEquals(0, list.size());
        A a = mock(A.class);
        //通过thenCallRealMethod来调用真实的api
        when(a.doSomething(anyInt())).thenCallRealMethod();
        assertEquals(999, a.doSomething(999));
    }

    class A {
        public int doSomething(int i) {
            return i;
        }
    }

    //2.13 重置mock
    @Test
    public void reset_mock() {
        List list = mock(List.class);
        when(list.size()).thenReturn(10);
        list.add(1);
        assertEquals(10, list.size());

        //重置mock，清除所有的互动和预设
        reset(list);
        assertEquals(0, list.size());
    }

    //2.14 验证确切的调用次数
    @Test
    public void verifying_number_of_invocations() {
        List list = mock(List.class);
        list.add(1);
        list.add(2);
        list.add(2);
        list.add(3);
        list.add(3);
        list.add(3);

        //验证方法是否被执行
        verify(list).add(1);
        //验证是否被调用一次，等效于下面的times(1)
        verify(list, times(1)).add(1);

        //验证是否被调用2次
        verify(list, times(2)).add(2);

        //验证是否被调用3次
        verify(list,times(3)).add(3);

        //验证是否从未被调用过
        verify(list,never()).add(4);

        //验证至少调用一次
        verify(list,atLeastOnce()).add(1);
        //验证至少调用2次
        verify(list,atLeast(2)).add(2);
        //验证至多调用3次
        verify(list,atMost(3)).add(3);
    }


}
