package uet.oop.bomberman.Base;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import uet.oop.bomberman.entities.Entity;
import uet.oop.bomberman.entities.MovableEntities.Balloon;
import uet.oop.bomberman.entities.MovableEntities.Bomber;
import uet.oop.bomberman.entities.MovableEntities.MovableEntity;
import uet.oop.bomberman.entities.MovableEntities.OneAl;

import java.util.ArrayList;
import java.util.List;

public class ScoreManagement {
    public static final int BALLOON = 3;
    public static final int ONEAL = 5;
    public static final int PLAYER = 10;

    private List<Integer> player_ID_list;
    private List<Score> scores_array = new ArrayList<>();

    public ScoreManagement(List<Integer> player_ID_list) {
        this.player_ID_list = player_ID_list;

        for (int i = 0; i < player_ID_list.size(); i++) {
            scores_array.add(new Score());
        }
    }

//    public int getScore() {
//        return scores_array;
//    }

//    public void add(int scores_array) {
//        this.scores_array += scores_array;
//    }

    public void add(int player_ID,  List<MovableEntity> killedEnemies) {
        int index = player_ID_list.indexOf(player_ID);

        for (Entity enemy : killedEnemies) {
            if (enemy instanceof Balloon) scores_array.get(index).current += ScoreManagement.BALLOON; //still got error
            else if (enemy instanceof OneAl) scores_array.get(index).current += ScoreManagement.ONEAL;
            else if (enemy instanceof Bomber) {
                if (((Bomber) enemy).getID() == player_ID) {
                    scores_array.get(index).current += ScoreManagement.PLAYER;
                    System.out.println("1 PLAYER killed");
                }
            }
            else System.out.println("SCORE did not increase.");
        }
    }

    public void render(GraphicsContext gc) {
        gc.setFill(Color.YELLOWGREEN);
        gc.fillText("Điểm: "+ scores_array.get(0).current, 50, 485, 200);
    }

    public int[] getScoreArray() {
        int[] arr= new int[scores_array.size()];

        for (int i = 0; i< scores_array.size(); i++) {
            arr[i] = scores_array.get(i).current;
        }

        return arr;
    }

    public List<Score> getScores_array() {
        return scores_array;
    }

    public void printScore(Label label) {
        if (scores_array.get(0).isDifferentFromPrevScore()) {
            label.setText("");
            label.setText("Điểm: " + scores_array.get(0).current);
        }
    }

    private class Score {
        int current = 0;
        int previous = 0;

        public Score() {}

        public boolean isDifferentFromPrevScore() {
            return current != previous;
        }


        public void getNewScore() {
            if (isDifferentFromPrevScore()) System.out.println("Current scores: " + current);
            previous = current;
        }
    }
}
