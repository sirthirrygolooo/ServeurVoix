package org.hehe.sirthirrygolooo;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class ServeurVoix {
    private static final int PORT = 65432;
    private static final String SAVE_DIR = "received_audio/";
    // Utilisation d'un ThreadPool pour éviter de surcharger le serveur
    private static final ExecutorService pool = Executors.newFixedThreadPool(10);

    public static void main(String[] args) {
        ensureDirectoryExists(SAVE_DIR);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("[+] Serveur IA démarré sur le port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("[*] Connexion entrante : " + clientSocket.getInetAddress());
                // On délègue le travail au pool de threads
                pool.execute(new ClientHandler(clientSocket));
            }
        } catch (IOException e) {
            System.err.println("[-] Erreur critique du serveur : " + e.getMessage());
        }
    }

    private static void ensureDirectoryExists(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            String fileName = "audio_" + System.currentTimeMillis() + ".wav";
            String filePath = SAVE_DIR + fileName;

            try (
                    DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
                    DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream())
            ) {
                // 1. Réception de la taille (Protocole: d'abord un Long)
                long fileSize = dis.readLong();
                System.out.println("Taille fichier : " + fileSize + " bytes");

                // 2. Sauvegarde du fichier binaire
                try (FileOutputStream fos = new FileOutputStream(filePath)) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    long remaining = fileSize;

                    while (remaining > 0 && (bytesRead = dis.read(buffer, 0, (int) Math.min(buffer.length, remaining))) != -1) {
                        fos.write(buffer, 0, bytesRead);
                        remaining -= bytesRead;
                    }
                }

                System.out.println("[+] Fichier sauvegardé : " + filePath);

                // 3. Appel au script Python (IA)
                String iaResponse = callPythonScript(filePath);

                dos.writeUTF(iaResponse);
                dos.flush();
                System.out.println("[->] Réponse envoyée au client : " + iaResponse);

            } catch (IOException e) {
                System.err.println("[-] Erreur communication client : " + e.getMessage());
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) { /* Ignored */ }
            }
        }

        private String callPythonScript(String audioPath) {
            try {
                ProcessBuilder pb = new ProcessBuilder("python", "script_ia.py", audioPath);
                pb.redirectErrorStream(true); // Fusionne stdout et stderr pour le debug

                Process process = pb.start();

                // Lecture de la sortie du script Python
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                StringBuilder output = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line);
                }

                int exitCode = process.waitFor();
                if (exitCode == 0) {
                    return output.toString(); // Le résultat de l'IA
                } else {
                    return "Erreur IA (Code " + exitCode + "): " + output.toString();
                }

            } catch (Exception e) {
                e.printStackTrace();
                return "Erreur Interne Serveur lors de l'analyse IA";
            }
        }
    }
}