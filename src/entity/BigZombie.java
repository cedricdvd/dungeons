package entity;

import processing.core.PApplet;
import environment.Room;
import constants.EntityValues;

public class BigZombie extends Enemy {
    
    public BigZombie(PApplet applet, double x, double y, Room room) {
        super(applet, x, y, room, "big_zombie/big_zombie");

        // Initialize frames
        frameAmount = 4;
        frameCycle = 20;
        createFrames();

        // Initialize speed
        speed = 1;
        xSpeed = 0;
        ySpeed = 0;

        // Movement timer
        moveTime = applet.millis();
        moveWait = 1500;

        // Hitbox
        leftShift = -18;
        rightShift = 18;
        bottomShift = -21;
        topShift = 33;

        score = EntityValues.LARGE_POINTS;
        health = EntityValues.LARGE_HEALTH;
    }
}
