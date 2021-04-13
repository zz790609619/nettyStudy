package netty.z03;

import io.netty.channel.EventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.GenericFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

public class FutureTest {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Logger logger = LoggerFactory.getLogger(FutureTest.class);
        // 1.JDK Future
        ExecutorService threadPool= Executors.newFixedThreadPool(2);
        Future<Integer> future = threadPool.submit(() -> {
            logger.debug("JDK Future开始计算....");
            Thread.sleep(1000);
            return 80;
        });
        logger.debug("JDK Future等待结果....");
        //阻塞主线程 等待子线程结束
        logger.debug("JDK Future同步获取结果为{}",future.get());
        //控制台打印
        //D:\application\java\bin\java.exe "-javaagent:D:\application\IntelliJ IDEA 2020.1\lib\idea_rt.jar=50684:D:\application\IntelliJ IDEA 2020.1\bin" -Dfile.encoding=UTF-8 -classpath D:\application\java\jre\lib\charsets.jar;D:\application\java\jre\lib\deploy.jar;D:\application\java\jre\lib\ext\access-bridge-64.jar;D:\application\java\jre\lib\ext\cldrdata.jar;D:\application\java\jre\lib\ext\dnsns.jar;D:\application\java\jre\lib\ext\jaccess.jar;D:\application\java\jre\lib\ext\jfxrt.jar;D:\application\java\jre\lib\ext\localedata.jar;D:\application\java\jre\lib\ext\nashorn.jar;D:\application\java\jre\lib\ext\sunec.jar;D:\application\java\jre\lib\ext\sunjce_provider.jar;D:\application\java\jre\lib\ext\sunmscapi.jar;D:\application\java\jre\lib\ext\sunpkcs11.jar;D:\application\java\jre\lib\ext\zipfs.jar;D:\application\java\jre\lib\javaws.jar;D:\application\java\jre\lib\jce.jar;D:\application\java\jre\lib\jfr.jar;D:\application\java\jre\lib\jfxswt.jar;D:\application\java\jre\lib\jsse.jar;D:\application\java\jre\lib\management-agent.jar;D:\application\java\jre\lib\plugin.jar;D:\application\java\jre\lib\resources.jar;D:\application\java\jre\lib\rt.jar;D:\project\java\netty01\target\test-classes;D:\project\java\netty01\target\classes;D:\application\m2\org\projectlombok\lombok\1.16.20\lombok-1.16.20.jar;D:\application\m2\io\netty\netty-all\4.1.38.Final\netty-all-4.1.38.Final.jar;D:\application\m2\org\slf4j\slf4j-api\1.7.25\slf4j-api-1.7.25.jar;D:\application\m2\ch\qos\logback\logback-core\1.2.3\logback-core-1.2.3.jar;D:\application\m2\ch\qos\logback\logback-classic\1.2.3\logback-classic-1.2.3.jar netty.z03.FutureTest
        //11:33:03.598 [pool-1-thread-1] DEBUG netty.z03.FutureTest - JDK Future开始计算....
        //11:33:03.598 [main] DEBUG netty.z03.FutureTest - JDK Future等待结果....
        //11:33:04.601 [main] DEBUG netty.z03.FutureTest - JDK Future结果为80

        // 2.Netty Future
        NioEventLoopGroup eventExecutors=new NioEventLoopGroup(2);
        EventLoop next = eventExecutors.next();
        io.netty.util.concurrent.Future<Integer> nettyFuture = next.submit(() -> {
            logger.debug("Netty Future开始计算....");
            Thread.sleep(1000);
            return 80;
        });
        // 2.1 同步获取结果
//        logger.debug("Netty Future等待结果....");
//        //阻塞主线程 等待子线程结束
//        logger.debug("Netty Future同步获取结果为{}",nettyFuture.get());
        //控制台打印
        //11:37:05.057 [main] DEBUG netty.z03.FutureTest - Netty Future等待结果....
        //11:37:05.058 [nioEventLoopGroup-2-1] DEBUG netty.z03.FutureTest - Netty Future开始计算....
        //11:37:06.071 [main] DEBUG netty.z03.FutureTest - Netty Future结果为80
        // 2.2 异步获取结果
        logger.debug("Netty Future等待结果....");
        //阻塞主线程 等待子线程结束
        nettyFuture.addListener(new GenericFutureListener<io.netty.util.concurrent.Future<? super Integer>>() {
            @Override
            public void operationComplete(io.netty.util.concurrent.Future<? super Integer> future) throws Exception {
                logger.debug("Netty Future异步获取结果为{}",future.get());
                //控制台输出
                //11:39:44.904 [main] DEBUG netty.z03.FutureTest - Netty Future等待结果....
                //11:39:44.906 [nioEventLoopGroup-2-1] DEBUG netty.z03.FutureTest - Netty Future开始计算....
                //11:39:45.915 [nioEventLoopGroup-2-1] DEBUG netty.z03.FutureTest - Netty Future异步获取结果为80
            }
        });
        // 3.Netty Promise
        NioEventLoopGroup promiseLoop=new NioEventLoopGroup(2);
        DefaultPromise<Integer> promise=new DefaultPromise<>(promiseLoop.next());
        new Thread(()->{
            logger.debug("Netty Promise开始计算....");
            try {
                Thread.sleep(1000);
                promise.setSuccess(80);
            } catch (InterruptedException e) {
                //如果子线程异常则会抛出异常到主线程处理
                promise.setFailure(e);
                e.printStackTrace();

            }
        }).start();
        logger.debug("Netty Promise等待结果....");
        logger.debug("Netty Promise异步获取结果为{}",promise.get());
        //控制台输出
        //11:43:44.730 [Thread-0] DEBUG netty.z03.FutureTest - Netty Promise开始计算....
        //11:43:45.739 [main] DEBUG netty.z03.FutureTest - Netty Promise异步获取结果为80
        //11:43:45.739 [nioEventLoopGroup-2-1] DEBUG netty.z03.FutureTest - Netty Future异步获取结果为80
    }
}
