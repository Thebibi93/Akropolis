package model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import util.Point3D;

/**
 * Represents the grid of the game board, containing tiles in different
 * positions.
 */
public class Grid {
    // Map to store tiles based on their positions
    private Map<Point3D, Tile> tiles;

    /**
     * Constructor to initialize the grid and add the starting tiles at the
     * beginning of the game.
     */
    public Grid() {
        tiles = new HashMap<>();

        // Creating starting tiles
        Point3D p1 = new Point3D(0, 0, 1);
        Point3D p2 = new Point3D(0, 1, 1);
        Point3D p3 = new Point3D(-1, -1, 1);
        Point3D p4 = new Point3D(1, -1, 1);
        Tile tile1 = new Place(p1, 1, DistrictColor.BLUE, this);
        Tile tile2 = new Quarrie(p2, this);
        Tile tile3 = new Quarrie(p3, this);
        Tile tile4 = new Quarrie(p4, this);

        tiles.put(p1, tile1);
        tiles.put(p2, tile2);
        tiles.put(p3, tile3);
        tiles.put(p4, tile4);
    }

    public Map<Point3D, Tile> getTiles() {
        return tiles;
    }

    /**
     * Adds a tile to the grid at the specified position.
     *
     * @param tile The tile to be added to the grid.
     * @return True if the tile is successfully added, false otherwise.
     */

