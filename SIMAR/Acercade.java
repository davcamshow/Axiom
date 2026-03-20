package SIMAR;

import java.awt.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Acercade extends JDialog implements ActionListener {
    private JEditorPane jep_info;
    private JScrollPane scroll;
    private JFrame owner;
    private JButton siguiente;
    private JButton anterior;
    private JButton aceptar;
    private int pantalla = 0;
    
    public Acercade(JFrame owner) {
        super(owner, "ACERCA DE SIMAR", true);
        this.owner = owner;
        crearGUI();
    }
    
    public void crearGUI() {
        setLayout(new BorderLayout());
        setSize(450, 460);
        setLocationRelativeTo(owner);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        jep_info = new JEditorPane();
        jep_info.setContentType("text/html");
        jep_info.setEditable(false);
        jep_info.setText(getTextoInicial());
        
        scroll = new JScrollPane(jep_info);
        scroll.setPreferredSize(new Dimension(430, 380));
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        aceptar = new JButton("Aceptar");
        siguiente = new JButton(">>");
        anterior = new JButton("<<");
        anterior.setEnabled(false);
        
        aceptar.addActionListener(this);
        siguiente.addActionListener(this);
        anterior.addActionListener(this);
        
        buttonPanel.add(anterior);
        buttonPanel.add(siguiente);
        buttonPanel.add(aceptar);
        
        mainPanel.add(scroll, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        setVisible(true);
    }
    
    private String getTextoInicial() {
        return "<html><body><center><font face='Comic Sans' color='#0000CD' size='5'>ACERCA DE SIMAR</font></center>"
             + "<p align='justify'>SIMAR (Sistema para el Manejo de Relaciones) Es un sistema pensado para ser utilizado "
             + "como apoyo para los alumnos de la materia Matemáticas para Computadoras impartida en el Instituto "
             + "Tecnológico de Morelia (ITM).</p>"
             + "<p align='justify'>SIMAR es capaz de construir una relación matemática a través de la definición de su "
             + "matriz, de su grafo o de la introducción directa de los pares que la conforman, así como de realizar "
             + "operaciones entre relaciones, obtener las cerraduras y ver las propiedades de las relaciones resultantes "
             + "en todo momento.</p>"
             + "<p align='justify'>SIMAR fue desarrollado por alumnos de Ingeniería en Sistemas Computacionales del ITM "
             + "como proyecto final para la materia de Planificación y Modelado. La información adicional de los "
             + "desarrolladores se encuentra en esta ventana.</p></body></html>";
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        JButton button = (JButton)e.getSource();
        
        if (button == aceptar) {
            dispose();
        } else if (button == siguiente) {
            pantalla++;
            actualizarContenido();
        } else if (button == anterior) {
            pantalla--;
            actualizarContenido();
        }
    }
    
    private void actualizarContenido() {
        anterior.setEnabled(pantalla > 0);
        siguiente.setEnabled(pantalla < 3);
        
        String texto = "";
        
        switch(pantalla) {
            case 0:
                texto = getTextoInicial();
                break;
            case 1:
                texto = getTextoAntonio();
                break;
            case 2:
                texto = getTextoMiriam();
                break;
            case 3:
                texto = getTextoAdrian();
                break;
        }
        
        jep_info.setText(texto);
    }
    
    private String getTextoAntonio() {
        return "<html><body><center><font face='Comic Sans' color='#0000CD' size='5'>Antonio Garcia Gil</font></center>"
             + "<p align='justify'>Nacido en Morelia, Mich. el 9 de Mayo de 1987. Actualmente estudia en el Instituto "
             + "Tecnológico de Morelia en la carrera de Ing. en Sistemas Computacionales.</p>"
             + "<p>Participó en la elaboración del proyecto SIMAR fungiendo como programador.</p>"
             + "<p>Tiene un gran interés por los dispositivos tecnológicos e innovadores, así como también de la "
             + "programación y tecnología en redes.</p>"
             + "<p>Contáctame: byakugan.hyuga@gmail.com</p></body></html>";
    }
    
    private String getTextoMiriam() {
        return "<html><body><center><font face='Comic Sans' color='#0000CD' size='5'>Eréndira Miriam Jiménez Hernández</font></center>"
             + "<p align='justify'>Mexicana. Originaria de Apizaco, Tlaxcala. Nacida el 29 de Septiembre de 1987.</p>"
             + "<p>Durante la creación de SIMAR fungió como Ingeniero de Requerimientos además de programar algunos módulos.</p>"
             + "<p>Contáctame: miriam16_eren@hotmail.com</p></body></html>";
    }
    
    private String getTextoAdrian() {
        return "<html><body><center><font face='Comic Sans' color='#0000CD' size='5'>Víctor Adrián Valle Rivera</font></center>"
             + "<p align='justify'>Nacido en Chilpancingo GRO en el año de 1987. Participó en la elaboración del proyecto "
             + "SIMAR fungiendo como programador y diseñador principal.</p>"
             + "<p>Contáctame: extradiable@gmail.com</p></body></html>";
    }
}