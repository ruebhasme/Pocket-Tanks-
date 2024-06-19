package Tanks;

import java.util.ArrayList;
import processing.core.PApplet;

public class Explosion {

    public App appInstance;
    public Tanks attackerTank; 

    PApplet app;
    float x, y; 
    float max_radi; 
    float animatedtime = 0.2f; 
    float currentTime = 0; 
    boolean isitActive; 

    float damage;
    float newHeight; 
    int totalDamage;    
    int i = 0;
    
    ArrayList<Tanks> rueTanks = new ArrayList<>();
    Projectile projectile;

    public Explosion(PApplet app, float x, float y, ArrayList<Tanks> rueTanks, App appInstance, Tanks attackerTank) {
        this.app = app;
        this.x = x;
        this.y = y;
        this.max_radi = 30;
        this.isitActive = true;
        this.rueTanks = rueTanks;
        this.appInstance = appInstance;
        this.attackerTank = getAttackerTank(rueTanks);
        this.damage = 0;
        this.totalDamage = 0;
    }

    
    /** 
     * @param time
     */
    public void update(float time) {
        if (isitActive) {
            currentTime += time;
            if (currentTime >= animatedtime) {
                isitActive = false; 
                if (projectile != null) {
                    Projectile.isprojmoving = false;
                }
            }
        }
    }

    public void render() {
        appInstance.tankHitSound();
        app.noStroke();
        drawExplosionCircles(x, y, currentTime, max_radi);
    }

    public void drawExplosionCircles(float x, float y, float currentTime, float max_radi) {
        float[] radii = {
                PApplet.map(currentTime, 0, animatedtime, 0, max_radi),
                PApplet.map(currentTime, 0, animatedtime, 0, max_radi * 0.5f),
                PApplet.map(currentTime, 0, animatedtime, 0, max_radi * 0.2f)
        };
        int[][] colors = {
                { 255, 0, 0 },
                { 255, 165, 0 },
                { 255, 255, 0 }
        };
        for (int i = 0; i < radii.length; i++) {
            app.fill(colors[i][0], colors[i][1], colors[i][2]);
            app.ellipse(x, y, radii[i], radii[i]);
        }
        damageTerrain(x, y, radii[0]);
    }

    public void damageTerrain(float impactPointX, float impactPointY, float explosionRadius) {
        int damageStart = Math.max(0, (int) (impactPointX - explosionRadius));
        int damageEnd = Math.min(App.newYCoord.size(), (int) (impactPointX + explosionRadius));

        for (int index = damageStart; index < damageEnd; index++) {
            float distFromImpact = PApplet.abs(index - impactPointX);
            if (distFromImpact <= explosionRadius) {
                float angle = (float) Math.acos(distFromImpact / explosionRadius);
                float impactDepth = explosionRadius * (float) Math.sin(angle);
                float terrainNewHeight = impactPointY + impactDepth + angle;
                App.newYCoord.set(index, Math.max(App.newYCoord.get(index), terrainNewHeight / App.CELLSIZE));
            }
        }

        damageHealth(impactPointX, impactPointY);
        updateTankPositions(impactPointX, impactPointY, explosionRadius);
    }

    public void damageHealth(float impactX, float impactY) {
        float totalDamage = 0;
        for (Tanks tank : rueTanks) {
            if (!tank.isDamaged) {
                float tankCenterX = tank.getXmain() + 30;
                float tankCenterY = tank.getYmain() + 10;
                float distance = PApplet.dist(tankCenterX, tankCenterY + 12, impactX, impactY);
                if (distance < 0) {
                    distance = Math.abs(distance);
                }
                if (0 <= distance && distance <= max_radi + 10) {
                    if (attackerTank != null) { 
                        if (tank.parachuteNumber > 0) {
                            damage = (int) (30 * App.CELLSIZE / distance);
                        } else {
                            damage = (int) (30 * App.CELLSIZE / distance);
                        }

                        if (damage >= 25) {
                            damage = 60;
                        }
                        tank.reduceHealth((int) damage);
                        totalDamage += (int) damage;
                        tank.isDamaged = true;
                        attackerTank.setScore((int) totalDamage); 
                    } else {
                        
                        System.out.println("Attacker tank is null");
                    }
                }
            }
        }
    }

    public void resetDamageFlags() {
        for (Tanks tank : rueTanks) {
            if (tank != null) {
                tank.isDamagedByCurrentExplosion = false;
            }
        }
    }

    public void updateTankPositions(float impactX, float impactY, float radius) {
        int start = Math.max(0, (int) (impactX - radius));
        int end = Math.min(App.newYCoord.size(), (int) (impactX + radius));

        for (int i = start; i < end; i++) {
            float distance = PApplet.abs(i - impactX);

            if (distance <= radius) {
                
                float arc = (float) Math.acos(distance / radius);

                
                float depth = (float) Math.sin(arc) * radius;

                
                newHeight = impactY + depth + arc;

                for (Tanks tank : rueTanks) {
                    if (tank != null) {
                        float tankX = tank.getXmain();

                        
                        if (tankX >= i - radius && tankX <= i + radius) {
                            
                            tank.shouldDisplayParachute = true;
                            tank.setYmain((int) newHeight);
                            tank.setYsmall((int) newHeight - 5);
                            tank.updateTurretPosition((int) newHeight - 10);
                        }
                    }
                }
            }
        }
    }

    public Tanks getAttackerTank(ArrayList<Tanks> rueTanks) {
        return App.currentAttackerTank;
    }

}