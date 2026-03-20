package SIMAR;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

public class VMatrizAdyacencia extends JPanel implements ActionListener {
    private JButton[][] jb_celdas;
    private JButton[][] jb_nodos;
    private JCheckBox[] jchb_switches;
    private ArrayList<String> nombres;
    private int length = 9;
    private SelectorEventosHandler sehandler;
    
    public VMatrizAdyacencia() {
        setLayout(new GridBagLayout());
        setPreferredSize(new Dimension(450, 330));
        sehandler = new SelectorEventosHandler();
        crearCeldas();
        
        JLabel et_tabla = new JLabel("Matriz de Adyacencia");
        et_tabla.setFont(new Font("Arial", Font.BOLD, 12));
        et_tabla.setForeground(Color.BLACK);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 10;
        gbc.insets = new Insets(0, 150, 10, 0);
        add(et_tabla, gbc);
    }
    
    private void crearCeldas() {
        jb_celdas = new JButton[9][9];
        jb_nodos = new JButton[9][2];
        jchb_switches = new JCheckBox[8];
        
        Font font = new Font("Arial", Font.BOLD, 12);
        
        // Crear celdas de la matriz
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                jb_celdas[i][j] = new JButton("0");
                jb_celdas[i][j].setPreferredSize(new Dimension(35, 35));
                jb_celdas[i][j].setMargin(new Insets(0, 0, 0, 0));
                jb_celdas[i][j].setBackground(Color.WHITE);
                jb_celdas[i][j].setForeground(Color.BLACK);
                jb_celdas[i][j].setFont(new Font("Arial", Font.BOLD, 12));
                jb_celdas[i][j].addActionListener(this);
            }
        }
        
        // Crear etiquetas de nodos
        for (int i = 0; i < 9; i++) {
            char c = (char)('a' + i);
            
            jb_nodos[i][0] = new JButton(String.valueOf(c));
            jb_nodos[i][0].setPreferredSize(new Dimension(35, 35));
            jb_nodos[i][0].setBackground(new Color(230, 230, 250));
            jb_nodos[i][0].setForeground(Color.BLACK);
            jb_nodos[i][0].setFont(font);
            jb_nodos[i][0].setFocusable(false);
            
            jb_nodos[i][1] = new JButton(String.valueOf(c));
            jb_nodos[i][1].setPreferredSize(new Dimension(35, 35));
            jb_nodos[i][1].setBackground(new Color(230, 230, 250));
            jb_nodos[i][1].setForeground(Color.BLACK);
            jb_nodos[i][1].setFont(font);
            jb_nodos[i][1].setFocusable(false);
            
            if (i < 8) {
                jchb_switches[i] = new JCheckBox();
                jchb_switches[i].setSelected(true);
                jchb_switches[i].setBackground(Color.LIGHT_GRAY);
                jchb_switches[i].addItemListener(sehandler);
            }
        }
        
        // Colocar componentes usando GridBagLayout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 2, 2, 2);
        
        // Colocar etiquetas superiores
        for (int i = 0; i < 9; i++) {
            gbc.gridx = i + 2;
            gbc.gridy = 1;
            add(jb_nodos[i][0], gbc);
        }
        
        // Colocar etiquetas laterales y celdas
        for (int j = 0; j < 9; j++) {
            gbc.gridx = 0;
            gbc.gridy = j + 2;
            add(jb_nodos[j][1], gbc);
            
            for (int i = 0; i < 9; i++) {
                gbc.gridx = i + 2;
                gbc.gridy = j + 2;
                add(jb_celdas[i][j], gbc);
            }
        }
        
        // Colocar checkboxes
        for (int i = 0; i < 8; i++) {
            gbc.gridx = 1;
            gbc.gridy = i + 3;
            add(jchb_switches[i], gbc);
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        JButton b = (JButton)e.getSource();
        if (b.getText().equals("0")) {
            b.setFont(new Font("Arial", Font.BOLD, 12));
            b.setText("1");
        } else {
            b.setFont(new Font("Arial", Font.PLAIN, 12));
            b.setText("0");
        }
    }
    
    public void setRelacion(Relacion rel) {
        this.nombres = rel.getNodos();
        boolean[][] matriz = rel.getMatriz();
        this.length = matriz.length;
        
        // Limpiar y configurar
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                jb_celdas[i][j].setBackground(Color.WHITE);
                jb_celdas[i][j].setForeground(Color.BLACK);
                jb_celdas[i][j].setEnabled(false);
                if (i < length && j < length) {
                    jb_celdas[i][j].setText(matriz[i][j] ? "1" : "0");
                } else {
                    jb_celdas[i][j].setText("0");
                    jb_celdas[i][j].setBackground(Color.BLACK);
                    jb_celdas[i][j].setForeground(Color.WHITE);
                }
            }
        }
        
        for (int i = 0; i < nombres.size(); i++) {
            String nombre = nombres.get(i);
            jb_nodos[i][0].setText(nombre);
            jb_nodos[i][0].setForeground(Color.BLACK);
            jb_nodos[i][1].setText(nombre);
            jb_nodos[i][1].setForeground(Color.BLACK);
        }
        
        // Configurar checkboxes
        for (int i = 0; i < 8; i++) {
            jchb_switches[i].setEnabled(false);
            jchb_switches[i].setBackground(Color.GRAY);
        }
        
        if (length < 9) {
            jchb_switches[length - 1].setEnabled(true);
            jchb_switches[length - 1].setBackground(Color.LIGHT_GRAY);
            jchb_switches[length - 1].setSelected(false);
        }
    }
    
    public boolean comprobarIntegridad() {
        return true;
    }
    
    public boolean[][] getMatriz() {
        boolean[][] matriz = new boolean[length][length];
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < length; j++) {
                matriz[i][j] = jb_celdas[i][j].getText().equals("1");
            }
        }
        return matriz;
    }
    
    public ArrayList<String> getNodos() {
        ArrayList<String> nodos = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            nodos.add(jb_nodos[i][0].getText());
        }
        return nodos;
    }
    
    public int getSizeRelacion() {
        return length;
    }
    
    public void setEnable(boolean b) {
        if (b) {
            for (int i = 0; i < length; i++) {
                for (int j = 0; j < length; j++) {
                    jb_celdas[i][j].setEnabled(true);
                    jb_celdas[i][j].setForeground(Color.BLACK);
                }
            }
            for (int i = 0; i < 8; i++) {
                jchb_switches[i].setEnabled(true);
            }
        } else {
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    jb_celdas[i][j].setEnabled(false);
                    if (i < length && j < length) {
                        jb_celdas[i][j].setForeground(Color.BLACK);
                    }
                }
            }
            for (int i = 0; i < 8; i++) {
                jchb_switches[i].setEnabled(false);
            }
        }
    }
    
    class SelectorEventosHandler implements ItemListener {
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.DESELECTED) {
                for (int i = 0; i < 8; i++) {
                    if (jchb_switches[i].equals(e.getSource())) {
                        length = i + 1;
                    } else {
                        jchb_switches[i].setEnabled(false);
                    }
                }
                
                for (int i = 0; i < 9; i++) {
                    for (int j = 0; j < 9; j++) {
                        if (i >= length || j >= length) {
                            jb_celdas[i][j].setBackground(Color.BLACK);
                            jb_celdas[i][j].setForeground(Color.WHITE);
                            jb_celdas[i][j].setEnabled(false);
                        } else {
                            jb_celdas[i][j].setBackground(Color.WHITE);
                            jb_celdas[i][j].setForeground(Color.BLACK);
                        }
                    }
                }
            } else {
                length = 9;
                for (int i = 0; i < 8; i++) {
                    jchb_switches[i].setEnabled(true);
                }
                for (int i = 0; i < 9; i++) {
                    for (int j = 0; j < 9; j++) {
                        jb_celdas[i][j].setBackground(Color.WHITE);
                        jb_celdas[i][j].setForeground(Color.BLACK);
                        jb_celdas[i][j].setEnabled(true);
                    }
                }
            }
        }
    }
}