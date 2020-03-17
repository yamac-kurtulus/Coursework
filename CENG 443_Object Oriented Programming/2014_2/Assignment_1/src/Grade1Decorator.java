
import java.awt.Color;
import java.awt.Graphics2D;

//
//
//  Generated by StarUML(tm) Java Add-In
//
//  @ Project : Untitled
//  @ File Name : Grade1Decorator.java
//  @ Date : 13.3.2015
//  @ Author : 
//
//




public class Grade1Decorator extends KnightDecorator {

    public Grade1Decorator(Knight component) {
        super(component);
    }
    
    public void draw (Graphics2D g2d) {
        component.draw(g2d);
        Graphics2D gc = (Graphics2D) g2d.create();
        gc.translate(component.getX()-10, component.getY()+10);
        gc.setColor(Color.MAGENTA);
        gc.fillOval(0,0, 4, 4);
    }

 
}
