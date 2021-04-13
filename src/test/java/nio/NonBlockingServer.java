package nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class NonBlockingServer {
    /**
     * 非阻塞模式的坏处 就是线程会一直使用
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        //使用nio来理解阻塞模式
        ByteBuffer byteBuffer=ByteBuffer.allocate(16);
        //1.创建服务器
        ServerSocketChannel ssc=ServerSocketChannel.open();
        ssc.configureBlocking(false);//非阻塞
        //2.绑定监听端口
        ssc.bind(new InetSocketAddress(8090));
        //3.建立监听端口得channel集合
        List<SocketChannel> channels=new ArrayList<>();
        while (true){
            //4.服务器通过socketchannel 和客户端通信 接收数据
            SocketChannel socketChannel=ssc.accept();//非阻塞模式下  accept线程还会运行 但是socketChannel为null
            System.out.println("connecting ...");
            if(socketChannel!=null){
                System.out.println("connected ...");
                channels.add(socketChannel);
            }
            for (SocketChannel s: channels) {
                System.out.println("before read");
                s.read(byteBuffer); //非阻塞 线程依然会继续运行 如果没有读到数据会返回0
                byteBuffer.flip();
                System.out.println(StandardCharsets.UTF_8.decode(byteBuffer));
                byteBuffer.clear();
                System.out.println("after read");
            }

        }


    }
}
