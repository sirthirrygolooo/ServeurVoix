package org.hehe.sirthirrygolooo.controller;

import org.hehe.sirthirrygolooo.config.AppConfig;
import org.hehe.sirthirrygolooo.model.AudioFeatures;
import org.hehe.sirthirrygolooo.service.AnalysisService;
import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final AnalysisService analysisService;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
        this.analysisService = new AnalysisService();
    }

    @Override
    public void run() {
        try (DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
             DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream())) {

            // Lecture données
            long size = dis.readLong();
            if (size > 10 * 1024 * 1024) throw new IOException("Fichier trop lourd");
            byte[] data = new byte[(int) size];
            dis.readFully(data);

            // Traitement via appel fnc d'analyses
            AudioFeatures features = analysisService.analyze(data);
            String json = features.toJson();

            // Envoi vers le serv TCP
            sendToCentral(json);

            // ACK pour client (mobile)
            dos.writeUTF("ACK");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendToCentral(String json) {
        try (Socket s = new Socket(AppConfig.CENTRAL_HOST, AppConfig.CENTRAL_PORT);
             DataOutputStream out = new DataOutputStream(s.getOutputStream())) {
            out.writeUTF(json);
        } catch (IOException e) {
            System.err.println("Erreur Central: " + e.getMessage());
        }
    }
}