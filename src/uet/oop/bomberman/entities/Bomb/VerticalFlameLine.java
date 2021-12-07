package uet.oop.bomberman.entities.Bomb;

import uet.oop.bomberman.Base.GameMap;
import uet.oop.bomberman.entities.Entity;
import uet.oop.bomberman.Motion.Movement;
import uet.oop.bomberman.Base.Point;
import uet.oop.bomberman.entities.MovableEntities.MovableEntity;
import uet.oop.bomberman.entities.StillEntities.BreakableStillObject;
import uet.oop.bomberman.entities.StillEntities.Brick;
import uet.oop.bomberman.entities.StillEntities.StableStillObject.Wall;
import uet.oop.bomberman.graphics.ImageLists;
import uet.oop.bomberman.graphics.Sprite;

public class VerticalFlameLine extends FlameLine {
    public VerticalFlameLine(int xOrigin, int yOrigin, int direction, int rangeUnit)
            throws Exception {

        super(xOrigin, yOrigin, rangeUnit);

        if (direction != Movement.UP && direction != Movement.DOWN) {
            throw new Exception("VerticalFlameLine Construction failed");
        }

        this.flame_images = ImageLists.explosionVerticalImages;

        if (direction == Movement.UP) last_flame_images = ImageLists.explosionVerticalTopLastImages;
        if (direction == Movement.DOWN) last_flame_images = ImageLists.explosionVerticalBottomLastImages;

        this.direction = direction;

        addAllFlameSegments();
    }

    @Override
    protected void addAllFlameSegments() {
        if (this.direction == Movement.DOWN) {
            int i = 1;
            for (; i < range_unit; i++) {
                //check whether there's a Wall or a Brick laying on flame line.
                if (GameMap.containsStillObjectAt(x, y + i * Sprite.SCALED_SIZE)) {

                    Entity entity = GameMap.getStillObjectAt(x, y + i * Sprite.SCALED_SIZE);

                    if (entity instanceof Wall || entity instanceof Brick) break;
                }
                flameSegments.add(new Point(x, y + i * Sprite.SCALED_SIZE));
            }
            lastFlameSegment = new Point(x, y + i * Sprite.SCALED_SIZE);
        }

        if (this.direction == Movement.UP) {
            int i = 1;
            for (; i < range_unit; i++) {
                //check whether there's a Wall or a Brick laying on flame line.
                if (GameMap.containsStillObjectAt(x, y - i * Sprite.SCALED_SIZE)) {

                    Entity entity = GameMap.getStillObjectAt(x, y - i * Sprite.SCALED_SIZE);

                    if (entity instanceof Wall || entity instanceof Brick) break;
                }
                flameSegments.add(new Point(x, y - i * Sprite.SCALED_SIZE));
            }
            lastFlameSegment = new Point(x, y - i * Sprite.SCALED_SIZE);
        }
    }

    @Override
    protected void burn() throws NullPointerException {
        //temporary variables
        MovableEntity movableEntity;
        BreakableStillObject object;
        Bomb bomb;

        try {
            //body of a flame line
            //still objects able to be broken here is items only.
            for (Point flame : flameSegments) {
                boolean canDiscard = false;

                for (int i = 0; i < Sprite.SCALED_SIZE; i++) {
                    if (canDiscard) break;

                    {
                        object = GameMap.getBreakableStillObjectAt(flame.x, flame.y + i);
                        movableEntity = GameMap.getMovableEntityAt(null,
                                flame.x, flame.y + i);
                        bomb = GameMap.getBombAt(flame.x, flame.y + i);

                        if (object != null) {
                            canDiscard = true;
                            object.setBroken(true);
                        }
                        if (movableEntity != null) {
                            canDiscard = true;
                            if (!movableEntity.isDead()) killed_enemy_list.add(movableEntity);//add once
                            movableEntity.setDead(true);
                        }
                        if (bomb != null) {
                            canDiscard = true;
                            bomb.setTimeOver(true);
                        }
                    }

                    {
                        object = GameMap.getBreakableStillObjectAt(flame.x, flame.y + i);
                        movableEntity = GameMap.getMovableEntityAt(null,
                                flame.x + Sprite.SCALED_SIZE - 1, flame.y + i);
                        bomb = GameMap.getBombAt(flame.x, flame.y + i);

                        if (object != null) {
                            canDiscard = true;
                            object.setBroken(true);
                        }
                        if (movableEntity != null) {
                            canDiscard = true;
                            if (!movableEntity.isDead()) killed_enemy_list.add(movableEntity);//add once
                            movableEntity.setDead(true);
                        }
                        if (bomb != null) {
                            canDiscard = true;
                            bomb.setTimeOver(true);
                        }
                    }
                }
            }

            //last flame
            for (int i = 0; i < Sprite.SCALED_SIZE; i++) {
                {
                    movableEntity = GameMap.getMovableEntityAt(null,
                            lastFlameSegment.x, lastFlameSegment.y + i);
                    object = GameMap.getBreakableStillObjectAt(lastFlameSegment.x,
                            lastFlameSegment.y + i);
                    bomb = GameMap.getBombAt(lastFlameSegment.x, lastFlameSegment.y + i);

                    if (movableEntity != null) {
                        if (!movableEntity.isDead()) killed_enemy_list.add(movableEntity);//add once
                        movableEntity.setDead(true);
                    }
                    if (object != null) object.setBroken(true);
                    if (bomb != null) bomb.setTimeOver(true);
                }

                {
                    movableEntity = GameMap.getMovableEntityAt(null,
                            lastFlameSegment.x + Sprite.SCALED_SIZE - 1, lastFlameSegment.y + i);
                    object = GameMap.getBreakableStillObjectAt(lastFlameSegment.x + Sprite.SCALED_SIZE - 1,
                            lastFlameSegment.y + i);
                    bomb = GameMap.getBombAt(lastFlameSegment.x + Sprite.SCALED_SIZE - 1,
                            lastFlameSegment.y + i);

                    if (movableEntity != null) {
                        if (!movableEntity.isDead()) killed_enemy_list.add(movableEntity);//add once
                        movableEntity.setDead(true);
                    }
                    if (object != null) object.setBroken(true);
                    if (bomb != null) bomb.setTimeOver(true);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
