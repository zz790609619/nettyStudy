package netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import netty.z02.EventLoopServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class test {
    public static void main(String[] args) throws InterruptedException {
        Logger logger = LoggerFactory.getLogger(test.class);
        CountDownLatch countDownLatch=new CountDownLatch(20);
        AtomicInteger atomicInteger=new AtomicInteger();
        for (int i = 1; i <= 20; i++) {
            new Thread(()->{
                List<Integer> list=new ArrayList<>();
                for (int j = 0; j < 10; j++) {
                    list.add(j);
                }
                atomicInteger.addAndGet(list.size());
                countDownLatch.countDown();
            }).start();
        }
        countDownLatch.await();
        System.out.println(atomicInteger);
    }
}
