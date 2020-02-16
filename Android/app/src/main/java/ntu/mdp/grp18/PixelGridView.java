package ntu.mdp.grp18;

import android.content.Context;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.nio.charset.Charset;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class PixelGridView extends View {

    // Declarations
    private int numColumns, numRows;
    private int cellWidth, cellHeight;
    private int frontStartPos, backStartPos, leftStartPos, rightStartPos;
    private int frontCurPos, backCurPos, leftCurPos, rightCurPos;
    private int robotDirection;
    private int[] startCoord = new int[2];
    private ArrayList<String[]> arrowImageCoords = new ArrayList<>();
    private boolean autoUpdate;
    private int[] curCoord = new int[2];
    private int[] wayPoint;
    private boolean selectStartPosition = false, selectWayPoint = false;
    private Paint blackPaint = new Paint();
    private Paint cyanPaint = new Paint();
    private Paint redPaint = new Paint();
    private Paint greenPaint = new Paint();
    private Paint yellowPaint = new Paint();
    private Paint grayPaint = new Paint();
    private Paint bluePaint = new Paint();
    private Paint lightGrayPaint = new Paint();
    private Paint obstaclePaint = new Paint();
    private Paint waypointPaint = new Paint();
    private boolean[][] cellExplored;
    private boolean[][] obstacles;

    public PixelGridView(Context context) {
        this(context, null);
    }

    //Initializing the colors
    @RequiresApi(api = Build.VERSION_CODES.O)
    public PixelGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        blackPaint.setColor(ContextCompat.getColor(context, R.color.dark_gray));
        obstaclePaint.setColor(ContextCompat.getColor(context, R.color.black));
        waypointPaint.setColor(ContextCompat.getColor(context, R.color.aquamarine));
        redPaint.setColor(ContextCompat.getColor(context, R.color.tomato));
        grayPaint.setColor(ContextCompat.getColor(context, R.color.floral_white));
        yellowPaint.setColor(ContextCompat.getColor(context, R.color.slate_gray));
        greenPaint.setColor(ContextCompat.getColor(context, R.color.pale_turquoise));
        bluePaint.setColor(ContextCompat.getColor(context, R.color.pink));
        cyanPaint.setColor(ContextCompat.getColor(context, R.color.red));
        lightGrayPaint.setColor(ContextCompat.getColor(context, R.color.cold));
    }

    //Initializing the map
    public void initializeMap() {
        this.setNumColumns(15);
        this.setNumRows(20);
        this.setAutoUpdate(true);

        this.obstacles = new boolean[this.getNumColumns()][this.getNumRows()];
        this.cellExplored = new boolean[this.getNumColumns()][this.getNumRows()];

        this.setStartCoord(1, 1);
        this.setStartPos(17, 0, 19, 2);
        this.setRobotDirection(0);
    }

    //Getters and Setters
    public int getNumColumns() {
        return numColumns;
    }

    public void setNumColumns(int numColumns) {
        this.numColumns = numColumns;
        calculateDimensions();
    }

    public int getNumRows() {
        return numRows;
    }

    public void setNumRows(int numRows) {
        this.numRows = numRows;
        calculateDimensions();
    }

    // Get Auto update
    public boolean getAutoUpdate() {
        return this.autoUpdate;
    }

    // Set Auto-update
    public void setAutoUpdate(boolean autoUpdate) {
        this.autoUpdate = autoUpdate;
    }

    // Set obstacle/update obstacle
    public void setObstacle(int i, int j, boolean obstacle) {
        this.obstacles[i][j] = obstacle;
    }

    // Set cell as Explored depending upon the parameters
    public void setCellExplored(int column, int row, boolean explored) {
        this.cellExplored[column][row] = explored;
    }

    // Get cell Explored
    public boolean[][] getCellExplored() {
        return this.cellExplored;
    }

    // Get obstacle
    public boolean[][] getObstacles() {
        return this.obstacles;
    }

    // Get Robot Direction
    public int getRobotDirection() {
        return this.robotDirection;
    }

    // Get Start Point coordinates
    public int[] getStartCoord() {
        return this.startCoord;
    }

    // Get current position coordinates
    public int[] getCurCoord() {
        return this.curCoord;
    }

    // Move current position coordinates
    public void moveCurCoord(int xInc, int yInc) {
        this.setCurPos(this.getCurCoord()[1] + yInc, this.getCurCoord()[0] + xInc);
    }

    // Get current position in the form of an array
    public int[] getCurPos() {
        int[] pos = new int[4];
        pos[0] = this.frontCurPos;
        pos[1] = this.leftCurPos;
        pos[2] = this.backCurPos;
        pos[3] = this.rightCurPos;

        return pos;
    }

    // Set current position (integers)
    public void setCurPos(int row, int column) {
        int[] edges = convertRobotPosToEdge(row, column);
        this.frontCurPos = edges[0];
        this.leftCurPos = edges[1];
        this.backCurPos = edges[2];
        this.rightCurPos = edges[3];
        this.curCoord[0] = column;
        this.curCoord[1] = row;

        this.refreshMap(this.getAutoUpdate());
    }

    // Set current position (integer array)
    public void setCurPos(int[] pos) {
        this.frontCurPos = pos[0];
        this.leftCurPos = pos[1];
        this.backCurPos = pos[2];
        this.rightCurPos = pos[3];

        this.refreshMap(this.getAutoUpdate());
    }

    // Convert Robot Position to Edge
    public int[] convertRobotPosToEdge(int row, int column) {
        int rowFormatConvert = inverseRowCoord(row);
        int topEdge, leftEdge, bottomEdge, rightEdge;
        topEdge = rowFormatConvert - 1;
        leftEdge = column - 1;
        bottomEdge = rowFormatConvert + 1;
        rightEdge = column + 1;
        int[] edges = new int[4];
        edges[0] = topEdge;
        edges[1] = leftEdge;
        edges[2] = bottomEdge;
        edges[3] = rightEdge;
        return edges;
    }

    // Function to inverse row's coordinates
    public int inverseRowCoord(int rowNum) {

        return (19 - rowNum);
    }

    // Calculate dimensions
    private void calculateDimensions() {
        if (numColumns < 1 || numRows < 1) {
            return;
        }

        int getwidth_ = getWidth();
        Log.d(TAG, "getwidth = " + getwidth_);


        int tempCellWidth = getWidth() / getNumColumns();
        Log.d(TAG, "tempcellwidth = " + tempCellWidth);
        /*cellWidth = (tempCellWidth/4)*3;*/
        cellWidth = getWidth() / getNumColumns();
        Log.d(TAG, "cellwidth = " + cellWidth);
        cellHeight = cellWidth;
        cellExplored = new boolean[getNumColumns()][getNumRows()];

        invalidate();
    }

    // Set Start Point Coordinates
    public void setStartCoord(int row, int column) {
        this.startCoord[0] = column;
        this.startCoord[1] = row;
        this.curCoord = this.startCoord;
        for (int i = 0; i < this.getNumColumns(); i++) {
            for (int j = 0; j < this.getNumRows(); j++) {
                this.setObstacle(i, j, false);
                this.setCellExplored(i, j, false);
            }
        }

        for (int i = column - 1; i <= column + 1; i++) {
            for (int j = row - 1; j <= row + 1; j++) {
                this.setCellExplored(i, j, true);
            }
        }

    }

    // Set Start Position and set  current position as starting position initially
    public void setStartPos(int frontStartPos, int leftStartPos, int backStartPos, int rightStartPos) {
        this.frontStartPos = frontStartPos;
        this.leftStartPos = leftStartPos;
        this.backStartPos = backStartPos;
        this.rightStartPos = rightStartPos;
        this.frontCurPos = frontStartPos;
        this.leftCurPos = leftStartPos;
        this.backCurPos = backStartPos;
        this.rightCurPos = rightStartPos;
    }

    // Set Robot Direction
    public void setRobotDirection(int direction) {
        this.robotDirection = direction;

        this.refreshMap(this.getAutoUpdate());
    }

    // Function to refresh the map on the screen
    public void refreshMap(boolean updateMap) {
        if (updateMap) {
            invalidate();
        }
    }

    // Check if robot has reached wall
    // Robot cannot go past map boundaries
    public boolean checkReachedWall(int[] pos, int direction) {

        int[] boundaries = new int[4];
        boundaries[0] = 0;
        boundaries[1] = 0;
        boundaries[2] = 19;
        boundaries[3] = 14;

        if (pos[direction] == boundaries[direction]) {
            return true;
        }
        return false;
    }

    // Check if robot reached obstacle
    public boolean checkReachedObstacle(int[] pos, int dir) {
        boolean[][] obstacle = this.getObstacles();
        if (dir == 0) {
            if (pos[0] == 0) return true;
            if (obstacle[pos[1]][20 - pos[0]] || obstacle[pos[1] + 1][20 - pos[0]] || obstacle[pos[3]][20 - pos[0]]) {
                return true;
            }
        } else if (dir == 1) {
            if (pos[1] == 0) return true;
            if (obstacle[pos[1] - 1][17 - pos[0]] || obstacle[pos[1] - 1][18 - pos[0]] || obstacle[pos[1] - 1][19 - pos[0]]) {
                return true;
            }
        } else if (dir == 2) {
            if (pos[2] == 19) return true;
            if (obstacle[pos[1]][18 - pos[2]] || obstacle[pos[1] + 1][18 - pos[2]] || obstacle[pos[3]][18 - pos[2]]) {
                return true;
            }
        } else {
            if (pos[3] == 14) return true;
            if (obstacle[pos[3] + 1][17 - pos[0]] || obstacle[pos[3] + 1][18 - pos[0]] || obstacle[pos[3] + 1][19 - pos[0]]) {
                return true;
            }
        }

        return false;

    }

    // Check whether the tile has been explored
    public void exploredTile() {
        for (int i = this.getCurCoord()[1] - 1; i <= this.getCurCoord()[1] + 1; i++) {
            for (int j = this.getCurCoord()[0] - 1; j <= this.getCurCoord()[0] + 1; j++) {
                this.setCellExplored(j, i, true);
            }
        }
        this.refreshMap(this.getAutoUpdate());
    }

    //function for the robot to move forward
    public void moveForward() {
        int[] pos = this.getCurPos();

        int dir = this.getRobotDirection();
        boolean reachedWall = this.checkReachedWall(pos, dir);
        boolean reachedObstacle = this.checkReachedObstacle(pos, dir);


        if (!reachedWall && !reachedObstacle) {
            if (dir == 0) {
                pos[0]--;
                pos[2]--;
                this.moveCurCoord(0, 1);
            } else if (dir == 1) {
                pos[1]--;
                pos[3]--;
                this.moveCurCoord(-1, 0);

            } else if (dir == 2) {
                pos[0]++;
                pos[2]++;
                this.moveCurCoord(0, -1);

            } else if (dir == 3) {
                pos[1]++;
                pos[3]++;
                this.moveCurCoord(1, 0);
            }


        }
        this.setCurPos(pos);
        this.exploredTile();
    }

    // Turn Left
    public void rotateLeft() {
        int dir = this.getRobotDirection();

        dir = (dir + 1) % 4;
        this.setRobotDirection(dir);
    }

    // Turn Right
    public void rotateRight() {
        int dir = this.getRobotDirection();

        dir = (dir + 3) % 4;
        this.setRobotDirection(dir);
    }

    // Move backwards = move right two times
    public void moveBackwards() {
        rotateRight();
        rotateRight();
    }

    // Select Waypoint
    public void selectWayPoint() {
        Log.d(TAG, "Setting Waypoint...");
        selectWayPoint = true;
    }

    // Check Start Point
    private boolean checkStartPoint(int row, int column) {
        if (row < 1 || row >= 19 || column < 1 || column >= 14) {
            return false;
        }
        return true;
    }

    // Set Start Position
    public void setStartPos(int row, int column) {
        this.setStartCoord(row, column);
        int[] startEdges = convertRobotPosToEdge(row, column);
        this.setStartPos(startEdges[0], startEdges[1], startEdges[2], startEdges[3]);
        for (int i = 0; i < this.getNumColumns(); i++) {
            for (int j = 0; j < this.getNumRows(); j++) {
                this.setObstacle(i, j, false);
                this.setCellExplored(i, j, false);
            }
        }
    }

    // Set Waypoint
    public void setWayPoint(int row, int column) {
        int[] wayPoint = new int[2];
        wayPoint[0] = column;
        wayPoint[1] = row;
        if (this.wayPoint == null) this.wayPoint = new int[2];
        this.wayPoint = wayPoint;
        String wayPointString = "Setting Waypoint to (" + String.valueOf(column) + "," + String.valueOf(row) + ")";
        Toast.makeText(this.getContext(), wayPointString, Toast.LENGTH_SHORT).show();

    }

    // Get Waypoint: Returns an array
    public int[] getWayPoint() {
        return this.wayPoint;
    }

    // Select Start Point
    public void selectStartPoint () {
        Log.d(TAG, "Setting Start Point...");
        selectStartPosition = true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            int column = (int) (event.getX() / cellWidth);
            int row = (int) (event.getY() / cellHeight);

            if (selectStartPosition) {
                if (!checkStartPoint(row, column)) {
                    selectStartPosition = false;
                    invalidate();
                    return true;
                }
                this.setStartPos(inverseRowCoord(row), column);
                selectStartPosition = false;

                Log.d(TAG, "Start Point: " + Integer.toString(this.getStartCoord()[0]) + "," + Integer.toString(this.getStartCoord()[1]));
                //TODO: Use btChat to write the starting position and send it to the algo

                invalidate();

            } else if (selectWayPoint) {
                this.setWayPoint(inverseRowCoord(row), column);
                selectWayPoint = false;

                Log.d(TAG, "Waypoint: " + Integer.toString(this.getWayPoint()[0]).concat(",").concat(Integer.toString(this.getWayPoint()[1])));
                //TODO: Use btChat to write the waypoint position and send it to the algo

                invalidate();
            }
        }

        return true;
    }

}
