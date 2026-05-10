package com.example.sagiproject;

public class MatchResult {
    public final boolean isCorrect;
    public final boolean isRoundFinished;
    public final int currentPlayerIndex;
    public final Integer leftIndex;
    public final Integer rightIndex;

    public MatchResult(boolean isCorrect, boolean isRoundFinished, int currentPlayerIndex,
                       Integer leftIndex, Integer rightIndex) {
        this.isCorrect = isCorrect;
        this.isRoundFinished = isRoundFinished;
        this.currentPlayerIndex = currentPlayerIndex;
        this.leftIndex = leftIndex;
        this.rightIndex = rightIndex;
    }
}
