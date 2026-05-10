package com.example.sagiproject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameManager {
    public static final int LEVEL_EASY = 1, LEVEL_HARD = 2;

    public Player player;
    public RoundData currentRound;
    public int matchedCount = 0;
    
    private final WordRepository wordRepository;
    private int selectedLeftIndex = -1;
    private final boolean[] matchedLeft = new boolean[4], matchedRight = new boolean[4];
    private int level;

    public GameManager(WordRepository repository) {
        this.wordRepository = repository;
    }

    public void startNewGame(int level, String name) {
        this.level = level;
        this.player = new Player(name);
    }

    public RoundData startRound(int startIndex) {
        selectedLeftIndex = -1;
        matchedCount = 0;
        for (int i = 0; i < 4; i++) matchedLeft[i] = matchedRight[i] = false;

        List<WordPair> all = wordRepository.getWordsByLevel(level);
        if (all == null || all.size() < 4) return null;

        String[] left = new String[4], right = new String[4];
        List<WordPair> chosen = new ArrayList<>();
        List<String> rightList = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            WordPair wp = all.get((startIndex + i) % all.size());
            chosen.add(wp);
            left[i] = wp.english;
            rightList.add(wp.hebrew);
        }

        Collections.shuffle(rightList);
        right = rightList.toArray(new String[0]);

        int[] mapping = new int[4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (right[j].equals(chosen.get(i).hebrew)) { mapping[i] = j; break; }
            }
        }

        currentRound = new RoundData(left, right, mapping);
        return currentRound;
    }

    public void selectLeft(int index) {
        if (index >= 0 && index < 4 && !matchedLeft[index]) selectedLeftIndex = index;
    }

    public MatchResult selectRight(int rightIdx) {
        if (selectedLeftIndex == -1 || matchedRight[rightIdx]) 
            return new MatchResult(false, matchedCount == 4, 0, null, null);

        int leftIdx = selectedLeftIndex;
        selectedLeftIndex = -1;
        boolean correct = currentRound.correctMapping[leftIdx] == rightIdx;

        if (correct) {
            matchedLeft[leftIdx] = matchedRight[rightIdx] = true;
            matchedCount++;
            player.addScore(10);
        } else {
            player.addScore(-5);
        }
        return new MatchResult(correct, matchedCount == 4, 0, leftIdx, rightIdx);
    }
}
