package nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;

public class ServerWriteClient {
    public static void main(String[] args) throws IOException {
        //流程
        //1.新建非阻塞服务端serverSocketchannel,并绑定端口
        //2.服务端channel注册在selector上，并且附上channel感兴趣的类型（读写链接等）
        //3.selector.selector() 线程等待客户端链接
        //4.循环selectionkeys
        //5.如果为accept 则通过key的channel获取serversocketchannel，
        // 新建serversocketchannel里的socketchannel，
        // 并且注册在selector并且附上channel感兴趣的类型（读写链接等）
        ServerSocketChannel ssc=ServerSocketChannel.open();
        ssc.configureBlocking(false);
        ssc.bind(new InetSocketAddress(8090));
        Selector selector=Selector.open();
        ssc.register(selector,SelectionKey.OP_ACCEPT);
        while (true) {
            selector.select();
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();
                if (key.isAcceptable()){
                    ServerSocketChannel serverSocketChannel= (ServerSocketChannel) key.channel();
                    SocketChannel sc= serverSocketChannel.accept();
                    sc.configureBlocking(false);
                    SelectionKey scKey=sc.register(selector,SelectionKey.OP_READ);
                    StringBuilder stringBuilder=new StringBuilder();
                    for (int i = 0; i < 100000000; i++) {
                        stringBuilder.append("a");
                    }
                    ByteBuffer byteBuffer=Charset.defaultCharset().encode(stringBuilder.toString());
                    int write = sc.write(byteBuffer);
                    System.out.println("服务端第一次写数量:"+write);
                    if(byteBuffer.hasRemaining()){
                        scKey.interestOps(scKey.interestOps()+SelectionKey.OP_WRITE);
                        scKey.attach(byteBuffer);
                    }
                }else if(key.isWritable()){
                    System.out.println("进入服务端写客户端模式");
                    ByteBuffer byteBuffer= (ByteBuffer) key.attachment();
                    SocketChannel socketChannel= (SocketChannel) key.channel();
                    int write = socketChannel.write(byteBuffer);
                    System.out.println("服务端写数量:"+write);
                    if(!byteBuffer.hasRemaining()){
                        key.attach(null);
                        key.interestOps(key.interestOps()-SelectionKey.OP_WRITE);
                    }
                }
            }
        }
    }
}
