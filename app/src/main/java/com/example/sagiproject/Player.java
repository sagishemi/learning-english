package com.example.sagiproject;

public class Player {
    public String name;
    public int score;

    public Player(String name) {
        this.name = name;
        this.score = 0;
    }



    public void addScore(int points) {
        score += points;
        if (score < 0) score = 0;
    }
}
