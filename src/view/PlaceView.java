package view;

import java.awt.Color;
import model.Place;

/**
 * Represents a place on the game grid.
 */
public class PlaceView extends HexagonView {
    private int stars;
    private Place place;
    private static Color strokeColor = Color.BLACK;

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
    public void paintComponent(java.awt.Graphics g) {
        super.paintComponent(g);
        if (render == null) {
            renderHexagon(strokeColor, texture);
        }
        g.drawImage(render, 0, 0, null);
    }
}
