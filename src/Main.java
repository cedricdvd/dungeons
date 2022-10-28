import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PFont;

import java.util.ArrayList;
import java.util.Scanner;

import java.io.File;
import java.io.PrintWriter;

import item.*;
import environment.*;
import entity.*;

import constants.EntityValues;
import constants.FilePaths;

// TODO: Place varialbes for hardcoded values
public class Main extends PApplet {
    private static final String MAIN_CLASS = "Main";
    private static final String FONT_FILE = "PressStart2P/PressStart2P.ttf";
    private static final int FONT_SIZE = 48;

    private static final int CHARACTER_COUNT = 8;
    private static final String ELF_FILE = "elf";
    private static final String KNIGHT_FILE = "knight";
    private static final String LIZARD_FILE = "lizard";
    private static final String WIZARD_FILE = "wizard";
    private static final String MALE_EXTENSION = "_m";
    private static final String FEMALE_EXTENSION = "_f";

    private static final int FRAME_COUNT = EntityValues.DEFAULT_FRAME_COUNT;

    private static final String MENU_FILE = "_menu_f";

    private static final int WEAPON_COUNT = 8;
    private static final String WEAPON_PATH = "assets/weapons/";
    private static final String WEAPON_MENU_EXTENSION = "_menu" + FilePaths.PNG_EXTENSION;
    private static final String AXE_FILE = "axe";
    private static final String BATON_FILE = "baton_with_spikes";
    private static final String GOLD_SWORD_FILE = "golden_sword";
    private static final String KNIGHT_SWORD_FILE = "knight_sword";
    private static final String GREEN_STAFF_FILE = "green_magic_staff";
    private static final String RED_STAFF_FILE = "red_magic_staff";
    private static final String KATANA_FILE = "katana";
    private static final String SPEAR_FILE = "spear";

    private static final String CSV_PATH = "src/resources/scores.csv";
    private static final String CSV_DELIMITER = ",";
    private static final String CSV_READ_ERROR = "Scores cannot be created.";
    private static final String CSV_WRITE_ERROR = "Unable to save scores.";

    private static final int START_STATE = 0;
    private static final int SETTINGS_STATE = 1;
    private static final int CHARACTER_SELECT_STATE = 2;
    private static final int WEAPON_SELECT_STATE = 3;
    private static final int PLAY_GAME_STATE = 4;
    private static final int GAME_OVER_STATE = 5;
    private static final int SCORE_BOARD_STATE = 6;
    private static final int CREDITS_STATE = 7;

    private static final char W_KEY = 'w';
    private static final char A_KEY = 'a';
    private static final char S_KEY = 's';
    private static final char D_KEY = 'd';
    private static final char ACTION_KEY = ' ';

    private static final int MENU_OPTIONS_COUNT = 3;

    private static final int MAX_NAME_LENGTH = 3;

    private static final String TITLE = "Dungeons,\nDungeons and\nMore Dungeons";
    private static final int TITLE_SIZE = 32;

    private static final String START_OPTION = "Start";
    private static final String SETTINGS_OPTION = "Settings";
    private static final String CREDITS_OPTION = "Credits";
    private static final int OPTION_SIZE = 24;


    // Number of Rows and Columns in Room
    private final int ROWS = 15;
    private final int COLS = 19;

    // Objects used while game is running
    private Room room;
    private Player player;
    private ArrayList<Enemy> enemies;
    private ArrayList<Heart> lives;

    // Text Font
    private PFont PressStart2P;

    // Holds the Current Game State
    private int gameState;

    // Game Menu
    private String[] optionTexts;
    private int[] optionColors;
    private int selectIndex;

    // Character Selection Menu
    private PImage[][] characterImages;
    private String[] characterFiles;
    private int charSelectIndex;
    private int frameNum;

    // Weapon Selection Menu
    private PImage[] weaponImages;
    private String[] weaponFiles;
    private int weaponSelectIndex;

    // Variables used in Game
    private int score, roomsCleared, enemiesSpawned, enemiesKilled;

    // Game Over
    private String name;

    // Scoreboard
    private String scoreFile;
    private ArrayList<String> names;
    private ArrayList<Integer> scores;

    public static void main(String[] args) {
        PApplet.main(MAIN_CLASS);
    }

