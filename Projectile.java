package Tanks;

import java.util.*;

import processing.core.PApplet;

public class Projectile {
    private static final float GRAVITY = 3.6f; 
    private static final float MIN_SPEED = 5.0f; 
    private static final float MAX_SPEED = 50.0f; 

    private PApplet app;
    public float x, y;
    private double vx, vy;
    private static int wind = -1;
    private static int changedWind;
    private App appInstance;
    Explosion explosion;
    public static boolean isprojmoving;
    Tanks tank;

    Random random = new Random();

    public Projectile(PApplet app, float x, float y, float angle, float power, App appInstance) {
        this.app = app;
        this.x = x;
        this.y = y;
        this.appInstance = appInstance;
        isprojmoving = true;
        if (changeWind()) {
            wind = changedWind;
        } else {
            wind = random.nextInt(35 - (-35) + 1) + (-35);
        }
        
        float speed = MIN_SPEED + (power / 100.0f) * (MAX_SPEED - MIN_SPEED);
        vx = speed * Math.cos(angle);
        vy = speed * Math.sin(angle);
    }

    
    /** 
     * @param i
     */
    public void draw(int i) {

        if (isprojmoving) {
            switch (i) {
                case 2:
                    app.fill(135, 0, 255); 
                    app.ellipse(x, y, 10, 10);
                    break;
                case 0:
                    app.fill(255, 135, 55); 
                    app.ellipse(x, y, 10, 10);
                    break;
                case 1:
                    app.fill(0, 0, 255); 
                    app.ellipse(x, y, 10, 10);
                    break;
                case 3:
                    app.fill(255, 55, 255); 
                    app.ellipse(x, y, 10, 10);
                    break;
                default:
                    app.fill(0); 
                    app.ellipse(x, y, 10, 10);
                    break;
            }
        }

    }

    public void update() {
        
        x += vx;
        y += vy;
        vx += wind(); 
        vy += GRAVITY; 

        int xIndex = (int) (x);

        if (xIndex >= 0 && xIndex < App.newYCoord.size()) {
            float mountainY = App.newYCoord.get(xIndex) * App.CELLSIZE;
            if (y >= mountainY) {
                explosion = new Explosion(appInstance, xIndex, y, appInstance.rueTanks, appInstance, tank);
                appInstance.activeExplosions.add(explosion); 
                isprojmoving = false;
            }
        }
    }
    public boolean changeWind() {
        
        int newWind = wind + (random.nextInt(5 - (-5) + 1) + (-5));
        wind = Math.max(-35, Math.min(newWind, 35));
        changedWind = wind;
        
        return true;
    }

    public static int wind() {
        return wind;
    }

    public boolean isWindPositive() {
        return wind >= 0;
    }

}
