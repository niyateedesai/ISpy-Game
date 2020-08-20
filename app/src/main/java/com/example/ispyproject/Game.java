package com.example.ispyproject;

import java.util.ArrayList;

public class Game {
    private String player1;
    private String player2;
    private int player1Score;
    private int player2Score;
    private String winner;
    private ArrayList<String> words;
    private ArrayList<String> possibilities;

    public Game(String player1, String player2) {
        this.player1 = player1;
        this.player2 = player2;
        this.player1Score = 0;
        this.player2Score = 0;
        this.winner = null;
        this.words = new ArrayList<>();
        this.possibilities = new ArrayList<>();

        possibilities.add("Musical instrument");
        possibilities.add("Space");
        possibilities.add("Sky");
        possibilities.add("Computer");
        possibilities.add("Foot");
        possibilities.add("Pattern");
        possibilities.add("Dog");
        possibilities.add("Hand");
        possibilities.add("Nail");
        possibilities.add("Chair");
        possibilities.add("Room");

        fillArrayList();
    }

    public Game() {
    }

    public ArrayList<String> getWords() {
        return words;
    }

    public String getPlayer1() {
        return player1;
    }

    public String getPlayer2() {
        return player2;
    }

    public int getPlayer1Score() {
        return player1Score;
    }

    public int getPlayer2Score() {
        return player2Score;
    }

    public String getWinner() {
        return winner;
    }

    public void player1Point(){
        player1Score++;
    }

    public void player2Point(){
        player2Score++;
    }

    private void fillArrayList(){
        for(int i = 0; i<9; i++) {
            words.add(possibilities.remove((int) (Math.random() * possibilities.size())));
        }

    }
}
