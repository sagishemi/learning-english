package com.example.sagiproject;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sagiproject.gemini.GeminiCallback;
import com.example.sagiproject.gemini.GeminiManager;

import java.util.ArrayList;

public class GameActivity extends AppCompatActivity {

    private Button[] leftBtns  = new Button[4];
    private Button[] rightBtns = new Button[4];

    private GameBoardView boardView;
    WordRepository wordRepository = new WordRepository();

    private GameManager gameManager;
    private FBsingleton fb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        initViews();

        fb = FBsingleton.getInstance(this);
        gameManager = new GameManager(wordRepository);
        gameManager.startNewGame(GameManager.LEVEL_EASY, "Player");

        fb.setOnDataLoadedListener(() -> runOnUiThread(this::refreshGameData));

        for (int i = 0; i < 4; i++) {
            final int index = i;
            leftBtns[i].setOnClickListener(v -> onLeftClicked(index));
            rightBtns[i].setOnClickListener(v -> onRightClicked(index));
        }
    }

    private void initViews() {
        boardView = findViewById(R.id.boardView);

        for (int i = 0; i < 4; i++) {
            leftBtns[i] = findViewById(getResources().getIdentifier("btnL" + i, "id", getPackageName()));
            rightBtns[i] = findViewById(getResources().getIdentifier("btnR" + i, "id", getPackageName()));
        }
    }

    private void refreshGameData() {
        if (fb.allWords != null && !fb.allWords.isEmpty()) {
            wordRepository.getEasyWords().clear();
            wordRepository.getEasyWords().addAll(fb.allWords);
        }

        if (wordRepository.getEasyWords().size() < fb.currentIndex + 4) {
            requestNewWordsFromAI();
            return;
        }

        RoundData round = gameManager.startRound(fb.currentIndex);
        applyRoundToButtons(round);
    }

    private void requestNewWordsFromAI() {
        String prompt = "Return ONLY 4 pairs of English-Hebrew words. Format: Eng - Heb | Eng - Heb. Avoid: " 
                        + wordListToString(wordRepository.getEasyWords());

        GeminiManager.getInstance().sendMessage(prompt, new GeminiCallback() {
            @Override
            public void onSuccess(String response) {
                runOnUiThread(() -> {
                    ArrayList<WordPair> newPairs = parseRobust(response);
                    if (newPairs.size() >= 4) fb.addWords(newPairs);
                });
            }
            @Override public void onError(Throwable t) { Log.e("Sagi", "AI Error", t); }
            @Override public void onError(Exception e) { Log.e("Sagi", "AI Error", e); }
        });
    }

    private ArrayList<WordPair> parseRobust(String response) {
        ArrayList<WordPair> pairs = new ArrayList<>();
        String clean = response.replaceAll("\\*", "").replace("\n", " ").replaceAll("\\d+\\.", "");
        for (String p : clean.split("\\|")) {
            String[] split = p.split("-");
            if (split.length == 2) pairs.add(new WordPair(split[0].trim(), split[1].trim()));
        }
        return pairs;
    }

    private void onRightClicked(int index) {
        MatchResult result = gameManager.selectRight(index);
        handleMatchVisuals(result);

        if (result.isRoundFinished) {
            fb.setScore(fb.currentIndex + 4);
            Toast.makeText(this, "מצוין! עוברים הלאה.", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleMatchVisuals(MatchResult result) {
        for (Button b : leftBtns) b.setBackgroundResource(R.drawable.button_background);
        if (result.leftIndex != null && result.rightIndex != null) {
            boardView.addAnimatedLineBetweenButtons(leftBtns[result.leftIndex], rightBtns[result.rightIndex], result.isCorrect);
            if (result.isCorrect) {
                leftBtns[result.leftIndex].setEnabled(false);
                rightBtns[result.rightIndex].setEnabled(false);
                leftBtns[result.leftIndex].setAlpha(0.5f);
                rightBtns[result.rightIndex].setAlpha(0.5f);
            }
        }
    }

    private void applyRoundToButtons(RoundData round) {
        for (int i = 0; i < 4; i++) {
            leftBtns[i].setText(round.left[i]);
            leftBtns[i].setEnabled(true);
            leftBtns[i].setAlpha(1f);
            leftBtns[i].setBackgroundResource(R.drawable.button_background);
            rightBtns[i].setText(round.right[i]);
            rightBtns[i].setEnabled(true);
            rightBtns[i].setAlpha(1f);
            rightBtns[i].setBackgroundResource(R.drawable.button_background);
        }
        boardView.resetLines();
    }

    private void onLeftClicked(int index) {
        gameManager.selectLeft(index);
        for (Button b : leftBtns) {
            if (b.isEnabled()) b.setBackgroundResource(R.drawable.button_background);
        }
        leftBtns[index].setBackgroundResource(R.drawable.button_selected);
    }


    public static String wordListToString(ArrayList<WordPair> list) {
        StringBuilder sb = new StringBuilder();
        for (WordPair wp : list) sb.append(wp.english).append(", ");
        return sb.toString();
    }
}
