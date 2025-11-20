package org.hehe.sirthirrygolooo;

import java.io.*;
import java.net.*;

public class ClientTest {
    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 65432;
    private static final String TEST_AUDIO_PATH = "C:\\Users\\jbfro\\OneDrive - Université de Franche-Comté (univ-fcomte.fr)\\Info\\S5\\Saé\\ServeurVoix\\src\\main\\java\\org\\hehe\\sirthirrygolooo\\test_audio.wav";

    public static void main(String[] args) {
        try (
                Socket socket = new Socket(SERVER_IP, SERVER_PORT);
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                DataInputStream dis = new DataInputStream(socket.getInputStream());
                FileInputStream fis = new FileInputStream(TEST_AUDIO_PATH)
        ) {
            // 1. Envoyer la taille du fichier
            long fileSize = new File(TEST_AUDIO_PATH).length();
            dos.writeLong(fileSize);

            // 2. Envoyer le fichier
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                dos.write(buffer, 0, bytesRead);
            }

            // 3. Recevoir le résultat
            String result = dis.readUTF();
            System.out.println("Résultat de l'analyse : " + result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
