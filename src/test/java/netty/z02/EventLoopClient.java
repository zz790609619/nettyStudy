package netty.z02;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

public class EventLoopClient {
    public static void main(String[] args) throws InterruptedException {
        Logger logger = LoggerFactory.getLogger(EventLoopClient.class);
        //1.启动器，负责组装netty组件 启动客户端
        Channel channel = new Bootstrap()
                //2.添加 线程和选择器
                .group(new NioEventLoopGroup())
                //3.选择服务器得Socketchannel
                .channel(NioSocketChannel.class)
                //4.event处理逻辑 在连接建立后才会执行
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        //添加客户端的处理逻辑  发送给服务端时经过这些处理逻辑
                        ChannelPipeline pipeline = nioSocketChannel.pipeline();
                        pipeline.addLast(new StringEncoder());
                        pipeline.addLast(new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                ByteBuf buf=(ByteBuf)msg;
                                logger.debug("msg:"+buf.toString(Charset.defaultCharset()));
                                super.channelRead(ctx, msg);
                            }
                            //会在连接成功后  会触发active事件
                            @Override
                            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                super.channelActive(ctx);
                            }
                        });
                    }

                })
                .connect(new InetSocketAddress("localhost", 8080))
                //阻塞方法  直到连接建立
                .sync()
                //代表连接对象
                .channel();
        //发送数据 然后服务器端的某个eventloop处理read事件 接收到bytebuffer 然后走服务器端的handler
        channel.writeAndFlush("hello world");

    }
}
