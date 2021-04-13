package nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SelectorServer {
    /**
     * 使用selector
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        //1.创建selector 管理多个channel
        Selector selector=Selector.open();
        ServerSocketChannel ssc=ServerSocketChannel.open();
        ssc.configureBlocking(false);//和selector关联的channel一定要是非阻塞的
        System.out.println("registerChannel："+ssc);
        //2.建立selector和channel的关系 channel注册在selector
        // 返回值为 SelectionKey  事件发生时 找到对应的channel
        //事件
        // accept 客户端链接请求时触发
        //connect 客户端连接建立后触发
        //read 可读事件  write可读事件
        SelectionKey sscKey = ssc.register(selector, 0, null);
        System.out.println("registerkey："+sscKey);
        //该key感兴趣的事件
        sscKey.interestOps(SelectionKey.OP_ACCEPT);
        //3.绑定监听端口
        ssc.bind(new InetSocketAddress(8090));
        while (true){
            //select方法 没有事件发生 线程阻塞 有事件，线程恢复运行  如果有事件未处理 则程序无限运行(使用cancel方法可以忽略事件)
            selector.select();
            //处理事件  selectedKeys 内部包含了所有发生的事件
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()){
                SelectionKey key=iterator.next();
                //处理完key对应的事件时候 不会移除key (解决办法:应该把key从selector.selectedKeys()中移除)
                iterator.remove();
                System.out.println("getkey："+key);
                //区分事件类型 处理完key对应的事件时候 不会移除key (解决办法:应该把key从selector.selectedKeys()中移除)
                if(key.isAcceptable()){
                    ServerSocketChannel serverSocketChannel=(ServerSocketChannel) key.channel();//获取触发事件的channel
                    SocketChannel socketChannel=serverSocketChannel.accept();
                    socketChannel.configureBlocking(false);
                    SelectionKey scKey=socketChannel.register(selector,0,null);//将bytebuffer作为附件放在key上
                    System.out.println("scKey:"+scKey);
                    scKey.interestOps(SelectionKey.OP_READ);
                    System.out.println("getChannel:"+socketChannel);
                }else if(key.isConnectable()){

                }else if(key.isReadable()){
                    try {
                        SocketChannel socketChannel=(SocketChannel)key.channel();//拿到触发事件的channel
//                        ByteBuffer byteBuffer=(ByteBuffer)key.attachment();
                        ByteBuffer byteBuffer=ByteBuffer.allocate(16);
                        int read=socketChannel.read(byteBuffer); //断开时会触发一次读 值为-1
                        if(read == -1){
                            key.cancel();
                        }else{
                            byteBuffer.flip();
                            System.out.println("read:"+StandardCharsets.UTF_8.decode(byteBuffer));
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        key.cancel();//客户端断开链接是 需要将key消失
                    }

                }else if(key.isWritable()){

                }

            }

        }


    }
}
