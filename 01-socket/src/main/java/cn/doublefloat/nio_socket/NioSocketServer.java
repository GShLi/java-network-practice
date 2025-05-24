package cn.doublefloat.nio_socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.Iterator;

/**
 * @author Double
 * @since 2025/5/24 20:15
 * @version 1.0
 */
public class NioSocketServer {

  public static void main(String[] args) {
    try {
      // 创建 Server Socket Channel 并监听 8080 端口
      ServerSocketChannel ssc = ServerSocketChannel.open();
      ssc.bind(new InetSocketAddress("127.0.0.1", 8080));
      // 设置为非阻塞模式
      ssc.configureBlocking(false);

      // 为 Server Socket Channel 注册选择器
      Selector selector = Selector.open();
      ssc.register(selector, SelectionKey.OP_ACCEPT);

      Handler handler = new Handler();
      while (true) {

        if (selector.select(3000) == 0) {
          System.out.println("等待请求超时...");
          continue;
        }
        System.out.println("处理请求...");
        Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
        while (iterator.hasNext()) {
          SelectionKey key = iterator.next();
          try {
            if (key.isAcceptable()) {
              handler.handleAccept(key);
            }
            if (key.isReadable()) {
              handler.handleRead(key);
            }
          } catch (Exception e) {
            iterator.remove();
            e.printStackTrace();
            continue;
          }
          iterator.remove();
        }
      }

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static class Handler {

    private int bufferSize = 1024;

    private String localCharset = "UTF-8";

    public Handler() {}

    public Handler(int bufferSize, String localCharset) {
      if (bufferSize >= 0) {
        this.bufferSize = bufferSize;
      }
      if (localCharset != null) {
        this.localCharset = localCharset;
      }
    }

    public void handleAccept(SelectionKey key) throws IOException {
      SocketChannel sc = ((ServerSocketChannel) key.channel()).accept();
      sc.configureBlocking(false);
      sc.register(key.selector(), SelectionKey.OP_READ, ByteBuffer.allocate(bufferSize));
    }

    public void handleRead(SelectionKey key) throws IOException {
      // 获取 channel
      try (SocketChannel sc = (SocketChannel) key.channel(); ) {

        ByteBuffer buffer = (ByteBuffer) key.attachment();
        buffer.clear();
        if (sc.read(buffer) != -1) {
          buffer.flip();
          String receivedString =
              Charset.forName(localCharset).newDecoder().decode(buffer).toString();
          System.out.println("received from client: " + receivedString);
          String sendString = "received data: " + receivedString;
          buffer = ByteBuffer.wrap(sendString.getBytes(localCharset));
          sc.write(buffer);
        }
      }
    }
  }
}