    public void settings() {
        size(COLS * 32, (ROWS + 2) * 32);
    }

    public void setup() {
        // Create and Set Font
        PressStart2P = createFont(FONT_FILE, FONT_SIZE);
        textFont(PressStart2P);

        // Start Menu
        optionTexts = new String[3];
        optionColors = new int[3];
        selectIndex = 0;

        // Character Selection Menu
        characterFiles = new String[]{
            ELF_FILE + MALE_EXTENSION, ELF_FILE + FEMALE_EXTENSION,
            KNIGHT_FILE + MALE_EXTENSION, KNIGHT_FILE + FEMALE_EXTENSION,
            LIZARD_FILE + MALE_EXTENSION, LIZARD_FILE + FEMALE_EXTENSION,
            WIZARD_FILE + MALE_EXTENSION, WIZARD_FILE + FEMALE_EXTENSION
        };

        for (int i = 0; i < CHARACTER_COUNT; i++) {
            String path = characterFiles[i];
            characterFiles[i] = path + "/" + path;
        }

        // 8: number of characters
        // 4: frames for each character
        characterImages = new PImage[CHARACTER_COUNT][FRAME_COUNT];
        for (int i = 0; i < CHARACTER_COUNT; i++) {
            for (int j = 0; j < FRAME_COUNT; j++) {
                String path = FilePaths.ASSETS_PATH + characterFiles[i] +
                    MENU_FILE + nf(j,1) + FilePaths.PNG_EXTENSION;
                characterImages[i][j] = loadImage(path);
            }
        }
        
        frameNum = 0;
        charSelectIndex = 0;

        // Weapon Selection Menu
        weaponFiles = new String[]{
            AXE_FILE, BATON_FILE, GOLD_SWORD_FILE, KNIGHT_SWORD_FILE,
            GREEN_STAFF_FILE, RED_STAFF_FILE, KATANA_FILE, SPEAR_FILE
        };

        weaponImages = new PImage[WEAPON_COUNT];
        for (int i = 0; i < WEAPON_COUNT; i++) {
            String path = WEAPON_PATH + weaponFiles[i] + WEAPON_MENU_EXTENSION;
            weaponImages[i] = loadImage(path);
        }
        weaponSelectIndex = 0;

        // Game Over Screen
        name = "";

        // Scoreboard
        names = new ArrayList<String>();
        scores = new ArrayList<Integer>();
        scoreFile = CSV_PATH;
        try {
            Scanner reader = new Scanner(new File(scoreFile));
            String line = "";
            String[] values;

            while (reader.hasNext()) {
                line = reader.nextLine();
                values = line.split(CSV_DELIMITER);
                names.add(values[0]);
                scores.add(Integer.valueOf(values[1]));
            }

            reader.close();
        }
        catch (Exception e) {
            System.out.println(CSV_READ_ERROR);
        }
    }

    public void draw() {
        switch (gameState) {
            case START_STATE:
                startMenu();
                break;
            case SETTINGS_STATE:
                settingsMenu();
                break;
            case CHARACTER_SELECT_STATE:
                characterSelectionMenu();
                break;
            case WEAPON_SELECT_STATE:
                weaponSelectionMenu();
                break;
            case PLAY_GAME_STATE:
                playGame();
                break;
            case GAME_OVER_STATE:
                gameOver();
                break;
            case SCORE_BOARD_STATE:
                scoreBoard();
                break;
            case CREDITS_STATE:
                creditsMenu();
                break;
        }
    }

