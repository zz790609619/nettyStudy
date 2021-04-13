package netty.z01;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.net.InetSocketAddress;

public class HelloClient {
    public static void main(String[] args) throws InterruptedException {
        //1.启动器，负责组装netty组件 启动客户端
        new Bootstrap()
                //2.添加 线程和选择器
                .group(new NioEventLoopGroup())
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
                .connect(new InetSocketAddress("localhost",8080))
                //阻塞方法  直到连接建立
                .sync()
                //代表连接对象
                .channel()
                //发送数据 然后服务器端的某个eventloop处理read事件 接收到bytebuffer 然后走服务器端的handler
                .writeAndFlush("hello world");
    }
}
