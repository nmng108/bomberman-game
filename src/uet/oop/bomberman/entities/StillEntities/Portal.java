package uet.oop.bomberman.entities.StillEntities;

import uet.oop.bomberman.Base.GameMap;
import uet.oop.bomberman.Base.Point;
import uet.oop.bomberman.entities.MovableEntities.Bomber;
import uet.oop.bomberman.entities.MovableEntities.OneAl;
import uet.oop.bomberman.graphics.Sprite;


public class Portal extends BreakableStillObject {
    private boolean open = false;

    public Portal(int xUnit, int yUnit) {
        super(xUnit, yUnit, Sprite.portal.getFxImage());
    }

    @Override
    public void update() {
        if (open) {
            if (GameMap.getOpenPortals().size() != 2) return;

            for (Bomber player : GameMap.getPlayers()) {
                Point player_position = GameMap.getMostStandingCell(player.getX(), player.getY());

                if (this.x / Sprite.SCALED_SIZE == player_position.x
                        && this.y / Sprite.SCALED_SIZE == player_position.y) {
                    if (player.hasTeleported()) return;

                    int portal_index = GameMap.getOpenPortals().indexOf(this);

                    Portal other_portal;
                    if (portal_index == 1) other_portal = GameMap.getOpenPortals().get(0);
                    else other_portal = GameMap.getOpenPortals().get(1);

                    player.teleportTo(other_portal);
                }
            }

            return;
        }

        if (broken) {
            if (open) return;

            this.open = true;
            GameMap.removeStillObject(this);
            GameMap.addOpenPortal(this);
            spawnOneAl(2);
        }
    }

    @Override
    protected void updateImage() {
    }

    public void spawnOneAl(int quantity) {
        int xUnit = this.x / Sprite.SCALED_SIZE;
        int yUnit = this.y / Sprite.SCALED_SIZE;

        for (int i = 0; i < quantity; i++) {
            try {
                GameMap.addBot(new OneAl(xUnit, yUnit, 40,
                        100));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
