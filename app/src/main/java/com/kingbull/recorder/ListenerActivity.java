package com.kingbull.recorder;

import android.media.AudioFormat;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import omrecorder.AudioChunk;
import omrecorder.AudioRecordConfig;
import omrecorder.PullTransport;
import omrecorder.PullableSource;

public class ListenerActivity extends AppCompatActivity {
    final int SAMPLE_RATE = 8000;
    //Recorder recorder;
    ImageView recordButton;
    CheckBox skipSilence;
    OutputStream audioStream;
    AudioChunk m_audioChunk;
    PullTransport pullTransport;
    private Button pauseResumeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recorder);
        getSupportActionBar().setTitle("Listener" + "  " + SAMPLE_RATE + "Hz");
        setupListener();
        skipSilence = findViewById(R.id.skipSilence);
        skipSilence.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    //setupNoiseListener();
                } else {
                    setupListener();
                }
            }
        });
        recordButton = findViewById(R.id.recordButton);
        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //recorder.startRecording();
                startRecording();
                skipSilence.setEnabled(false);
            }
        });
        findViewById(R.id.stopButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //recorder.stopRecording();
                stopRecording();
                skipSilence.setEnabled(true);
                recordButton.post(new Runnable() {
                    @Override
                    public void run() {
                        animateVoice(0);
                    }
                });
            }
        });
        pauseResumeButton = findViewById(R.id.pauseResumeButton);
        pauseResumeButton.setOnClickListener(new View.OnClickListener() {
            boolean isPaused = false;

            @Override
            public void onClick(View view) {
                /*if (recorder == null) {
                    Toast.makeText(ListenerActivity.this, "Please start recording first!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }*/
                if (!isPaused) {
                    pauseResumeButton.setText(getString(R.string.resume_recording));
                    //recorder.pauseRecording();
                    pauseRecording();
                    pauseResumeButton.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            animateVoice(0);
                        }
                    }, 100);
                } else {
                    pauseResumeButton.setText(getString(R.string.pause_recording));
                    //recorder.resumeRecording();
                    resumeRecording();
                }
                isPaused = !isPaused;
            }
        });

    }

    private void setupListener() {
        try {
            audioStream = new FileOutputStream(file());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        pullTransport = new PullTransport.Default(mic(), new PullTransport.OnAudioChunkPulledListener() {
            @Override
            public void onAudioChunkPulled(AudioChunk audioChunk) {
                m_audioChunk = audioChunk;
                animateVoice((float) (audioChunk.maxAmplitude() / 200.0));
            }
        });
    }

    private void startRecording() {
        try {
            pullTransport.start(audioStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (IllegalStateException e) {
            throw new RuntimeException("AudioRecord state has uninitialized state", e);
        }
    }

    private void pauseRecording() {

    }

    private void resumeRecording() {

    }

    private void stopRecording() {
        try {
            pullTransport.stop();
            audioStream.flush();
            audioStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void animateVoice(final float maxPeak) {
        recordButton.animate().scaleX(1 + maxPeak).scaleY(1 + maxPeak).setDuration(10).start();
    }

    private PullableSource mic() {
        return new PullableSource.Default(
                new AudioRecordConfig.Default(
                        MediaRecorder.AudioSource.MIC, AudioFormat.ENCODING_PCM_16BIT,
                        AudioFormat.CHANNEL_IN_MONO, SAMPLE_RATE
                )
        );
    }

    @NonNull
    private File file() {
        return new File(Environment.getExternalStorageDirectory(), "listener.wav");
    }

}

