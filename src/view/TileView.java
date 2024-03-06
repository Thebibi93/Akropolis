package view;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.Timer;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.BasicStroke;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Represents a single tile in the game.
 * A tile is three hexagons that form a triangle.
 */
public class TileView extends JComponent implements View {

    private boolean isHovered = false;
    private HexagonView hex1, hex2, hex3;
    private boolean isClicked = false;
    private float glow = 0.0f;
    private boolean increasing = true;
    private Timer glowTimer;

    public TileView() {
        // Empty constructor
    }

    public TileView(HexagonView hex1, HexagonView hex2, HexagonView hex3) {
        setOpaque(false);
        this.hex1 = hex1;
        this.hex2 = hex2;
        this.hex3 = hex3;
        int hexWidth = 80; // Fixed amount to ensure they will be properly displayed
        int hexHeight = (int) (Math.sqrt(3) / 2 * hexWidth);
        this.setPreferredSize(new Dimension((int) (Math.sqrt(3) * 80), hexHeight));
        add(hex1);
        add(hex2);
        add(hex3);
        setupListener();
        this.revalidate();
        this.repaint();

        setupDragging();
    }



    private void setupDragging() {
        MouseAdapter ma = new MouseAdapter() {
            Point initialClick;

            @Override
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint();
                SwingUtilities.convertPointToScreen(initialClick, TileView.this);
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                Point currentScreenLocation = e.getLocationOnScreen();
                Point parentLocation = TileView.this.getParent().getLocationOnScreen();
                
                int x = currentScreenLocation.x - parentLocation.x - initialClick.x;
                int y = currentScreenLocation.y - parentLocation.y - initialClick.y;
                
                TileView.this.setLocation(x, y);
            }
        };

        this.addMouseListener(ma);
        this.addMouseMotionListener(ma);
    }





    private void setupListener() {
        // Add the mouse listener to the hexagons
        // Remove the mouse listener from the hexagons
        for (HexagonView hex : new HexagonView[] { hex1, hex2, hex3 }) {
            hex.removeMouseListener(hex.getMouseListeners()[0]);
        }
        // Add an empty mouse event listener to the hexagons to send the mouse events to
        // the parent
        for (HexagonView hex : new HexagonView[] { hex1, hex2, hex3 }) {
            hex.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    // Do nothing
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    // Do nothing
                }
            });
        }
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                repaint();
            }
        });
        // Create a Timer that fires every 100 milliseconds
        Timer timer = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Update the glow variable
                if (increasing) {
                    glow += 0.1f;
                    if (glow > 1.0f) {
                        glow = 1.0f;
                        increasing = false;
                    }
                } else {
                    glow -= 0.1f;
                    if (glow < 0.0f) {
                        glow = 0.0f;
                        increasing = true;
                    }
                }

                // Repaint the component
                repaint();
            }
        });
        this.glowTimer = timer;
    }

    @Override
    public void doLayout() {
        super.doLayout();

        int hexWidth = HexagonView.size;
        int hexHeight = (int) (Math.sqrt(3) / 2 * hexWidth);

        // Calculate the center of the TileView
        int centerX = this.getWidth() / 2;
        int centerY = this.getHeight() / 4;

        // Position the first hexagon at the center y and right most x
        hex1.setLocation(centerX + hexWidth / 4, centerY);

        // Position the second and third hexagons at the left of the first hexagon
        hex2.setLocation(centerX - hexWidth/2, centerY - hexHeight / 2);
        hex3.setLocation(centerX - hexWidth/2, centerY + hexHeight / 2);
    }

    public void setHexagons(HexagonView hex1, HexagonView hex2, HexagonView hex3) {
        removeAll();
        this.hex1 = hex1;
        this.hex2 = hex2;
        this.hex3 = hex3;
        add(hex1);
        add(hex2);
        add(hex3);
        setupListener();
        this.revalidate();
        this.repaint();
    }

    public ArrayList<HexagonView> getHexagons() {
        return new ArrayList<>(Arrays.asList(hex1, hex2, hex3));
    }

    protected void paintComponent(java.awt.Graphics g) {
        super.paintComponent(g);
        // Change the color of the hexagons if the tile is hovered
        if (isHovered && !isClicked) {
            if (!glowTimer.isRunning()) {
                glowTimer.start();
            }
            Graphics2D g2 = (Graphics2D) g.create();

            g2.setStroke(new BasicStroke(1));

            // Draw a dark border around this component which is a rectangle
            g2.setColor(new Color(73, 216, 230));
            g2.drawRect(0, 0, getWidth() - 1, getHeight() - 1);

            // Draw the glow
            g2.setColor(new Color(73 / 255f, 216 / 255f, 230 / 255f, glow));
            g2.setStroke(new BasicStroke(10));
            g2.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
            g2.dispose();

        } else {
            if (glowTimer.isRunning()) {
                glowTimer.stop();
            }
        }
    }
}
