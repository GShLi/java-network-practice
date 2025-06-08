package cn.doublefloat.io.byte_stream;

import java.io.*;

/** 字节流案例 */
public class ByteStreamDemo {
  public static void main(String[] args) throws IOException {
    String filePath = "/Users/double/Desktop/技能表.txt";
    try (InputStream is = new FileInputStream(filePath)) {
      System.out.println("可读取字节数: " + is.available());
      int content;
      while ((content = is.read()) != -1) {
        System.out.println(content);
      }
    }

    String copyFilePath = "/Users/double/Desktop/技能表-copy.txt";
    try (FileInputStream is = new FileInputStream(filePath);
        OutputStream os = new FileOutputStream(copyFilePath)) {
      int content;
      while ((content = is.read()) != -1) {
        os.write(content);
      }
    }
  }
}
