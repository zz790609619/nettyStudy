package netty.z04;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public class NianBaoBanBaoServer {
    //粘包
    //客户端分10批发送16个字节 而服务端只接受了一次160个字节
    //LoggingHandler - [id: 0x09967222, L:/127.0.0.1:8080 - R:/127.0.0.1:49303] READ: 160B
    //半包(服务端设置缓冲区为10个字节)
    //客户端分10批发送16个字节 而客户端只接受了5次 但是每一次字节数都不和我们客户端发送得字节数匹配
    //LoggingHandler - [id: 0x88884e2c, L:/127.0.0.1:8080 - R:/127.0.0.1:49391] READ: 36B
    //LoggingHandler - [id: 0x88884e2c, L:/127.0.0.1:8080 - R:/127.0.0.1:49391] READ: 40B
    //LoggingHandler - [id: 0x88884e2c, L:/127.0.0.1:8080 - R:/127.0.0.1:49391] READ: 40B
    //LoggingHandler - [id: 0x88884e2c, L:/127.0.0.1:8080 - R:/127.0.0.1:49391] READ: 40B
    //LoggingHandler - [id: 0x88884e2c, L:/127.0.0.1:8080 - R:/127.0.0.1:49391] READ: 4B
    public static void main(String[] args) {
        lengthFieldBasedFrameDecoder();
        NioEventLoopGroup boss=new NioEventLoopGroup();
        NioEventLoopGroup worker=new NioEventLoopGroup();
        try {
            new ServerBootstrap()
                    .group(boss,worker)
                    //.option(ChannelOption.SO_RCVBUF, 10)//代表服务端接受缓冲区(滑动窗口)大小为10个字节
                    .childOption(ChannelOption.RCVBUF_ALLOCATOR,new AdaptiveRecvByteBufAllocator(16,16,16))//设置netty临时缓冲区大小
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                            nioSocketChannel.pipeline().addLast(new IdleStateHandler(1,1,0));
                            nioSocketChannel.pipeline().addLast(new ChannelDuplexHandler(){
                                @Override
                                public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                                    //自定义触发特殊时间
                                    IdleStateEvent event=(IdleStateEvent)evt;
                                    if(event.state()== IdleState.READER_IDLE){
                                        //触发时间
                                    }
                                }
                            });
                            //1.定长处理器
                            //nioSocketChannel.pipeline().addLast(new FixedLengthFrameDecoder(10));
                            //2.固定字符处理
                            //2.1 自定义字符
                            nioSocketChannel.pipeline().addLast(new DelimiterBasedFrameDecoder(1024));
                            //2.2默认分割符是\n 最大长度1024 如果超过最大长度还没有遇到分割符则报错
                            nioSocketChannel.pipeline().addLast(new LineBasedFrameDecoder(1024));
                            //3 使用LengthFieldBasedFrameDecoder
                            //  private final int lengthFieldOffset; 长度偏移量
                            //  private final int lengthFieldLength; 内容长度
                            //  private final int lengthAdjustment;中间如果遇到其他长度字节写入 则需要把长度放入此参数
                            //   private final int initialBytesToStrip; 结果需不需要剥离
                            nioSocketChannel.pipeline().addLast(new LengthFieldBasedFrameDecoder(1024,4,0,0,0));
                            nioSocketChannel.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                        }
                    }).bind(8080).sync();

        } catch (Exception e) {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

    /**
     * LengthFieldBasedFrameDecoder 处理
     */
    static void lengthFieldBasedFrameDecoder(){
        EmbeddedChannel embeddedChannel=new EmbeddedChannel(
                new LengthFieldBasedFrameDecoder(1024,4,0,0,0)
                ,new LoggingHandler(LogLevel.DEBUG));

        ByteBuf buf= ByteBufAllocator.DEFAULT.buffer();
        byte[] bytes="hello,zz".getBytes();
        int length = bytes.length;
        buf.writeInt(length);
        buf.writeBytes(bytes);
        embeddedChannel.writeAndFlush(buf);
        ByteBuf bufz= ByteBufAllocator.DEFAULT.buffer();
        byte[] bytee="im uu".getBytes();
        int lengthee = bytee.length;
        bufz.writeInt(lengthee);
        bufz.writeBytes(bytee);
        embeddedChannel.writeAndFlush(bufz);
    }
}
