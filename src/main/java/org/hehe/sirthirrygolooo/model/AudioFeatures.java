package org.hehe.sirthirrygolooo.model;

public class AudioFeatures {
    private double pitchMean;
    private double pitchStdDev;
    private double rmsMean;
    private double silenceRatio;
    public double[] mfccMean;

    // Setters et getter pour manip. des données
    public void setPitchMean(double pitchMean) { this.pitchMean = pitchMean; }
    public void setPitchStdDev(double pitchStdDev) { this.pitchStdDev = pitchStdDev; }
    public void setRmsMean(double rmsMean) { this.rmsMean = rmsMean; }
    public void setSilenceRatio(double silenceRatio) { this.silenceRatio = silenceRatio; }
    public void setMfccMean(double[] mfccMean) { this.mfccMean = mfccMean; }


    public double getSilenceRatio() { return silenceRatio; }

    public String toJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"pitchMean\":").append(pitchMean).append(",");
        sb.append("\"pitchStdDev\":").append(pitchStdDev).append(",");
        sb.append("\"rmsMean\":").append(rmsMean).append(",");
        sb.append("\"silenceRatio\":").append(silenceRatio).append(",");
        sb.append("\"mfccMean\":[");
        if (mfccMean != null) {
            for (int i = 0; i < mfccMean.length; i++) {
                sb.append(mfccMean[i]);
                if (i < mfccMean.length - 1) sb.append(",");
            }
        }
        sb.append("]}");
        return sb.toString();
    }
}