
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

//
//
//  Generated by StarUML(tm) Java Add-In
//
//  @ Project : Untitled
//  @ File Name : Mage.java
//  @ Date : 13.3.2015
//  @ Author : 
//
//
public class Mage extends Knight {
    
    GeneralPath shape;
    Polygon poly = new Polygon(
                    new int[]{0, -8, 0, 8},
                    new int[]{-12, 0, 12, 0},
                    4);
    
    @Override
    public void rangedAttack() {
//       public void rangedAttack() {
        if (CanAttack()) {
            setSkillUsed(true);
            setCanAttack(false);
            Knight t;
            for (int i = 0; i < getTeam().size(); i++) {
                if ((t = getTeam().enemy.get(i)) != null && insideArea (t))
                    if (t.takeDmg(75))
                        this.setScore (this.getScore() + 75);
            }
            getCoolDown().schedule(new TimerTask() {
                @Override
                public void run() {
                    setSkillUsed(false);
                }
            }, 500);
            getCoolDown().schedule(new TimerTask() {

                @Override
                public void run() {
                    setCanAttack(true);
                }
            }, (long) getCdLength());
        }     
    }

    @Override
    public void draw(Graphics2D g) {
        Graphics2D gc = (Graphics2D) g.create();
        gc.setColor(getTeam().color);
        gc.translate(getX(), getY());
        gc.fill(poly);
         if (isSkillUsed()) {
            gc.setColor(new java.awt.Color(1f, 1f, 0f, 0.5f));
            gc.fillOval(-50, -50, 100, 100);
        }
         if (isAttacking()) {
            gc.setColor(Color.MAGENTA);
            gc.drawLine(0,0, (int) (getAttackTarget().getX() - getX()), (int) ((int)getAttackTarget().getY() - getY()));
         }
        gc.setColor(Color.WHITE);
        gc.setFont(Font.decode("arial 10"));
        gc.drawString("" + this.getHP(), -8, 5);

        gc.dispose();
    }

    
    Mage () {
        speed = 75f;
        canAttack = true;
        coolDown = new Timer();
        hitPoint = 400;
        maxHealth = 400;
        score = 0;
        pxToMove = speed / (1000f / Simulation.DELAY);
        attackRadius = 75;
        skillRadius = 100;
        cdLength = 2000;
    }

   boolean insideArea(Knight k) {
        float dx = k.getX() - this.getX();
        float dy = k.getY() - this.getY();
        return getSkillRadius()*getSkillRadius() > dx*dx+dy*dy;
    }

    
    
}