    public void keyPressed() {
        
        switch (gameState) {
            // Start Menu
            case START_STATE:
                if (key == W_KEY) {
                    selectIndex = (selectIndex + MENU_OPTIONS_COUNT - 1) % MENU_OPTIONS_COUNT;
                }

                if (key == S_KEY) {
                    selectIndex = (selectIndex + 1) % MENU_OPTIONS_COUNT;
                }

                if (key == ACTION_KEY) {
                    if (selectIndex == 0) {
                        gameState = CHARACTER_SELECT_STATE;
                    }
                    else if (selectIndex == 1) {
                        gameState = SETTINGS_STATE;
                    }
                    else {
                        gameState = CREDITS_STATE;
                    }
                }
                break;
            // Settings Menu
            case SETTINGS_STATE:
                if (key == ACTION_KEY) {
                    HitBox.toggle();
                }

                if (key == BACKSPACE) {
                    gameState = START_STATE;
                }
                break;
            // Character Select Menu
            case CHARACTER_SELECT_STATE:
                if (key == W_KEY || key == S_KEY) {
                    if (selectIndex % 2 == 0) {
                        selectIndex++;
                    }
                    else {
                        selectIndex--;
                    }
                    frameNum = 0;
                }
                else if (key == A_KEY) {
                    selectIndex = (selectIndex + 6) % CHARACTER_COUNT;
                    frameNum = 0;
                }
                else if (key == D_KEY) {
                    selectIndex = (selectIndex + 2) % CHARACTER_COUNT;
                    frameNum = 0;
                }

                if (key == ACTION_KEY) {
                    gameState = WEAPON_SELECT_STATE;
                    charSelectIndex = selectIndex;
                    selectIndex = 0;
                }
                break;
            // Weapon Select Menu
            case WEAPON_SELECT_STATE:
                if (key == W_KEY || key == S_KEY) {
                    if (selectIndex % 2 == 0) {
                        selectIndex++;
                    }
                    else {
                        selectIndex--;
                    }
                    frameNum = 0;
                }
                else if (key == A_KEY) {
                    selectIndex = (selectIndex + 6) % WEAPON_COUNT;
                    frameNum = 0;
                }
                else if (key == D_KEY) {
                    selectIndex = (selectIndex + 2) % WEAPON_COUNT;
                    frameNum = 0;
                }

                if (key == ACTION_KEY) {
                    gameState = PLAY_GAME_STATE;
                    weaponSelectIndex = selectIndex;
                    selectIndex = 0;
                    createGame();
                }
                break;
            // Game
            case PLAY_GAME_STATE:
                if (!player.isHit()) {
                    if (key == W_KEY && !player.isMovingDown()) {
                        player.setMoveUp(true);
                        player.setRecentKey(W_KEY);
                    }
                    if (key == A_KEY && !player.isMovingRight()) {
                        player.setMoveLeft(true);
                        player.setRecentKey(A_KEY);
                    }
                    if (key == S_KEY && !player.isMovingUp()) {
                        player.setMoveDown(true);
                        player.setRecentKey(S_KEY);
                    }
                    if (key == D_KEY && !player.isMovingLeft()) {
                        player.setMoveRight(true);
                        player.setRecentKey(D_KEY);
                    }
        
                    if (key == ' ') {
                        player.setAttack(true);
                    }
                }
                break;
            // Game Over
            case GAME_OVER_STATE:
                if (key == ACTION_KEY) {
                    int i = scores.size() - 1;
                    // linear search scores
                    while (i >= 0 && (score > scores.get(i))) {
                        i--;
                    }
                    names.add(i + 1, name);
                    scores.add(i + 1, score);

                    saveScores();
                    gameState = SCORE_BOARD_STATE;
                }
                else if (key != CODED && name.length() > 0 && key == BACKSPACE) {
                    name = name.substring(0, name.length() - 1);
                }
                else if (key != CODED && name.length() < MAX_NAME_LENGTH && key != BACKSPACE) {
                    name = (name + key).toUpperCase();
                }
                break;
            // Score Board
            case SCORE_BOARD_STATE:
                if (key == ACTION_KEY) {
                    gameState = START_STATE;
                }
                break;
            // Credits Menu
            case CREDITS_STATE:
                if (key == ACTION_KEY) {
                    gameState = START_STATE;
                }
                break;
        }
    }

    public void keyReleased() {
        switch (gameState) {
            case PLAY_GAME_STATE:
                if (!player.isHit()) {
                    if (key == W_KEY && !player.isMovingDown()) {
                        player.setMoveUp(false);
                    }
                    if (key == A_KEY && !player.isMovingRight()) {
                        player.setMoveLeft(false);
                    }
                    if (key == S_KEY && !player.isMovingUp()) {
                        player.setMoveDown(false);
                    }
                    if (key == D_KEY && !player.isMovingLeft()) {
                        player.setMoveRight(false);
                    }

                    if (key == ' ') {
                        player.setAttack(false);
                    }
                }
                break;
        }
    }
    
