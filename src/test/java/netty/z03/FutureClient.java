package netty.z03;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;

import java.net.InetSocketAddress;

public class FutureClient {
    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup nioEventLoopGroup=new NioEventLoopGroup();
        //1.启动器，负责组装netty组件 启动客户端
        ChannelFuture channelFuture = new Bootstrap()
                //2.添加 线程和选择器
                .group(nioEventLoopGroup)
                //3.选择服务器得Socketchannel
                .channel(NioSocketChannel.class)
                //4.event处理逻辑 在连接建立后才会执行
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        //添加客户端的处理逻辑  发送给服务端时经过这些处理逻辑
                        nioSocketChannel.pipeline().addLast(new StringEncoder());
                    }
                })
                // 连接到服务器 异步非阻塞 主线程开启一个nio线程去连接服务器端，主线程继续执行
                .connect(new InetSocketAddress("localhost", 8080));
        //主线程无阻塞得获取channel只有地址 没有服务器端得端口和客户端端口   连接服务器端得线程并未连接上
        Channel localhost = channelFuture .channel();//为空
        //解决方法 1：
        //所以需要 阻塞主线程  直到nio线程连接建立
        Channel localhost1 = channelFuture.sync().channel();
        //解决方法 2 addListener(回调对象) 获取异步结果：
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            //在nio线程建立好之后 会调用该方法
            public void operationComplete(ChannelFuture cf) throws Exception {
                Channel channel = cf.channel();
                System.out.println(localhost);
                channel.writeAndFlush("helo");
            }
        });
        System.out.println(localhost);
        localhost.writeAndFlush("lohe");
        //关闭客户端连接 Channel.close()是异步方法
        //解决方案1：closeFuture().sync()方法
        localhost.closeFuture().sync();
        System.out.println("关闭客户端");
        //解决方案2：closeFuture().addListener
        localhost.closeFuture().addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                //优雅的关闭：不接受新的任务 把现有任务执行完停止正在运行的线程
                nioEventLoopGroup.shutdownGracefully();
                System.out.println("关闭客户端");
            }
        });
    }
}
