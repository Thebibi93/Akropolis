package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import model.Place;

/**
 * Represents a place on the game grid.
 */
public class PlaceView extends HexagonView {
    private int stars;
    private BasicStroke stroke = new BasicStroke(size / 25);
    private Place place;

    public PlaceView(int x, int y, int z, Place place) {
        super(x, y, z);
        this.stars = place.getStars();
        this.place = place;
        switch (place.getType()) {
            case "Barrack Place":
                this.texture = TextureFactory.getTexture("barrack");
                break;
            case "Building Place":
                this.texture = TextureFactory.getTexture("building");
                break;
            case "Garden Place":
                this.texture = TextureFactory.getTexture("garden");
                break;
            case "Market Place":
                this.texture = TextureFactory.getTexture("market");
                break;
            case "Temple Place":
                this.texture = TextureFactory.getTexture("temple");
                break;
            default:
                break;
        }
    }

    @Override
    public PlaceView copy(){
        return new PlaceView((int) getPosition().getX(), (int) getPosition().getY(), (int) getPosition().getZ(), place);
    }

    @Override
    public void paintComponent(java.awt.Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setPaint(this.texture);
        g2d.fill(this.hexagon);
        // Draw the border of the hexagon
        if (isHovered) {
            g2d.setColor(java.awt.Color.YELLOW);
        }
        else {
            g2d.setColor(java.awt.Color.BLACK);
        }
        g2d.setStroke(stroke);
        g2d.draw(this.hexagon);
        g2d.dispose();
    }
}
