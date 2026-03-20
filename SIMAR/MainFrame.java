package SIMAR;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class MainFrame extends JFrame {
    private GraphScreen screen;
    private VMatrizAdyacencia tabla;
    private JComboBox<String> jcbRelaciones;
    private JComboBox<String> jcbModoOperacion;
    private ArrayList<Relacion> relaciones;
    private int relacionActual = 0;
    private JTextArea jta_Pares;
    private JTextArea jta_editor;
    private JButton bnodo;
    private JButton benlace;
    private JButton bseleccion;
    private JButton bborrar;
    private Lexer lexer;
    private JButton[] brelaciones;
    private JButton jbNuevaRelacion;
    private JButton jbGenerar;
    private JButton jbBorrar;
    private JComboBox<String> jcbPropiedades;
    private JButton jbTransformacion;
    private JButton jbdominio;
    private JButton jbcodominio;
    private JCheckBox[] jcbpropiedades;
    private JButton[] jlpropiedades;
    private ArrayList<String> idrelaciones;
    private char[] NOMBRES_RELACIONES = { 'R', 'S', 'P', 'Q' };
    private boolean[] img_nombres_relaciones;
    private final String[] stetiquetas = {
        "Reflexiva", "Irreflexiva", "Simetrica", "Asimetrica", 
        "Antisimetrica", "Transitiva", "Equivalencia", "Particion",
        "Funcion", "Funcion Suprayectiva", "Funcion Inyectiva", 
        "Funcion Biyectiva", "Funcion Invertible"
    };
    
    private final String[] PROPIEDADES = {
        "Es cuando todo elemento de un conjunto A está relacionado consigo mismo",
        "Es cuando ningún elemento del conjunto A está relacionado consigo mismo",
        "Se dice que una relación R: A→B es simétrica cuando (a,b) Є R y (b,a) Є R",
        "Una relación R de A en B es asimétrica si cuando (a,b) Є R entonces (b,a) ∉ R",
        "Es cuando uno de los pares colocados simétricamente no está en la relación",
        "Una relación de A en B tiene la propiedad de ser transitiva si cuando aRb y bRc entonces existe un par aRc",
        "Es aquella que tiene las tres propiedades: reflexiva, simétrica y transitiva",
        "Es un conjunto de clases de equivalencia",
        "Para que una relación sea función, el dominio debe ser igual a X",
        "Una función f: A→B se llama suprayectiva",
        "Una función f: A→B se llama inyectiva",
        "Cuando una función f es inyectiva y suprayectiva",
        "Una función f: A→B es invertible si su inversa también es función"
    };
    
    public MainFrame() {
        setTitle("SIMAR");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(960, 700);
        setLocationRelativeTo(null);
        setLayout(null);
        setResizable(false);
        
        // Inicializar datos
        relaciones = new ArrayList<>();
        idrelaciones = new ArrayList<>();
        img_nombres_relaciones = new boolean[4];
        
        ArrayList<String> nodos = new ArrayList<>();
        for (char c = 'a'; c <= 'i'; c++) {
            nodos.add(String.valueOf(c));
        }
        boolean[][] r = new boolean[9][9];
        Relacion relacion = new Relacion(r, nodos, nodos.size());
        relaciones.add(relacion);
        idrelaciones.add("R");
        img_nombres_relaciones[0] = true;
        
        // Inicializar lexer
        lexer = new Lexer();
        configurarLexer();
        
        // Crear componentes
        crearComponentes();
        
        // Configurar relación inicial
        jta_Pares.setText(relacion.toString());
        
        setVisible(true);
    }
    
    private void configurarLexer() {
        lexer.agregarExprReg("^\\s+", "espacio");
        lexer.agregarExprReg("^[a-zA-Z0-9]", "nodo");
        lexer.agregarExprReg("^[,]", "coma");
        lexer.agregarExprReg("^[{]", "op_llaveAbre");
        lexer.agregarExprReg("^[}]", "op_llaveCierra");
        lexer.agregarExprReg("^[(]", "op_parentAbre");
        lexer.agregarExprReg("^[)]", "op_parentCierra");
    }
    
    private void crearComponentes() {
        // GraphScreen
        screen = new GraphScreen();
        screen.setBounds(20, 125, 550, 300);
        screen.setEnabled(false);
        add(screen);
        
        // Matriz de adyacencia
        tabla = new VMatrizAdyacencia();
        tabla.setBounds(580, 100, 370, 320);
        tabla.setRelacion(relaciones.get(0));
        add(tabla);
        
        // Menú superior
        crearMenuBar();
        
        // Barra de herramientas de grafos
        crearMenuGrafos();
        
        // Barra de relaciones
        crearMenuRelaciones();
        
        // Barra de propiedades
        crearBarraHerramientasPropiedades();
        
        // Hoja de propiedades
        crearHojaPropiedades();
        
        // Área de pares ordenados
        crearAreaPares();
        
        // Editor y calculadora
        crearEditor();
        crearCalculadora();
    }
    
    private void crearMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBounds(0, 0, 960, 25);
        
        JMenu archivo = new JMenu("Archivo");
        JMenuItem nuevo = new JMenuItem("Nuevo");
        JMenuItem borrar = new JMenuItem("Borrar");
        JMenuItem generar = new JMenuItem("Generar");
        
        nuevo.addActionListener(e -> nuevaRelacion());
        borrar.addActionListener(e -> borrarRelacion());
        generar.addActionListener(e -> generarRelacion());
        
        archivo.add(nuevo);
        archivo.add(borrar);
        archivo.add(generar);
        
        JMenu ver = new JMenu("Ver");
        JMenuItem grafo = new JMenuItem("Grafo");
        JMenuItem matriz = new JMenuItem("Matriz");
        JMenuItem conjunto = new JMenuItem("Conjunto");
        
        grafo.addActionListener(e -> jcbModoOperacion.setSelectedIndex(1));
        matriz.addActionListener(e -> jcbModoOperacion.setSelectedIndex(0));
        conjunto.addActionListener(e -> jcbModoOperacion.setSelectedIndex(2));
        
        ver.add(grafo);
        ver.add(matriz);
        ver.add(conjunto);
        
        JMenu ayuda = new JMenu("Ayuda");
        JMenuItem acerca = new JMenuItem("Acerca de");
        acerca.addActionListener(e -> new Acercade(this));
        ayuda.add(acerca);
        
        menuBar.add(archivo);
        menuBar.add(ver);
        menuBar.add(ayuda);
        
        add(menuBar);
    }
    
    private void crearMenuGrafos() {
        JPanel panel = new JPanel();
        panel.setBounds(20, 95, 220, 40);
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        
        bnodo = new JButton("Nodo");
        benlace = new JButton("Enlace");
        bseleccion = new JButton("Seleccionar");
        bborrar = new JButton("Borrar");
        
        bnodo.setEnabled(false);
        benlace.setEnabled(false);
        bseleccion.setEnabled(false);
        bborrar.setEnabled(false);
        
        bnodo.addActionListener(e -> screen.cambiaOperacion('\001'));
        benlace.addActionListener(e -> screen.cambiaOperacion('\002'));
        bseleccion.addActionListener(e -> screen.cambiaOperacion('\003'));
        bborrar.addActionListener(e -> screen.cambiaOperacion('\005'));
        
        panel.add(bnodo);
        panel.add(benlace);
        panel.add(bseleccion);
        panel.add(bborrar);
        
        add(panel);
    }
    
    private void crearMenuRelaciones() {
        JPanel panel = new JPanel();
        panel.setBounds(20, 30, 650, 40);
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        
        jbNuevaRelacion = new JButton("Nueva Relacion");
        jbGenerar = new JButton("Generar");
        jbBorrar = new JButton("Borrar");
        
        jcbRelaciones = new JComboBox<>();
        jcbRelaciones.setPreferredSize(new Dimension(120, 25));
        jcbRelaciones.addItem("Relacion R");
        
        jcbModoOperacion = new JComboBox<>();
        jcbModoOperacion.setPreferredSize(new Dimension(120, 25));
        jcbModoOperacion.addItem("Matriz");
        jcbModoOperacion.addItem("Grafo");
        jcbModoOperacion.addItem("Parejas");
        
        jbNuevaRelacion.addActionListener(e -> nuevaRelacion());
        jbGenerar.addActionListener(e -> generarRelacion());
        jbBorrar.addActionListener(e -> borrarRelacion());
        jcbRelaciones.addActionListener(e -> cambiarRelacion());
        jcbModoOperacion.addActionListener(e -> cambiarVista());
        
        panel.add(jbNuevaRelacion);
        panel.add(jbGenerar);
        panel.add(jcbRelaciones);
        panel.add(jcbModoOperacion);
        panel.add(jbBorrar);
        
        add(panel);
    }
    
    private void crearBarraHerramientasPropiedades() {
        JPanel panel = new JPanel();
        panel.setBounds(20, 430, 350, 40);
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        
        jcbPropiedades = new JComboBox<>();
        jcbPropiedades.addItem("Cerradura Transitiva");
        jcbPropiedades.addItem("Cerradura Reflexiva");
        jcbPropiedades.addItem("Cerradura Simetrica");
        jcbPropiedades.addItem("Diagrama de Hasse");
        jcbPropiedades.addItem("Clases de equivalencia");
        jcbPropiedades.addItem("Particiones");
        
        jbTransformacion = new JButton("Aplicar");
        jbdominio = new JButton("Dominio");
        jbcodominio = new JButton("Codominio");
        
        jbTransformacion.addActionListener(e -> aplicarTransformacion());
        jbdominio.addActionListener(e -> {
            Relacion r = relaciones.get(relacionActual);
            r.aplicarDominio(this);
        });
        jbcodominio.addActionListener(e -> {
            Relacion r = relaciones.get(relacionActual);
            r.aplicarCodominio(this);
        });
        
        panel.add(jcbPropiedades);
        panel.add(jbTransformacion);
        panel.add(jbdominio);
        panel.add(jbcodominio);
        
        add(panel);
    }
    
    private void crearHojaPropiedades() {
        JPanel panel = new JPanel();
        panel.setBounds(20, 470, 350, 180);
        panel.setLayout(new GridLayout(13, 2, 5, 2));
        
        jcbpropiedades = new JCheckBox[13];
        jlpropiedades = new JButton[13];
        
        for (int i = 0; i < 13; i++) {
            jcbpropiedades[i] = new JCheckBox();
            jcbpropiedades[i].setEnabled(false);
            
            jlpropiedades[i] = new JButton(stetiquetas[i]);
            jlpropiedades[i].setFont(new Font("Arial", Font.PLAIN, 11));
            
            final int index = i;
            jlpropiedades[i].addActionListener(e -> {
                JOptionPane.showMessageDialog(this, PROPIEDADES[index], stetiquetas[index], 1);
            });
            
            panel.add(jcbpropiedades[i]);
            panel.add(jlpropiedades[i]);
        }
        
        JScrollPane scroll = new JScrollPane(panel);
        scroll.setBounds(20, 470, 350, 180);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(scroll);
        
        setProperties();
    }
    
    private void crearAreaPares() {
        JLabel label = new JLabel("Pares ordenados");
        label.setFont(new Font("Arial", Font.BOLD, 12));
        label.setBounds(400, 430, 150, 20);
        add(label);
        
        jta_Pares = new JTextArea();
        jta_Pares.setEditable(false);
        jta_Pares.setBackground(Color.LIGHT_GRAY);
        jta_Pares.setFont(new Font("Arial", Font.PLAIN, 12));
        
        JScrollPane scroll = new JScrollPane(jta_Pares);
        scroll.setBounds(400, 450, 500, 60);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(scroll);
    }
    
    private void crearEditor() {
        jta_editor = new JTextArea();
        jta_editor.setFont(new Font("SansSerif", Font.BOLD, 16));
        
        JScrollPane scroll = new JScrollPane(jta_editor);
        scroll.setBounds(400, 520, 500, 40);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(scroll);
    }
    
    private void crearCalculadora() {
        JPanel panel = new JPanel();
        panel.setBounds(400, 560, 500, 80);
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        
        brelaciones = new JButton[4];
        
        JButton union = new JButton("∪");
        JButton interseccion = new JButton("∩");
        JButton inversa = new JButton("ˉ¹");
        JButton complemento = new JButton("'");
        JButton composicion = new JButton("°");
        JButton parentA = new JButton("(");
        JButton parentC = new JButton(")");
        JButton calcular = new JButton("Calcular");
        
        for (int i = 0; i < 4; i++) {
            brelaciones[i] = new JButton(String.valueOf(NOMBRES_RELACIONES[i]));
            final int index = i;
            brelaciones[i].addActionListener(e -> 
                jta_editor.append(brelaciones[index].getText()));
            if (i > 0) brelaciones[i].setEnabled(false);
            panel.add(brelaciones[i]);
        }
        
        union.addActionListener(e -> jta_editor.append("∪"));
        interseccion.addActionListener(e -> jta_editor.append("∩"));
        inversa.addActionListener(e -> jta_editor.append("ˉ¹"));
        complemento.addActionListener(e -> jta_editor.append("'"));
        composicion.addActionListener(e -> jta_editor.append("°"));
        parentA.addActionListener(e -> jta_editor.append("("));
        parentC.addActionListener(e -> jta_editor.append(")"));
        calcular.addActionListener(e -> calcularOperacion());
        
        panel.add(union);
        panel.add(interseccion);
        panel.add(inversa);
        panel.add(complemento);
        panel.add(composicion);
        panel.add(parentA);
        panel.add(parentC);
        panel.add(calcular);
        
        add(panel);
    }
    
    private void nuevaRelacion() {
        int cantidad = jcbRelaciones.getItemCount();
        if (cantidad >= 4) {
            JOptionPane.showMessageDialog(this, "Limite excedido", "No se permiten mas de 4 relaciones", 0);
            return;
        }
        
        relacionActual = cantidad;
        
        for (int i = 0; i < img_nombres_relaciones.length; i++) {
            if (!img_nombres_relaciones[i]) {
                img_nombres_relaciones[i] = true;
                idrelaciones.add(String.valueOf(NOMBRES_RELACIONES[i]));
                break;
            }
        }
        
        ArrayList<String> nodos = new ArrayList<>();
        for (char c = 'a'; c <= 'i'; c++) {
            nodos.add(String.valueOf(c));
        }
        boolean[][] r = new boolean[9][9];
        relaciones.add(new Relacion(r, nodos, nodos.size()));
        
        jcbRelaciones.addItem("Relacion " + idrelaciones.get(relacionActual));
        jcbRelaciones.setSelectedIndex(relacionActual);
        
        resetPantallas();
        jcbModoOperacion.setSelectedIndex(0);
    }
    
    private void borrarRelacion() {
        int indice = jcbRelaciones.getSelectedIndex();
        
        if (jcbRelaciones.getItemCount() == 1) {
            ArrayList<String> nodos = new ArrayList<>();
            for (char c = 'a'; c <= 'i'; c++) {
                nodos.add(String.valueOf(c));
            }
            boolean[][] r = new boolean[9][9];
            Relacion relacion = new Relacion(r, nodos, nodos.size());
            relaciones.set(0, relacion);
            tabla.setRelacion(relacion);
            screen.setRelacion(relacion);
            screen.setEnabled(false);
            jta_Pares.setText(relacion.toString());
            jta_Pares.setEditable(false);
            jta_Pares.setBackground(Color.LIGHT_GRAY);
            return;
        }
        
        String s = jcbRelaciones.getItemAt(indice);
        s = s.substring(9);
        
        jcbRelaciones.removeItemAt(indice);
        relaciones.remove(indice);
        
        for (int i = 0; i < img_nombres_relaciones.length; i++) {
            if (s.equals(String.valueOf(NOMBRES_RELACIONES[i]))) {
                img_nombres_relaciones[i] = false;
                break;
            }
        }
        
        relacionActual = jcbRelaciones.getSelectedIndex();
        resetPantallas();
    }
    
    private void cambiarRelacion() {
        relacionActual = jcbRelaciones.getSelectedIndex();
        Relacion r = relaciones.get(relacionActual);
        tabla.setRelacion(r);
        jta_Pares.setText(r.toString());
        jta_Pares.setEditable(false);
        jta_Pares.setBackground(Color.LIGHT_GRAY);
        screen.setRelacion(r);
        setProperties();
    }
    
    private void cambiarVista() {
        int index = jcbModoOperacion.getSelectedIndex();
        
        if (index == 0) { // Matriz
            jta_Pares.setEditable(false);
            jta_Pares.setBackground(Color.LIGHT_GRAY);
            tabla.setEnable(true);
            screen.setEnabled(false);
            bnodo.setEnabled(false);
            benlace.setEnabled(false);
            bborrar.setEnabled(false);
            bseleccion.setEnabled(false);
        } else if (index == 1) { // Grafo
            jta_Pares.setEditable(false);
            jta_Pares.setBackground(Color.LIGHT_GRAY);
            tabla.setEnable(false);
            screen.setEnabled(true);
            bnodo.setEnabled(true);
            benlace.setEnabled(true);
            bborrar.setEnabled(true);
            bseleccion.setEnabled(true);
        } else { // Parejas
            jta_Pares.setEditable(true);
            jta_Pares.setBackground(Color.WHITE);
            tabla.setEnable(false);
            screen.setEnabled(false);
            bnodo.setEnabled(false);
            benlace.setEnabled(false);
            bborrar.setEnabled(false);
            bseleccion.setEnabled(false);
        }
    }
    
    private void generarRelacion() {
        int entrada = jcbModoOperacion.getSelectedIndex();
        
        if (entrada == 0) { // Matriz
            boolean[][] tmp = tabla.getMatriz();
            ArrayList<String> nombres = tabla.getNodos();
            Relacion relacion = new Relacion(tmp, nombres, tabla.getSizeRelacion());
            jta_Pares.setText(relacion.toString());
            relaciones.set(relacionActual, relacion);
            screen.setRelacion(relacion);
            screen.setEnabled(false);
        } else if (entrada == 2) { // Parejas
            String s = jta_Pares.getText();
            lexer.cargarArchivo(s);
            if (!lexer.analizarArchivo()) {
                JOptionPane.showMessageDialog(this, "Verifique la entrada", "Error", 0);
                return;
            }
            ArrayList<Token> tokens = (ArrayList<Token>)lexer.getList();
            Relacion relacion = new Relacion();
            boolean b = relacion.verificarPares(tokens);
            if (b) {
                relaciones.set(relacionActual, relacion);
                tabla.setRelacion(relacion);
                screen.setRelacion(relacion);
                setProperties();
                jta_Pares.setText(relacion.toString());
            } else {
                JOptionPane.showMessageDialog(this, relacion.getError(), relacion.getTipo(), 0);
            }
        } else { // Grafo
            Relacion relacion = screen.getRelacion();
            relacion.verificarPropiedades();
            jta_Pares.setText(relacion.toString());
            relaciones.set(relacionActual, relacion);
            tabla.setRelacion(relacion);
            screen.cambiaOperacion('\003');
            setProperties();
        }
    }
    
    private void aplicarTransformacion() {
        Relacion relacion = relaciones.get(relacionActual);
        
        switch (jcbPropiedades.getSelectedIndex()) {
            case 0:
                relacion.aplicarCerraduraTransitiva();
                break;
            case 1:
                relacion.aplicarCerraduraReflexiva();
                break;
            case 2:
                relacion.aplicarCerraduraSimetrica();
                break;
            case 3:
                relacion.aplicarHasse(this);
                break;
            case 4:
                relacion.verEquivalencia(this);
                return;
            case 5:
                relacion.verParticion(this);
                return;
        }
        
        tabla.setRelacion(relacion);
        jta_Pares.setText(relacion.toString());
        screen.setRelacion(relacion);
        setProperties();
    }
    
    private void calcularOperacion() {
        ArrayList<String> existentes = new ArrayList<>();
        for (int i = 0; i < jcbRelaciones.getItemCount(); i++) {
            String st = jcbRelaciones.getItemAt(i);
            st = st.substring(9);
            existentes.add(st);
        }
        
        ParserC parser = new ParserC(relaciones, existentes);
        Relacion r = parser.ejecutar(jta_editor.getText());
        
        if (r == null) {
            JOptionPane.showMessageDialog(this, parser.getErrorType(), "Error", 0);
            return;
        }
        
        int n = parser.getRelacion();
        relaciones.set(n, r);
        
        if (n != jcbRelaciones.getSelectedIndex()) {
            jcbRelaciones.setSelectedIndex(n);
        } else {
            tabla.setRelacion(r);
            jta_Pares.setText(r.toString());
            screen.setRelacion(r);
            jcbModoOperacion.setSelectedIndex(0);
            setProperties();
        }
        relacionActual = n;
    }
    
    private void resetPantallas() {
        setProperties();
        
        for (int i = 0; i < img_nombres_relaciones.length; i++) {
            if (i < brelaciones.length && brelaciones[i] != null) {
                brelaciones[i].setEnabled(img_nombres_relaciones[i]);
            }
        }
        
        Relacion r = relaciones.get(relacionActual);
        jta_Pares.setText(r.toString());
    }
    
    private void setProperties() {
        Relacion r = relaciones.get(relacionActual);
        
        jcbpropiedades[0].setSelected(r.esReflexiva());
        jcbpropiedades[1].setSelected(r.esIrreflexiva());
        jcbpropiedades[2].setSelected(r.esSimetrica());
        jcbpropiedades[3].setSelected(r.esAsimetrica());
        jcbpropiedades[4].setSelected(r.esAntisimetrica());
        jcbpropiedades[5].setSelected(r.esTransitiva());
        jcbpropiedades[6].setSelected(r.esDeEquivalencia());
        jcbpropiedades[7].setSelected(r.esDeParticion());
        jcbpropiedades[8].setSelected(r.esFuncion());
        jcbpropiedades[9].setSelected(r.esSuprayectiva());
        jcbpropiedades[10].setSelected(r.esInyectiva());
        jcbpropiedades[11].setSelected(r.esBiyectiva());
        jcbpropiedades[12].setSelected(r.esInvertible());
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        new MainFrame();
    }
}