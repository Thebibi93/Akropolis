package controller;

import model.Board;
import model.Player;
import view.BoardUI;
import java.beans.PropertyChangeEvent;

public class UIController extends Controller {

    public UIController(Board model, BoardUI boardUI) {
        super(model, boardUI);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();
        if (propertyName.equals("playerUpdated ")) {
            updatePlayerInfo();
        } else if (propertyName.equals("tilesRemainingUpdated")) {
            updateRemainingTilesInfo();
        }
    }

    public void updatePlayerInfo() {
        Player currentPlayer = ((Board)model).getCurrentPlayer();
        BoardUI boardUI = (BoardUI)view;
        boardUI.setPlayer(currentPlayer.getName());
        boardUI.setRock(currentPlayer.getResources());
    }

    public void updateRemainingTilesInfo() {
        BoardUI boardUI = (BoardUI)view;
        System.out.println(((Board)model).getStackTiles().size());
        int remainingTiles = (((Board)model).getStackTiles()).size();
        boardUI.setRemainingTiles(remainingTiles+1);
    }
}

