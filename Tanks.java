package Tanks;

import java.util.ArrayList;
import java.util.List;
import processing.core.PApplet;

public class Tanks {
    private static final int MAX_HEALTH = 100;
    private static final int MAX_FUEL = 250;

    private int health;
    private int fuel;
    public float turretAngle;
    private float turretPower;
    private static final float CELLSIZE = 32.0f;
    public boolean shouldDisplayParachute = false; 
    public int parachuteNumber;
    public boolean parachuteActive = false;
    public String tankName;
    float turretBaseX;
    float turretBaseY;
    public boolean isDamagedByCurrentExplosion = false;
    public boolean isAlive = true;
    public boolean shouldBeRemoved = false;

    private PApplet app;
    private float xsmall;
    private float ysmall;
    private float xmain;
    private float ymain;
    public String Tank;
    private int tankScore;
    private App appInstance;
    boolean scoreUpdated;
    public boolean isDamaged = false;
    String colorOfTank;


    

    private List<Projectile> projectiles = new ArrayList<>();
    public int damageCount;

    public Tanks(PApplet app, float xmain, float ymain, float xsmall, float ysmall, String Tank, App appInstance) {
        this.app = app;
        this.xmain = xmain;
        this.ymain = ymain;
        this.xsmall = xsmall;
        this.ysmall = ysmall;
        this.health = MAX_HEALTH;
        this.fuel = MAX_FUEL;
        this.turretAngle = 0;
        this.turretPower = 50;
        this.Tank = Tank;
        this.appInstance = appInstance;
        this.tankScore = 0;
        this.parachuteNumber = 1;
        this.scoreUpdated = false;
        
    }

    public void drawT() {
        if (shouldBeRemoved) {
            return; 
        }

        app.fill(0); 
        float turretLength = 30;
        float turretWidth = 6;
        turretBaseX = xmain; 
        turretBaseY = ymain - 10; 
        float wheelSize = 10; 
        float wheelSpacing = 20; 

        
        switch (Tank) {
            case "A":
          
                app.fill(0);
                app.pushMatrix();
                app.translate(turretBaseX, turretBaseY);
                app.rotate(turretAngle);
                app.rect(0, 0, turretLength, turretWidth);
                app.popMatrix();
                app.fill(appInstance.color1); 
                app.ellipse(xsmall, ysmall, 20, 20); 
                app.ellipse(xmain, ymain, 30, 10); 
                app.fill(0);
                for (float wheelX = xmain - 15; wheelX <= xmain + 15; wheelX += wheelSpacing) {
                    app.ellipse(wheelX + 5, ymain + 7, wheelSize, wheelSize); 
                    app.ellipse(wheelX + 5, ymain + 7, wheelSize, wheelSize); 
                }
                break;
            case "B":
                app.fill(0);
                app.pushMatrix();
                app.translate(turretBaseX, turretBaseY);
                app.rotate(turretAngle);
                app.rect(0, 0, turretLength, turretWidth);
                app.popMatrix();
                app.fill(appInstance.color2); 
                app.ellipse(xsmall, ysmall, 20, 20); 
                app.ellipse(xmain, ymain, 30, 10); 
                app.fill(0);
                for (float wheelX = xmain - 15; wheelX <= xmain + 15; wheelX += wheelSpacing) {
                    app.ellipse(wheelX + 5, ymain + 7, wheelSize, wheelSize); 
                    app.ellipse(wheelX + 5, ymain + 7, wheelSize, wheelSize); 
                }
                break;
            case "C":
                app.fill(0);
                app.pushMatrix();
                app.translate(turretBaseX, turretBaseY);
                app.rotate(turretAngle);
                app.rect(0, 0, turretLength, turretWidth);
                app.popMatrix();
                app.fill(appInstance.color3); 
                app.ellipse(xsmall, ysmall, 20, 20); 
                app.ellipse(xmain, ymain, 30, 10); 
                app.fill(0);
                for (float wheelX = xmain - 15; wheelX <= xmain + 15; wheelX += wheelSpacing) {
                    app.ellipse(wheelX + 5, ymain + 7, wheelSize, wheelSize); 
                    app.ellipse(wheelX + 5, ymain + 7, wheelSize, wheelSize); 
                }
                break;
            case "D":
                app.fill(0);
                app.pushMatrix();
                app.translate(turretBaseX, turretBaseY);
                app.rotate(turretAngle);
                app.rect(0, 0, turretLength, turretWidth);
                app.popMatrix();
                app.fill(appInstance.color4); 
                app.ellipse(xsmall, ysmall, 20, 20); 
                app.ellipse(xmain, ymain, 30, 10); 
                app.fill(0);
                
                for (float wheelX = xmain - 15; wheelX <= xmain + 15; wheelX += wheelSpacing) {
                    app.ellipse(wheelX + 5, ymain + 7, wheelSize, wheelSize); 
                    app.ellipse(wheelX + 5, ymain + 7, wheelSize, wheelSize); 
                }
                break;
            default:
                break;
        }
    }

    
    /** 
     * @param x
     */
    public void updateYCoordinate(float x) {
        int index = (int) x;
        if (index >= 0 && index < App.newYCoord.size()) {
            float newYmain = (App.newYCoord.get(index) + 1) * CELLSIZE - 40;
            float newYsmall = newYmain - 5;

            if (newYmain >= 0 && newYmain < App.HEIGHT) {
                ymain = newYmain;
                ysmall = newYsmall;
            }
        }
    }

