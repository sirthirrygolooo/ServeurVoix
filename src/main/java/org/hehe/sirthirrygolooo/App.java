package org.hehe.sirthirrygolooo;

import org.hehe.sirthirrygolooo.config.AppConfig;
import org.hehe.sirthirrygolooo.controller.ClientHandler;
import java.net.ServerSocket;
import java.util.concurrent.Executors;

public class App {
    public static void main(String[] args) throws Exception {
        var pool = Executors.newFixedThreadPool(10);
        try (var server = new ServerSocket(AppConfig.LISTEN_PORT)) {
            System.out.println("Serveur Audio démarré sur " + AppConfig.LISTEN_PORT);
            while (true) pool.execute(new ClientHandler(server.accept()));
        }
    }
}
