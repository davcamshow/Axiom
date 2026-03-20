package SIMAR;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;

public class GNodo {
    private Stroke pincel;
    private Ellipse2D.Double path;
    private boolean selected;
    private char id;
    
    public GNodo(int x1, int y1, char id, boolean s) {
        this.path = new Ellipse2D.Double(x1, y1, 35.0D, 35.0D);
        this.id = id;
        this.selected = true;
        this.pincel = new BasicStroke(6.0F, 1, 0);
    }
    
    public Point getPoint() {
        int x = (int)this.path.getMinX();
        int y = (int)this.path.getMinY();
        return new Point(x, y);
    }
    
    public void setSelected(boolean b) {
        this.selected = b;
    }
    
    public void dibujarUnselectedNodo(Graphics2D gr2d) {
        gr2d.setStroke(this.pincel);
        gr2d.setColor(Color.BLUE);
        gr2d.draw(this.path);
        gr2d.setColor(Color.YELLOW);
        gr2d.fillOval((int)this.path.getMinX() + 4, (int)this.path.getMinY() + 4, 28, 28);
        gr2d.setColor(Color.BLACK);
        gr2d.drawString("" + this.id, (int)this.path.getCenterX() - 2, (int)this.path.getCenterY() + 2);
    }
    
    public void dibujarSelectedNodo(Graphics2D gr2d) {
        gr2d.setStroke(this.pincel);
        gr2d.setColor(Color.RED);
        gr2d.draw(this.path);
        gr2d.setColor(Color.YELLOW);
        gr2d.fillOval((int)this.path.getMinX() + 4, (int)this.path.getMinY() + 4, 28, 28);
        gr2d.setColor(Color.BLACK);
        gr2d.drawString("" + this.id, (int)this.path.getCenterX() - 2, (int)this.path.getCenterY() + 2);
    }
    
    public char getId() {
        return this.id;
    }
    
    public Point getCenter() {
        Point p = new Point();
        p.x = (int)this.path.getCenterX();
        p.y = (int)this.path.getCenterY();
        return p;
    }
    
    public int getMinX() {
        return (int)this.path.getMinX();
    }
    
    public int getMinY() {
        return (int)this.path.getMinY();
    }
    
    public int getMaxX() {
        return (int)this.path.getMaxX();
    }
    
    public int getMaxY() {
        return (int)this.path.getMaxY();
    }
    
    public void set(int x, int y) {
        this.path = new Ellipse2D.Double(x, y, 35.0D, 35.0D);
    }
    
    public boolean isSelected() {
        return this.selected;
    }
    
    public boolean contains(int xi, int yi) {
        return this.path.contains(xi, yi);
    }
    
    public void setId(char c) {
        this.id = c;
    }
}