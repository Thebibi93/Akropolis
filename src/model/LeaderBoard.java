package model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.util.Collections;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LeaderBoard {
    private LinkedHashMap<String, LinkedHashMap<String, Integer>> scores;

    public LeaderBoard() {
        scores = new LinkedHashMap<>();
        try {
            InputStream i = getClass().getResourceAsStream("/save/leaderBoard.save");
            BufferedReader reader = new BufferedReader(new InputStreamReader(i));
            String line;
            String currentMode = null;
            while ((line = reader.readLine()) != null) {
                if (line.endsWith("PLAYER")) {
                    currentMode = line;
                    scores.put(currentMode, new LinkedHashMap<>());
                } else if (currentMode != null) {
                    String[] parts = line.split("/");
                    if (parts.length > 1) {
                        String[] nameParts = parts[0].split("-");
                        String playerName = nameParts[nameParts.length - 1]; // Extract the actual player name
                        scores.get(currentMode).put(playerName, Integer.parseInt(parts[1]));
                    }
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds a score to the leaderboard.
     *
     * @param mode       The mode of the game.
     * @param score      The score to add.
     * @param playerName The name of the player.
     */
    public void addScore(String mode, int score, String playerName) {
        if (!scores.containsKey(mode)) {
            scores.put(mode, new LinkedHashMap<>());
        }
        Map<String, Integer> playerScores = scores.get(mode);
        if (playerScores.size() == 5) {
            String lowestScorePlayer = Collections.min(playerScores.entrySet(), Map.Entry.comparingByValue()).getKey();
            if (score > playerScores.get(lowestScorePlayer)) {
                playerScores.remove(lowestScorePlayer);
                playerScores.put(playerName, score);
            }
        } else if (!playerScores.containsKey(playerName) || playerScores.get(playerName) < score) {
            playerScores.put(playerName, score);
        }
        saveScores();
    }

    /**
     * Saves the scores to a file.
     */
    private void saveScores() {
        try {
            File file = new File("./res/save/leaderBoard.save");
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            for (Map.Entry<String, LinkedHashMap<String, Integer>> entry : scores.entrySet()) {
                writer.write(entry.getKey() + "\n");
                Map<String, Integer> playerScores = entry.getValue();
                List<Map.Entry<String, Integer>> scoreList = new ArrayList<>(playerScores.entrySet());
                scoreList.sort((a, b) -> b.getValue().compareTo(a.getValue())); // Sort in descending order
                int rank = 1;
                for (Map.Entry<String, Integer> playerScore : scoreList) {
                    if (rank > 5)
                        break; // Write only top 5 scores
                    writer.write(rank + "-" + playerScore.getKey() + "/" + playerScore.getValue() + "\n");
                    rank++;
                }
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Resets the leaderboard.
     */
    public void resetLeaderBoard() {
        scores.clear();
        // Add the goofy default scores
        LinkedHashMap<String, Integer> onePlayerScores = new LinkedHashMap<>();
        scores.put("ONE_PLAYER", onePlayerScores);
        onePlayerScores.put("Lasagna", 100);
        onePlayerScores.put("Zangief", 60);
        onePlayerScores.put("BarkBark", 50);
        onePlayerScores.put("ChickenMH", 45);
        onePlayerScores.put("Lezgo", 15);
        LinkedHashMap<String, Integer> twoPlayerScores = new LinkedHashMap<>();
        scores.put("TWO_PLAYER", twoPlayerScores);
        twoPlayerScores.put("JohnAkropolis", 60);
        twoPlayerScores.put("Suckaball", 40);
        twoPlayerScores.put("Jeej", 30);
        twoPlayerScores.put("Shrek", 25);
        twoPlayerScores.put("Ligma", 20);
        LinkedHashMap<String, Integer> threePlayerScores = new LinkedHashMap<>();
        scores.put("THREE_PLAYER", threePlayerScores);
        threePlayerScores.put("CacaProut", 50);
        threePlayerScores.put("Kiryu", 48);
        threePlayerScores.put("DGSI", 40);
        threePlayerScores.put("Dragunov", 35);
        threePlayerScores.put("FREEROBUX", 10);
        LinkedHashMap<String, Integer> fourPlayerScores = new LinkedHashMap<>();
        scores.put("FOUR_PLAYER", fourPlayerScores);
        fourPlayerScores.put("FreeCandy", 40);
        fourPlayerScores.put("LikeAndSub", 30);
        fourPlayerScores.put("THEGOAT", 25);
        fourPlayerScores.put("EarLicker", 10);
        fourPlayerScores.put("Nvidia", 0);
        saveScores();
    }

    /**
     * Gets the scores for a specific mode.
     *
     * @param mode The mode of the game.
     * @return The scores for the mode.
     */
    public LinkedHashMap<String, Integer> getScores(String mode) {
        return scores.get(mode);
    }

    // Test
    public static void main(String[] args) {
        LeaderBoard leaderBoard = new LeaderBoard();
        Map<String, Integer> scores = leaderBoard.getScores("ONE_PLAYER");
        leaderBoard.addScore("ONE_PLAYER", 50, "TOTO");
        for (Map.Entry<String, Integer> entry : scores.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
    }
}
