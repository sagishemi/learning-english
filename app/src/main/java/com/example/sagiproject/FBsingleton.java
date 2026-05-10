package com.example.sagiproject;

import android.content.Context;
import androidx.annotation.NonNull;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class FBsingleton {
    private static FBsingleton instance;
    public ArrayList<WordPair> allWords = new ArrayList<>();
    public int currentIndex = 0;
    
    public interface OnDataLoadedListener { void onLoaded(); }
    private OnDataLoadedListener listener;

    private FBsingleton() {
        loadData();
    }

    public static FBsingleton getInstance(Context ctx) {
        if (instance == null) instance = new FBsingleton();
        return instance;
    }

    public void setOnDataLoadedListener(OnDataLoadedListener l) {
        this.listener = l;
        if (!allWords.isEmpty() && listener != null) listener.onLoaded();
    }

    private void loadData() {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("records/" + uid);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) return;
                allWords.clear();
                for (DataSnapshot ds : snapshot.child("Words").getChildren()) {
                    WordPair wp = ds.getValue(WordPair.class);
                    if (wp != null) allWords.add(wp);
                }
                Integer score = snapshot.child("MyScore").getValue(Integer.class);
                currentIndex = (score != null) ? score : 0;
                if (listener != null) listener.onLoaded();
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    public void initializeDefaultWords() {
        allWords.clear();
        allWords.add(new WordPair("Dog", "כלב"));
        allWords.add(new WordPair("Cat", "חתול"));
        allWords.add(new WordPair("Sun", "שמש"));
        allWords.add(new WordPair("Apple", "תפוח"));
        save();
    }

    public void addWords(ArrayList<WordPair> newWords) {
        for (WordPair nw : newWords) {
            boolean exists = false;
            for (WordPair old : allWords) {
                if (old.english.equalsIgnoreCase(nw.english)) { exists = true; break; }
            }
            if (!exists) allWords.add(nw);
        }
        save();
    }

    public void setScore(int score) {
        this.currentIndex = score;
        save();
    }

    public void setName(String name) {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid != null) FirebaseDatabase.getInstance().getReference("records/" + uid + "/MyName").setValue(name);
    }

    private void save() {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("records/" + uid);
        ref.child("Words").setValue(allWords);
        ref.child("MyScore").setValue(currentIndex);
    }
}
