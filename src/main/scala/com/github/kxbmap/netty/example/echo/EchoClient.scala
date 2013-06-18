package com.github.kxbmap.netty.example
package echo

import io.netty.bootstrap.Bootstrap
import io.netty.channel.socket.SocketChannel
import java.net.InetSocketAddress


object EchoClient extends App with Usage {
  val (host, port, firstMessageSize) =
    parseOptions("<host> <port> [<first message size>]") {
      case List(h, p, s) => (h, p.toInt, s.toInt)
      case List(h, p) => (h, p.toInt, 256)
    }

  // Configure the client
  val group = new DefaultEventLoopGroup()
  try {
    val b = new Bootstrap()
      .group(group)
      .channel(classOf[DefaultSocketChannel])
      .remoteAddress(new InetSocketAddress(host, port))
      .handler { ch: SocketChannel =>
        ch.pipeline().addLast(
          // new LoggingHandler(LogLevel.INFO),
          new EchoClientHandler(firstMessageSize))
      }

    // Start the client.
    val f = b.connect().sync()

    // Wait until the connection is closed.
    f.channel().closeFuture().sync()
  }
  // Shut down the event loop to terminate all threads.
  finally group.shutdownGracefully()
}
