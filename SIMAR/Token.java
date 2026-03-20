package SIMAR;

import java.util.ArrayList;

public class Token {
    public String nombre;
    public String tk;
    public short tipo;
    public boolean[][] matriz;
    public ArrayList<String> nodos;
    
    public Token(String nombre, String tk, short tipo) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.tk = tk;
        this.nodos = new ArrayList<>();
    }
}