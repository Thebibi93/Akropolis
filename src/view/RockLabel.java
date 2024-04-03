package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.Font;
import java.awt.Color;


/**
 * Represents the label that indicates to the user their current rock count.
 */
public class RockLabel extends JLabel {
    private Timer timer;
    private boolean isBlinking = false;
    
    public RockLabel(){
        super("Rocks: 0");
        setOpaque(false);
        setForeground(java.awt.Color.WHITE);
        setBorder(new EmptyBorder(38, 0, 0, 0)); // Ajoute un espace en haut du texte
    }
    public void setRocks(int rocks){
        
        setFont(new Font("Serif", Font.BOLD, 18)); 
        this.setText("                  " + rocks);
        startBlinking();

        validate();
        repaint();
    }
    private void startBlinking() {
        if (timer != null && timer.isRunning()) {
            return;
        }

        timer = new Timer(900, e -> {
            isBlinking = !isBlinking;
            setForeground(isBlinking ? Color.RED : Color.WHITE);
            if (!isBlinking) {
                ((Timer)e.getSource()).stop();
            }
        });
        timer.start();
    }
    
    
}
