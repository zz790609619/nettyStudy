package nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class client {
    public static void main(String[] args) throws IOException {
        //1.创建服务器
        SocketChannel ssc=SocketChannel.open();
        ssc.connect(new InetSocketAddress("localhost",8090));
        while (true){
            ByteBuffer byteBuffer=ByteBuffer.allocate(1024*1024);
            int read = ssc.read(byteBuffer);
            System.out.println("客户端数量："+read);
            byteBuffer.flip();
            System.out.println(Charset.defaultCharset().decode(byteBuffer));
            byteBuffer.clear();
        }


    }
}