    /**
     * Save scores to a <code>csv</code> file
     */
    private void saveScores() {
        try {
            PrintWriter writer = new PrintWriter(new File(scoreFile));
            String line = "";
            int length = scores.size();
            for (int i = 0; i < length; i++) {
                line = names.get(i) + CSV_DELIMITER + scores.get(i);
                writer.println(line);
            }
            writer.close();
        }
        catch (Exception e) {
            System.out.println(CSV_WRITE_ERROR);
        }
    }

    /* Game creation functions */
    private void createGame() {
        // Create Room and Player
        room = new Room(this, 0, 64, ROWS, COLS);
        player = new Player(this, COLS * 16, (ROWS + 3) * 16, room, characterFiles[charSelectIndex], weaponFiles[weaponSelectIndex]);

        // Create Enemies
        enemiesSpawned = 10;
        enemies = new ArrayList<Enemy>();
        createEnemies();

        // Set up 3 Lives
        lives = new ArrayList<Heart>();
        for (int i = 0; i < 3; i++) {
            addHeart();
        }

        // Set up game variables
        score = 0;
        roomsCleared = 0;
        enemiesKilled = 0;
    }

    private void createEnemies() {
        for (int i = 0; i < enemiesSpawned; i++) {
            double xPos = Math.random() * 32 * 6 + 64;
            double yPos = Math.random() * 32 * 4 + 128;

            if (Math.random() < .5) {
                xPos += 32 * 9;
            }

            if (Math.random() < .5) {
                yPos += 32 * 7;
            }

            int rand = (int)(Math.random() * 3);

            switch (rand) {
                case 0:
                    enemies.add(new BigZombie(this, xPos, yPos, room));
                    break;
                case 1:
                    enemies.add(new Swampy(this, xPos, yPos, room));
                    break;
                case 2:
                    enemies.add(new Goblin(this, xPos, yPos, room));
                    break;
            }
        }
    }

    private void createDungeon() {
        createEnemies();
        room.closeDoors();
        room.createFloor();
        roomsCleared++;
        
        if (roomsCleared % 10 == 0) {
            if (enemiesSpawned < 30) {
                enemiesSpawned++;
            }

            if (lives.size() < 18) {
                addHeart();
            }
        }
    }

    /* Change the amount of lives */
    private void increaseLives() {
        for (int i = 0; i < lives.size(); i++) {
            if (lives.get(i).getState() != 2) {
                lives.get(i).increaseState();
                player.increaseHealth();
                break;
            }
        }
    }

    private void decreaseLives() {
        for (int i = lives.size() - 1; i >= 0; i--) {
            if (lives.get(i).getState() != 0) {
                lives.get(i).decreaseState();
                player.decreaseHealth();
                break;
            }
        }
    }

    private void addHeart() {
        int xPos = 32 + 16 * (lives.size() / 2);
        int yPos = 32 + 16 * (lives.size() % 2);
        lives.add(new Heart(this, xPos, yPos));
        for (int i = 0; i < 2; i++) {
            increaseLives();
        }
    }

    /* Calcualte the distance between player and tile */
    private double playerTileDistance(Player p, Tile t) {
        double xDistance = Math.pow(p.getX() - t.getX(), 2);
        double yDistance = Math.pow(p.getY() - t.getY(), 2);

        return Math.sqrt(xDistance + yDistance);
    }

    /* Different Game States */
    private void menuBackground() {
        background(0xFF483B3A);
        fill(0);
        rect(32, 32, width - 64, height - 64);
        fill(0xFFFFFFFF);
    }

    // TODO: Place magic numbers in variables
    // State 0
    private void startMenu() {
        menuBackground();
        textAlign(CENTER, CENTER);
        textSize(TITLE_SIZE);
        text(TITLE, width / 2, height / 6 + 24);

        // Menu options
        textSize(OPTION_SIZE);
        optionTexts[0] = START_OPTION;
        optionTexts[1] = SETTINGS_OPTION;
        optionTexts[2] = CREDITS_OPTION;

        optionColors[0] = 0xFFFFFFFF;
        optionColors[1] = 0xFFFFFFFF;
        optionColors[2] = 0xFFFFFFFF;

        // Change color and String depending on selectIndex
        optionTexts[selectIndex] = "> " + optionTexts[selectIndex] + " <";
        optionColors[selectIndex] = 0xFF918FFA;

        for (int i = 0; i < 3; i++) {
            fill(optionColors[i]);
            text(optionTexts[i], width / 2, height / 2 + 48 * (i - 1));
        }

        // Directions
        textSize(16);
        fill(0xFFFFFFFF);
        text("Press W or S to Scroll", width / 2, 3 * height / 4);
        text("Press SPACE to Select", width / 2, 3 * height / 4 + 24);
    }
    