    public void stopMoving() {
        drawT();
    }

    public void moveLeft() {
        if (!isAlive) {
            return; 
        }
        
        if (xmain > 0 && this.fuel > 0) { 
            xmain -= 3;
            xsmall -= 3;
            updateYCoordinate(xmain); 
            drawT();
            fuel();
        }
    }

    public void moveRight() {
        if (!isAlive) {
            return; 
        }
        
        if (xmain < app.width - 30 && this.fuel > 0) { 
            xmain += 3;
            xsmall += 3;
            updateYCoordinate(xmain); 
            drawT();
            fuel();
        }
    }

    public void increaseTurretPower() {
        if (turretPower < 100 && turretPower < health) {
            this.turretPower += 1;
        } else if (turretPower >= 100) {
            this.turretPower = 100;
        }
    }

    public void decreaseTurretPower() {
        if (turretPower > 0) {
            this.turretPower -= 1;
        } else {
            this.turretPower = 0;
        }
    }

    public void rotateTurretLeft() {
        if (!isAlive) {
            return; 
        }
        if (turretAngle < -Math.PI) { 
            turretAngle = (float) -Math.PI; 
        }
        turretAngle -= 0.1; 
        drawT();
    }

    public void rotateTurretRight() {
        if (!isAlive) {
            return; 
        }
        if (turretAngle > 0.0) { 
            turretAngle = (float) 0.0; 
        }
        turretAngle += 0.05; 
        drawT();
    }

    public void fireProjectile() {
        float turretBaseX = xmain; 
        float turretBaseY = ymain - 10; 

        float x = turretBaseX + (float) Math.cos(turretAngle) * 30;
        float y = turretBaseY + (float) Math.sin(turretAngle) * 30;
        
        Projectile projectile = new Projectile(app, x, y, turretAngle, turretPower, appInstance); 
        
        parachuteActive = true;
        projectiles.add(projectile);
        projectile.changeWind();
    }

    public float getXmain() {
        return xmain;
    }

    public void fuel() {
        this.fuel = this.fuel - 1;
    }

    public List<Projectile> getProjectiles() {
        return projectiles;
    }

    public int getFuel() {
        return this.fuel;
    }

    public void takeDamage(int damage) {
        health -= damage;
        if (health <= 0) {
            
            
        }
    }

    public void removeProjectile(Projectile projectile) {
        projectiles.remove(projectile);
    }

    public float getYmain() {
        return ymain;
    }

    public int getTurretPower() {
        return (int) turretPower;
    }

    public void setYmain(float ymain) {
        this.ymain = ymain;
    }

    public void setXsmall(float xsmall) {
        this.xsmall = xsmall;
    }

    public float getXsmall() {
        return xsmall;
    }

    public void setYsmall(float ysmall) {
        this.ysmall = ysmall;
    }

    public float getYsmall() {
        return ysmall;
    }

    public void setXmain(float xmain) {
        this.xmain = xmain;
    }

    public int getHealth() {
        return this.health;
    }

    public void reduceHealth(int damage) {
        health -= damage;
        if (health <= 0) {
            isAlive = false; 
            
        }
    }

    public void updateTurretPosition(int newTurretBaseY) {
        this.ymain = newTurretBaseY + 10;
        this.ysmall = newTurretBaseY + 5;
    }

    public void drawParachute(App appInstance) {
        if (shouldDisplayParachute) {
            if (this.parachuteNumber > 0) {
                appInstance.image(App.parachuteImage, xsmall - 30, ysmall - 60);
                this.parachuteNumber--;
            }
        }
    }

    public void resetParachuteDisplay() {
        shouldDisplayParachute = false;
    }

    public void repairKit() {
        if (this.health <= 80 && this.tankScore >= 20) { 
            this.health += 20;
            this.setScore(-20); 
            appInstance.drawScoreBoard();
            appInstance.drawHealthBar();
        }
    }
    
    public void addFuel() {
        if (this.fuel <= 50 && this.tankScore >= 10) { 
            this.fuel += 200;
            this.setScore(-10); 
        }
    }

    public void setScore(int i) {
        this.tankScore = this.tankScore + i;
    }

    public int getScore() {
        return this.tankScore;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void handleDeath() {
        isAlive = false;
        if (this.shouldBeRemoved == true) {
            appInstance.rueTanks.remove(this);
        }
    }
}