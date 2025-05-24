package cn.doublefloat.socket;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * @author Double
 * @since 2025/5/24 20:07
 * @version 1.0
 */
public class Client {
  public static void main(String[] args) {
    try {
      Socket socket = new Socket("127.0.0.1", 8080);
      PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
      BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      printWriter.println("Client Data!");
      printWriter.flush();
      String str = reader.readLine();
      System.out.println("received from server: " + str);
      printWriter.close();
      reader.close();
      socket.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
