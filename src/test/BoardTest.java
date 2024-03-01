package test;

import model.*;
import util.Point3D;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class BoardTest {
    @Test
    public void testAddTile() {
        Player player = new Player("TestPlayer");
        Board board = new Board(List.of(player));
        Quarrie validHexagon1 = new Quarrie(new Point3D(0, 2));
        Quarrie validHexagon2 = new Quarrie(new Point3D(-1, 3));
        Quarrie validHexagon3 = new Quarrie(new Point3D(1, 2));
        Tile tileTrio1 = new Tile(validHexagon1, validHexagon2, validHexagon3);
        // We verify that we can add a tile to the board
        assertTrue(board.addTile(tileTrio1));
        // And that we can get it back
        assertEquals(validHexagon1, board.getHexagon(0, 2));
        assertEquals(validHexagon2, board.getHexagon(-1, 3));
        assertEquals(validHexagon3, board.getHexagon(1, 2));
    }

    @Test
    public void testAddInvalidTile() {
        Player player = new Player("TestPlayer");
        Board board = new Board(List.of(player));
        Quarrie invalidHexagon1 = new Quarrie(new Point3D(0, 1));
        Quarrie invalidHexagon2 = new Quarrie(new Point3D(-1, 2));
        Quarrie invalidHexagon3 = new Quarrie(new Point3D(1, 1));
        Tile tileTrio1 = new Tile(invalidHexagon1, invalidHexagon2, invalidHexagon3);
        // We verify that we can't add a tile to the board if it's not correctly overlapping
        assertFalse(board.addTile(tileTrio1));
        // And that we can't get it back
        assertNotEquals(invalidHexagon1, board.getHexagon(0, 1));
        assertNotEquals(invalidHexagon2, board.getHexagon(0, 2));
        assertNotEquals(invalidHexagon3, board.getHexagon(3, 2));

        // Then we will verify we can't add a tile that has no neighbors
        Quarrie invalidHexagon4 = new Quarrie(new Point3D(0, 3));
        Quarrie invalidHexagon5 = new Quarrie(new Point3D(-1, 3));
        Quarrie invalidHexagon6 = new Quarrie(new Point3D(1, 2));
        Tile tileTrio2 = new Tile(invalidHexagon4, invalidHexagon5, invalidHexagon6);
        // We verify that we can't add a tile to the board if it has no neighbors
        assertFalse(board.addTile(tileTrio2));
        // And that we can't get it back
        assertNotEquals(invalidHexagon4, board.getHexagon(0, 3));
        assertNotEquals(invalidHexagon5, board.getHexagon(-1, 3));
        assertNotEquals(invalidHexagon6, board.getHexagon(1, 2));

        // Finally we will verify we cant overlap hexagons of the same type more than once
        Quarrie invalidTile7 = new Quarrie(new Point3D(0, 0));
        Quarrie invalidTile8 = new Quarrie(new Point3D(-1, -1));
        Quarrie invalidTile9 = new Quarrie(new Point3D(1, 0));
        Tile tileTrio3 = new Tile(invalidTile7, invalidTile8, invalidTile9);
        // We verify that we can't add a tile to the board if it's not respecting the rules
        assertFalse(board.addTile(tileTrio3));
        // And that we can't get it back
        assertNotEquals(invalidTile7, board.getHexagon(0, 0));
        assertNotEquals(invalidTile8, board.getHexagon(-1, -1));
        assertNotEquals(invalidTile9, board.getHexagon(1, 0));
    }

    @Test
    public void testOverlapTile(){
        Player player = new Player("TestPlayer");
        Board board = new Board(List.of(player));
        District validHexagon1 = new District(new Point3D(0, 0), DistrictColor.RED);
        District validHexagon2 = new District(new Point3D(-1, -1), DistrictColor.BLUE);
        District validHexagon3 = new District(new Point3D(1, 0), DistrictColor.GREEN);
        Tile tileTrio1 = new Tile(validHexagon1, validHexagon2, validHexagon3);
        // We verify that we can add a tile to the board
        assertTrue(board.addTile(tileTrio1));
        // We verify that their height has been updated
        assertEquals(2, validHexagon1.getElevation());
        assertEquals(2, validHexagon2.getElevation());
        assertEquals(2, validHexagon3.getElevation());
        // And that we can get it back
        assertEquals(validHexagon1, board.getHexagon(0, 0));
        assertEquals(validHexagon2, board.getHexagon(-1, -1));
        assertEquals(validHexagon3, board.getHexagon(1, 0));
    }

    @Test
    public void testScore() {
        Player player = new Player("TestPlayer");
        Board board = new Board(List.of(player));

        // Add tiles to cover multiple districts with different heights
        District district1 = new District(new Point3D(0, 0), DistrictColor.RED);
        District district2 = new District(new Point3D(-1, -1), DistrictColor.BLUE);
        District district3 = new District(new Point3D(1, -1), DistrictColor.GREEN);
        District district4 = new District(new Point3D(0, 2), DistrictColor.BLUE);
        District district5 = new District(new Point3D(-1, 3), DistrictColor.BLUE);
        District district6 = new District(new Point3D(1, 3), DistrictColor.BLUE);
        Tile tile1 = new Tile(district1, district2, district3);
        Tile tile2 = new Tile(district4, district5, district6);
        board.addTile(tile1);
        board.addTile(tile2);

        // Add more tiles to create complex overlapping scenarios
        // Add more complex tile configurations to test scoring
        District district7 = new District(new Point3D(2, 0), DistrictColor.RED);
        District district8 = new District(new Point3D(1, -1), DistrictColor.BLUE);
        District district9 = new District(new Point3D(3, -1), DistrictColor.GREEN);
        District district10 = new District(new Point3D(2, 2), DistrictColor.BLUE);
        District district11 = new District(new Point3D(3, 3), DistrictColor.BLUE);
        District district12 = new District(new Point3D(4, 3), DistrictColor.BLUE);
        Tile tile3 = new Tile(district7, district8, district9);
        Tile tile4 = new Tile(district10, district11, district12);
        board.addTile(tile3);
        board.addTile(tile4);

        // Ensure that the score is calculated correctly
        assertEquals(13, board.getScore(player));
    }
}
