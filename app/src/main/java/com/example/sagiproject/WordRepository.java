package com.example.sagiproject;

import java.util.ArrayList;
import java.util.List;

public class WordRepository {

    private ArrayList<WordPair> easyWords = new ArrayList<>();
    private ArrayList<WordPair> hardWords = new ArrayList<>();

    public WordRepository() {
        loadDefaultWords();
    }

    private void loadDefaultWords() {
        easyWords.add(new WordPair("Dog", "כלב"));
        easyWords.add(new WordPair("Cat", "חתול"));
        easyWords.add(new WordPair("Sun", "שמש"));
        easyWords.add(new WordPair("Apple", "תפוח"));

        hardWords.add(new WordPair("Environment", "סביבה"));
        hardWords.add(new WordPair("Experience", "חוויה"));
        hardWords.add(new WordPair("Knowledge", "ידע"));
        hardWords.add(new WordPair("Responsibility", "אחריות"));
    }

    public List<WordPair> getWordsByLevel(int level) {
        if (level == 1) { // EASY
            return easyWords;
        } else {
            return hardWords;
        }
    }

    public ArrayList<WordPair> getEasyWords() {
        return easyWords;
    }
}