    // State 1
    private void settingsMenu() {
        menuBackground();
        textAlign(CENTER, CENTER);
        textSize(48);
        text("Settings", width / 2, height / 8 + 16);

        textSize(24);
        textAlign(RIGHT, CENTER);
        int xShift = 128;
        text("Show Hit Boxes:", width / 2 + xShift, height / 2);

        textAlign(LEFT, CENTER);
        if (HitBox.canShowHitBox()) {
            fill(0xFFBFD5A2);
        }
        else {
            fill(0xFFDD4A68);
        }
        text("" + HitBox.canShowHitBox(), width / 2 + xShift, height / 2);

        textSize(16);
        fill(0xFFFFFFFF);
        textAlign(CENTER, CENTER);
        text("Press SPACE to Toggle Settings", width / 2, 3 * height / 4);
        text("Press BACKSPACE to Return to Menu", width / 2, 3 * height / 4 + 24);
    }

    // State 2
    private void characterSelectionMenu() {
        menuBackground();
        textAlign(CENTER, CENTER);
        textSize(24);
        text("Select Character", width / 2, height / 8 + 16);

        for (int i = 0; i < 8; i++) {
            int length = 128;
            int xPos = width / 2 + (length + 8) * (i/ 2) - (2 * length + 12);
            int yPos = height / 2 + (length + 8) * (i % 2) - (length);
            if (i == selectIndex) {
                fill(0xFFB6CBCF);
            }
            else {
                fill(0xFF111111);
            }
            rect(xPos, yPos, length, length);
            imageMode(CENTER);

            xPos += length / 2;
            yPos += length / 2 - 16;
            if (i == selectIndex) {
                image(characterImages[i][frameNum / 5], xPos, yPos);
                frameNum = (frameNum + 1) % 20;
            }
            else {
                image(characterImages[i][0], xPos, yPos);
            }

            textSize(16);
            fill(0xFFFFFFFF);
            text("Press WASD to Change Selection", width / 2, 3 * height / 4 + 32);
            text("Press SPACE to Select", width / 2, 3 * height / 4 + 56);
        }
    }

    // State 3
    private void weaponSelectionMenu() {
        menuBackground();
        textAlign(CENTER, CENTER);
        textSize(24);
        text("Select Weapon", width / 2, height / 8 + 16);

        for (int i = 0; i < 8; i++) {
            int length = 128;
            int xPos = width / 2 + (length + 8) * (i/ 2) - (2 * length + 12);
            int yPos = height / 2 + (length + 8) * (i % 2) - (length);
            if (i == selectIndex) {
                fill(0xFFB6CBCF);
            }
            else {
                fill(0xFF111111);
            }
            rect(xPos, yPos, length, length);
            imageMode(CENTER);

            xPos += length / 2;
            yPos += length / 2;
            image(weaponImages[i], xPos, yPos);
        }

        textSize(16);
        fill(0xFFFFFFFF);
        text("Press WASD to Change Selection", width / 2, 3 * height / 4 + 32);
        text("Press SPACE to Select", width / 2, 3 * height / 4 + 56);
    }

