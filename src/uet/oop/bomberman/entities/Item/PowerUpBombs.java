package uet.oop.bomberman.entities.Item;

import uet.oop.bomberman.entities.MovableEntities.Bomber;
import uet.oop.bomberman.graphics.Sprite;

/**
 * Increase the number of bombs placed concurrently of a character.
 */
public class PowerUpBombs extends Item {
    public PowerUpBombs(int xUnit, int yUnit) {
        super(xUnit, yUnit, Sprite.powerup_bombs.getFxImage());
    }

    @Override
    public void update() {

    }

    @Override
    public void buff(Bomber owner) {
        super.buff(owner);
        owner.increaseBombCapacity();
    }
}
