package uet.oop.bomberman.graphics;

import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.List;

public class ImageLists {
    public static List<Image> playerMovingUpImages = new ArrayList<>() {{
        add(Sprite.player_up.getFxImage());
        add(Sprite.player_up_1.getFxImage());
        add(Sprite.player_up_2.getFxImage());
    }};

    public static List<Image> playerMovingDownImages = new ArrayList<>() {{
        add(Sprite.player_down.getFxImage());
        add(Sprite.player_down_1.getFxImage());
        add(Sprite.player_down_2.getFxImage());
    }};

    public static List<Image> playerMovingLeftImages = new ArrayList<>() {{
        add(Sprite.player_left.getFxImage());
        add(Sprite.player_left_1.getFxImage());
        add(Sprite.player_left_2.getFxImage());
    }};

    public static List<Image> playerMovingRightImages = new ArrayList<>() {{
        add(Sprite.player_right.getFxImage());
        add(Sprite.player_right_1.getFxImage());
        add(Sprite.player_right_2.getFxImage());
    }};

    public static List<Image> playerDeadImages = new ArrayList<>() {{
        add(Sprite.player_dead1.getFxImage());
        add(Sprite.player_dead2.getFxImage());
        add(Sprite.player_dead3.getFxImage());
    }};

    public static List<Image> balloonLeftImages = new ArrayList<>() {{
        add(Sprite.balloom_left1.getFxImage());
        add(Sprite.balloom_left2.getFxImage());
        add(Sprite.balloom_left3.getFxImage());
    }};

    public static List<Image> balloonRightImages = new ArrayList<>() {{
        add(Sprite.balloom_right1.getFxImage());
        add(Sprite.balloom_right2.getFxImage());
        add(Sprite.balloom_right3.getFxImage());
    }};

    public static List<Image> bombImages = new ArrayList<>() {{
        add(Sprite.bomb.getFxImage());
        add(Sprite.bomb_1.getFxImage());
        add(Sprite.bomb_2.getFxImage());
    }};

    public static List<Image> bombExplodedImages = new ArrayList<>() {{
       add(Sprite.bomb_exploded.getFxImage());
       add(Sprite.bomb_exploded1.getFxImage());
       add(Sprite.bomb_exploded2.getFxImage());
    }};

    public static List<Image> explosionHorizontalImages = new ArrayList<>() {{
       add(Sprite.explosion_horizontal.getFxImage());
       add(Sprite.explosion_horizontal1.getFxImage());
       add(Sprite.explosion_horizontal2.getFxImage());
    }};

    public static List<Image> explosionVerticalImages = new ArrayList<>() {{
       add(Sprite.explosion_vertical.getFxImage());
       add(Sprite.explosion_vertical1.getFxImage());
       add(Sprite.explosion_vertical2.getFxImage());
    }};

    public static List<Image> explosionHorizontalLeftLastImages = new ArrayList<>() {{
       add(Sprite.explosion_horizontal_left_last.getFxImage());
       add(Sprite.explosion_horizontal_left_last1.getFxImage());
       add(Sprite.explosion_horizontal_left_last2.getFxImage());
    }};

    public static List<Image> explosionHorizontalRightLastImages = new ArrayList<>() {{
       add(Sprite.explosion_horizontal_right_last.getFxImage());
       add(Sprite.explosion_horizontal_right_last1.getFxImage());
       add(Sprite.explosion_horizontal_right_last2.getFxImage());
    }};

    public static List<Image> explosionVerticalTopLastImages = new ArrayList<>() {{
       add(Sprite.explosion_vertical_top_last.getFxImage());
       add(Sprite.explosion_vertical_top_last1.getFxImage());
       add(Sprite.explosion_vertical_top_last2.getFxImage());
    }};

    public static List<Image> explosionVerticalBottomLastImages = new ArrayList<>() {{
       add(Sprite.explosion_vertical_down_last.getFxImage());
       add(Sprite.explosion_vertical_down_last1.getFxImage());
       add(Sprite.explosion_vertical_down_last2.getFxImage());
    }};

    public static List<Image> oneAlLeftImages = new ArrayList<>() {{
        add(Sprite.oneal_left1.getFxImage());
        add(Sprite.oneal_left2.getFxImage());
        add(Sprite.oneal_left3.getFxImage());
    }};

    public static List<Image> oneAlRightImages = new ArrayList<>() {{
        add(Sprite.oneal_right1.getFxImage());
        add(Sprite.oneal_right2.getFxImage());
        add(Sprite.oneal_right3.getFxImage());
    }};
}