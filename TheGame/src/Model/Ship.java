package Model;

import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;


public class Ship extends Parent {
    //Define asset location

    public int size;
    public String type, color;
    public boolean vertical = true;
    private int health;

    public Ship(int size, boolean vertical) {
        this.size = size;
        this.vertical = vertical;
        this.type = type;
        health = size;

    }

    public void hit() {
        health--;

    }

    public boolean isAlive() {
        return health > 0;
    }

    public boolean isVertical() {

        return vertical;
    }
}

