package org.hehe.sirthirrygolooo.service;

import org.hehe.sirthirrygolooo.model.AudioFeatures;
import be.tarsos.dsp.*;
import be.tarsos.dsp.io.jvm.JVMAudioInputStream;
import be.tarsos.dsp.mfcc.MFCC;
import be.tarsos.dsp.pitch.PitchProcessor;
import be.tarsos.dsp.pitch.PitchProcessor.PitchEstimationAlgorithm;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

public class AnalysisService {

    public AudioFeatures analyze(byte[] audioBytes) throws Exception {
        AudioFeatures features = new AudioFeatures();
        List<Float> pitches = new ArrayList<>();
        List<float[]> mfccs = new ArrayList<>();
        List<Double> rmsList = new ArrayList<>();

        ByteArrayInputStream bais = new ByteArrayInputStream(audioBytes);
        AudioInputStream stream = AudioSystem.getAudioInputStream(bais);
        AudioDispatcher dispatcher = new AudioDispatcher(new JVMAudioInputStream(stream), 1024, 512);

        // Pitch
        dispatcher.addAudioProcessor(new PitchProcessor(PitchEstimationAlgorithm.YIN, 44100, 1024, (res, e) -> {
            if (res.getPitch() != -1) pitches.add(res.getPitch());
        }));

        // MFCC & RMS
        final MFCC mfccAlgo = new MFCC(1024, 44100f, 13, 40, 300f, 3000f);
        dispatcher.addAudioProcessor(mfccAlgo);
        dispatcher.addAudioProcessor(new AudioProcessor() {
            @Override
            public boolean process(AudioEvent audioEvent) {
                mfccs.add(mfccAlgo.getMFCC());
                rmsList.add(audioEvent.getRMS());
                return true;
            }
            @Override
            public void processingFinished() {}
        });

        dispatcher.run();

        // Calculs Statistiques
        features.setPitchMean(calculateMeanFloat(pitches));
        features.setPitchStdDev(calculateStdDevFloat(pitches, calculateMeanFloat(pitches)));
        features.setRmsMean(calculateMeanDouble(rmsList));

        long silenceCount = rmsList.stream().filter(rms -> rms < 0.01).count();
        features.setSilenceRatio((double) silenceCount / (rmsList.isEmpty() ? 1 : rmsList.size()));

        features.setMfccMean(new double[13]);
        if (!mfccs.isEmpty()) {
            for (float[] frameMfcc : mfccs) {
                for (int i = 0; i < frameMfcc.length; i++) features.mfccMean[i] += frameMfcc[i];
            }
            for (int i = 0; i < features.mfccMean.length; i++) features.mfccMean[i] /= mfccs.size();
        }

        return features;
    }

    // Helpers Math (Private)
    private double calculateMeanFloat(List<Float> v) { double s=0; for(Float x:v) s+=x; return v.isEmpty()?0:s/v.size(); }
    private double calculateStdDevFloat(List<Float> v, double m) { double s=0; for(Float x:v) s+=Math.pow(x-m,2); return v.isEmpty()?0:Math.sqrt(s/v.size()); }
    private double calculateMeanDouble(List<Double> v) { double s=0; for(Double x:v) s+=x; return v.isEmpty()?0:s/v.size(); }
}