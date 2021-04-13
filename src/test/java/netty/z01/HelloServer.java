package netty.z01;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class HelloServer {
    public static void main(String[] args) throws IOException {
        //1.启动器，负责组装netty组件 启动服务器
        new ServerBootstrap()
                //2.添加 线程和选择器
                .group(new NioEventLoopGroup()) //accept事件
                //3.选择服务器得serverSocketchannel
                .channel(NioServerSocketChannel.class)
                //4.event处理逻辑
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    //5. channel 代表和客户端进行数据读写的通道 在连接建立后才会执行  负责处理其他得handler
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        //6.添加了具体得handler 类似装饰器 一层一层逻辑处理
                        nioSocketChannel.pipeline().addLast(new StringDecoder());
                        nioSocketChannel.pipeline().addLast(new ChannelInboundHandlerAdapter(){//自定义入栈handler
                            //重写读事件
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                System.out.println(msg);
                            }
                        });
                    }
                })
                //7.NioServerSocketChannel绑定端口
                .bind(8080);
    }
}
