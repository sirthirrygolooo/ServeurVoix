package org.hehe.sirthirrygolooo.steps;

import io.cucumber.java.en.*;
import org.hehe.sirthirrygolooo.model.AudioFeatures;
import org.hehe.sirthirrygolooo.service.AnalysisService;
import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import javax.sound.sampled.*;

public class AnalysisSteps {
    private AnalysisService service;
    private AudioFeatures result;
    private byte[] audioData;

    @Given("le service d'analyse est prêt")
    public void le_service_est_pret() {
        service = new AnalysisService();
    }

    @When("je traite un fichier audio contenant du silence")
    public void je_traite_silence() throws Exception {
        // Génération d'un WAV silencieux de 1 seconde à la volée
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        AudioFormat format = new AudioFormat(44100, 16, 1, true, false);
        byte[] silence = new byte[44100 * 2]; // 1 sec de 0

        // Ecriture des headers WAV (Nécessaire pour Tarsos)
        try (AudioInputStream ais = new AudioInputStream(new ByteArrayInputStream(silence), format, silence.length / 2)) {
            AudioSystem.write(ais, AudioFileFormat.Type.WAVE, out);
        }
        audioData = out.toByteArray();
        result = service.analyze(audioData);
    }

    @Then("le résultat doit indiquer un ratio de silence supérieur à {double}")
    public void check_silence_ratio(Double seuil) {
        assertTrue("Le ratio silence doit être élevé", result.getSilenceRatio() > seuil);
    }

    @Then("le résultat ne doit pas être null")
    public void check_not_null() {
        assertNotNull(result);
    }
}