package SIMAR;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {
    private ArrayList<String> li_exprReg;
    private ArrayList<String> li_nombre;
    private ArrayList<Short> li_tipo;
    private ArrayList<Token> li_Tokens;
    private ArrayList<String> li_PalabraReservada;
    private int numero_Linea = 1;
    private String texto;
    
    private static final String[] ST_EXPR = {
        "\\n", "\\s+", "[a-zA-Z][a-zA-Z0-9_]*", "[0-9]+\\.[0-9]*", "[0-9]+",
        "[.]|[+]|[-]|[*]|[;]|[==]|[<]|[>]|[=]|[!=]|[!]|[#]|[\"]|[\\/]",
        "[(]", "[)]", "[{]", "[}]"
    };
    
    private static final String[] ST_NOMBRE = {
        "Salto de linea", "ws", "identificador", "flotante", "entero",
        "operador", "op_parentAbre", "op_parentCierra", "op_llaveAbre", "op_llaveCierra"
    };
    
    public Lexer() {
        li_exprReg = new ArrayList<>();
        li_nombre = new ArrayList<>();
        li_tipo = new ArrayList<>();
        li_Tokens = new ArrayList<>();
        li_PalabraReservada = new ArrayList<>();
    }
    
    public void cargarDefault() {
        for (int i = 0; i < ST_NOMBRE.length; i++) {
            li_exprReg.add(ST_EXPR[i]);
            li_nombre.add(ST_NOMBRE[i]);
            li_tipo.add((short)i);
        }
    }
    
    public void cargarArchivo(String archivo) {
        this.texto = archivo;
    }
    
    public boolean analizarArchivo() {
        preparar();
        int elements = li_exprReg.size();
        int caracteres = texto.length();
        boolean isOk = true;
        
        while (caracteres > 0 && isOk) {
            boolean matched = false;
            for (int i = 0; i < elements; i++) {
                Pattern pattern = Pattern.compile("^" + li_exprReg.get(i));
                Matcher matcher = pattern.matcher(texto);
                
                if (matcher.find()) {
                    matched = true;
                    short tipo = (short)i;
                    String stg = matcher.group();
                    texto = texto.substring(stg.length());
                    caracteres = texto.length();
                    String nombre = li_nombre.get(i);
                    
                    if (tipo == 0) {
                        numero_Linea++;
                        break;
                    }
                    
                    if (!stg.trim().isEmpty()) {
                        li_Tokens.add(new Token(nombre, stg, tipo));
                        break;
                    }
                }
            }
            if (!matched) isOk = false;
        }
        return isOk;
    }
    
    public String listarTokens() {
        StringBuilder listaTokens = new StringBuilder();
        for (Token token : li_Tokens) {
            listaTokens.append(token.tipo).append("\t")
                       .append(token.nombre).append("\t")
                       .append(token.tk).append("\n");
        }
        return listaTokens.toString();
    }
    
    public void agregarExprReg(String regExpr, String nombre) {
        li_exprReg.add(regExpr);
        li_nombre.add(nombre);
        short i = (short)li_nombre.indexOf(nombre);
        li_tipo.add(i);
    }
    
    public boolean agregarPalabraReservada(String st) {
        Pattern pattern = Pattern.compile(ST_EXPR[2]);
        Matcher matcher = pattern.matcher(st);
        boolean b = matcher.matches();
        if (b) {
            b = esPalabraReservada(st);
            if (!b) {
                li_PalabraReservada.add(st);
            }
        }
        return true;
    }
    
    public int Linea() {
        return numero_Linea;
    }
    
    public boolean esPalabraReservada(String st) {
        return li_PalabraReservada.contains(st);
    }
    
    private void preparar() {
        numero_Linea = 0;
        li_Tokens.clear();
    }
    
    public List<Token> getList() {
        return li_Tokens;
    }
}