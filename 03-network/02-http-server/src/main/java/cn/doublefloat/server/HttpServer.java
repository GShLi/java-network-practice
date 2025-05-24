package cn.doublefloat.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Double
 * @since 2025/5/24 21:05
 * @version 1.0
 */
public class HttpServer {
  public static void main(String[] args) throws IOException {

    ExecutorService executor = Executors.newFixedThreadPool(2);

    try (ServerSocketChannel ssc = ServerSocketChannel.open()) {
      ssc.bind(new InetSocketAddress(8080));
      // 设置非阻塞模式
      ssc.configureBlocking(false);

      // 为 ssc 注册选择器
      Selector selector = Selector.open();
      ssc.register(selector, SelectionKey.OP_ACCEPT);

      while (true) {
        if (selector.select(3000) == 0) {
          continue;
        }

        Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
        while (iterator.hasNext()) {
          SelectionKey key = iterator.next();
          // 不能提交到线程池处理, key 会被 cancel
          // executor.execute(new HttpHandler(key));
          // 开启新线程处理请求
          new HttpHandler(key).run();
          iterator.remove();
        }
      }
    }
  }

  public static class HttpHandler implements Runnable {

    private int bufferSize = 1024;

    private String localCharset = "UTF-8";

    private SelectionKey key;

    public HttpHandler(SelectionKey key) {
      this.key = key;
    }

    public void handleRead() throws IOException {
      // 获取 channel
      SocketChannel sc = (SocketChannel) key.channel();
      // 获取 buffer 并重置
      ByteBuffer buffer = (ByteBuffer) key.attachment();
      buffer.clear();

      if (sc.read(buffer) == -1) {
        sc.close();
      } else {
        buffer.flip();
        String receivedString =
            Charset.forName(localCharset).newDecoder().decode(buffer).toString();
        if (receivedString.isEmpty()) {
          return;
        }
        String[] requestMessage = receivedString.split("\r\n");
        for (String line : requestMessage) {
          System.out.println(line);
          if (line.isEmpty()) {
            break;
          }
        }
        // 请求类型
        String[] firstLine = requestMessage[0].split(" ");
        System.out.println("Method: " + firstLine[0]);
        System.out.println("Url: " + firstLine[1]);
        System.out.println("HTTP Version: " + firstLine[2]);
        StringBuilder responseBuilder = new StringBuilder();
        responseBuilder.append("HTTP/1.1 200 OK\r\n");
        responseBuilder
            .append("Content-Type: text/html;charset=")
            .append(localCharset)
            .append("\r\n")
            .append("\r\n");
        responseBuilder.append("<!DOCTYPE html>\r\n");
        responseBuilder.append("<html>\r\n");
        responseBuilder.append("<head>\r\n");
        responseBuilder.append("<meta charset=\"").append(localCharset).append("\">\r\n");
        responseBuilder.append("<title>显示报文</title>\r\n");
        responseBuilder.append("</head>\r\n");
        responseBuilder.append("<body>\r\n");
        responseBuilder.append("<h1>显示报文</h1>\r\n");
        for (String line : requestMessage) {
          responseBuilder.append(line).append("<br>");
        }
        responseBuilder.append("</body>\r\n");
        responseBuilder.append("</html>\r\n");
        responseBuilder.append("\r\n");
        buffer = ByteBuffer.wrap(responseBuilder.toString().getBytes(localCharset));
        sc.write(buffer);
        sc.close();
      }
    }

    @Override
    public void run() {
      try {
        if (key.isAcceptable()) {
          handleAccept();
        }
        if (key.isReadable()) {
          handleRead();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    private void handleAccept() throws IOException {
      SocketChannel sc = ((ServerSocketChannel) key.channel()).accept();
      if (sc != null) {
        sc.configureBlocking(false);
        sc.register(key.selector(), SelectionKey.OP_READ, ByteBuffer.allocate(bufferSize));
      }
    }
  }
}
