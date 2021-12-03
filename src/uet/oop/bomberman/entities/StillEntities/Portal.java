package uet.oop.bomberman.entities.StillEntities;

import javafx.scene.image.Image;
import uet.oop.bomberman.BombermanGame;

public class Portal extends BreakableStillObject {
    public Portal(int xUnit, int yUnit, Image image) {
        super(xUnit, yUnit, image);
    }

    @Override
    public void update() {
        updateImage();
        if (broken) deleted = true;
    }

    @Override
    protected void updateImage() {
        if (broken || deleted) this.img = null;
    }
}