    // on regarde si l'elevation est de 1 alors faut que la tuille aie des voisisns
    // pour etre placer
    public boolean canAdd(Tile tile, Point3D p) {
        int x = tile.getX();
        int y = tile.getY();
        int z = tile.getZ();
        // Check if the tile has at least one neighbor in the grid
        int[][] directions = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 }, { -1, 1 }, { 1, -1 } };
        for (int[] direction : directions) {
            int nx = x + direction[0];
            int ny = y + direction[1];
            if (tiles.containsKey(new Point3D(nx, ny, z))) {
                return true;
            }
        }
        // If the tile has no neighbors in the grid, return false
        return false;
    }

    public boolean addTile(Tile tile) {
        // Iterate over each tile in the TileTrio associated with the main tile.
        boolean canBePlaced = true;
        for (int i = 0; i < 3; i++) {
            Tile newTile_i = tile.getTileTrio().getTile(i);
            // First, check if the tile can be placed we only need to verify for the first
            // one to be a neighbor (using canAdd for ground-level tiles
            // or handleElevation for higher elevation).
            if (!tiles.containsKey(newTile_i.getPosition()) && i == 0) {
                // That means that this tile will not overlap with another tile
                canBePlaced = canBePlaced && canAdd(newTile_i, newTile_i.getPosition());
            } else if (tiles.containsKey(newTile_i.getPosition())) {
                // Handle the case where the tile is elevated
                canBePlaced = canBePlaced && handleElevation(newTile_i, tiles.get(newTile_i.getPosition()));
            }
        }
        // We need to verify that they are all at the same level
        int elevation = tile.getTileTrio().getTile(0).getElevation();
        for (int i = 0; i < 3; i++) {
            if (tile.getTileTrio().getTile(i).getElevation() != elevation) {
                canBePlaced = false;
            }
        }
        if (canBePlaced) {
            // We add each tile of the trio to the grid
            for (int i = 0; i < 3; i++) {
                Tile newTile_i = tile.getTileTrio().getTile(i);
                tiles.put(newTile_i.getPosition(), newTile_i);
                newTile_i.setGrid(this);
                System.out.println("Tile added at " + newTile_i.getPosition());
            }
        }
        return canBePlaced;
    }

    private boolean handleElevation(Tile new_tile, Tile existing_tile) {
        // We need to get the correct z value by getting the tile above the current tile
        Tile topMostTile = existing_tile;
        while (topMostTile.hasAbove()) {
            topMostTile = topMostTile.getAbove();
        }
        new_tile.getPosition().z = topMostTile.getElevation() + 1;
        if (topMostTile.getType().equals(new_tile.getType())) {
            return false;
        }
        topMostTile.setAbove(new_tile);
        return true;
    }

    /**
     * Retrieves the topmost tile at the specified position in the grid.
     *
     * @param x The x-coordinate of the tile.
     * @param y The y-coordinate of the tile.
     * @return The topmost tile at the specified position, or null if no tile
     *         exists.
     */
    public Tile getTile(int x, int y) {
        Point point = new Point(x, y);
        // Retrieve all tiles at the specified position
        Tile topMostTile = tiles.get(new Point3D(point, 1));
        while (topMostTile != null && topMostTile.hasAbove()) {
            topMostTile = topMostTile.getAbove();
        }
        return topMostTile;
    }

    /**
     * Displays information about each tile in the grid.
     */
    public void display() {
        for (Tile tile : tiles.values()) {
            System.out.println(tile.getType() + " at " + tile.getPosition());
        }
    }

    /**
     * Return whether the tile is surrounded by other tiles or not
     * 
     * @param tile The tile to check if it is surrounded
     */
    private boolean tileIsSurrounded(Tile tile) {
        return tile.getNeighbors().size() == 6;
    }

    /***
     * Clear the grid/board, for new game or otherwise
     */
    public void clearGrid() {

        tiles.clear();

    }

    /**
     * Calculate the score of each district
     */
    /*
     * public int calculateScore() {
     * // TODO : This method calculation is not correct, it should be fixed to
     * calculate the score of each district
     * // TODO : peut être séparé cette méthode en plusieurs méthode car elle risque
     * d'être longue
     * // it gives just an example of how to calculate the score
     * int gardenScore = 0;
     * int barrackScore = 0;
     * int buildingScore = 0;
     * ArrayList<Integer> buildingScores = new ArrayList<Integer>();
     * int currentNumberOfBuilding = 0; // We count all the building
     * int maxNumberOfBuilding = 0; // We only count the maximum adjacent building
     * int maxBuildingScore = 0;
     * int templeScore = 0;
     * int marketScore = 0;
     * 
     * int gardenMultiplier = 1;
     * int barrackMultiplier = 1;
     * int buildingMultiplier = 1;
     * int templeMultiplier = 1;
     * int marketMultiplier = 1;
     * 
     * for (Tile tile : tiles.values()) {
     * switch (tile.getType()) {
     * case "Garden Place":
     * gardenMultiplier = ((Place) tile).getStars();
     * break;
     * case "Barrack Place":
     * barrackMultiplier = ((Place) tile).getStars();
     * break;
     * case "Building Place":
     * buildingMultiplier = ((Place) tile).getStars();
     * break;
     * case "Temple Place":
     * templeMultiplier = ((Place) tile).getStars();
     * break;
     * case "Market Place":
     * marketMultiplier = ((Place) tile).getStars();
     * break;
     * 
     * case "Garden":
     * gardenScore += tile.getElevation(); // It should always increase the score
     * break;
     * case "Barrack":
     * // We only increase the score if the barrack is in the border of the grid
     * if (tile.getNeighbors().size() < 6) {
     * barrackScore += tile.getElevation();
     * }
     * break;
     * case "Building":
     * // We only increase the score if the building is next to another building
     * for (Tile neighbor : tile.getNeighbors()) {
     * if (neighbor.getType().equals("Building")) {
     * currentNumberOfBuilding++;
     * buildingScore += tile.getElevation();
     * maxNumberOfBuilding = Math.max(maxNumberOfBuilding, currentNumberOfBuilding);
     * maxBuildingScore = Math.max(maxBuildingScore, buildingScore);
     * break;
     * }
     * }
     * break;
     * case "Temple":
     * // We only increase the score if the temple is surrounded by 6 tiles
     * if (tileIsSurrounded(tile)) {
     * templeScore++;
     * }
     * break;
     * case "Market":
     * // We only increase the score if the market has no adjacent market
     * boolean hasMarket = false;
     * for (Tile neighbor : tile.getNeighbors()) {
     * if (neighbor.getType().equals("Market")) {
     * hasMarket = true;
     * break;
     * }
     * }
     * if (!hasMarket) {
     * marketScore++;
     * }
     * break;
     * }
     * }
     * return gardenScore * gardenMultiplier +
     * barrackScore * barrackMultiplier +
     * maxBuildingScore * buildingMultiplier +
     * templeScore * templeMultiplier +
     * marketScore * marketMultiplier;
     * }
     */

    public List<Tile> getTopTiles() {
        ArrayList<Tile> topTiles = new ArrayList<>();
        for (Tile tile : tiles.values()) {
            if (!tile.hasAbove()) {
                topTiles.add(tile);
            }
        }
        return topTiles;
    }

    public int calculateScore() {
        int totalScore = 0;

        for (Tile tile : getTopTiles()) {
            switch (tile.getType()) {
                case "Garden":
                    totalScore += calculateGardenScore(tile);
                    break;
                case "Barrack":
                    totalScore += calculateBarrackScore(tile);
                    break;
                case "Building":
                    totalScore += calculateBuildingScore(tile);
                    break;
                case "Temple":
                    totalScore += calculateTempleScore(tile);
                    break;
                case "Market":
                    totalScore += calculateMarketScore(tile);
                    break;
                default:
                    break;
            }
        }

        return totalScore;
    }

    private int calculateGardenScore(Tile tile) {
        return tile.getElevation();
    }

    private int calculateBarrackScore(Tile tile) {
        return (tile.getNeighbors().size() < 6) ? tile.getElevation() : 0;
    }

    private int calculateBuildingScore(Tile tile) {
        int adjacentBuildingScore = 0;
        for (Tile neighbor : tile.getNeighbors()) {
            if (neighbor.getType().equals("Building")) {
                adjacentBuildingScore = Math.max(adjacentBuildingScore, neighbor.getElevation());
            }
        }
        return adjacentBuildingScore;
    }

    private int calculateTempleScore(Tile tile) {
        return tileIsSurrounded(tile) ? 1 : 0;
    }

    private int calculateMarketScore(Tile tile) {
        for (Tile neighbor : tile.getNeighbors()) {
            if (neighbor.getType().equals("Market")) {
                return 0;
            }
        }
        return 1;
    }

    private int calculatePlaceScore(Place place) {
        return place.getStars() * place.getElevation();
    }
}
