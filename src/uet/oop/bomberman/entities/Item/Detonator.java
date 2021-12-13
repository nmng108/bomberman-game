package uet.oop.bomberman.entities.Item;

import uet.oop.bomberman.entities.MovableEntities.Bomber;
import uet.oop.bomberman.graphics.Sprite;

public class Detonator extends Item {
    public Detonator(int xUnit, int yUnit) {
        super(xUnit, yUnit, Sprite.powerup_detonator.getFxImage());
    }

    @Override
    public void update() {
    }

    @Override
    public void buff(Bomber owner) {
        super.buff(owner);
        owner.receiveDetonator();
    }

}
