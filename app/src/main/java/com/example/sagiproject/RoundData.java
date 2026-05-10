package com.example.sagiproject;

public class RoundData {
    public String[] left;
    public String[] right;
    public int[] correctMapping;

    public RoundData(String[] left, String[] right, int[] correctMapping) {
        this.left = left;
        this.right = right;
        this.correctMapping = correctMapping;
    }
}
