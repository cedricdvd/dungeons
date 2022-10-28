package entity;
import environment.Room;
import processing.core.PApplet;

public class Enemy extends Entity {
    private static final double UP_PROBABILITY = 0.45;
    private static final double DOWN_PROBABILITY = 0.45;
    private static final double LEFT_PROBABILITY = 0.45;
    private static final double RIGHT_PROBABILITY = 0.45;

    protected int moveTime, moveWait;
    protected int score;

    public Enemy(PApplet applet, double x, double y, Room room, String file) {
        super(applet, x, y, room, file);
    }

    @Override
    public void move() {
        /* Future Enemy Movement:
         * If enemy is not moving:
         * Check if tiles around it are walls
         * Decide which tile to move to
         * Change movement variables
         * Move to that tile
         */

        // Current Movement: move in random directions every 1.5 seconds
        if (applet.millis() - moveTime >= moveWait) {
            setMoveDown(false);
            setMoveUp(false);
            setMoveLeft(false);
            setMoveRight(false);

            // Vertical Movement
            double rand = Math.random();
            if (rand < UP_PROBABILITY) {
                setMoveUp(true);
            }
            else if (rand < UP_PROBABILITY + DOWN_PROBABILITY) {
                setMoveDown(true);
            }

            // Horizontal Movement
            rand = Math.random();
            if (rand < RIGHT_PROBABILITY) {
                setMoveRight(true);
            }
            else if (rand < RIGHT_PROBABILITY + LEFT_PROBABILITY) {
                setMoveLeft(true);
            }

            moveTime = applet.millis();
        }
        
        super.move();
    }

    @Override
    public void setHit(boolean isHit, Entity e) {
        super.setHit(isHit, e);
        moveTime += 500;
    }

    public int getScore() {
        return score;
    }
}
