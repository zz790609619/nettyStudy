package nio;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

public class TestBuffer {
    public void byteBufferExplain(){
        /**
         * ByteFuffer 组成
         * position:指针起始位置
         * limit:指针结束位置
         * capacity:容量
         *
         * bytebuffer 使用 循环下列操作
         * 1.声明一个空的bytebuffer
         * 2.写入数据 【同时移动position指针,直至读完将limit指针指向读取最后一个】
         * 3.切换成读模式filp() 【此时position指针指向初始位置，而limit指向读取最后一个(不需要读到capacity位置)】
         * 4.读取bytebuffer 例如 get()  【同时移动position指针,直至读完将limit指针指向的位置】
         * 5.切换成写  compact() 【此时position指针指向未读完后的第一个空位，而limit指向capacity位置】 或 clear() 【此时position指针指向初始位置，而limit指向capacity位置】
         */

    }

    public static void main(String[] args) throws IOException {
        //FileChannel
        //1  输入输出流  2RandomAccessFile
        try(FileChannel channel=new FileInputStream("data.txt").getChannel()) {
            //分配缓冲区
            ByteBuffer byteBuffer=ByteBuffer.allocate(10);//在堆内存里 收到GC影响 读写效率低
            ByteBuffer  byteBufferDir=ByteBuffer.allocateDirect(10);//直接在系统内存  少一次拷贝 分配效率低
            while (true){
                //从channel读 写入buffer
                int i=channel.read(byteBuffer);
                if(i==-1){
                    break;
                }
                //打印buffer
                //1.切换至读模式
                byteBuffer.flip();
                while (byteBuffer.hasRemaining()){ //判断缓冲区是否有未读数据
                    byte b=byteBuffer.get();
                    //get() 会移动position指针  而get(i)不会
                    //rewind() 移动position指针到初始位置
                    //mark() 标记position指针位置 reset()移动position指针到mark得位置
                    System.out.println((char)b);
                }
                byteBuffer.clear();//切换成写模式
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void stringTOByteBuffer(){
        ByteBuffer byteBuffer1=ByteBuffer.allocate(16);
        //1.直接.getBytes() 只是还是写模式 position指针还在o位置 所以如果读byteBuffer则无值
        byteBuffer1.put("hello".getBytes());
        System.out.println(StandardCharsets.UTF_8.decode(byteBuffer1));
        byteBuffer1.flip();
        System.out.println(StandardCharsets.UTF_8.decode(byteBuffer1));
        //2. charset  会切换成读模式 position指针会移到初始位置
        ByteBuffer byteBuffer2=ByteBuffer.allocate(16);
        byteBuffer2= StandardCharsets.UTF_8.encode("hello");
        System.out.println(StandardCharsets.UTF_8.decode(byteBuffer2));
        //2. wrap  会切换成读模式 position指针会移到初始位置
        ByteBuffer byteBuffer3=ByteBuffer.allocate(16);
        byteBuffer3= ByteBuffer.wrap("hello".getBytes());
        System.out.println(StandardCharsets.UTF_8.decode(byteBuffer3));

    }
}
