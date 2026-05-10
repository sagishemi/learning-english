package com.example.sagiproject;

public class WordPair {
    public String english;
    public String hebrew;

    public WordPair(String english, String hebrew) {
        this.english = english;
        this.hebrew = hebrew;
    }

    public WordPair() {}

    public String getEnglish() { return english; }
    public String getHebrew() { return hebrew; }
}
