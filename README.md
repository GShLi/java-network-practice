# Java 网络编程练习项目

## 01-socket

Java 的 Socket 分为普通 Socket 和 NioSocket 两种。

### 普通 Socket

Java 中的网络通信使用 Socket 实现，Socket 分为 ServerSocket 和 Socket 两种。

其中 ServerSocket 用于服务端，监听指定端口，接收请求完成处理后并返回 Socket。

Socket 则作为客户端，向 ServerSocket 发送请求。
