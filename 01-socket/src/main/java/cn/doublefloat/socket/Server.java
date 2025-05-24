package cn.doublefloat;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Double
 * @since 2025/5/24 20:03
 * @version 1.0
 */
public class Server {

  public static void main(String[] args) {

    try {
      ServerSocket serverSocket = new ServerSocket(8080);
      Socket socket = serverSocket.accept();
      BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      String line = reader.readLine();
      System.out.println("socket receive: " + line);

      PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
      printWriter.println("received data: " + line);
      printWriter.flush();
      printWriter.close();
      reader.close();
      socket.close();
      serverSocket.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
