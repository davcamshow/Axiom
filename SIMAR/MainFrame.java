package SIMAR;

import javax.swing.*;
import javax.swing.border.*;
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
    
    // Colores modernos
    private final Color COLOR_PRIMARIO = new Color(70, 130, 180);
    private final Color COLOR_SECUNDARIO = new Color(100, 149, 237);
    private final Color COLOR_FONDO = new Color(245, 245, 245);
    private final Color COLOR_BOTON_TEXTO = Color.BLACK;
    private final Color COLOR_PANEL = Color.WHITE;
    
    public MainFrame() {
        setTitle("SIMAR - Sistema para el Manejo de Relaciones");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1450, 900);
        setLocationRelativeTo(null);
        setLayout(null);
        setResizable(false);
        
        // Fondo de la ventana
        getContentPane().setBackground(COLOR_FONDO);
        
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
        crearMenuBar();
        crearPanelRelaciones();
        crearPanelHerramientas();
        crearPanelGrafo();
        crearPanelMatriz();
        crearPanelPares();
        crearPanelPropiedades();
        crearPanelCalculadora();
        crearPanelEditor();
    }
    
    private void crearMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBounds(0, 0, 1450, 30);
        menuBar.setBackground(COLOR_PRIMARIO);
        
        JMenu archivo = new JMenu("Archivo");
        archivo.setForeground(Color.WHITE);
        JMenuItem nuevo = new JMenuItem("Nueva Relación");
        JMenuItem borrar = new JMenuItem("Borrar Relación");
        JMenuItem generar = new JMenuItem("Generar Relación");
        JMenuItem salir = new JMenuItem("Salir");
        
        nuevo.addActionListener(e -> nuevaRelacion());
        borrar.addActionListener(e -> borrarRelacion());
        generar.addActionListener(e -> generarRelacion());
        salir.addActionListener(e -> System.exit(0));
        
        archivo.add(nuevo);
        archivo.add(borrar);
        archivo.add(generar);
        archivo.addSeparator();
        archivo.add(salir);
        
        JMenu ver = new JMenu("Ver");
        ver.setForeground(Color.WHITE);
        JMenuItem grafo = new JMenuItem("Vista Grafo");
        JMenuItem matriz = new JMenuItem("Vista Matriz");
        JMenuItem conjunto = new JMenuItem("Vista Conjunto");
        
        grafo.addActionListener(e -> jcbModoOperacion.setSelectedIndex(1));
        matriz.addActionListener(e -> jcbModoOperacion.setSelectedIndex(0));
        conjunto.addActionListener(e -> jcbModoOperacion.setSelectedIndex(2));
        
        ver.add(grafo);
        ver.add(matriz);
        ver.add(conjunto);
        
        JMenu ayuda = new JMenu("Ayuda");
        ayuda.setForeground(Color.WHITE);
        JMenuItem acerca = new JMenuItem("Acerca de SIMAR");
        acerca.addActionListener(e -> new Acercade(this));
        ayuda.add(acerca);
        
        menuBar.add(archivo);
        menuBar.add(ver);
        menuBar.add(ayuda);
        
        add(menuBar);
    }
    
    private void crearPanelRelaciones() {
        JPanel panel = new JPanel();
        panel.setBounds(20, 40, 1410, 70);
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 20));
        panel.setBackground(COLOR_PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)));
        
        JLabel lblRelaciones = new JLabel("GESTIÓN DE RELACIONES");
        lblRelaciones.setFont(new Font("Arial", Font.BOLD, 14));
        lblRelaciones.setForeground(COLOR_PRIMARIO);
        
        jbNuevaRelacion = crearBotonConIcono("Nueva", "➕", Color.BLACK, new Color(255, 255, 200));
        jbGenerar = crearBotonConIcono("Generar", "⟳", Color.BLACK, new Color(220, 255, 220));
        jbBorrar = crearBotonConIcono("Borrar", "✖", Color.BLACK, new Color(255, 220, 220));
        
        jcbRelaciones = new JComboBox<>();
        jcbRelaciones.setPreferredSize(new Dimension(150, 35));
        jcbRelaciones.addItem("Relación R");
        jcbRelaciones.setBackground(Color.WHITE);
        jcbRelaciones.setFont(new Font("Arial", Font.PLAIN, 12));
        
        jcbModoOperacion = new JComboBox<>();
        jcbModoOperacion.setPreferredSize(new Dimension(150, 35));
        jcbModoOperacion.addItem("Matriz");
        jcbModoOperacion.addItem("Grafo");
        jcbModoOperacion.addItem("Pares");
        jcbModoOperacion.setBackground(Color.WHITE);
        jcbModoOperacion.setFont(new Font("Arial", Font.PLAIN, 12));
        
        jbNuevaRelacion.addActionListener(e -> nuevaRelacion());
        jbGenerar.addActionListener(e -> generarRelacion());
        jbBorrar.addActionListener(e -> borrarRelacion());
        jcbRelaciones.addActionListener(e -> cambiarRelacion());
        jcbModoOperacion.addActionListener(e -> cambiarVista());
        
        panel.add(lblRelaciones);
        panel.add(jcbRelaciones);
        panel.add(jcbModoOperacion);
        panel.add(jbNuevaRelacion);
        panel.add(jbGenerar);
        panel.add(jbBorrar);
        
        add(panel);
    }
    
    private void crearPanelHerramientas() {
        JPanel panel = new JPanel();
        panel.setBounds(20, 120, 600, 70);
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 20));
        panel.setBackground(COLOR_PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)));
        
        JLabel lblHerramientas = new JLabel("HERRAMIENTAS DE GRAFO");
        lblHerramientas.setFont(new Font("Arial", Font.BOLD, 12));
        lblHerramientas.setForeground(COLOR_PRIMARIO);
        
        bnodo = crearBotonConIcono("Nodo", "●", Color.BLACK, new Color(255, 255, 200));
        benlace = crearBotonConIcono("Enlace", "→", Color.BLACK, new Color(230, 230, 250));
        bseleccion = crearBotonConIcono("Seleccionar", "🔍", Color.BLACK, new Color(220, 240, 220));
        bborrar = crearBotonConIcono("Borrar", "✕", Color.BLACK, new Color(255, 220, 220));
        
        bnodo.setEnabled(false);
        benlace.setEnabled(false);
        bseleccion.setEnabled(false);
        bborrar.setEnabled(false);
        
        bnodo.addActionListener(e -> screen.cambiaOperacion('\001'));
        benlace.addActionListener(e -> screen.cambiaOperacion('\002'));
        bseleccion.addActionListener(e -> screen.cambiaOperacion('\003'));
        bborrar.addActionListener(e -> screen.cambiaOperacion('\005'));
        
        panel.add(lblHerramientas);
        panel.add(bnodo);
        panel.add(benlace);
        panel.add(bseleccion);
        panel.add(bborrar);
        
        add(panel);
    }
    
    private void crearPanelGrafo() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBounds(20, 200, 620, 320);
        panel.setBackground(COLOR_PANEL);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(COLOR_PRIMARIO, 2),
            " VISUALIZACIÓN DEL GRAFO ",
            TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 12),
            COLOR_PRIMARIO));
        
        screen = new GraphScreen();
        screen.setBackground(Color.WHITE);
        screen.setEnabled(false);
        
        panel.add(screen, BorderLayout.CENTER);
        add(panel);
    }
    
    private void crearPanelMatriz() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBounds(660, 200, 470, 320);
        panel.setBackground(COLOR_PANEL);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(COLOR_PRIMARIO, 2),
            " MATRIZ DE ADYACENCIA ",
            TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 12),
            COLOR_PRIMARIO));
        
        tabla = new VMatrizAdyacencia();
        tabla.setBackground(Color.WHITE);
        tabla.setRelacion(relaciones.get(0));
        
        panel.add(tabla, BorderLayout.CENTER);
        add(panel);
    }
    
    private void crearPanelPares() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBounds(20, 530, 620, 100);
        panel.setBackground(COLOR_PANEL);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(COLOR_PRIMARIO, 2),
            " PARES ORDENADOS ",
            TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 12),
            COLOR_PRIMARIO));
        
        jta_Pares = new JTextArea();
        jta_Pares.setEditable(false);
        jta_Pares.setBackground(new Color(250, 250, 250));
        jta_Pares.setFont(new Font("Monospaced", Font.PLAIN, 13));
        jta_Pares.setMargin(new Insets(10, 10, 10, 10));
        
        JScrollPane scroll = new JScrollPane(jta_Pares);
        scroll.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        panel.add(scroll, BorderLayout.CENTER);
        add(panel);
    }
    
    private void crearPanelPropiedades() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBounds(1150, 200, 250, 420);
        panel.setBackground(COLOR_PANEL);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(COLOR_PRIMARIO, 2),
            " PROPIEDADES ",
            TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 12),
            COLOR_PRIMARIO));
        
        // Panel de propiedades sin scroll
        JPanel panelPropiedades = new JPanel(new GridLayout(13, 1, 5, 5));
        panelPropiedades.setBackground(COLOR_PANEL);
        panelPropiedades.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        jcbpropiedades = new JCheckBox[13];
        jlpropiedades = new JButton[13];
        
        for (int i = 0; i < 13; i++) {
            JPanel fila = new JPanel(new BorderLayout(5, 0));
            fila.setBackground(COLOR_PANEL);
            
            jcbpropiedades[i] = new JCheckBox();
            jcbpropiedades[i].setEnabled(false);
            jcbpropiedades[i].setBackground(COLOR_PANEL);
            
            jlpropiedades[i] = new JButton(stetiquetas[i]);
            jlpropiedades[i].setFont(new Font("Arial", Font.PLAIN, 10));
            jlpropiedades[i].setHorizontalAlignment(SwingConstants.LEFT);
            jlpropiedades[i].setBorderPainted(false);
            jlpropiedades[i].setContentAreaFilled(false);
            jlpropiedades[i].setCursor(new Cursor(Cursor.HAND_CURSOR));
            jlpropiedades[i].setForeground(Color.BLACK);
            
            final int index = i;
            jlpropiedades[i].addActionListener(e -> {
                JOptionPane.showMessageDialog(this, 
                    "<html><body style='width: 300px;'>" + PROPIEDADES[index] + "</body></html>", 
                    stetiquetas[index], 
                    JOptionPane.INFORMATION_MESSAGE);
            });
            
            fila.add(jcbpropiedades[i], BorderLayout.WEST);
            fila.add(jlpropiedades[i], BorderLayout.CENTER);
            
            panelPropiedades.add(fila);
        }
        
        // Panel de transformaciones
        JPanel panelTransformaciones = new JPanel(new GridLayout(4, 1, 5, 5));
        panelTransformaciones.setBackground(COLOR_PANEL);
        panelTransformaciones.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Transformaciones",
            TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.BOLD, 10), COLOR_PRIMARIO));
        
        jcbPropiedades = new JComboBox<>();
        jcbPropiedades.addItem("Cerradura Transitiva");
        jcbPropiedades.addItem("Cerradura Reflexiva");
        jcbPropiedades.addItem("Cerradura Simétrica");
        jcbPropiedades.addItem("Diagrama de Hasse");
        jcbPropiedades.addItem("Clases de equivalencia");
        jcbPropiedades.addItem("Particiones");
        jcbPropiedades.setFont(new Font("Arial", Font.PLAIN, 10));
        
        jbTransformacion = crearBotonSimple("APLICAR", Color.BLACK, new Color(255, 255, 200));
        jbdominio = crearBotonSimple("DOMINIO", Color.BLACK, new Color(230, 230, 250));
        jbcodominio = crearBotonSimple("CODOMINIO", Color.BLACK, new Color(220, 240, 220));
        
        jbTransformacion.addActionListener(e -> aplicarTransformacion());
        jbdominio.addActionListener(e -> {
            Relacion r = relaciones.get(relacionActual);
            r.aplicarDominio(this);
        });
        jbcodominio.addActionListener(e -> {
            Relacion r = relaciones.get(relacionActual);
            r.aplicarCodominio(this);
        });
        
        panelTransformaciones.add(jcbPropiedades);
        panelTransformaciones.add(jbTransformacion);
        panelTransformaciones.add(jbdominio);
        panelTransformaciones.add(jbcodominio);
        
        panel.add(panelPropiedades, BorderLayout.CENTER);
        panel.add(panelTransformaciones, BorderLayout.SOUTH);
        
        add(panel);
        
        setProperties();
    }
    
    private void crearPanelCalculadora() {
        JPanel panel = new JPanel();
        panel.setBounds(20, 640, 1110, 80);
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 20));
        panel.setBackground(COLOR_PANEL);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(COLOR_PRIMARIO, 2),
            " OPERACIONES ENTRE RELACIONES ",
            TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 12),
            COLOR_PRIMARIO));
        
        brelaciones = new JButton[4];
        
        String[] nombresRelaciones = {"R", "S", "P", "Q"};
        Color[] coloresRelaciones = {
            new Color(255, 200, 200),
            new Color(200, 255, 200),
            new Color(200, 200, 255),
            new Color(255, 255, 200)
        };
        
        for (int i = 0; i < 4; i++) {
            brelaciones[i] = crearBotonSimple(nombresRelaciones[i], Color.BLACK, coloresRelaciones[i]);
            brelaciones[i].setPreferredSize(new Dimension(45, 35));
            brelaciones[i].setFont(new Font("Arial", Font.BOLD, 14));
            final int index = i;
            brelaciones[i].addActionListener(e -> 
                jta_editor.append(brelaciones[index].getText()));
            if (i > 0) brelaciones[i].setEnabled(false);
            panel.add(brelaciones[i]);
        }
        
        // Operadores matemáticos estándar
        panel.add(crearBotonOperador("u", "Unión"));
        panel.add(crearBotonOperador("∩", "Intersección"));
        panel.add(crearBotonOperador("⁻¹", "Inversa"));
        panel.add(crearBotonOperador("ᶜ", "Complemento"));
        panel.add(crearBotonOperador("∘", "Composición"));
        panel.add(crearBotonOperador("(", "Abrir paréntesis"));
        panel.add(crearBotonOperador(")", "Cerrar paréntesis"));
        
        JButton calcular = crearBotonSimple("Calcular", Color.BLACK, new Color(144, 238, 144));
        calcular.setPreferredSize(new Dimension(90, 35));
        calcular.setFont(new Font("Arial", Font.BOLD, 12));
        calcular.addActionListener(e -> calcularOperacion());
        panel.add(calcular);
        
        add(panel);
    }
    
    private void crearPanelEditor() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBounds(20, 730, 1110, 70);
        panel.setBackground(COLOR_PANEL);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(COLOR_PRIMARIO, 2),
            " EXPRESIÓN ",
            TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 12),
            COLOR_PRIMARIO));
        
        jta_editor = new JTextArea();
        jta_editor.setFont(new Font("Monospaced", Font.BOLD, 14));
        jta_editor.setMargin(new Insets(10, 10, 10, 10));
        jta_editor.setBackground(new Color(250, 250, 250));
        
        JScrollPane scroll = new JScrollPane(jta_editor);
        scroll.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        panel.add(scroll, BorderLayout.CENTER);
        
        add(panel);
    }
    
    private JButton crearBotonConIcono(String texto, String icono, Color colorTexto, Color colorFondo) {
        JButton boton = new JButton(icono + " " + texto);
        boton.setBackground(colorFondo);
        boton.setForeground(colorTexto);
        boton.setFont(new Font("Arial", Font.BOLD, 11));
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(colorFondo.darker(), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Efecto hover
        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                boton.setBackground(colorFondo.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                boton.setBackground(colorFondo);
            }
        });
        
        return boton;
    }
    
    private JButton crearBotonSimple(String texto, Color colorTexto, Color colorFondo) {
        JButton boton = new JButton(texto);
        boton.setBackground(colorFondo);
        boton.setForeground(colorTexto);
        boton.setFont(new Font("Arial", Font.BOLD, 11));
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(colorFondo.darker(), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Efecto hover
        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                boton.setBackground(colorFondo.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                boton.setBackground(colorFondo);
            }
        });
        
        return boton;
    }
    
    private JButton crearBotonOperador(String simbolo, String tooltip) {
        JButton boton = new JButton(simbolo);
        boton.setFont(new Font("Arial", Font.BOLD, 16));
        boton.setBackground(new Color(255, 255, 255));
        boton.setForeground(Color.BLACK);
        boton.setPreferredSize(new Dimension(45, 35));
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setToolTipText(tooltip);
        
        boton.addActionListener(e -> jta_editor.append(simbolo));
        
        // Efecto hover
        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                boton.setBackground(new Color(240, 240, 240));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                boton.setBackground(Color.WHITE);
            }
        });
        
        return boton;
    }
    
    private void nuevaRelacion() {
        int cantidad = jcbRelaciones.getItemCount();
        if (cantidad >= 4) {
            JOptionPane.showMessageDialog(this, 
                "<html><body style='width: 250px;'>Límite de relaciones alcanzado.<br>No se permiten más de 4 relaciones.</body></html>", 
                "Límite excedido", 
                JOptionPane.WARNING_MESSAGE);
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
        
        jcbRelaciones.addItem("Relación " + idrelaciones.get(relacionActual));
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
            jta_Pares.setBackground(new Color(250, 250, 250));
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
        jta_Pares.setBackground(new Color(250, 250, 250));
        screen.setRelacion(r);
        setProperties();
    }
    
    private void cambiarVista() {
        int index = jcbModoOperacion.getSelectedIndex();
        
        if (index == 0) { // Matriz
            jta_Pares.setEditable(false);
            jta_Pares.setBackground(new Color(250, 250, 250));
            tabla.setEnable(true);
            screen.setEnabled(false);
            bnodo.setEnabled(false);
            benlace.setEnabled(false);
            bborrar.setEnabled(false);
            bseleccion.setEnabled(false);
        } else if (index == 1) { // Grafo
            jta_Pares.setEditable(false);
            jta_Pares.setBackground(new Color(250, 250, 250));
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
                JOptionPane.showMessageDialog(this, 
                    "<html><body style='width: 250px;'>Error de sintaxis en la entrada.<br>Verifique el formato de los pares ordenados.</body></html>", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
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
                JOptionPane.showMessageDialog(this, 
                    "<html><body style='width: 300px;'>" + relacion.getError() + "</body></html>", 
                    relacion.getTipo(), 
                    JOptionPane.ERROR_MESSAGE);
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
            JOptionPane.showMessageDialog(this, 
                "<html><body style='width: 250px;'>" + parser.getErrorType() + "</body></html>", 
                "Error en la operación", 
                JOptionPane.ERROR_MESSAGE);
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
        
        SwingUtilities.invokeLater(() -> {
            new MainFrame();
        });
    }
}