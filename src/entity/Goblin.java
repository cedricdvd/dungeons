package entity;

import processing.core.PApplet;
import environment.Room;
import constants.EntityValues;

public class Goblin extends Enemy {
    public Goblin(PApplet applet, double x, double y, Room room) {
        super(applet, x, y, room, "goblin/goblin");

        // Initialize frames
        frameAmount = 4;
        frameCycle = 20;
        createFrames();

        // Initialize speed
        speed = 1.5;
        xSpeed = 0;
        ySpeed = 0;

        // Movement timer
        moveTime = applet.millis();
        moveWait = 750;

        // Hitbox
        leftShift = -7;
        rightShift = 10;
        bottomShift = -3;
        topShift = 16;

        score = EntityValues.SMALL_POINTS;
        health = EntityValues.SMALL_HEALTH;
    }
}
