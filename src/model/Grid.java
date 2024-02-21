package model;

import java.awt.Point;
import java.util.*;
import util.Point3D;

/**
 * Represents the grid of the game board, containing hexagons in different
 * positions.
 */
public class Grid {
    // Map to store hexagons based on their positions
    private Map<Point3D, Hexagon> hexagons;

    /**
     * Constructor to initialize the grid and add the starting hexagons at the
     * beginning of the game.
     */
    public Grid() {
        hexagons = new HashMap<>();
        // Creating starting hexagons
        Point3D p1 = new Point3D(0, 0);
        Point3D p2 = new Point3D(0, 1);
        Point3D p3 = new Point3D(-1, -1);
        Point3D p4 = new Point3D(1, 0);
        Hexagon hexagon1 = new Place(p1, 1, DistrictColor.BLUE, this);
        Hexagon hexagon2 = new Quarrie(p2, this);
        Hexagon hexagon3 = new Quarrie(p3, this);
        Hexagon hexagon4 = new Quarrie(p4, this);
        hexagons.put(p1, hexagon1);
        hexagons.put(p2, hexagon2);
        hexagons.put(p3, hexagon3);
        hexagons.put(p4, hexagon4);
    }


    public Map<Point3D, Hexagon> getHexagons() {
        return hexagons;
    }

    /**
     * Adds a hexagon to the grid at the specified position.
     *
     * @param hexagon The hexagon to be added to the grid.
     * @return True if the hexagon is successfully added, false otherwise.
     */

    // on regarde si l'elevation est de 1 alors faut que la tuille aie des voisisns
    // pour etre placer
    public boolean canAdd(Hexagon hexagon, Point3D p) {
        int x = hexagon.getX();
        int y = hexagon.getY();
        int z = hexagon.getZ();
        // Check if the hexagon has at least one neighbor in the grid
        // Define the directions for the 6 neighbors in a hexagonal grid
        Point[] axialDirections = {
                new Point(1, 0), new Point(1, -1), new Point(0, -1),
                new Point(-1, 0), new Point(-1, 1), new Point(0, 1)
        };
        for (Point direction : axialDirections) {
            Hexagon neighbor = hexagons.get(new Point3D(x + direction.x, y + direction.y, z));
            if (neighbor != null) {
                return true;
            }
        }
        // If the hexagon has no neighbors in the grid, return false
        return false;
    }

    public boolean addTile(Tile tile,Player player) {
        Hexagon[] bellowHexagons = new Hexagon[3];
        boolean hasNeighbor = checkNeighborsAndSetBelowTile(tile, bellowHexagons);
        boolean canBePlaced = checkElevation(tile);
        int samehexagon = countSameHexagons(tile, bellowHexagons);

        if (canBePlaced && hasNeighbor && samehexagon <= 1) {
            addHexagonsToGrid(tile, bellowHexagons);
            for (Hexagon hexagon:bellowHexagons){
                if(hexagon instanceof Quarrie){
                    player.setRocks(1);
                }
            }
        }

        display();
        System.out.println("canBePlaced: " + canBePlaced + ", hasNeighbor: " + hasNeighbor + ", samehexagon: " + samehexagon);
        return canBePlaced && hasNeighbor && samehexagon <= 1;
    }

    private boolean checkNeighborsAndSetBelowTile(Tile tile, Hexagon[] bellowHexagons) {
        boolean hasNeighbor = false;
        for (int i = 0; i < 3; i++) {
            Hexagon newHexagon_i = tile.hexagons.get(i);
            if (!hexagons.containsKey(newHexagon_i.getPosition()) && !hasNeighbor) {
                hasNeighbor = hasNeighbor || canAdd(newHexagon_i, newHexagon_i.getPosition());
            } else if (hexagons.containsKey(newHexagon_i.getPosition())) {
                Hexagon topMosthexagon = getHexagon(newHexagon_i.getX(), newHexagon_i.getY());
                if (topMosthexagon != null) {
                    bellowHexagons[i] = topMosthexagon;
                }
                newHexagon_i.getPosition().z = topMosthexagon.getZ() + 1;
                hasNeighbor = true;
            }
        }
        return hasNeighbor;
    }

