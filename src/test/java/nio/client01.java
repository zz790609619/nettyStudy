package nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class client01 {
    public static void main(String[] args) throws IOException {
        //1.创建服务器
        SocketChannel ssc=SocketChannel.open();
        ssc.connect(new InetSocketAddress("localhost",8090));
        ssc.write(StandardCharsets.UTF_8.encode("hello01"));
        System.out.println("nio.client waiting");



    }
}
