package entity;
import environment.Room;
import item.Weapon;
import processing.core.PApplet;
import processing.core.PImage;
import processing.sound.*;
import constants.FilePaths;

public class Player extends Entity {
    private static final String CHAINMAIL_PATH_1 = "chainmail/chainmail1" + FilePaths.WAV_EXTENSION;
    private static final String CHAINMAIL_PATH_2 = "chainmail/chainmail2" + FilePaths.WAV_EXTENSION;
    private static final String SWING_PATH = "battle/swing";
    private static final String HIT_EXTENSION = "_hit_anim_f0" + FilePaths.PNG_EXTENSION;
    private static final float AMP_MODIFIER = 0.25f;
    private static final int SWING_AMOUNT = 3;
    // Steps
    private SoundFile step1, step2;
    private int stepTime, stepWait;
    private boolean step1Played;

    // Weapon
    private Weapon weapon;

    // Attacking Variables
    private boolean isAttacking;
    private char recentKey;

    // Hit Animation
    private PImage hitFrame;

    // Swing Sounds
    private SoundFile[] swingSounds;
    private int swingNum;

    public Player(PApplet applet, double x, double y, Room room, String charFile, String weaponFile) {
        super(applet, x, y, room, charFile);

        // Create Frames
        frameAmount = 4;
        frameCycle = 20;
        createFrames();

        speed = 3.5;
        moveUp = moveDown = moveLeft = moveRight = false;
        faceLeft = false;

        // Steps
        step1 = new SoundFile(applet, CHAINMAIL_PATH_1);
        step2 = new SoundFile(applet, CHAINMAIL_PATH_2);

        stepTime = applet.millis();
        stepWait = 500;
        step1Played = false;

        // Weapon
        weapon = new Weapon(applet, this, weaponFile);

        // HitBox (30, 40)
        // hitbox = new HitBox(applet, x - 17, y - 13, x + 17, y + 28);
        leftShift = -17;
        rightShift = 17;
        bottomShift = -13;
        topShift = 28;
        
        // Attack
        isAttacking = false;
        recentKey = ' ';

        // health
        health = 0;

        // Hit Frame
        hitFrame = applet.loadImage(FilePaths.ASSETS_PATH + file + HIT_EXTENSION);

        // Swings
        swingSounds = new SoundFile[SWING_AMOUNT];
        for (int i = 1; i <= SWING_AMOUNT; i++) {
            swingSounds[i - 1] = new SoundFile(applet, SWING_PATH + i + FilePaths.WAV_EXTENSION);
        }
        swingNum = 0;
    }

    @Override
    public void show() {
        if (isHit) {
            applet.imageMode(PApplet.CENTER);
            int scaleX = 1;
            if (isFacingLeft()) {
                scaleX *= -1;
            }

            applet.pushMatrix();
            applet.translate((float)x, (float)y);
            applet.scale(scaleX, 1);
            applet.image(hitFrame, 0, 0);
            applet.popMatrix();
        }
        else if (moveUp || moveDown || moveLeft || moveRight) {
            playStep();
            showFrame(runFrames);
        }
        else {
            showFrame(idleFrames);
        }

        if (isAttacking) {
            weapon.attack();
        }
        else {
            weapon.show();
        }

        hitbox.show();
    }

    @Override
    public void setHit(boolean isHit, Entity e) {
        super.setHit(isHit, e);
        prevDown = false;
        prevUp = false;
        prevLeft = false;
        prevRight = false;
        isAttacking = false;
    }

    private void playStep() {
        if (applet.millis() - stepTime >= stepWait) {
            if (step1Played) {
                step2.play(1, AMP_MODIFIER);
            }
            else {
                step1.play(1, AMP_MODIFIER);
            }
            step1Played = !step1Played;
            stepTime = applet.millis();
        }
    }

    public void setAttack(boolean isAttacking) {
        int previousSwing = (swingNum + SWING_AMOUNT - 1) % SWING_AMOUNT;
        if (isAttacking &&
            !swingSounds[previousSwing].isPlaying() &&
            this.isAttacking != isAttacking) {

            swingSounds[swingNum].play();
            swingNum = (swingNum + 1) % SWING_AMOUNT;
        }

        this.isAttacking = isAttacking;
    }

    public void setRecentKey(char recentKey) {
        this.recentKey = recentKey;
    }

    public boolean isAttacking() {
        return isAttacking;
    }

    public char getRecentKey() {
        return recentKey;
    }
    
    public Weapon getWeapon() {
        return weapon;
    }
}
