package org.hehe.sirthirrygolooo;

import java.io.*;
import java.net.*;
import java.nio.file.*;

public class ServeurVoix {
    private static final int PORT = 65432;
    private static final String SAVE_DIR = "received_audio/";

    public static void main(String[] args) {
        new File(SAVE_DIR).mkdirs();

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("[+] Serveur démarré sur le port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("[*] Nouveau client connecté : " + clientSocket.getInetAddress());

                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try (
                    DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
                    DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream())
            ) {
                // on lit la taille du fichier
                long fileSize = dis.readLong();
                // on génère un nom avec un timestamp pour éviter les problèmes
                String fileName = "audio_" + System.currentTimeMillis() + ".wav";
                String filePath = SAVE_DIR + fileName;

                // lecture + sauvegarde du fichier
                try (FileOutputStream fos = new FileOutputStream(filePath)) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    long remaining = fileSize;

                    while (remaining > 0 && (bytesRead = dis.read(buffer, 0, (int) Math.min(buffer.length, remaining))) != -1) {
                        fos.write(buffer, 0, bytesRead);
                        remaining -= bytesRead;
                    }
                }

                // simulation d'analyse tant que ml pas ops
                String analysisResult = analyzeAudio(filePath);

                dos.writeUTF(analysisResult);
                dos.flush();

                System.out.println("[*] Fichier reçu et analysé : " + fileName);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private String analyzeAudio(String filePath) {
            // TODO: Faut implémenter une analyse audio
            return "Analyse ok : " + filePath;
        }
    }
}
