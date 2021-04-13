package netty.z02;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;

public class EventLoopServer {
    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(EventLoopServer.class);
        //细分1：一个负责NioServerSocketChannel得accept事件，另一个负责读写
        //细分2: 生成一个defaultEventLoopGroup 用来处理特定的逻辑
        DefaultEventLoopGroup defaultEventLoopGroup=new DefaultEventLoopGroup(2);
        new ServerBootstrap()
                //boss 不需要指定线程数是因为服务器只有一个
                .group(new NioEventLoopGroup(),new NioEventLoopGroup(2))
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        nioSocketChannel.pipeline().addLast("NioEventLoopGroup",new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                ByteBuf buf=(ByteBuf)msg;
                                logger.debug("msg:"+buf.toString(Charset.defaultCharset()));
                                ctx.fireChannelRead(msg);//将消息值传递给下一个handler
                            }
                        }).addLast(defaultEventLoopGroup,"defaultEventLoopGroup",new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                ByteBuf buf=(ByteBuf)msg;
                                logger.debug("defaltMsg:"+buf.toString(Charset.defaultCharset()));
                            }
                        }).addLast(new ChannelOutboundHandlerAdapter(){
                            @Override
                            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                nioSocketChannel.writeAndFlush("hello".getBytes());
                                super.write(ctx, msg, promise);
                            }
                        });
                    }

                })
                .bind(8080);
    }

}
