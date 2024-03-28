package view;

import javax.swing.JLabel;

/**
 * Represents the label that indicates to the user their current rock count.
 */
public class RockLabel extends JLabel {
    
    public RockLabel(){
        super("Rocks: 0");
        setOpaque(false);
        setForeground(java.awt.Color.WHITE);
    }
    
    
}