    private void addHexagonsToGrid(Tile tile, Hexagon[] bellowHexagons) {
        for (int i = 0; i < 3; i++) {
            Hexagon newHexagon_i = tile.hexagons.get(i);
            newHexagon_i.setGrid(this);
            newHexagon_i.setTile(tile);
            if (bellowHexagons[i] != null) {
                newHexagon_i.setBelow(bellowHexagons[i]);
                bellowHexagons[i].setAbove(newHexagon_i);
            }
            hexagons.put(newHexagon_i.getPosition(), newHexagon_i);
        }
    }

    private boolean checkElevation(Tile t) {
        int elevation = t.hexagons.get(0).getZ();
        for (int i = 1; i < 3; i++) {
            if (t.hexagons.get(i).getZ() != elevation) {
                return false;
            }
        }
        return true;
    }

    private int countSameHexagons(Tile tile, Hexagon[] bellowHexagons) {
        int samehexagon = 0;
        for (int i = 0; i < 3; i++) {
            Hexagon newHexagon_i = tile.hexagons.get(i);
            if (bellowHexagons[i] != null && bellowHexagons[i].getType().equals(newHexagon_i.getType())) {
                samehexagon++;
            }
        }
        return samehexagon;
    }

    public Hexagon neighbor(Hexagon t, Point p) {
        Point p2 = new Point(t.getX(), t.getY());
        return hexagons.get(sommePos(p, p2));
    }

    public Point sommePos(Point p1, Point p2) {
        return new Point(p1.x + p2.x, p1.y + p2.y);
    }

    public static boolean isAValidTile(Tile t) {
        Hexagon h1 = t.hexagons.get(0);
        Hexagon h2 = t.hexagons.get(1);
        Hexagon h3 = t.hexagons.get(2);
        boolean adjacent1_2 = h1.isAdjacent(h2);
        boolean adjacent1_3 = h1.isAdjacent(h3);
        boolean adjacent2_3 = h2.isAdjacent(h3);
        int z1 = h1.getZ();
        int z2 = h2.getZ();
        int z3 = h3.getZ();
        System.out.println("z1: " + h1.getZ() + ", z2: " + h2.getZ() + ", z3: " + h3.getZ());
        return (z1 == z2) && (z2 == z3) && adjacent1_2 && adjacent1_3 && adjacent2_3;
    }
    /*
     * public void addhexagon(hexagon hexagon){
     * boolean hasNeighbor;
     * boolean isSpported ;
     * int elevation = hexagon.gethexagon(0).getZ() ;// verifier d'abord si au meme
     * niveau avant d'ajouter
     * 
     * for (int i = 1; i < 3; i++) {
     * if (hexagon.gethexagon(i).getZ()!= elevation) {
     * return ;
     * }
     * }
     * 
     * for (int i = 0; i < 3; i++) {
     * hexagon hexagonToadd = hexagon.gethexagon(i);
     * if (!hexagons.containsKey(hexagonToadd.getPosition())) {
     * hasNeighbor = canAdd(hexagonToadd);
     * }
     * else{
     * isSpported =
     * }
     * 
     * }
     * }
     * public boolean isSpported(hexagon t ){
     * 
     * }
     * 
     * public boolean canAdd(hexagon hexagon){
     * Point [] axialDirection = {new Point(0, 1), new Point(0, -1),new Point(-1, 1)
     * ,new Point(1, 1),new Point(1, -1),new Point(-1, -1),};
     * for (Point point : axialDirection) {
     * Point3D hexagonneighber = new Point3D(hexagon.getX()+point.x, hexagon.getY()+point.y,
     * 0);
     * if (hexagons.containsKey(hexagonneighber)) {
     * return true;
     * }
     * }
     * return false ;
     * }
     */

    /**
     * Retrieves the topmost hexagon at the specified position in the grid.
     *
     * @param x The x-coordinate of the hexagon.
     * @param y The y-coordinate of the hexagon.
     * @return The topmost hexagon at the specified position, or null if no hexagon
     *         exists.
     */
    public Hexagon getHexagon(int x, int y) {
        // Retrieve all hexagons at the specified position
        Hexagon topMosthexagon = hexagons.get(new Point3D(x,y));
        if (topMosthexagon == null) {
            return null;
        }
        while (topMosthexagon.hasAbove()) {
            topMosthexagon = topMosthexagon.getAbove();
        }
        return topMosthexagon;
    }

