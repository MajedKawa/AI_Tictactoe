
package com.mk.aiproject1;

import java.util.ArrayList;
import java.util.List;


public class Board {//A value of 0 represents an empty cell, a value of 1 represents a cell occupied by the player, and a value of 2 represents a cell occupied by the AI.
    public static final int PLAYER = 1;
    public static final int AI = 2;
    
    private int[][] board;
    
    public Board() {
        board = new int[3][3]; //board[row][col]
    }
    
    public Board(Board other) {
        board = new int[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = other.board[i][j];
            }
        }
    }
    
    public void makeMove(int row, int col, int player) {
        board[row][col] = player;
    }
    
public boolean checkWin(int player) {
        // Check rows
        for (int i = 0; i < 3; i++) {
            if (board[i][0] == player && board[i][1] == player && board[i][2] == player) {
                return true;
            }
        }
        
        // Check columns
        for (int i = 0; i < 3; i++) {
            if (board[0][i] == player && board[1][i] == player && board[2][i] == player) {
                return true;
            }
        }
        
        // Check diagonals
        if (board[0][0] == player && board[1][1] == player && board[2][2] == player) {
            return true;
        }
        if (board[0][2] == player && board[1][1] == player && board[2][0] == player) {
            return true;
        }
        
        return false;
    }

public int[] getWinPath(int player) {
    // Check rows
    for (int i = 0; i < 3; i++) {
        if (board[i][0] == player && board[i][1] == player && board[i][2] == player) {
            return new int[]{i * 3, i * 3 + 1, i * 3 + 2};
        }
    }
    
    // Check columns
    for (int i = 0; i < 3; i++) {
        if (board[0][i] == player && board[1][i] == player && board[2][i] == player) {
            return new int[]{i, i + 3, i + 6};
        }
    }
    
    // Check diagonals
    if (board[0][0] == player && board[1][1] == player && board[2][2] == player) {
        return new int[]{0, 4, 8};
    }
    if (board[0][2] == player && board[1][1] == player && board[2][0] == player) {
        return new int[]{2, 4, 6};
    }
    
    return null;
}
    
    public boolean isFull() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == 0) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public List<int[]> getAvailableMoves() {
        List<int[]> moves = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == 0) {
                    moves.add(new int[]{i, j});
                }
            }
        }
        return moves;
    }
}