package netty.z04;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;

public class TestRedis {
    public static void main(String[] args) throws InterruptedException {
        final byte[] line={13,10};//换行符
        new Bootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        nioSocketChannel.pipeline().addLast(new LoggingHandler());
                        nioSocketChannel.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                //写入
                                //redis协议
                                // *元素个数 \n $第一个元素得长度 \n 第一个元素 \n $第二个元素得长度 \n 第二个元素 \n $第三个元素得长度 \n 第三个元素
                                ByteBuf buf=ctx.alloc().buffer();
                                buf.writeBytes("*3".getBytes());
                                buf.writeBytes(line);
                                buf.writeBytes("$3".getBytes());
                                buf.writeBytes(line);
                                buf.writeBytes("set".getBytes());
                                buf.writeBytes(line);
                                buf.writeBytes("$3".getBytes());
                                buf.writeBytes(line);
                                buf.writeBytes("age".getBytes());
                                buf.writeBytes(line);
                                buf.writeBytes("$2".getBytes());
                                buf.writeBytes(line);
                                buf.writeBytes("18".getBytes());
                                buf.writeBytes(line);
                                ctx.writeAndFlush(buf);
                            }

                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                //读取
                                ByteBuf buf=(ByteBuf)msg;
                                System.out.println(buf.toString(Charset.defaultCharset()));
                                super.channelRead(ctx, msg);
                            }
                        });

                    }
                }).connect(new InetSocketAddress("localhost",6379)).sync();

    }
}
