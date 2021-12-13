package uet.oop.bomberman.entities.Item;

import uet.oop.bomberman.entities.MovableEntities.Bomber;
import uet.oop.bomberman.graphics.Sprite;

public class PowerUpFlamePass extends Item {
    public PowerUpFlamePass(int xUnit, int yUnit) {
        super(xUnit, yUnit, Sprite.powerup_flamepass.getFxImage());
    }

    @Override
    public void update() {
    }

    @Override
    public void buff(Bomber owner) {
        super.buff(owner);
        owner.setFlamePassAbility(true);
    }
}
