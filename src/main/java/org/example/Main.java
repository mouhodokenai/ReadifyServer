package org.example;

import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import org.snf4j.core.SelectorLoop;
import org.snf4j.core.factory.AbstractSessionFactory;
import org.snf4j.core.handler.IStreamHandler;

public class Main {
    static final String PREFIX = "192.168.43.100";
    static final int PORT = Integer.getInteger(PREFIX+"Port", 8006);

    public static void main(String[] args) throws Exception {
        SelectorLoop loop = new SelectorLoop();

        try {
            loop.start();
            // Initialize the listener
            ServerSocketChannel channel = ServerSocketChannel.open();
            channel.configureBlocking(false);
            channel.socket().bind(new InetSocketAddress(PORT));
            // Register the listener
            loop.register(channel, new AbstractSessionFactory() {
                @Override
                protected IStreamHandler createHandler(SocketChannel channel) {
                    ServerHandler serverHandler = null;
                    try {
                        serverHandler = new ServerHandler();
                        System.out.println("Новый клиент");
                    }
                    catch (Exception e){
                        System.out.println("нет подключения");
                        e.printStackTrace();
                    }
                    return serverHandler;
                }
            }).sync();

            loop.join();
        }
        finally {

            loop.stop();
            System.out.println("Сервер закрыт");
        }
    }
}