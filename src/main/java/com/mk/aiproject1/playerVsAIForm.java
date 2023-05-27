package com.mk.aiproject1;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;

public class playerVsAIForm extends javax.swing.JFrame {

    private static int difficulty;// easy = 1, Med = 2, Hard = 3
    List<JButton> buttonList = new ArrayList<>();//list of all buttons 
    private boolean win = false;//no winner yet
    private Board board;

    void loadButtons() {
        for (int i = 1; i <= 9; i++) {
            try {
                buttonList.add((JButton) this.getClass().getDeclaredField("btn" + i).get(this));
            } catch (NoSuchFieldException ex) {
                Logger.getLogger(playerVsAIForm.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SecurityException ex) {
                Logger.getLogger(playerVsAIForm.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(playerVsAIForm.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(playerVsAIForm.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void highlightWinningPath(int[] winPath) {
        for (int index : winPath) {
            buttonList.get(index).setBackground(Color.GREEN);
        }
    }

    void disableButtons(List<JButton> buttonList) {
        for (JButton button : buttonList) {
            button.setEnabled(false);
        }

    }

    public void btn_click(ActionEvent e) {
        // Get the button that was clicked
        JButton clickedButton = (JButton) e.getSource();
        if (clickedButton.getText().isEmpty()) {
            // Make the player's move on the board
            int index = buttonList.indexOf(clickedButton);
            int row = index / 3;
            int col = index % 3;
            board.makeMove(row, col, Board.PLAYER);
            // Update the button text
            clickedButton.setText("X");
            clickedButton.setForeground(Color.RED);

            // Check if the game is over
            int[] winPath = board.getWinPath(Board.PLAYER);
            if (board.checkWin(Board.PLAYER)) {
                label1.setText("X Won !");
                disableButtons(buttonList);
                highlightWinningPath(winPath);
                return;
            } else if (board.isFull()) {
                label1.setText("Draw !");
                disableButtons(buttonList);
                return;
            }
            // Make the AI's move using the alpha-beta algorithm
            int[] aiMove = alphaBeta(board, difficulty * 2 +1 , Integer.MIN_VALUE, Integer.MAX_VALUE, Board.AI);
            board.makeMove(aiMove[0], aiMove[1], Board.AI);
            // Update the button text
            JButton aiButton = buttonList.get(aiMove[0] * 3 + aiMove[1]);
            aiButton.setText("O");
            aiButton.setForeground(Color.blue);
            // Check if the game is over
            if (board.checkWin(Board.AI)) {
                winPath = board.getWinPath(Board.AI);
                highlightWinningPath(winPath);
                label1.setText("O Won !");
                disableButtons(buttonList);
                return;
            } else if (board.isFull()) {
                label1.setText("Draw !");
                disableButtons(buttonList);
                return;
            }
        }
    }

    private int[] alphaBeta(Board board, int depth, int alpha, int beta, int player) {
        // Check if we have reached the maximum depth (leaf node) or if the game is over
        if (depth == 0 || board.checkWin(Board.PLAYER) || board.checkWin(Board.AI) || board.isFull()) {
            return new int[]{-1, -1, evaluate(board)};
        }
        int[] bestMove = new int[3];// array will be used to store the row and column indices of the best move, as well as the score associated with that move.

        if (player == Board.AI) {
            bestMove[2] = Integer.MIN_VALUE;//v = -inf
            for (int[] move : board.getAvailableMoves()) {// for each child of node
                Board newBoard = new Board(board);
                newBoard.makeMove(move[0], move[1], player);
                int[] currentMove = alphaBeta(newBoard, depth - 1, alpha, beta, Board.PLAYER);
                if (currentMove[2] > bestMove[2]) {// v = max (v, alpaBeta(curr==child))
                    bestMove = currentMove;
                    bestMove[0] = move[0];
                    bestMove[1] = move[1];
                }
                alpha = Math.max(alpha, bestMove[2]);// alpha = max (alpha,v)
                if (alpha >= beta) { //cut-off
                    break;
                }
            }
        } else {
            bestMove[2] = Integer.MAX_VALUE;//v = inf
            for (int[] move : board.getAvailableMoves()) {
                Board newBoard = new Board(board);
                newBoard.makeMove(move[0], move[1], player);
                int[] currentMove = alphaBeta(newBoard, depth - 1, alpha, beta, Board.AI);
                if (currentMove[2] < bestMove[2]) {// v = min (v,alpha-beta(child))
                    bestMove = currentMove;
                    bestMove[0] = move[0];
                    bestMove[1] = move[1];
                }
                beta = Math.min(beta, bestMove[2]);//beta = min (beta,v)
                if (alpha >= beta) {//cut-off if alpha >= beta
                    break;
                }
            }
        }

        return bestMove;
    }

    public static int getDifficulty() {
        return difficulty;
    }

    private int evaluate(Board board) {
        if (board.checkWin(Board.AI)) {
            return 100;
        } else if (board.checkWin(Board.PLAYER)) {
            return -100;
        } else {
            return 0;
        }
    }

    public static void setDifficulty(int difficulty) {
        playerVsAIForm.difficulty = difficulty;
    }

    /**
     * Creates new form playerVsAIForm
     *
     * @param difficulty
     */
    public playerVsAIForm(int difficulty) {
        initComponents();
        board = new Board();
        buttonList.add(btn1);
        buttonList.add(btn2);
        buttonList.add(btn3);
        buttonList.add(btn4);
        buttonList.add(btn5);
        buttonList.add(btn6);
        buttonList.add(btn7);
        buttonList.add(btn8);
        buttonList.add(btn9);
        win = false;
        setDifficulty(difficulty);
        String diff = "";
        switch (difficulty) {
            case 1 -> {
                diff = "Easy";
            }
            case 2 -> {
                diff = "Medium";
            }
            case 3 -> {
                diff = "Hard";
            }
        }
        label1.setText(diff);
        for (JButton button : buttonList) {
            button.setEnabled(true);
            button.setBackground(Color.LIGHT_GRAY); // reset the background color
            button.setText("");
        }

    }

    private void resetGame() {
        // Reset the board
        board = new Board();
        for (JButton button : buttonList) {
            button.setEnabled(true);
            button.setBackground(Color.LIGHT_GRAY);
            button.setText("");
        }

        String diff = "";
        switch (difficulty) {
            case 1 -> {
                diff = "Easy";
            }
            case 2 -> {
                diff = "Medium";
            }
            case 3 -> {
                diff = "Hard";
            }
        }
        label1.setText(diff);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        btnExitOnePlayer = new javax.swing.JButton();
        btnPlayAgain = new javax.swing.JButton();
        btn2 = new javax.swing.JButton();
        btn3 = new javax.swing.JButton();
        btn1 = new javax.swing.JButton();
        btn7 = new javax.swing.JButton();
        btn4 = new javax.swing.JButton();
        btn9 = new javax.swing.JButton();
        btn6 = new javax.swing.JButton();
        btn5 = new javax.swing.JButton();
        btn8 = new javax.swing.JButton();
        label1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(51, 51, 51));
        jPanel1.setPreferredSize(new java.awt.Dimension(500, 500));

        btnExitOnePlayer.setBackground(new java.awt.Color(204, 0, 0));
        btnExitOnePlayer.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        btnExitOnePlayer.setText("Back");
        btnExitOnePlayer.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(255, 255, 255)));
        btnExitOnePlayer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExitOnePlayerActionPerformed(evt);
            }
        });

        btnPlayAgain.setBackground(new java.awt.Color(0, 153, 153));
        btnPlayAgain.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        btnPlayAgain.setText("Play Again");
        btnPlayAgain.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(255, 255, 255)));
        btnPlayAgain.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPlayAgainActionPerformed(evt);
            }
        });

        btn2.setFont(new java.awt.Font("Dialog", 1, 36)); // NOI18N
        btn2.setPreferredSize(new java.awt.Dimension(134, 134));
        btn2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn2ActionPerformed(evt);
            }
        });

        btn3.setFont(new java.awt.Font("Dialog", 1, 36)); // NOI18N
        btn3.setPreferredSize(new java.awt.Dimension(134, 134));
        btn3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn3ActionPerformed(evt);
            }
        });

        btn1.setFont(new java.awt.Font("Dialog", 1, 36)); // NOI18N
        btn1.setPreferredSize(new java.awt.Dimension(134, 134));
        btn1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn1ActionPerformed(evt);
            }
        });

        btn7.setFont(new java.awt.Font("Dialog", 1, 36)); // NOI18N
        btn7.setPreferredSize(new java.awt.Dimension(134, 134));
        btn7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn7ActionPerformed(evt);
            }
        });

        btn4.setFont(new java.awt.Font("Dialog", 1, 36)); // NOI18N
        btn4.setPreferredSize(new java.awt.Dimension(134, 134));
        btn4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn4ActionPerformed(evt);
            }
        });

        btn9.setFont(new java.awt.Font("Dialog", 1, 36)); // NOI18N
        btn9.setPreferredSize(new java.awt.Dimension(134, 134));
        btn9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn9ActionPerformed(evt);
            }
        });

        btn6.setFont(new java.awt.Font("Dialog", 1, 36)); // NOI18N
        btn6.setPreferredSize(new java.awt.Dimension(134, 134));
        btn6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn6ActionPerformed(evt);
            }
        });

        btn5.setFont(new java.awt.Font("Dialog", 1, 36)); // NOI18N
        btn5.setPreferredSize(new java.awt.Dimension(134, 134));
        btn5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn5ActionPerformed(evt);
            }
        });

        btn8.setFont(new java.awt.Font("Dialog", 1, 36)); // NOI18N
        btn8.setPreferredSize(new java.awt.Dimension(134, 134));
        btn8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn8ActionPerformed(evt);
            }
        });

        label1.setBackground(new java.awt.Color(255, 204, 102));
        label1.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        label1.setForeground(new java.awt.Color(255, 255, 255));
        label1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label1.setText("           Difficulty ");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(7, 7, 7)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btn7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(btn5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(btn6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(btn2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(btn3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(btn8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btn9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(7, 7, 7))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btnPlayAgain, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(label1, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnExitOnePlayer, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(36, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btn1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btn6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 19, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btn7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnPlayAgain)
                    .addComponent(label1, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnExitOnePlayer))
                .addGap(28, 28, 28))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 504, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 504, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 521, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 521, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnExitOnePlayerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExitOnePlayerActionPerformed
        dispose();
        diff diff = new diff();
        diff.setVisible(true);
    }//GEN-LAST:event_btnExitOnePlayerActionPerformed

    private void btnPlayAgainActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPlayAgainActionPerformed
        resetGame();
    }//GEN-LAST:event_btnPlayAgainActionPerformed

    private void btn4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn4ActionPerformed
        btn_click(evt);
    }//GEN-LAST:event_btn4ActionPerformed

    private void btn1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn1ActionPerformed
        btn_click(evt);
    }//GEN-LAST:event_btn1ActionPerformed

    private void btn2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn2ActionPerformed
        btn_click(evt);
    }//GEN-LAST:event_btn2ActionPerformed

    private void btn3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn3ActionPerformed
        btn_click(evt);
    }//GEN-LAST:event_btn3ActionPerformed

    private void btn5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn5ActionPerformed
        btn_click(evt);
    }//GEN-LAST:event_btn5ActionPerformed

    private void btn6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn6ActionPerformed
        btn_click(evt);
    }//GEN-LAST:event_btn6ActionPerformed

    private void btn7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn7ActionPerformed
        btn_click(evt);
    }//GEN-LAST:event_btn7ActionPerformed

    private void btn8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn8ActionPerformed
        btn_click(evt);
    }//GEN-LAST:event_btn8ActionPerformed

    private void btn9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn9ActionPerformed
        btn_click(evt);
    }//GEN-LAST:event_btn9ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new playerVsAIForm(getDifficulty()).setVisible(true);

            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn1;
    private javax.swing.JButton btn2;
    private javax.swing.JButton btn3;
    private javax.swing.JButton btn4;
    private javax.swing.JButton btn5;
    private javax.swing.JButton btn6;
    private javax.swing.JButton btn7;
    private javax.swing.JButton btn8;
    private javax.swing.JButton btn9;
    private javax.swing.JButton btnExitOnePlayer;
    private javax.swing.JButton btnPlayAgain;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel label1;
    // End of variables declaration//GEN-END:variables
}