    // State 4
    private void playGame() {
        // HUD
        background(0);
        textSize(16);

        textAlign(RIGHT, CENTER);
        text("Score:" + nf(score, 6), width - 16, 16);
        text("Room:" + nf(roomsCleared + 1, 3), width - 16, 48);

        textAlign(CENTER, CENTER);
        text("-LIVES-", 16 * 6, 16);
        for (Heart h : lives) {
            h.show();
        }
        
        // Show Room and Player
        room.show();
        player.show();

        // Determine if Weapon and Enemy HitBox overlaps
        if (player.isAttacking()) {
            HitBox wBox = player.getWeapon().getHitBox();
            for (int i = enemies.size() - 1; i >= 0; i--) {
                Enemy e = enemies.get(i);

                if (wBox.isOverlapping(e.getHitBox())) {
                    if (!e.isHit()) {
                        e.decreaseHealth();
                        e.setHit(true, player);
                    }

                    if (e.getHealth() == 0) {
                        enemies.remove(i);
                        enemiesKilled++;
                        score += e.getScore();
                        if (enemiesKilled % 4 == 0) {
                            increaseLives();
                        }
                    }
                }
            }

            if (enemies.size() == 0 && room.doorsClosed()) {
                room.openDoors();
            }
        }

        // Show each enemy, determine if HitBox overlaps
        for (Enemy e : enemies) {
            e.show();
            if (e.getHitBox().isOverlapping(player.getHitBox())) {
                if (!player.isHit()) {
                    decreaseLives();
                    player.setHit(true, e);
                }
            }

            if (e.isHit()) {
                e.hit();
            }
            else {
                e.move();
            }
        }

        if (player.isHit()) {
            player.hit();
        }
        else {
            player.move();
        }

        // Determine if player is nearby open door
        if (!room.doorsClosed()) {
            if (playerTileDistance(player, room.getTile(0, COLS / 2)) < 20 && player.isMovingUp()) {
                createDungeon();
            }
            else if (playerTileDistance(player, room.getTile(ROWS / 2, 0)) < 40 && player.isMovingLeft()) {
                createDungeon();
            }
            else if (playerTileDistance(player, room.getTile(ROWS / 2, COLS - 1)) < 40 && player.isMovingRight()) {
                createDungeon();
            }
            else if (playerTileDistance(player, room.getTile(ROWS - 1, COLS / 2)) < 60 && player.isMovingDown()) {
                createDungeon();
            }
        }

        if (player.getHealth() <= 0 && gameState == 4) {
            gameState = 5;
        }
    }

    // State 5
    private void gameOver() {
        menuBackground();
        textAlign(CENTER, CENTER);
        textSize(48);
        fill(0xFFDA4E37);
        text("Game Over", width / 2, height / 4);

        textSize(24);
        fill(0xFFFFFFFF);
        text("Score: " + nf(score, 6), width / 2, height / 2 - 24 - 16);
        text("Rooms Cleared:" + roomsCleared, width / 2, height / 2 + 24 - 16);

        int xShift = 16 * 6;
        textAlign(RIGHT, CENTER);
        text("Enter Name:", width / 2 + xShift, height / 2 + 96 - 16);
        textAlign(LEFT, CENTER);
        text(name, width / 2 + xShift, height / 2 + 96 - 16);

        textSize(16);
        textAlign(CENTER, CENTER);
        text("Press SPACE to Enter Name", width / 2, 3 * height / 4 + 24);
    }

    // State 6
    private void scoreBoard() {
        menuBackground();
        textAlign(CENTER, CENTER);
        textSize(48);
        fill(0xFF52894C);
        text("Top Scores", width / 2, height / 4);

        textSize(24);
        fill(0xFFFFFFFF);
        for (int i = 0; i < 5; i++) {
            String line = names.get(i) + " " + nf(scores.get(i), 6);
            textAlign(LEFT, CENTER);
            text(line, width / 2 - 16 * 7, height / 2 - 16 * 4 + 48 * i);
        }

        textAlign(CENTER, CENTER);
        textSize(16);
        text("Press SPACE to Return to Menu", width / 2, 3 * height / 4 + 48);
    }

    // State 7
    private void creditsMenu() {
        menuBackground();
        textAlign(CENTER, CENTER);
        textSize(48);
        text("Credits", width / 2, height / 4);

        textSize(16);
        textAlign(LEFT, CENTER);
        int xShift = width / 10;
        text("Game Assets from 16x16", xShift, height / 2 - 48);
        text("    DungeonTileSet II by 0x72", xShift, height / 2 - 24);
        text("Sound Effects by artisticdude", xShift, height / 2 + 12);
        text("Font by The Press Start 2P", xShift, height / 2 + 48);
        text("    Project Authors", xShift, height / 2 + 72);
        text("Game by Cedric David", xShift, height / 2 + 108);

        textAlign(CENTER, CENTER);
        textSize(16);
        text("Press SPACE to Return to Menu", width / 2, 3 * height / 4 + 48);
    }

}
