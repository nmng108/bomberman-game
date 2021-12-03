package uet.oop.bomberman.entities.StillEntities;

import javafx.scene.image.Image;
import uet.oop.bomberman.entities.Entity;

public class Grass extends Entity {

    public Grass(int xUnit, int yUnit, Image img) {
        super(xUnit, yUnit, img);
    }

    @Override
    public void update() {
        //do nothing
    }
}
