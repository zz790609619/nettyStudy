package netty.z03;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import netty.z02.EventLoopServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;

public class NettyPipeline {
    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(NettyPipeline.class);
//      n个handler组成一个pipeline
//      channelHandler分为出站和入站
        new ServerBootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        ChannelPipeline pipeline = nioSocketChannel.pipeline();
                        //流水线有 head  -->   入栈 addlast(h1->h2)  出栈addlast(->h4->h3)   -->tail
                        //入栈 h1->h2
                        pipeline.addLast("h1",new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                logger.debug("h1");
                                //转换成字符串
                                ByteBuf buf=(ByteBuf)msg;
                                String s=buf.toString(Charset.defaultCharset());
                                //将执行权传给后面的handler
                                super.channelRead(ctx, s);
                            }
                        });
                        pipeline.addLast("h2",new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                logger.debug("h2");
                                logger.debug("h2"+msg.toString());
                                //从当前handler向前找出战处理器
                                //ctx.writeAndFlush(ctx.alloc().buffer().writeBytes("server".getBytes()));
                                //从尾部handler向前找出战处理器
                                nioSocketChannel.writeAndFlush(ctx.alloc().buffer().writeBytes("server".getBytes()));
                            }
                        });
                        //出栈 h4->h3 相反
                        pipeline.addLast("h3",new ChannelOutboundHandlerAdapter(){
                            @Override
                            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                nioSocketChannel.writeAndFlush("hello".getBytes());
                                logger.debug("h3");
                                super.write(ctx, msg, promise);

                            }
                        });
                        pipeline.addLast("h4",new ChannelOutboundHandlerAdapter(){
                            @Override
                            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                logger.debug("h4");
                                super.write(ctx, msg, promise);
                            }
                        });


                    }
                })
                .bind(8080);

    }
}
