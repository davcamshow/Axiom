package SIMAR;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.QuadCurve2D;

public class Conector extends QuadCurve2D.Double {
    private char origen;
    private char destino;
    private boolean selected;
    
    public Conector(int xi, int yi, int xc, int yc, int xf, int yf, char o, char d) {
        super(xi, yi, xc, yc, xf, yf);
        this.selected = true;
        this.origen = o;
        this.destino = d;
    }
    
    public void dibujarConectores(Graphics2D gr2D) {
        Stroke pincel = new BasicStroke(2.0F, 1, 0);
        gr2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        if (!this.selected) {
            gr2D.setColor(Color.BLACK);
            gr2D.setStroke(pincel);
            gr2D.draw(this);
            gr2D.setColor(Color.RED);
            gr2D.fillOval((int)getX1() - 3, (int)getY1() - 3, 6, 6);
        } else {
            gr2D.setColor(Color.RED);
            gr2D.setStroke(pincel);
            gr2D.draw(this);
            gr2D.setColor(Color.BLACK);
            gr2D.fillOval((int)getX1() - 3, (int)getY1() - 3, 6, 6);
        }
    }
    
    public char getOrigen() {
        return this.origen;
    }
    
    public char getDestino() {
        return this.destino;
    }
    
    public void setOrigen(char c) {
        this.origen = c;
    }
    
    public void setDestino(char c) {
        this.destino = c;
    }
    
    public void setSelected(boolean b) {
        this.selected = b;
    }
    
    public boolean isSelected() {
        return this.selected;
    }
}