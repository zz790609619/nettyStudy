package netty.note;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class CompactEventLoop {
    //一.EventLoop
    //1.本质上是个单线程执行器(同时维护了selector),里面有run方法处理channel上源源不断得io事件
    //1.1 继承juc ScheduledExecutor 故包含了线程池中所有得方法
    //1.2 继承 netty 得 OrderedEventExecutor 有序得
    //二.EventLoopGroup
    //1. 一组EventLoop channel一般会调用register方法注册在一个eventloop 进行绑定 保证了一个线程处理io 数据安全
    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(CompactEventLoop.class);
        //1.创建事件循环组
        // 1.1 NioEventLoopGroup（io 事件 ，普通任务 定时任务） DefaultEventLoopGroup（普通任务 定时任务）
        EventLoopGroup group=new NioEventLoopGroup(2);//参数：线程数 1或者默认为cpu得核心线程数*2得最大值
        //System.out.println(NettyRuntime.availableProcessors());
        //2.获取下一个事件循环对象(轮询)
        System.out.println("group.next() = " + group.next());//group.next() = io.netty.channel.nio.NioEventLoop@5f8ed237
        System.out.println("group.next1() = " + group.next());//group.next1() = io.netty.channel.nio.NioEventLoop@2f410acf
        System.out.println("group.next2() = " + group.next());//group.next2() = io.netty.channel.nio.NioEventLoop@5f8ed237
        System.out.println("group.next3() = " + group.next());//group.next3() = io.netty.channel.nio.NioEventLoop@2f410acf
        //3.执行普通的任务 类似异步任务
        group.next().execute(()->{
            logger.debug("普通任务执行："+Thread.currentThread().getName());
        });
        //4.定时任务
        group.next().schedule(()->{
            logger.debug("延迟任务执行："+Thread.currentThread().getName());
        },1, TimeUnit.SECONDS);
        group.next().scheduleAtFixedRate(()->{
            //按时间间隔1s执行一次
            logger.debug("每秒任务执行："+Thread.currentThread().getName());
        },0,1,TimeUnit.SECONDS);

    }
}
