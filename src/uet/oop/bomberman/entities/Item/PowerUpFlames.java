package uet.oop.bomberman.entities.Item;

import uet.oop.bomberman.entities.MovableEntities.Bomber;
import uet.oop.bomberman.graphics.Sprite;

public class PowerUpFlames extends Item {
    @Override
    public void update() {
    }

    public PowerUpFlames(int xUnit, int yUnit) {
        super(xUnit, yUnit, Sprite.powerup_flames.getFxImage());
    }

    @Override
    public void buff(Bomber owner) {
        super.buff(owner);
        owner.increaseExplosionRange();
    }
}
