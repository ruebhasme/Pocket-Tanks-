package Tanks;

import processing.core.PApplet;
import processing.core.PImage;
import processing.data.JSONArray;
import processing.data.JSONObject;
import ddf.minim.Minim;
import ddf.minim.AudioPlayer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class App extends PApplet {

    public static final float CELLSIZE = 32.0f;
    public static final float CELLHEIGHT = 32.0f;

    public static final int TOPBAR = 0;
    public static float WIDTH = 864.0f; 
    public static float HEIGHT = 640.0f; 
    public static final float BOARD_WIDTH = WIDTH / CELLSIZE;
    public static final float BOARD_HEIGHT = 20.0f;

    Tanks A, B, C, D;
    Tanks[] tanks = new Tanks[4]; 
    static int currentTankIndex = 0; 
    String[] playerNames = { "PLAYER A", "PLAYER B", "PLAYER C", "PLAYER D" };
    int[] scores = { 0, 0, 0, 0 };
    int scoreBoardX = (int) WIDTH - 100;
    int scoreBoardY = 40;

    public float x = 0;
    public float y = 0;
    int i = 0;

    public float xpositiontank = 0;
    public float ypositiontank = 0;

    public float ysmall = 0;
    public float yturret = 0;

    public PImage backgroundImage; 
    public PImage treeImage; 
    public PImage windImageLeft; 
    public PImage windImageRight;
    public PImage fuelBox;
    public static PImage parachuteImage;
    public PImage parachuteIcon;
    public PImage startScreen;
    Minim minim;

    boolean windDirection = false; 

    public static int FPS = 60;

    public String configPath;
    public static Random random = new Random();

    ArrayList<Float> xCoord = new ArrayList<>(); 
    ArrayList<Float> yCoord = new ArrayList<>(); 
    static ArrayList<Float> newYCoord = new ArrayList<>();
    ArrayList<Float> xTreeCoord = new ArrayList<>(); 
    ArrayList<Float> yTreeCoord = new ArrayList<>(); 
    ArrayList<Float> xTankCoord = new ArrayList<>();
    ArrayList<Float> yTankCoord = new ArrayList<>();
    ArrayList<Float> newYCoordTank = new ArrayList<>();
    ArrayList<Tanks> tanksList;
    Queue<Explosion> explosionsQueue = new LinkedList<>();
    ArrayList<Explosion> activeExplosions = new ArrayList<>();
    ArrayList<Tanks> rueTanks = new ArrayList<>();

    boolean leftKeyPressed = false;
    boolean rightKeyPressed = false;
    boolean colorChanged = false;
    boolean gameStart = false;
    private boolean gameOver = false;

    static AudioPlayer tankHit;
    static AudioPlayer loadScreen;
    static AudioPlayer lets_play;
    static AudioPlayer ace;

    boolean changePlayer = false;

    public int counter = 0;
    public int index = 0;
    int j = 0;
    int t = 0;
    int h = 0;
    int RED;
    int GREEN;
    int BLUE;
    int color1;
    int color2;
    int color3;
    int color4;
    static Tanks currentAttackerTank;

    JSONObject RootNode;
    JSONArray LevelsNode;
    JSONObject LevelArray;
    JSONObject playerColors;
    JSONArray tankColors;
    String layout;
    String myLevels;
    String background;
    String groundColor;
    String trees;
    String[] groundColorAlpha;

    Map<Float, Float> map = new HashMap<>();

    public void load() {

        RootNode = loadJSONObject("config.json");
        LevelsNode = RootNode.getJSONArray("levels");
        playerColors = RootNode.getJSONObject("player_colours");

        LevelArray = LevelsNode.getJSONObject(index);
        layout = LevelArray.getString("layout");
        
        background = LevelArray.getString("background");
        groundColor = LevelArray.getString("foreground-colour");
        groundColorAlpha = groundColor.split(",");
        trees = LevelArray.getString("trees");
        backgroundImage = this.loadImage("src/main/resources/Tanks/" + background);
        if (index != 1) {
            treeImage = this.loadImage("src/main/resources/Tanks/" + trees);
            treeImage.resize(32, 32);
        }
        minim = new Minim(this);
        tankHit = minim.loadFile("src/main/resources/Tanks/tankHit.mp3");
        lets_play = minim.loadFile("src/main/resources/Tanks/let's_play.mp3");
        ace = minim.loadFile("src/main/resources/Tanks/ace_sound.mp3");
        startScreen = this.loadImage("src/main/resources/Tanks/start_screen.png");
        startScreen.resize((int) (WIDTH), (int) HEIGHT);
        loadScreen = minim.loadFile("src/main/resources/Tanks/load.mp3");
        windImageLeft = this.loadImage("src/main/resources/Tanks/wind-1.png");
        windImageLeft.resize(41, 41);
        windImageRight = this.loadImage("src/main/resources/Tanks/wind.png");
        windImageRight.resize(41, 41);
        fuelBox = this.loadImage("src/main/resources/Tanks/fuel.png");
        fuelBox.resize(30, 30);
        parachuteImage = this.loadImage("src/main/resources/Tanks/parachute.png");
        parachuteImage.resize(50, 50);
        parachuteIcon = this.loadImage("src/main/resources/Tanks/parachute.png");
        parachuteIcon.resize(38, 38);
        File gameFile = new File(layout);
        try {
            float yi = 0.0f;
            Scanner reader = new Scanner(gameFile);
            while (reader.hasNextLine()) {
                String data = reader.nextLine();
                char[] dataCoords = data.toCharArray();
                for (float xi = 0.0f; xi < dataCoords.length; xi++) {
                    if (dataCoords[(int) xi] == 'X') { 
                        xCoord.add(xi);
                        yCoord.add(yi);
                    } else if (dataCoords[(int) xi] == 'T') {
                        xTreeCoord.add(xi);
                        yTreeCoord.add(yi);
                    } else if (dataCoords[(int) xi] == 'B') {
                        xTankCoord.add(xi);
                    } else if (dataCoords[(int) xi] == 'C') {
                        xTankCoord.add(xi);
                    } else if (dataCoords[(int) xi] == 'A') {
                        xTankCoord.add(xi);
                    } else if (dataCoords[(int) xi] == 'D') {
                        xTankCoord.add(xi);
                    }
                }
                yi++;
            }
            reader.close();

            for (int i = 0; i < xCoord.size(); i++) {
                map.put(xCoord.get(i), yCoord.get(i));
            }

            Collections.sort(xCoord, Comparator.comparing(a -> a));

            yCoord.clear();
            for (Float x : xCoord) {
                yCoord.add(map.get(x));
            }

            for (Float y : yCoord) {
                for (int i = 0; i < 32; i++) {
                    newYCoord.add(y);
                }
            }

            int windowSize = 32;

            for (int i = 0; i < newYCoord.size() - windowSize; i++) {
                float sum = 0.0f;
                for (int j = 0; j < windowSize; j++) {
                    sum += newYCoord.get(j + i);
                }
                newYCoord.set(i, (sum / windowSize));
            }

            for (int i = 0; i < newYCoord.size() - windowSize; i++) {
                float sum = 0.0f;
                for (int j = 0; j < windowSize; j++) {
                    sum += newYCoord.get(j + i);
                }
                newYCoord.set(i, (sum / windowSize));
            }
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

    }
    

    @Override
    public void settings() {
        size((int) WIDTH, (int) HEIGHT);
    }

    
    /** 
     * @param colorString
     * @return int[]
     */
    int[] parseRGB(String colorString) {
        String[] components = colorString.split(",");
        int r = Integer.parseInt(components[0].trim());
        int g = Integer.parseInt(components[1].trim());
        int b = Integer.parseInt(components[2].trim());
        return new int[] { r, g, b };
    }

    @Override
    public void setup() {

        frameRate(FPS);
        load();
        for (int i = 0; i < xTankCoord.size(); i++) {
            float x = (xTankCoord.get(i) * CELLSIZE) - 10;
            int index = (int) (xTankCoord.get(i) * 32);
            float y = ((newYCoord.get(index) + 1) * CELLSIZE) - 40;
            xpositiontank = x;
            ypositiontank = y;
            ysmall = y - 5;
            yturret = y - 10;

            switch (i) {
                case 0:
                    String tankColor1 = playerColors.getString("A"); 
                    int[] rgb1 = parseRGB(tankColor1);
                    color1 = color(rgb1[0], rgb1[1], rgb1[2]);
                    A = new Tanks(this, x, y, x, ysmall, "A", this);
                    tanks[0] = A;
                    rueTanks.add(A);
                    break;
                case 1:
                    String tankColor2 = playerColors.getString(new String("B")); 
                    int[] rgb2 = parseRGB(tankColor2);
                    color2 = color(rgb2[0], rgb2[1], rgb2[2]);
                    B = new Tanks(this, x, y, x, ysmall, "B", this);
                    tanks[1] = B;
                    rueTanks.add(B);
                    break;
                case 2:
                    String tankColor3 = playerColors.getString(new String("C")); 
                    int[] rgb3 = parseRGB(tankColor3);
                    color3 = color(rgb3[0], rgb3[1], rgb3[2]);
                    C = new Tanks(this, x, y, x, ysmall, "C", this);
                    tanks[2] = C;
                    rueTanks.add(C);
                    break;
                case 3:
                    String tankColor4 = playerColors.getString(new String("D")); 
                    int[] rgb4 = parseRGB(tankColor4);
                    color4 = color(rgb4[0], rgb4[1], rgb4[2]);
                    D = new Tanks(this, x, y, x, ysmall, "D", this);
                    tanks[3] = D;
                    rueTanks.add(D);
                    break;
                default:
                    break;
            }
        }
        Arrays.sort(tanks, Comparator.comparingDouble(Tanks::getXmain));
    }

    public void gamestart() {
        image(startScreen, 0, 0);
        loadingScreen();
    }

    @Override
    public void draw() {
        if (gameOver) {
            
            background(0); 
            fill(255);
            textAlign(CENTER, CENTER);
            textSize(32);
            text("Game Over!", width / 2, height / 2);
            
        } else {
            if (!gameStart) {
            gamestart();
        } else {
            letsplay();
            drawTerrain();
            displayMessage();
            drawfuelBox();
            drawWind();
            drawScoreBoard();
            drawHealthBar();
            drawPowerBar();
            drawParachuteImage();
            currentAttackerTank = tanks[currentTankIndex];
            ArrayList<Tanks> rueTanksCopy = new ArrayList<>(rueTanks);
            for (Tanks tank : rueTanksCopy) {
                if (tank.isAlive() && tank.getHealth() > 0) {
                    tank.drawT();
                    tank.drawParachute(this);
                    tank.resetParachuteDisplay();
                    drawArrow();
                    Iterator<Projectile> projectileIterator = tank.getProjectiles().iterator();
                    while (projectileIterator.hasNext()) {
                        Projectile projectile = projectileIterator.next();
                        projectile.draw(i);
                        projectile.update();
                        if (!Projectile.isprojmoving) { 
                                                        
                            
                            Explosion explosion = new Explosion(this, projectile.x, projectile.y, rueTanks, this,
                                    currentAttackerTank);
                            explosionsQueue.add(explosion);
                            
                            
                            projectileIterator.remove();
                            
                        }
                    }

                    if (i < 3) {
                        i++;
                    } else {
                        i = 0;
                    }

                }
            }

            
            for (Explosion explosion : activeExplosions) {
                explosion.update(1.0f / App.FPS); 
                explosion.render();
                if (!explosion.isitActive) {
                    explosion.resetDamageFlags(); 
                }
            }

            
            activeExplosions.removeIf(explosion -> !explosion.isitActive);
        }
    }}

    @Override
    public void keyPressed() {
        if (keyCode == 'C') {
            gameStart = true;
        }
        if (gameStart && keyCode == LEFT) {
            tanks[currentTankIndex].moveLeft(); 
        } else if (keyCode == RIGHT) {
            tanks[currentTankIndex].moveRight(); 
        } else if (keyCode == UP) {
            tanks[currentTankIndex].rotateTurretLeft();
        } else if (keyCode == DOWN) {
            tanks[currentTankIndex].rotateTurretRight();
        } else if (keyCode == 'W') {
            tanks[currentTankIndex].increaseTurretPower();
        } else if (keyCode == 'S') {
            tanks[currentTankIndex].decreaseTurretPower();
        } else if (keyCode == 'r' || keyCode == 'R') {
            tanks[currentTankIndex].repairKit();
        } else if (keyCode == 'f' || keyCode == 'F') {
            tanks[currentTankIndex].addFuel();
        } else if (keyCode == 'C') {
            gameStart = true;
        } else if (keyCode == 'T' || keyCode == 'T') {
            if(index < 2){
            index++;
            load();
            drawTerrain();
        }else{
            index = 0;
            load();
            
        }
        } else if (key == ' ') { 
            if (tanks[currentTankIndex].isAlive()) {
                tanks[currentTankIndex].fireProjectile(); 
                findNextAliveTank(); 
                counter = 0;
                changePlayer = true;
            }
        } 

    }

    public void drawScoreBoard() {
        
        noFill(); 
        rect(scoreBoardX - 60, scoreBoardY, 150, 150); 

        
        rect(scoreBoardX - 60, scoreBoardY, 150, 30); 

        fill(0); 
        textAlign(LEFT, TOP);
        textSize(18); 
        text("Scores", scoreBoardX - 50, scoreBoardY); 

        for (int i = 0; i < playerNames.length; i++) {
            switch (i) {
                case 0:
                    fill(color3);
                    text(playerNames[i] + ":   " + String.valueOf(tanks[1].getScore()),
                            scoreBoardX - 50, scoreBoardY + 30 * (i + 1));
                    break;
                case 1:
                    fill(color1);
                    text(playerNames[i] + ":   " + String.valueOf(tanks[2].getScore()),
                            scoreBoardX - 50, scoreBoardY + 30 * (i + 1));
                    break;
                case 2:
                    fill(color2);
                    text(playerNames[i] + ":   " + String.valueOf(tanks[3].getScore()),
                            scoreBoardX - 50, scoreBoardY + 30 * (i + 1));
                    break;
                case 3:
                    fill(color4);
                    text(playerNames[i] + ":   " + String.valueOf(tanks[0].getScore()),
                            scoreBoardX - 50, scoreBoardY + 30 * (i + 1));
                    break;
                default:
                    break;
            }
        }

        fill(99, 67, 215);
        textSize(13); 
        text("ENTER \"T\" TO CHANGE LAYOUT", scoreBoardX - 100, scoreBoardY + 160); 



    }

    public void drawPowerBar() {
        fill(0);
        text("POWER:  " + tanks[currentTankIndex].getTurretPower(), 400, 40);
    }

    public void drawTerrain() {
        image(backgroundImage, 0, 0);
        noStroke(); 
        RED = Integer.parseInt(groundColorAlpha[0]);
        GREEN = Integer.parseInt(groundColorAlpha[1]);
        BLUE = Integer.parseInt(groundColorAlpha[2]);
        fill(RED, GREEN, BLUE); 
        beginShape(); 
        vertex(0, height); 
        for (int i = 0; i < newYCoord.size(); i++) {
            vertex(i, newYCoord.get(i) * CELLSIZE); 
        }
        vertex(newYCoord.size() - 1, height); 
        endShape(CLOSE); 
        stroke(RED, GREEN, BLUE); 
        for (int i = 0; i < newYCoord.size() - 1; i++) {
            line(i, newYCoord.get(i) * CELLSIZE, i + 1, newYCoord.get(i + 1) * CELLSIZE);
        }
        if(index != 1){
            drawTrees(); 
        }
    }

    public void displayMessage() {
        String currentPlayer = playerNames[currentTankIndex];
        fill(0);
        textAlign(LEFT, TOP);
        textSize(21);
        text(currentPlayer, 10, 10);

    }

    public void drawfuelBox() {
        image(fuelBox, 150, 5);
        fill(0);
        textAlign(CENTER, CENTER);
        textSize(18);
        text(tanks[currentTankIndex].getFuel(), 200, 19);

    }

    public void drawWind() {
        windDirection = false;
        for (Tanks tank : tanks) {
            for (Projectile projectile : tank.getProjectiles()) {
                if (projectile.isWindPositive()) {
                    windDirection = true;
                    break;
                }
            }
        }

        if (windDirection) {
            image(windImageRight, scoreBoardX, 0);
        } else {
            image(windImageLeft, scoreBoardX, 0);
        }

        fill(0);
        textAlign(LEFT, TOP);
        textSize(18);
        text(Projectile.wind(), scoreBoardX + 50, 5);
    }

    public void drawTrees() {
        int i = 0;
        while (i < xTreeCoord.size()) {
            float xPosition = xTreeCoord.get(i) * CELLSIZE;
            float yPosition = (newYCoord.get((int) (xTreeCoord.get(i) * 32)) + 1) * CELLSIZE;
            yPosition += treeImage.height / 2 - 2 * CELLSIZE;
            image(treeImage, xPosition - 13, yPosition - 14, treeImage.width, treeImage.height);
            i++;
        }

    }

    public void drawHealthBar() {
        int health = tanks[currentTankIndex].getHealth();
        if (health <= 0) {
            tanks[currentTankIndex].isAlive = false; 
            aceSound();
            return; 
        }

        
        if (tanks[currentTankIndex] == A) {
            fill(255, 135, 55);
        } else if (tanks[currentTankIndex] == B) {
            fill(0, 0, 255); 
        } else if (tanks[currentTankIndex] == C) {
            fill(135, 0, 255); 
        } else if (tanks[currentTankIndex] == D) {
            fill(255, 5, 255); 
        }

        textSize(18);
        text("HEALTH:", 400, 15);
        rect(480, 20, 100, 15); 

        
        float sliderX = map(tanks[currentTankIndex].getTurretPower(), 0, health, 480, 570);
        fill(99, 99, 99); 
        rect(sliderX, 20, 10, 15); 
        text(health, 590, 15);
    }

    public void drawParachuteImage() {
        image(parachuteIcon, 145, 38);
        textAlign(CENTER, CENTER);
        textSize(18);
        fill(0);
        text(tanks[currentTankIndex].parachuteNumber, 200, 55);
    }

    public void drawArrow() {
        pushMatrix(); 
        translate(tanks[currentTankIndex].getXsmall(), tanks[currentTankIndex].getYsmall() - 40); 
                                                                                                  
                                                                                                  
                                                                                                  
        rotate(PI); 
        strokeWeight(2); 

        

        stroke(0); 

        
        line(0, 0, 0, -20); 
        line(0, -20, -5, -15); 
        line(0, -20, 5, -15); 

        strokeWeight(1);
        stroke(255);

        popMatrix(); 
    }

    public void tankHitSound() {
        if (tankHit != null) {
            tankHit.rewind();
            tankHit.play();
        }
    }

    public void loadingScreen() {
        if (loadScreen != null) {
            loadScreen.play();
        }
    }

    public void letsplay() {
        if (lets_play != null) {
            lets_play.play();
        }
    }

    public void aceSound() {
        if (ace != null) {
            ace.play();
        }
    }

   private void findNextAliveTank() {
    int startIndex = currentTankIndex;
    int nextIndex = (currentTankIndex + 1) % tanks.length; 

    while (nextIndex != startIndex) {
        if (tanks[nextIndex].isAlive()) {
            currentTankIndex = nextIndex;
            return; 
        }

        
        nextIndex = (nextIndex + 1) % tanks.length;
    }

    
    gameOver = true;
}

    public static void main(String[] args) {
        PApplet.main("Tanks.App");
    }

}