package test;

import javax.sound.sampled.*;

public class AudioFormatChecker {
    public static void main(String[] args) {
        Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
        for (Mixer.Info info : mixerInfos) {
            Mixer mixer = AudioSystem.getMixer(info);
            Line.Info[] lineInfos = mixer.getTargetLineInfo();
            for (Line.Info lineInfo : lineInfos) {
                if (lineInfo instanceof DataLine.Info) {
                    DataLine.Info dataLineInfo = (DataLine.Info) lineInfo;
                    AudioFormat[] formats = dataLineInfo.getFormats();
                    System.out.println("Mixer: " + info.getName());
                    for (AudioFormat format : formats) {
                        System.out.println("Supported format: " + format);
                    }
                }
            }
        }
    }
}