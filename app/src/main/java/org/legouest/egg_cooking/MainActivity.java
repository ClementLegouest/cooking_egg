package org.legouest.egg_cooking;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
    private final int BOILED_EGG_COOKING_TIME = 3 * MINUTE_IN_MILLISECONDS;

    private CountDownTimer countDownTimer;
    private long timeLeftInMilliseconds = HARD_EGG_COOKING_TIME;
    private boolean timeIsRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

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
                timeIsRunning = false;
                startButton.setText("STOP");
            }
        }.start();

        startButton.setText("PAUSE");
        timeIsRunning = true;
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