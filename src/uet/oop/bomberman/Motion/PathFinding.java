package uet.oop.bomberman.Motion;

import uet.oop.bomberman.Base.Point;

import java.util.*;

/**
 * This class uses A* algorithm for finding a path leads to players.
 */
public class PathFinding {
    private int xSourceUnit, ySourceUnit;
    private int xTargetUnit, yTargetUnit;

    private int height;
    private int width;

    private Cell[][] grid;

    private PriorityQueue<Cell> openList;
    private boolean[][] closedList;

    private LinkedList<Point> positionList = new LinkedList<>();

    private boolean existPath = true;

    public PathFinding(int xSourceUnit, int ySourceUnit, int xTargetUnit, int yTargetUnit, char[][] baseMap) throws Exception {
        if (baseMap == null || baseMap[0] == null) throw new Exception("Cannot instantiate a pathFinding");

        this.xSourceUnit = xSourceUnit;
        this.ySourceUnit = ySourceUnit;
        this.xTargetUnit = xTargetUnit;
        this.yTargetUnit = yTargetUnit;

        height = baseMap.length;
        width = baseMap[0].length;

        grid = new Cell[height][width];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (baseMap[y][x] == ' ') {
                    //instantiate new object
                    grid[y][x] = new Cell(x, y);
                }
                else grid[y][x] = null;
//                System.out.print(baseMap[y][x]);
            }
//            System.out.println();
        }

        openList = new PriorityQueue<>((Cell cell_1, Cell cell_2) -> {
            return Integer.compare(cell_1.fCost, cell_2.fCost);
        });
        closedList = new boolean[height][width];
        
        process();
    }

    private void compareAndUpdateCell(Cell current, Cell adjCell, int new_gCost) {
        if (adjCell == null || closedList[adjCell.y][adjCell.x]
            || current == null) return;

        if (!openList.contains(adjCell) || new_gCost < adjCell.gCost) {
            adjCell.gCost = new_gCost;
            adjCell.fCost = adjCell.hCost + new_gCost;
            adjCell.parent = current;

            if (!openList.contains(adjCell)) openList.add(adjCell);
        }
    }

    private void process() {
        openList.add(grid[ySourceUnit][xSourceUnit]);
        Cell current;

        while ((current = openList.poll()) != null) {
            closedList[current.y][current.x] = true;

            if (current == grid[yTargetUnit][xTargetUnit]) break;

            Cell adjCell;

            if (current.x + 1 < width) {
                adjCell = grid[current.y][current.x + 1];
                compareAndUpdateCell(current, adjCell, current.gCost + 1);
            }
            if (current.x - 1 >= 0) {
                adjCell = grid[current.y][current.x - 1];
                compareAndUpdateCell(current, adjCell, current.gCost + 1);
            }
            if (current.y + 1 < height) {
                adjCell = grid[current.y + 1][current.x];
                compareAndUpdateCell(current, adjCell, current.gCost + 1);
            }
            if (current.y - 1 >= 0) {
                adjCell = grid[current.y - 1][current.x];
                compareAndUpdateCell(current, adjCell, current.gCost + 1);
            }
        }

        if (current == null || current != grid[yTargetUnit][xTargetUnit]) {
            existPath = false;
            return;
        }

        //add all positionList needed to move in order
        Cell tmpCell = grid[yTargetUnit][xTargetUnit];
        while (tmpCell != null) {
            positionList.add(new Point(tmpCell.x, tmpCell.y));
            tmpCell = tmpCell.parent;
        }
        Collections.reverse(positionList);
    }

    public LinkedList<Point> getOrderedPositionList() {
        return positionList;
    }

    //for 4 types of direction: UP, DOWN, LEFT, RIGHT
    public LinkedList<Integer> getOrderedDirectionList() {
        LinkedList<Integer> directionList = new LinkedList<>();

        for (int i = 1; i < positionList.size(); i++) {
            if (positionList.get(i).x > positionList.get(i - 1).x) directionList.add(Movement.RIGHT);
            else if (positionList.get(i).x < positionList.get(i - 1).x) directionList.add(Movement.LEFT);
            else if (positionList.get(i).y > positionList.get(i - 1).y) directionList.add(Movement.DOWN);
            else if (positionList.get(i).y < positionList.get(i - 1).y) directionList.add(Movement.UP);
        }

        return directionList;
    }

    public boolean isFound() {
        return existPath;
    }

    public int getDistance() {
        return positionList.size();
    }


    private class Cell {
        public int x, y;

        public int fCost = 0;
        public int gCost = 0;
        public int hCost = 0;

        Cell parent;

        public void hEstimate() {
            hCost = Math.max(Math.abs(x - xTargetUnit), Math.abs(y - yTargetUnit));
        }

        public Cell(int x, int y) {
            this.x = x;
            this.y = y;

            hEstimate();
        }
    }

//    public static void main(String[] args) throws Exception {
//        Point source = new Point(1, 1);
//        Point target = new Point(5, 3);
//
//        String baseMap = "###############################\n" +
//                         "#   b  ** *  1 * 2 *  * * *   #\n" +
//                         "#     #*# #  *#*# # # #*#*#*# #\n" +
//                         "#  x* s   *    *      * 2 * * #\n" +
//                         "#   #   # #*# # #*#*# # # # #*#\n" +
//                         "#f    s    x **  *  *   1     #\n" +
//                         "# # # # # # # # # #*# #*# # # #\n" +
//                         "#*  *  1 b *  *      *        #\n" +
//                         "# # # # #*# # # #*#*# # # # # #\n" +
//                         "#*    **  *       *     1     #\n" +
//                         "# #*# # # # # # #*# # # # # # #\n" +
//                         "#    f      *   *  *          #\n" +
//                         "###############################";
//        String[] b = baseMap.split("\n");
//
//        char[][] map = new char[13][31];
//        for (int y = 0; y < map.length; y++) {
//            map[y] = b[y].toCharArray();
//        }
//
//        for (int y = 0; y < map.length; y++) {
//            for (int x = 0; x < map[0].length; x++) {
//                System.out.print(map[y][x]);
//            }
//            System.out.println();
//        }
//
//        PathFinding p = new PathFinding(source.x, source.y, target.x, target.y, map);
//
//        for (Point point : p.positionList) {
//            map[point.y][point.x] = '.';
//        }
//
//        for (int y = 0; y < map.length; y++) {
//            for (int x = 0; x < map[y].length; x++) {
//                System.out.print(map[y][x]);
//            }
//            System.out.println();
//        }
//    }
}
