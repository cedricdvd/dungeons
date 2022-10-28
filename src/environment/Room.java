package environment;
import processing.core.PApplet;
import processing.sound.*;
import constants.FilePaths;

public class Room {
    private static final String FLOOR_FILE = "floor_";
    private static final String WALL_FILE = "wall_mid";
    private static final String DOOR_SOUND = "door/door" + FilePaths.WAV_EXTENSION;
    private static final String DOOR_OPEN = "doors_leaf_open";
    private static final String DOOR_CLOSED = "doors_leaf_closed";
    private static final int FLOOR_FILE_COUNT = 8;
    private static final double DEFAULT_FLOOR_PROBABILITY = 0.8;
    private static final float AMP_MODIFIER = 0.75f;
    // 2D Tile Array Variables
    private Tile[][] tiles;
    private int ROWS, COLS;

    // Boundary variables for entities
    private int leftBorder;
    private int rightBorder;
    private int upBorder;
    private int downBorder;

    // Check if Doors are closed
    private boolean doorsClosed;

    // Doors Open
    private SoundFile doorOpen;

    public Room(PApplet applet, int xShift, int yShift, int ROWS, int COLS) {
        this.ROWS = ROWS;
        this.COLS = COLS;
        tiles = new Tile[ROWS][COLS];

        String file;
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (row > 0 && row < ROWS - 1 && col > 0 && col < COLS - 1) {
                    if (Math.random() < DEFAULT_FLOOR_PROBABILITY) {
                        file = FLOOR_FILE + 1;
                    }
                    else {
                        int tileNumber = (int)(Math.random() * FLOOR_FILE_COUNT) + 1;
                        file = FLOOR_FILE + PApplet.nf(tileNumber, 1);
                    }
                }
                else {
                    file = WALL_FILE;
                }

                tiles[row][col] = new Tile(applet, col * 32 + 16 + xShift, row * 32 + 16 + yShift);
                tiles[row][col].setTile(file);
            }

        }

        closeDoors();

        doorOpen = new SoundFile(applet, DOOR_SOUND);
        leftBorder = xShift + 32;
        rightBorder = xShift + (COLS - 1) * 32;
        upBorder = yShift + 16;
        downBorder = yShift + (ROWS - 1) * 32;
    }

    public void show() {
        for (Tile[] row : tiles) {
            for (Tile t: row) {
                t.show();
            }
        }
    }

    public int getLeftBorder() {
        return leftBorder;
    }

    public int getRightBorder() {
        return rightBorder;
    }

    public int getDownBorder() {
        return downBorder;
    }

    public int getUpBorder() {
        return upBorder;
    }

    public Tile getTile(int row, int col) {
        return tiles[row][col];
    }

    public void openDoors() {
        doorsClosed = false;
        String door = DOOR_OPEN;
        doorOpen.play(1, AMP_MODIFIER);
        tiles[0][COLS / 2].setTile(door);
        tiles[ROWS / 2][0].setTile(door);
        tiles[ROWS / 2][COLS - 1].setTile(door);
        tiles[ROWS - 1][COLS / 2].setTile(door);
    }

    public void closeDoors() {
        doorsClosed = true;
        String door = DOOR_CLOSED;
        tiles[0][COLS / 2].setTile(door);
        tiles[ROWS / 2][0].setTile(door);
        tiles[ROWS / 2][COLS - 1].setTile(door);
        tiles[ROWS - 1][COLS / 2].setTile(door);
    }

    public void createFloor() {
        String file;
        for (int row = 1; row < ROWS - 1; row++) {
            for (int col = 1; col < COLS - 1; col++) {
                if (Math.random() < DEFAULT_FLOOR_PROBABILITY) {
                    file = FLOOR_FILE + 1;
                }
                else {
                    int tileNumber = (int)(Math.random() * FLOOR_FILE_COUNT) + 1;
                    file = FLOOR_FILE + PApplet.nf(tileNumber, 1);
                }

                tiles[row][col].setTile(file);
            }
        }
    }

    public boolean doorsClosed() {
        return doorsClosed;
    }
}
