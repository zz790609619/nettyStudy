package netty.z04;


import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

public class NianBaoBanBaoClient {
    public static void main(String[] args) throws InterruptedException {
        //短链接
        shotUrlMethod();



    }

    static void shotUrlMethod(){
        //短链接
        for (int i = 0; i < 10; i++) {
            NioEventLoopGroup nioEventLoopGroup=new NioEventLoopGroup();
            try {
                new Bootstrap()
                        .group(nioEventLoopGroup)
                        .channel(NioSocketChannel.class)
                        .handler(new ChannelInitializer<NioSocketChannel>() {
                            @Override
                            protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                                nioSocketChannel.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                                    @Override
                                    public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                        //连接建立后直接走这个方法
                                        //发送数据给服务器端
                                        ByteBuf buf=ctx.alloc().buffer();
                                        buf.writeBytes(new byte[]{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15});
                                        ctx.writeAndFlush(buf);
                                        ctx.close();
                                        super.channelActive(ctx);
                                    }
                                });
                            }
                        }).connect(new InetSocketAddress("localhost",8080)).sync();

            } catch (Exception e) {
                nioEventLoopGroup.shutdownGracefully();
            }
        }
    }
}
