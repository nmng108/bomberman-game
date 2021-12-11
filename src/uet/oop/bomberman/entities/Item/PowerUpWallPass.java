package uet.oop.bomberman.entities.Item;

import uet.oop.bomberman.entities.MovableEntities.Bomber;
import uet.oop.bomberman.graphics.Sprite;

public class PowerUpWallPass extends Item {
    public PowerUpWallPass(int xUnit, int yUnit) {
        super(xUnit, yUnit, Sprite.powerup_wallpass.getFxImage());
    }

    @Override
    public void update() {
    }

    @Override
    public void buff(Bomber owner) {
        super.buff(owner);
        owner.receiveFlashItem();
    }
}
