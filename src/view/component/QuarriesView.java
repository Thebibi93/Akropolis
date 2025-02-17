package view.component;

/**
 * Implements a quarries view.
 * This class is used to draw the quarries.
 */
public class QuarriesView extends HexagonView {
    private static final java.awt.Color strokeColor = java.awt.Color.BLACK;
    
    public QuarriesView(int x, int y, int z, int size) {
        super(x, y, z, size);
        this.texture = TextureFactory.getTexture("quarries");
    }

    @Override
    public void paintComponent(java.awt.Graphics g) {
        super.paintComponent(g);
        if (render == null) {
            renderHexagon(strokeColor, texture);
        }
        g.drawImage(render, 0, 0, null);
    }
}
