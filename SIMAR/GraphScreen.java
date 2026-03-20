package SIMAR;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

public class GraphScreen extends JPanel {
    public final char NULL = Character.MIN_VALUE;
    public final char NODE = '\001';
    public final char CONECT = '\002';
    public final char SELECT = '\003';
    public final char SELECTED = '\004';
    public final char DELETE = '\005';
    public final char ARC = '\006';
    public final char CONECTING = '\007';
    
    private boolean[][] matriz;
    private GNodo[] nodos;
    private ArrayList<Conector> lazos;
    private int totalNodos;
    private int nodoActual;
    private char[] nombres = new char[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i' };
    private boolean[] imgnombres;
    private short key;
    Relacion relacion;
    private Image OSI;
    private boolean nuevo;
    private int operacion;
    final int LARGO = 550;
    final int ANCHO = 300;
    private int xi;
    private int yi;
    private int xf;
    private int yf;
    private Graphics2D offscreen;
    private int prevx;
    private int prevy;
    private int origen;
    private int destino;
    private boolean enabled;
    
    public boolean isFocusable() {
        return true;
    }
    
    public GraphScreen() {
        setPreferredSize(new Dimension(550, 300));
        setBackground(Color.WHITE);
        this.origen = -1;
        this.destino = -1;
        this.matriz = new boolean[9][9];
        this.nodos = new GNodo[9];
        this.totalNodos = 0;
        this.nodoActual = -1;
        this.key = 0;
        this.imgnombres = new boolean[9];
        this.operacion = 0;
        this.enabled = false;
        this.lazos = new ArrayList<>();
        
        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (!GraphScreen.this.enabled)
                    return;
                int opcion = e.getModifiersEx();
                
                if (opcion == 4) {
                    GraphScreen.this.operacion = 3;
                    
                    boolean isns = (GraphScreen.this.nodoActual > -1);
                    int isls = -1;
                    if (!isns) {
                        for (int i = 0; i < GraphScreen.this.lazos.size(); i++) {
                            Conector con = GraphScreen.this.lazos.get(i);
                            if (con.isSelected()) {
                                isls = i;
                                break;
                            }
                        }
                    }
                    String eliminate = (isns == true) ? "Eliminar Nodo" : "Eliminar lazo";
                    
                    JPopupMenu popup = new JPopupMenu();
                    JMenuItem jmeliminar = new JMenuItem(eliminate);
                    if (isns == true) {
                        jmeliminar.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent e) {
                                GraphScreen.this.borrarNodo();
                            }
                        });
                    } else {
                        jmeliminar.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent e) {
                                GraphScreen.this.borrarConector();
                                GraphScreen.this.clearOffscreen();
                                GraphScreen.this.dibujarGrafo();
                            }
                        });
                    }
                    JMenuItem jmcambiar = new JMenuItem("Cambiar Números/Letras");
                    jmcambiar.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            GraphScreen.this.cambiarNombres();
                        }
                    });
                    
                    JMenuItem jmborrarasociados = new JMenuItem("Borrar asociaciones");
                    jmborrarasociados.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            char ch = GraphScreen.this.nodos[GraphScreen.this.nodoActual].getId();
                            for (int i = 0; i < GraphScreen.this.lazos.size(); i++) {
                                Conector c = GraphScreen.this.lazos.get(i);
                                if (c.getOrigen() == ch || c.getDestino() == ch) {
                                    int ori = -1;
                                    int des = -1;
                                    int j;
                                    for (j = 0; j < GraphScreen.this.totalNodos; j++) {
                                        if (GraphScreen.this.nodos[j].getId() == c.getOrigen()) {
                                            ori = j;
                                            break;
                                        }
                                    }
                                    for (j = 0; j < GraphScreen.this.totalNodos; j++) {
                                        if (GraphScreen.this.nodos[j].getId() == c.getDestino()) {
                                            des = j;
                                            break;
                                        }
                                    }
                                    GraphScreen.this.lazos.remove(i);
                                    i--;
                                    GraphScreen.this.matriz[ori][des] = false;
                                }
                            }
                            GraphScreen.this.clearOffscreen();
                            GraphScreen.this.dibujarGrafo();
                        }
                    });
                    
                    if (isns == true || isls > -1) {
                        popup.add(jmborrarasociados);
                        popup.add(jmeliminar);
                    }
                    
                    popup.add(jmcambiar);
                    popup.show(e.getComponent(), e.getX(), e.getY());
                    return;
                }
            }
            
            public void mouseEntered(MouseEvent e) {
                if (GraphScreen.this.enabled) {
                    GraphScreen.this.requestFocus();
                }
            }
            
            public void mousePressed(MouseEvent e) {
                if (!GraphScreen.this.enabled)
                    return;
                int opcion = e.getModifiersEx();
                
                if (opcion == 4) {
                    GraphScreen.this.operacion = 3;
                }
                
                GraphScreen.this.xf = GraphScreen.this.xi = e.getX();
                GraphScreen.this.yf = GraphScreen.this.yi = e.getY();
                GraphScreen.this.clearOffscreen();
                GraphScreen.this.borraSeleccionConectores();
                
                if (GraphScreen.this.operacion == 1) {
                    GraphScreen.this.nuevoNodo();
                    return;
                }
                if (GraphScreen.this.operacion == 3) {
                    GraphScreen.this.comprobarSeleccionNodos();
                    if (GraphScreen.this.nodoActual < 0)
                        GraphScreen.this.comprobarSeleccionConectores();
                    GraphScreen.this.dibujarGrafo();
                    return;
                }
                if (GraphScreen.this.operacion == 2) {
                    GraphScreen.this.borraSeleccionConectores();
                    GraphScreen.this.origen = GraphScreen.this.esPosicionValida();
                    if (GraphScreen.this.origen > -1) {
                        GraphScreen.this.operacion = 7;
                    }
                    return;
                }
            }
            
            public void mouseReleased(MouseEvent e) {
                if (!GraphScreen.this.enabled)
                    return;
                GraphScreen.this.xf = e.getX();
                GraphScreen.this.yf = e.getY();
                
                if (GraphScreen.this.operacion == 7) {
                    GraphScreen.this.destino = GraphScreen.this.esPosicionValida();
                    if (GraphScreen.this.destino > -1) {
                        GraphScreen.this.creaConexion();
                    } else {
                        GraphScreen.this.destino = -1;
                        GraphScreen.this.origen = -1;
                    }
                    GraphScreen.this.operacion = 2;
                    GraphScreen.this.dibujarGrafo();
                    return;
                }
                if (GraphScreen.this.nodoActual > -1) {
                    if (GraphScreen.this.xf > 535 || GraphScreen.this.yf > 285 || GraphScreen.this.xf < 0 || GraphScreen.this.yf < 0) {
                        GraphScreen.this.mensajeError("Error de dibujo", "Imposible dibujar en esa region");
                        if (GraphScreen.this.nuevo) {
                            GraphScreen.this.borrarNodo();
                            GraphScreen.this.nuevo = false;
                            GraphScreen.this.operacion = 3;
                        } else {
                            GraphScreen.this.cancelaPosicionamiento();
                            GraphScreen.this.actualizarPosConectores();
                            GraphScreen.this.clearOffscreen();
                            GraphScreen.this.dibujarGrafo();
                        }
                        return;
                    }
                    if (GraphScreen.this.traslape()) {
                        GraphScreen.this.mensajeError("Error de dibujo", "Evitar traslapes entre nodos");
                        if (GraphScreen.this.nuevo) {
                            GraphScreen.this.borrarNodo();
                            GraphScreen.this.nuevo = false;
                            GraphScreen.this.operacion = 3;
                        } else {
                            GraphScreen.this.cancelaPosicionamiento();
                            GraphScreen.this.actualizarPosConectores();
                            GraphScreen.this.clearOffscreen();
                            GraphScreen.this.dibujarGrafo();
                        }
                        return;
                    }
                    GraphScreen.this.nuevo = false;
                    GraphScreen.this.operacion = 3;
                    GraphScreen.this.clearOffscreen();
                    GraphScreen.this.actualizarPosConectores();
                    GraphScreen.this.dibujarGrafo();
                }
            }
        });
        
        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                GraphScreen.this.xf = e.getX();
                GraphScreen.this.yf = e.getY();
                
                if (GraphScreen.this.operacion == 4) {
                    if (GraphScreen.this.nodoActual > -1) {
                        GraphScreen.this.clearOffscreen();
                        GraphScreen.this.actualizarPosNodo();
                        GraphScreen.this.dibujarGrafo();
                        GraphScreen.this.actualizarPosConectores();
                    }
                    return;
                }
                
                if (GraphScreen.this.operacion == 7) {
                    GraphScreen.this.repaint();
                    return;
                }
            }
        });
        
        addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                if (GraphScreen.this.nodoActual > -1) {
                    if (e.getKeyCode() == 127) {
                        GraphScreen.this.borrarNodo();
                        GraphScreen.this.operacion = 3;
                        return;
                    }
                } else if (e.getKeyCode() == 127) {
                    GraphScreen.this.borrarConector();
                    GraphScreen.this.clearOffscreen();
                    GraphScreen.this.dibujarGrafo();
                }
            }
        });
        
        requestFocus();
        repaint();
    }
    
    private void automatizarDibujado() {
        ArrayList<?> lista_nodos = this.relacion.getNodos();
        
        for (int i = 0; i < this.relacion.size(); i++) {
            this.nombres[i] = lista_nodos.get(i).toString().charAt(0);
        }
        this.key = 0;
        this.totalNodos = this.relacion.size();
        this.imgnombres = new boolean[9];
        switch (this.totalNodos) {
            case 1:
                dibujarUno();
                break;
            case 2:
                dibujarDos();
                break;
            case 3:
                dibujarTres();
                break;
            case 4:
                dibujarCuatro();
                break;
            case 5:
                dibujarCinco();
                break;
            case 6:
                dibujarSeis();
                break;
            case 7:
                dibujarSiete();
                break;
            case 8:
                dibujarOcho();
                break;
            case 9:
                dibujarNueve();
                break;
        }
        crearConectores();
    }
    
    private void actualizarPosNodo() {
        this.nodos[this.nodoActual].set(this.xf, this.yf);
        this.nodos[this.nodoActual].dibujarSelectedNodo(this.offscreen);
    }
    
    private void actualizarPosConectores() {
        for (int i = 0; i < this.lazos.size(); i++) {
            Conector c = this.lazos.get(i);
            if (c.isSelected()) {
                int o = -1;
                int d = -1;
                int j;
                for (j = 0; j < this.nodos.length; j++) {
                    if (this.nodos[j] != null && this.nodos[j].getId() == c.getOrigen()) {
                        o = j;
                        break;
                    }
                }
                for (j = 0; j < this.nodos.length; j++) {
                    if (this.nodos[j] != null && this.nodos[j].getId() == c.getDestino()) {
                        d = j;
                        break;
                    }
                }
                if (o == d && o != -1) {
                    int xn = this.nodos[o].getMinX() + 10;
                    int yn = this.nodos[o].getMinY();
                    int xn2 = xn + 15;
                    int pcx = xn + 5;
                    int pcy = yn - 35;
                    
                    Conector c1 = new Conector(xn, yn, pcx, pcy, xn2, yn, this.nodos[o].getId(), this.nodos[o].getId());
                    c.setCurve(c1);
                    repaint();
                } else if (o != -1 && d != -1) {
                    Point ori = this.nodos[o].getCenter();
                    Point des = this.nodos[d].getCenter();
                    
                    if (ori.x == des.x) {
                        ori.x++;
                    }
                    
                    Point nori = resolverEcuacion(ori.x, ori.y, des.x, des.y, true);
                    Point ndes = resolverEcuacion(des.x, des.y, ori.x, ori.y, false);
                    
                    int xc = (nori.x + ndes.x) / 2;
                    int yc = (nori.y + ndes.y) / 2;
                    
                    int dx = Math.abs(nori.x - ndes.x);
                    int dy = Math.abs(nori.y - ndes.y);
                    
                    int incx = (int)(dx * 0.35F);
                    int incy = (int)(dy * 0.35F);
                    
                    int pcx = -1;
                    int pcy = -1;
                    
                    if (o > d) {
                        pcx = xc + incy;
                        pcy = yc + incx;
                    } else if (o < d) {
                        pcx = xc - incy;
                        pcy = yc - incx;
                    }
                    
                    Conector c1 = new Conector(nori.x, nori.y, pcx, pcy, ndes.x, ndes.y, this.nodos[o].getId(), this.nodos[d].getId());
                    c.setCurve(c1);
                }
            }
        }
        repaint();
    }
    
    private void borraSeleccionConectores() {
        if (this.lazos == null)
            return;
        for (int i = 0; i < this.lazos.size(); i++) {
            Conector c = this.lazos.get(i);
            c.setSelected(false);
        }
        dibujarGrafo();
    }
    
    private void borrarNodo() {
        if (this.totalNodos == 1) {
            mensajeError("Error de inconsistencia", "Imposible eliminar el último nodo de la relacion");
            return;
        }
        borrarConectores();
        recuperarKey(this.nodos[this.nodoActual].getId());
        this.nodos[this.nodoActual] = null;
        int indice = 10;
        int i;
        for (i = this.nodoActual; i < this.nodos.length; i++) {
            if (i + 1 == this.nodos.length) {
                indice = i;
                break;
            }
            this.nodos[i] = this.nodos[i + 1];
        }
        
        for (i = indice; i < this.nodos.length; i++) {
            this.nodos[i] = null;
        }
        
        renombrarNodos();
        
        this.nodoActual = -1;
        this.totalNodos--;
        clearOffscreen();
        dibujarGrafo();
    }
    
    private void borrarConectores() {
        this.lazos.clear();
        for (int i = 0; i < this.matriz.length; i++) {
            for (int j = 0; j < this.matriz.length; j++) {
                this.matriz[i][j] = false;
            }
        }
    }
    
    private void cancelaPosicionamiento() {
        this.nuevo = false;
        this.operacion = 3;
        this.nodos[this.nodoActual].set(this.prevx, this.prevy);
        clearOffscreen();
        dibujarGrafo();
    }
    
    public void cambiaOperacion(char op) {
        this.operacion = op;
        if (this.nodoActual > -1) {
            if (op == '\005') {
                borrarNodo();
                this.operacion = 3;
            } else {
                this.nodos[this.nodoActual].setSelected(false);
                this.nodoActual = -1;
            }
        } else if (op == '\005') {
            borrarConector();
        }
        
        borraSeleccionConectores();
        clearOffscreen();
        dibujarGrafo();
    }
    
    private void cambiarNombres() {
        if (this.nodos[0] == null) return;
        char c = this.nodos[0].getId();
        if (Character.isLetter(c)) {
            char resta = '0';
            int i;
            for (i = 0; i < this.nodos.length && this.nodos[i] != null; i++) {
                this.nodos[i].setId((char)(this.nodos[i].getId() - resta));
            }
            for (i = 0; i < this.lazos.size(); i++) {
                Conector con = this.lazos.get(i);
                con.setOrigen((char)(con.getOrigen() - resta));
                con.setDestino((char)(con.getDestino() - resta));
            }
            for (i = 0; i < this.nombres.length; i++) {
                this.nombres[i] = (char)(this.nombres[i] - resta);
            }
        } else {
            char adicion = '0';
            int i;
            for (i = 0; i < this.totalNodos; i++) {
                this.nodos[i].setId((char)(this.nodos[i].getId() + adicion));
            }
            for (i = 0; i < this.lazos.size(); i++) {
                Conector con = this.lazos.get(i);
                con.setOrigen((char)(con.getOrigen() + adicion));
                con.setDestino((char)(con.getDestino() + adicion));
            }
            for (i = 0; i < this.nombres.length; i++) {
                this.nombres[i] = (char)(this.nombres[i] + adicion);
            }
        }
        clearOffscreen();
        dibujarGrafo();
    }
    
    private void crearConectores() {
        for (int i = 0; i < this.relacion.size(); i++) {
            for (int j = 0; j < this.relacion.size(); j++) {
                if (this.matriz[i][j]) {
                    if (i == j) {
                        int xn = this.nodos[i].getMinX() + 10;
                        int yn = this.nodos[i].getMinY();
                        int xn2 = xn + 15;
                        int pcx = xn + 5;
                        int pcy = yn - 35;
                        
                        Conector c = new Conector(xn, yn, pcx, pcy, xn2, yn, this.nodos[i].getId(), this.nodos[i].getId());
                        c.setSelected(false);
                        this.lazos.add(c);
                    } else {
                        char o = this.nodos[j].getId();
                        char d = this.nodos[i].getId();
                        
                        Point ori = this.nodos[j].getCenter();
                        Point des = this.nodos[i].getCenter();
                        
                        if (ori.x == des.x) {
                            ori.x++;
                        }
                        
                        Point nori = resolverEcuacion(ori.x, ori.y, des.x, des.y, true);
                        Point ndes = resolverEcuacion(des.x, des.y, ori.x, ori.y, false);
                        
                        int xc = (nori.x + ndes.x) / 2;
                        int yc = (nori.y + ndes.y) / 2;
                        
                        int dx = Math.abs(nori.x - ndes.x);
                        int dy = Math.abs(nori.y - ndes.y);
                        
                        int incx = (int)(dx * 0.35F);
                        int incy = (int)(dy * 0.35F);
                        
                        int pcx = -1;
                        int pcy = -1;
                        
                        if (o > d) {
                            pcx = xc + incy;
                            pcy = yc + incx;
                        } else if (o < d) {
                            pcx = xc - incy;
                            pcy = yc - incx;
                        }
                        
                        Conector c = new Conector(nori.x, nori.y, pcx, pcy, ndes.x, ndes.y, o, d);
                        c.setSelected(false);
                        this.lazos.add(c);
                    }
                }
            }
        }
    }
    
    private void borrarConector() {
        for (int i = 0; i < this.lazos.size(); i++) {
            Conector c = this.lazos.get(i);
            if (c.isSelected()) {
                char ori = c.getOrigen();
                char des = c.getDestino();
                
                int iori = -1;
                int ides = -1;
                int j;
                for (j = 0; j < this.nombres.length; j++) {
                    if (this.nombres[j] == ori) {
                        iori = j;
                        break;
                    }
                }
                for (j = 0; j < this.nombres.length; j++) {
                    if (this.nombres[j] == des) {
                        ides = j;
                        break;
                    }
                }
                this.matriz[iori][ides] = false;
                this.lazos.remove(i);
                i--;
            }
        }
        this.operacion = 3;
    }
    
    private void checkOSI() {
        if (this.OSI == null) {
            this.OSI = createImage((getSize()).width, (getSize()).height);
            Graphics OSG = this.OSI.getGraphics();
            OSG.setColor(getBackground());
            OSG.fillRect(0, 0, (getSize()).width, (getSize()).height);
            OSG.dispose();
        }
    }
    
    private void creaConexion() {
        if (this.origen == -1 || this.destino == -1) return;
        if (this.matriz[this.origen][this.destino]) {
            mensajeError("Informacion redundante", "La relacion ya existe");
        } else {
            this.matriz[this.origen][this.destino] = true;
            if (this.origen == this.destino) {
                int xn = this.nodos[this.origen].getMinX() + 10;
                int yn = this.nodos[this.origen].getMinY();
                int xn2 = xn + 15;
                int pcx = xn + 5;
                int pcy = yn - 35;
                
                Conector c = new Conector(xn, yn, pcx, pcy, xn2, yn, 
                    this.nodos[this.origen].getId(), this.nodos[this.destino].getId());
                this.lazos.add(c);
            } else {
                char o = this.nodos[this.origen].getId();
                char d = this.nodos[this.destino].getId();
                
                Point ori = this.nodos[this.origen].getCenter();
                Point des = this.nodos[this.destino].getCenter();
                
                if (ori.x == des.x) {
                    ori.x++;
                }
                
                Point nori = resolverEcuacion(ori.x, ori.y, des.x, des.y, true);
                Point ndes = resolverEcuacion(des.x, des.y, ori.x, ori.y, false);
                
                int xc = (nori.x + ndes.x) / 2;
                int yc = (nori.y + ndes.y) / 2;
                
                int dx = Math.abs(nori.x - ndes.x);
                int dy = Math.abs(nori.y - ndes.y);
                
                int incx = (int)(dx * 0.35F);
                int incy = (int)(dy * 0.35F);
                
                int pcx = -1;
                int pcy = -1;
                
                if (o > d) {
                    pcx = xc + incy;
                    pcy = yc + incx;
                } else {
                    pcx = xc - incy;
                    pcy = yc - incx;
                }
                
                Conector c = new Conector(nori.x, nori.y, pcx, pcy, ndes.x, ndes.y, 
                    this.nodos[this.origen].getId(), this.nodos[this.destino].getId());
                this.lazos.add(c);
            }
        }
        this.destino = -1;
        this.origen = -1;
    }
    
    public void clearOffscreen() {
        if (this.offscreen != null) {
            this.offscreen.setColor(Color.WHITE);
            this.offscreen.fillRect(0, 0, 550, 300);
        }
    }
    
    private void comprobarSeleccionNodos() {
        this.nodoActual = -1;
        for (int i = 0; i < this.totalNodos; i++) {
            boolean b = this.nodos[i].contains(this.xi, this.yi);
            if (b) {
                this.nodoActual = i;
                this.nodos[i].setSelected(true);
                this.operacion = 4;
                Point p = this.nodos[i].getPoint();
                this.prevx = p.x;
                this.prevy = p.y;
                seleccionaConectores();
                break;
            }
        }
    }
    
    private void comprobarSeleccionConectores() {
        boolean searching = true;
        for (int i = 0; i < this.lazos.size(); i++) {
            Conector c = this.lazos.get(i);
            boolean b = c.intersects(new Rectangle2D.Double((this.xi - 6), (this.yi - 6), 12.0D, 12.0D));
            if (b && searching) {
                c.setSelected(true);
                searching = false;
            } else {
                c.setSelected(false);
            }
        }
    }
    
    public Relacion getRelacion() {
        ArrayList<String> ns = new ArrayList<>();
        int i;
        for (i = 0; this.nodos[i] != null && i < 9; i++) {
            ns.add(String.valueOf(this.nodos[i].getId()));
        }
        for (; i < 9; i++) {
            ns.add(String.valueOf(this.nombres[i]));
        }
        boolean[][] tabla = new boolean[this.totalNodos][this.totalNodos];
        for (int j = 0; j < this.totalNodos; j++) {
            for (int k = 0; k < this.totalNodos; k++) {
                tabla[j][k] = this.matriz[k][j];
            }
        }
        Relacion rel = new Relacion(tabla, ns, this.totalNodos);
        this.relacion = rel;
        return rel;
    }
    
    private void dibujarConexiones() {
        for (int i = 0; i < this.lazos.size(); i++) {
            Conector c = this.lazos.get(i);
            c.dibujarConectores(this.offscreen);
        }
    }
    
    public void dibujarGrafo() {
        if (this.OSI != null) {
            this.offscreen = (Graphics2D)this.OSI.getGraphics();
            this.offscreen.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            dibujarNodos();
            dibujarConexiones();
        }
        repaint();
    }
    
    private void dibujarNodos() {
        for (int i = 0; i < this.nodos.length; i++) {
            if (this.nodos[i] == null)
                return;
            if (i == this.nodoActual) {
                this.nodos[i].dibujarSelectedNodo(this.offscreen);
            } else {
                this.nodos[i].dibujarUnselectedNodo(this.offscreen);
            }
        }
    }
    
    private int esPosicionValida() {
        for (int i = 0; i < this.totalNodos; i++) {
            boolean b = this.nodos[i].contains(this.xf, this.yf);
            if (b) {
                return i;
            }
        }
        return -1;
    }
    
    private void mensajeError(String titulo, String error) {
        JOptionPane.showMessageDialog(this, error, titulo, 0);
    }
    
    private void nuevoNodo() {
        this.nuevo = true;
        if (this.totalNodos < 9) {
            this.operacion = 4;
            this.nodoActual = this.totalNodos;
            this.totalNodos++;
            GNodo nodo = new GNodo(this.xi, this.yi, this.nombres[this.key], true);
            this.nodos[this.nodoActual] = nodo;
            recalcularKey();
            dibujarGrafo();
        } else {
            mensajeError("Desbordamiento", "Maximo 9 nodos permitidos");
            this.operacion = 3;
        }
    }
    
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D gr2D = (Graphics2D)g;
        checkOSI();
        
        if (!this.enabled) {
            if (this.OSI != null) {
                this.offscreen = (Graphics2D)this.OSI.getGraphics();
                this.offscreen.setColor(Color.LIGHT_GRAY);
                this.offscreen.fillRect(0, 0, 550, 300);
                dibujarGrafo();
            }
            gr2D.drawImage(this.OSI, 0, 0, this);
            return;
        }
        gr2D.drawImage(this.OSI, 0, 0, this);
        
        if (this.operacion == 7) {
            Stroke pincel = new BasicStroke(2.0F, 1, 0);
            gr2D.setStroke(pincel);
            gr2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            gr2D.drawLine(this.xi, this.yi, this.xf, this.yf);
        }
        
        gr2D.setFont(new Font("Arial", 1, 12));
        gr2D.drawString("Total de nodos = " + this.totalNodos, 0, 300);
        String sactual = (this.nodoActual > -1 && this.nodos[this.nodoActual] != null) ? 
            (this.nodos[this.nodoActual].getId() + "") : "Ninguno";
        gr2D.drawString("Nodo seleccionado = " + sactual, 120, 300);
    }
    
    private void recalcularKey() {
        this.imgnombres[this.key] = true;
        for (int i = 0; i < this.nombres.length; i++) {
            if (!this.imgnombres[i]) {
                this.key = (short)i;
                break;
            }
        }
    }
    
    private void recuperarKey(char c) {
        for (short i = 0; i < this.imgnombres.length; i = (short)(i + 1)) {
            this.imgnombres[i] = false;
        }
        for (short i = 0; i < this.totalNodos; i = (short)(i + 1)) {
            this.imgnombres[i] = true;
        }
        this.key = (short)(this.totalNodos - 1);
    }
    
    private void renombrarNodos() {
        for (int i = this.nodoActual; i < this.nodos.length; i++) {
            if (this.nodos[i] == null)
                break;
            this.nodos[i].setId((char)(this.nodos[i].getId() - 1));
        }
        if (this.nodos[0] != null) {
            char c = this.nodos[0].getId();
            c = (char)(c + this.nodoActual);
            for (int j = 0; j < this.lazos.size(); j++) {
                Conector con = this.lazos.get(j);
                if (con.getDestino() > c) {
                    con.setDestino((char)(con.getDestino() - 1));
                }
                if (con.getOrigen() > c) {
                    con.setOrigen((char)(con.getOrigen() - 1));
                }
            }
        }
    }
    
    public void setEnabled(boolean b) {
        if (b) {
            setBackground(Color.WHITE);
            clearOffscreen();
            this.operacion = 3;
            this.enabled = b;
            dibujarGrafo();
            repaint();
        } else {
            setBackground(Color.LIGHT_GRAY);
            this.operacion = 0;
            this.enabled = b;
            this.nodoActual = -1;
            
            if (this.nodos[0] != null && this.relacion != null) {
                for (int i = 0; i < this.relacion.size(); i++) {
                    if (i < this.nodos.length && this.nodos[i] != null) {
                        this.nodos[i].setSelected(false);
                    }
                }
                borraSeleccionConectores();
                dibujarGrafo();
            }
        }
    }
    
    private boolean traslape() {
        int repeticiones = this.totalNodos;
        if (repeticiones == 0) {
            return false;
        }
        for (int i = 0; i < repeticiones; i++) {
            if (i != this.nodoActual) {
                Point p = this.nodos[i].getPoint();
                int xtest = Math.abs(p.x - this.xf);
                int ytest = Math.abs(p.y - this.yf);
                if (xtest < 45 && ytest < 45)
                    return true;
            }
        }
        return false;
    }
    
    private void seleccionaConectores() {
        if (this.nodoActual == -1) return;
        char n = this.nodos[this.nodoActual].getId();
        for (int i = 0; i < this.lazos.size(); i++) {
            Conector c = this.lazos.get(i);
            if (c.getDestino() == n || c.getOrigen() == n) {
                c.setSelected(true);
            }
        }
    }
    
    public void setRelacion(Relacion r) {
        ArrayList<?> pnodos = r.getNodos();
        boolean[][] pmatriz = r.getMatriz();
        
        ArrayList<String> cnodos = new ArrayList<>();
        for (int i = 0; i < pnodos.size(); i++) {
            String s = pnodos.get(i).toString();
            cnodos.add(s);
        }
        
        boolean[][] cmatriz = new boolean[9][9];
        
        for (int j = 0; j < pmatriz.length; j++) {
            for (int m = 0; m < pmatriz.length; m++) {
                cmatriz[j][m] = pmatriz[j][m];
            }
        }
        
        this.relacion = new Relacion(cmatriz, cnodos, r.size());
        this.nodos = this.relacion.getGNodos();
        
        ArrayList<?> nombresn = this.relacion.getNodos();
        for (int k = 0; k < this.relacion.size(); k++) {
            String s = nombresn.get(k).toString();
            this.nombres[k] = s.charAt(0);
        }
        if (this.nodos[0] == null || this.matriz.length != (this.relacion.getMatriz()).length) {
            boolean[][] matriztmp = this.relacion.getMatriz();
            for (int m = 0; m < matriztmp.length; m++) {
                for (int n = 0; n < matriztmp.length; n++) {
                    this.matriz[m][n] = matriztmp[m][n];
                }
            }
            this.lazos = new ArrayList<>();
            automatizarDibujado();
        } else {
            clearOffscreen();
            boolean[][] matriztmp = this.relacion.getMatriz();
            for (int m = 0; m < this.relacion.size(); m++) {
                for (int n = 0; n < this.relacion.size(); n++) {
                    this.matriz[m][n] = matriztmp[m][n];
                }
            }
            this.lazos = new ArrayList<>();
            crearConectores();
        }
    }
    
    private void dibujarNueve() {
        int[] xcoords = { 120, 210, 300, 390, 80, 430, 160, 250, 340 };
        int[] ycoords = { 50, 50, 50, 50, 130, 130, 220, 220, 220 };
        
        for (int i = 0; i < 9; i++) {
            this.nodos[i] = new GNodo(xcoords[i], ycoords[i], this.nombres[this.key], false);
            recalcularKey();
        }
    }
    
    private void dibujarOcho() {
        int[] xcoords = { 120, 210, 300, 390, 120, 210, 300, 390 };
        int[] ycoords = { 65, 65, 65, 65, 200, 200, 200, 200 };
        
        for (int i = 0; i < 8; i++) {
            this.nodos[i] = new GNodo(xcoords[i], ycoords[i], this.nombres[this.key], false);
            recalcularKey();
        }
    }
    
    private void dibujarSiete() {
        int[] xcoords = { 120, 210, 300, 390, 160, 250, 340 };
        int[] ycoords = { 65, 65, 65, 65, 200, 200, 200 };
        
        for (int i = 0; i < 7; i++) {
            this.nodos[i] = new GNodo(xcoords[i], ycoords[i], this.nombres[this.key], false);
            recalcularKey();
        }
    }
    
    private void dibujarSeis() {
        int[] xcoords = { 260, 165, 345, 165, 345, 260 };
        int[] ycoords = { 30, 90, 90, 180, 180, 250 };
        
        for (int i = 0; i < 6; i++) {
            this.nodos[i] = new GNodo(xcoords[i], ycoords[i], this.nombres[this.key], false);
            recalcularKey();
        }
    }
    
    private void dibujarCinco() {
        int[] xcoords = { 260, 165, 365, 165, 365 };
        int[] ycoords = { 30, 90, 90, 210, 210 };
        
        for (int i = 0; i < 5; i++) {
            this.nodos[i] = new GNodo(xcoords[i], ycoords[i], this.nombres[this.key], false);
            recalcularKey();
        }
    }
    
    private void dibujarCuatro() {
        this.nodos[0] = new GNodo(175, 60, this.nombres[this.key], false);
        recalcularKey();
        this.nodos[1] = new GNodo(355, 60, this.nombres[this.key], false);
        recalcularKey();
        this.nodos[2] = new GNodo(175, 190, this.nombres[this.key], false);
        recalcularKey();
        this.nodos[3] = new GNodo(355, 190, this.nombres[this.key], false);
        recalcularKey();
    }
    
    private void dibujarTres() {
        this.nodos[0] = new GNodo(195, 50, this.nombres[this.key], false);
        recalcularKey();
        this.nodos[1] = new GNodo(355, 50, this.nombres[this.key], false);
        recalcularKey();
        this.nodos[2] = new GNodo(270, 220, this.nombres[this.key], false);
        recalcularKey();
    }
    
    private void dibujarDos() {
        this.nodos[0] = new GNodo(175, 130, this.nombres[this.key], false);
        recalcularKey();
        this.nodos[1] = new GNodo(355, 130, this.nombres[this.key], false);
        recalcularKey();
    }
    
    private void dibujarUno() {
        this.nodos[0] = new GNodo(260, 130, this.nombres[this.key], false);
        recalcularKey();
    }
    
    private Point resolverEcuacion(int x1, int y1, int x2, int y2, boolean bxf) {
        Point p = new Point();
        double m = 0.0D, b = 0.0D, sx1 = 0.0D, sy = 0.0D;
        double r = 441.0D;
        
        m = (double)(y2 - y1) / (double)(x2 - x1);
        b = m * -x1 + y1;
        double bx = x2 * 2.0D;
        double cx = x2 * x2;
        double ax = m * m;
        bx -= 2.0D * m * (b - y2);
        ax = -1.0D - m * m;
        cx = r - cx - (b - y2) * (b - y2);
        sx1 = -bx;
        bx = bx * bx - 4.0D * ax * cx;
        bx = Math.pow(bx, 0.5D);
        
        if (bxf) {
            if (x2 > x1) {
                sx1 = (sx1 + bx) / (2.0D * ax);
            } else {
                sx1 = (sx1 - bx) / (2.0D * ax);
            }
        } else if (x1 < x2) {
            sx1 = (sx1 + bx) / (2.0D * ax);
        } else {
            sx1 = (sx1 - bx) / (2.0D * ax);
        }
        
        sy = m * sx1 + b;
        
        p.x = (int)sx1;
        p.y = (int)sy;
        
        return p;
    }
}