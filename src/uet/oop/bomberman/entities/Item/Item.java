package uet.oop.bomberman.entities.Item;

import javafx.scene.image.Image;
import uet.oop.bomberman.Base.GameMap;
import uet.oop.bomberman.entities.Entity;
import uet.oop.bomberman.entities.MovableEntities.Bomber;

public abstract class Item extends Entity {
    boolean pickedUp = false;

    public Item(int xUnit, int yUnit, Image img) {
        super(xUnit, yUnit, img);
    }

    // Not used
    public boolean isPickedUp() {
        return pickedUp;
    }

    // Invoke when picked up by bomber
    public void buff(Bomber owner) {
        this.pickedUp = true;
        GameMap.removeItem(this);
    }
}
