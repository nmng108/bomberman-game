package uet.oop.bomberman.Base;

// Java program to play an Audio
// file using Clip Object
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Sound {

    // to store current position
    private Long currentFrame;
    private Clip clip;

    private String file_path;
    // current status of clip
    private String status;

    private boolean hasLoop;

    private AudioInputStream audioInputStream;

    // constructor to initialize streams and clip
    public Sound(String file, boolean hasLoop) throws UnsupportedAudioFileException,
            IOException, LineUnavailableException {
        file_path = "res/sounds/" + file + ".wav";
        // create AudioInputStream object
        audioInputStream = AudioSystem.getAudioInputStream(new File(file_path).getAbsoluteFile());

        // create clip reference
        clip = AudioSystem.getClip();

        // open audioInputStream to the clip
        clip.open(audioInputStream);

        this.hasLoop = hasLoop;
        if (hasLoop) clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    // Method to play the audio
    public void play() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                clip.start();
            }
        }).start();
    }

    // Method to pause the audio
    public void pause() {
        this.currentFrame = this.clip.getMicrosecondPosition();
        new Thread(new Runnable() {
            @Override
            public void run() {
                clip.stop();
            }
        }).start();
    }

    // Method to resume the audio
    public void resumeAudio() {
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    clip.close();
                    resetAudioStream();
                    clip.setMicrosecondPosition(currentFrame);
                    play();
                }
            }).start();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to restart the audio
    public void restart() {
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    clip.stop();
                    clip.close();
                    resetAudioStream();
                    currentFrame = 0L;
                    clip.setMicrosecondPosition(0);
                    play();
                }
            }).start();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to stop the audio
    public void stop() {
        currentFrame = 0L;
        new Thread(new Runnable() {
            @Override
            public void run() {
                //start the clip
                clip.stop();
                clip.close();
            }
        }).start();
    }

    // Method to reset audio stream
    public void resetAudioStream() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    audioInputStream = AudioSystem.getAudioInputStream(
                            new File(file_path).getAbsoluteFile());
                    clip.open(audioInputStream);
                    if (hasLoop) clip.loop(Clip.LOOP_CONTINUOUSLY);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
