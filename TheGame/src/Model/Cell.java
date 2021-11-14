package Model;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

public class Cell extends Rectangle {
    public int x, y;
    // Tell that cell belong to part of the ship
    public Ship ship = null;
    public boolean wasShot = false;
    private Board board;

    public Cell(int x, int y, Board board) {
        super(30,30);
        this.x = x;
        this.y = y;
        this.board = board;
        Image img = new Image("Assets/tile/tile_map.png");
        setFill(new ImagePattern(img));
        setStroke(Color.BLACK);
    }

    public boolean shoot() {
        wasShot = true;
        Image img1 = new Image("Assets/tile/hit.png");
        setFill(new ImagePattern(img1));

        if (ship != null) {
            ship.hit();
            Image img = new Image("Assets/tile/explosion.png");
            setFill(new ImagePattern(img));
            if (!ship.isAlive()) {
                board.shipNumber--;
            }
            return true;
        }

        return false;
    }
}