package org.legouest.egg_cooking;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private TextView countdownText;
    private Button startButton;
    private Button hardEggButton; // Oeuf dur // 8 minutes
    private Button pochedEggButton; // Oeuf mollet // 6 minutes
    private Button poachedEggButton; // Oeuf poché // 4 minutes
    private Button boiledEggButton; // Oeuf à la coque // 3 minutes

    private final int MINUTE_IN_MILLISECONDS = 60000;
    private final int HARD_EGG_COOKING_TIME = 8 * MINUTE_IN_MILLISECONDS;
    private final int POCHED_EGG_COOKING_TIME = 6 * MINUTE_IN_MILLISECONDS;
    private final int POACHED_EGG_COOKING_TIME = 4 * MINUTE_IN_MILLISECONDS;
    private final double BOILED_EGG_COOKING_TIME = 0.1 * MINUTE_IN_MILLISECONDS;

    private CountDownTimer countDownTimer;
    private MediaPlayer mediaPlayer;
    private long timeLeftInMilliseconds = HARD_EGG_COOKING_TIME;
    private boolean timeIsRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        countdownText = findViewById(R.id.countdown_text);
        startButton = findViewById(R.id.start_button);
        hardEggButton = findViewById(R.id.hard_egg_button);
        pochedEggButton = findViewById(R.id.poched_egg_button);
        poachedEggButton = findViewById(R.id.poached_egg_button);
        boiledEggButton = findViewById(R.id.boiled_egg_button);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startStop();
            }
        });

        hardEggButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TEST", "onClick: Oeufs dur");
                timeLeftInMilliseconds = HARD_EGG_COOKING_TIME;
                updateTimer();
            }
        });

        pochedEggButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TEST", "onClick: Ouefs mollets");
                timeLeftInMilliseconds = POCHED_EGG_COOKING_TIME;
                updateTimer();
            }
        });

        poachedEggButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeLeftInMilliseconds = POACHED_EGG_COOKING_TIME;
                updateTimer();
            }
        });

        boiledEggButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeLeftInMilliseconds = (long) BOILED_EGG_COOKING_TIME;
                updateTimer();
            }
        });

        updateTimer();
    }

    private void startStop() {
        if(timeIsRunning) {
            enableButtons();
            stopTimer();
        } else if(startButton.getText() == "STOP") {
            mediaPlayer.stop();
            enableButtons();
            resetTimer();
        } else {
            disableButtons();
            startTimer();
        }
    }

    private void enableButtons() {
        hardEggButton.setClickable(true);
        pochedEggButton.setClickable(true);
        poachedEggButton.setClickable(true);
        boiledEggButton.setClickable(true);
    }

    private void disableButtons() {
        hardEggButton.setClickable(false);
        pochedEggButton.setClickable(false);
        poachedEggButton.setClickable(false);
        boiledEggButton.setClickable(false);
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMilliseconds, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMilliseconds = millisUntilFinished;
                updateTimer();
            }

            @Override
            public void onFinish() {
                showToastMessage("Cuisson terminée");
                try {
                    playRingtone();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                timeIsRunning = false;
                startButton.setText("STOP");
            }
        }.start();

        startButton.setText("PAUSE");
        timeIsRunning = true;
    }

    private void playRingtone() throws IOException {
        Context context = getApplicationContext();

        mediaPlayer = new MediaPlayer();
        AssetFileDescriptor afd = context.getResources().openRawResourceFd(R.raw.short_chicken_song);
        mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
        afd.close();

        if (Build.VERSION.SDK_INT >= 21) {
            AudioAttributes audioAttributes = new AudioAttributes
                    .Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            mediaPlayer.setAudioAttributes(audioAttributes);
        } else {
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
        }

        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                try {
                    mediaPlayer.prepare();
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return false;
            }

            @Override
            protected void onPostExecute(Boolean prepared) {
                if (prepared) {
                    mediaPlayer.start();
                }
            }
        }.execute();
    }

    private void showToastMessage(String message) {
        Context context = getApplicationContext();
        CharSequence text = message;
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    private void stopTimer() {
        countDownTimer.cancel();

        startButton.setText("START");
        timeIsRunning = false;
    }

    private void resetTimer() {
        countDownTimer.cancel();

        timeLeftInMilliseconds = HARD_EGG_COOKING_TIME;
        startButton.setText("START");
        updateTimer();
    }

    private void updateTimer() {
        int minutes = (int) timeLeftInMilliseconds / MINUTE_IN_MILLISECONDS;
        int seconds = (int) timeLeftInMilliseconds % MINUTE_IN_MILLISECONDS / 1000;

        String timeLeftText;
        timeLeftText = "" + minutes;
        timeLeftText += ":";
        if (seconds < 10) timeLeftText += "0";
        timeLeftText += seconds;

        countdownText.setText(timeLeftText);
    }
}