    /**
     * Displays information about each hexagon in the grid.
     */
    public void display() {
        for (Map.Entry<Point3D, Hexagon> entry : hexagons.entrySet()) {
            Point3D point = entry.getKey();
            Hexagon hexagon = entry.getValue();
            System.out.println("Point: " + point + ", hexagon: " + hexagon);
        }
    }

    /**
     * Return whether the hexagon is surrounded by other hexagons or not
     * 
     * @param hexagon The hexagon to check if it is surrounded
     */
    private boolean hexagonIsSurrounded(Hexagon hexagon) {
        return hexagon.getNeighbors().size() == 6;
    }

    /***
     * Clear the grid/board, for new game or otherwise
     */
    public void clearGrid() {
        hexagons.clear();
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
     * for (hexagon hexagon : hexagons.values()) {
     * switch (hexagon.getType()) {
     * case "Garden Place":
     * gardenMultiplier = ((Place) hexagon).getStars();
     * break;
     * case "Barrack Place":
     * barrackMultiplier = ((Place) hexagon).getStars();
     * break;
     * case "Building Place":
     * buildingMultiplier = ((Place) hexagon).getStars();
     * break;
     * case "Temple Place":
     * templeMultiplier = ((Place) hexagon).getStars();
     * break;
     * case "Market Place":
     * marketMultiplier = ((Place) hexagon).getStars();
     * break;
     * 
     * case "Garden":
     * gardenScore += hexagon.getElevation(); // It should always increase the score
     * break;
     * case "Barrack":
     * // We only increase the score if the barrack is in the border of the grid
     * if (hexagon.getNeighbors().size() < 6) {
     * barrackScore += hexagon.getElevation();
     * }
     * break;
     * case "Building":
     * // We only increase the score if the building is next to another building
     * for (hexagon neighbor : hexagon.getNeighbors()) {
     * if (neighbor.getType().equals("Building")) {
     * currentNumberOfBuilding++;
     * buildingScore += hexagon.getElevation();
     * maxNumberOfBuilding = Math.max(maxNumberOfBuilding, currentNumberOfBuilding);
     * maxBuildingScore = Math.max(maxBuildingScore, buildingScore);
     * break;
     * }
     * }
     * break;
     * case "Temple":
     * // We only increase the score if the temple is surrounded by 6 hexagons
     * if (hexagonIsSurrounded(hexagon)) {
     * templeScore++;
     * }
     * break;
     * case "Market":
     * // We only increase the score if the market has no adjacent market
     * boolean hasMarket = false;
     * for (hexagon neighbor : hexagon.getNeighbors()) {
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

    public ArrayList<Hexagon> getTopHexagons() {
        ArrayList<Hexagon> tophexagons = new ArrayList<>();
        for (Hexagon hexagon : hexagons.values()) {
            if (!hexagon.hasAbove() && tophexagons.contains(hexagon) == false) {
                tophexagons.add(hexagon);
            }
        }
        return tophexagons;
    }

    public int calculateScore() {
        int totalScore = 0;

        for (Hexagon hexagon : getTopHexagons()) {
            switch (hexagon.getType()) {
                case "Garden":
                    totalScore += calculateGardenScore(hexagon);
                    break;
                case "Barrack":
                    totalScore += calculateBarrackScore(hexagon);
                    break;
                case "Building":
                    totalScore += calculateBuildingScore(hexagon);
                    break;
                case "Temple":
                    totalScore += calculateTempleScore(hexagon);
                    break;
                case "Market":
                    totalScore += calculateMarketScore(hexagon);
                    break;
                default:
                    break;
            }
        }

        return totalScore;
    }

    private int calculateGardenScore(Hexagon hexagon) {
        return hexagon.getElevation();
    }

    private int calculateBarrackScore(Hexagon hexagon) {
        return (hexagon.getNeighbors().size() < 6) ? hexagon.getElevation() : 0;
    }

    private int calculateBuildingScore(Hexagon hexagon) {
        int adjacentBuildingScore = 0;
        for (Hexagon neighbor : hexagon.getNeighbors()) {
            if (neighbor.getType().equals("Building")) {
                adjacentBuildingScore = Math.max(adjacentBuildingScore, neighbor.getElevation());
            }
        }
        return adjacentBuildingScore;
    }

    private int calculateTempleScore(Hexagon hexagon) {
        return hexagonIsSurrounded(hexagon) ? 1 : 0;
    }

    private int calculateMarketScore(Hexagon hexagon) {
        for (Hexagon neighbor : hexagon.getNeighbors()) {
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
