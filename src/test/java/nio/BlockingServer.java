package nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class BlockingServer {
    public static void main(String[] args) throws IOException {
        //使用nio来理解阻塞模式
        ByteBuffer byteBuffer=ByteBuffer.allocate(16);
        //1.创建服务器
        ServerSocketChannel ssc=ServerSocketChannel.open();
        //2.绑定监听端口
        ssc.bind(new InetSocketAddress(8090));
        //3.建立监听端口得channel集合
        List<SocketChannel> channels=new ArrayList<>();
        while (true){
            //4.服务器通过socketchannel 和客户端通信 接收数据
            SocketChannel socketChannel=ssc.accept();
            System.out.println("建立链接");
            channels.add(socketChannel);
            for (SocketChannel s: channels) {
                s.read(byteBuffer);
                byteBuffer.flip();
                System.out.println(StandardCharsets.UTF_8.decode(byteBuffer));
                byteBuffer.clear();
            }
        }


    }
}
