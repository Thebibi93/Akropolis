package view;

import java.awt.Color;

/**
 * Implements a hexagon outline view.
 */
public class HexagonOutline extends HexagonView {
    private static Color strokeColor = Color.BLUE;

    public HexagonOutline(int x, int y, int z, int size) {
        super(x, y, z, size);
        setOpaque(false);
    }

    @Override
    public void unfill() {
        renderHexagon(Color.BLUE, texture);
        repaint();
    }

    @Override
    protected void paintComponent(java.awt.Graphics g) {
        super.paintComponent(g);
        if (render == null) {
            renderHexagon(strokeColor, null);
        }
        g.drawImage(render, 0, 0, null);
    }
}
