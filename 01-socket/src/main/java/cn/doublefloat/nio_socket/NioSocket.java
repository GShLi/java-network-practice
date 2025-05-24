package cn.doublefloat.nio_socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

/**
 * @author Double
 * @since 2025/5/24 20:15
 * @version 1.0
 */
public class NioSocket {

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


    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
