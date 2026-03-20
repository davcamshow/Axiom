package SIMAR;

import java.util.ArrayList;

public class ParserC {
    Lexer lexer;
    ArrayList<Token> lista;
    Token currentToken;
    private String serror;
    private int elementos;
    private ArrayList<Relacion> relaciones;
    private ArrayList<String> nombres;
    private String nrelacion = "";
    
    public ParserC(ArrayList<Relacion> r, ArrayList<String> nom) {
        this.relaciones = r;
        this.nombres = nom;
    }
    
    private boolean actualizarToken() {
        this.elementos++;
        if (this.elementos < this.lista.size()) {
            this.currentToken = this.lista.get(this.elementos);
            return true;
        }
        return false;
    }
    
    public void ConfigurarLexer() {
        this.lexer = new Lexer();
        this.lexer.agregarExprReg("^\n", "salto linea");
        this.lexer.agregarExprReg("^\\s+", "espacio");
        this.lexer.agregarExprReg("^[RSPQ]", "relacion");
        this.lexer.agregarExprReg("^[=]", "asignacion");
        this.lexer.agregarExprReg("^[∩]", "interseccion");
        this.lexer.agregarExprReg("^∪", "union");
        this.lexer.agregarExprReg("^ˉ¹", "transpuesta");
        this.lexer.agregarExprReg("^[']", "negacion");
        this.lexer.agregarExprReg("^[°]", "composicion");
        this.lexer.agregarExprReg("^[(]", "op_parentAbre");
        this.lexer.agregarExprReg("^[)]", "op_parentCierra");
    }
    
    public String getErrorType() {
        return this.serror;
    }
    
    public int getRelacion() {
        int index = this.nombres.indexOf(this.nrelacion);
        return index;
    }
    
    public Relacion ejecutar(String codigo) {
        ConfigurarLexer();
        this.lexer.cargarArchivo(codigo);
        
        Token tk_final = null;
        
        boolean b = this.lexer.analizarArchivo();
        if (b) {
            this.elementos = 0;
            this.lista = (ArrayList<Token>)this.lexer.getList();
            
            if (this.lista.size() == 0) {
                this.serror = "No se especifico ninguna operacion";
                return null;
            }
            
            this.currentToken = this.lista.get(0);
            
            if (this.currentToken.nombre.equals("relacion")) {
                this.nrelacion = this.currentToken.tk;
                boolean existe = this.nombres.contains(this.nrelacion);
                if (!existe) {
                    this.serror = "Relacion " + this.nrelacion + " no definida";
                    return null;
                }
                
                actualizarToken();
                
                if (this.currentToken.nombre.equals("asignacion")) {
                    actualizarToken();
                } else {
                    this.serror = "El resultado de las operaciones debe ser asignado a alguna relacion existente";
                    return null;
                }
            } else {
                this.serror = "Inicio incorrecto de expresion";
                return null;
            }
            
            while (this.elementos < this.lista.size()) {
                this.currentToken = this.lista.get(this.elementos);
                tk_final = Sent();
                if (tk_final == null) {
                    break;
                }
                if (this.elementos < 0) {
                    break;
                }
            }
            
            if (tk_final == null || this.elementos < 0) {
                return null;
            }
            
            boolean[][] matrizfinal = new boolean[9][9];
            for (int i = 0; i < tk_final.matriz.length; i++) {
                for (int j = 0; j < tk_final.matriz.length; j++) {
                    matrizfinal[i][j] = tk_final.matriz[i][j];
                }
            }
            Relacion r = new Relacion(matrizfinal, tk_final.nodos, tk_final.matriz.length);
            return r;
        }
        
        this.serror = "Error, caracteres invalidos en la linea " + (this.lexer.Linea() + 1);
        return null;
    }
    
    private Token Sent() {
        Token t = union_interseccion();
        return t;
    }
    
    private Token union_interseccion() {
        Token t1 = composicion();
        
        if (t1 == null) return null;
        
        while (this.currentToken != null && 
               (this.currentToken.nombre.equals("union") || this.currentToken.nombre.equals("interseccion"))) {
            
            String opcion = this.currentToken.nombre;
            actualizarToken();
            
            if (opcion.equals("union")) {
                Token token = composicion();
                if (token == null) {
                    this.serror = "Error, Se esperaba un operando.";
                    return null;
                }
                t1 = union(t1, token);
            } else {
                Token t2 = composicion();
                if (t2 == null) {
                    this.serror = "Error, Se esperaba un operando.";
                    return null;
                }
                t1 = interseccion(t1, t2);
            }
        }
        
        return t1;
    }
    
    private Token composicion() {
        Token t1 = transpuesta_negacion();
        
        if (t1 == null) return null;
        
        while (this.currentToken != null && this.currentToken.nombre.equals("composicion")) {
            actualizarToken();
            Token t2 = transpuesta_negacion();
            if (t2 == null) {
                this.serror = "Error, Se esperaba un operando.";
                return null;
            }
            t1 = composicion(t1, t2);
        }
        
        return t1;
    }
    
    private Token transpuesta_negacion() {
        Token trelacion = atom();
        
        if (trelacion == null) {
            return null;
        }
        if (this.elementos >= this.lista.size()) {
            return trelacion;
        }
        String tipo = this.currentToken.nombre;
        
        while (tipo.equals("negacion") || tipo.equals("transpuesta")) {
            if (tipo.equals("negacion")) {
                negacion(trelacion);
            } else {
                transpuesta(trelacion);
            }
            if (!actualizarToken())
                break;
            tipo = this.currentToken.nombre;
        }
        
        return trelacion;
    }
    
    private Token atom() {
        if (this.currentToken == null) return null;
        String atom = this.currentToken.nombre;
        
        if (atom.equals("relacion")) {
            boolean existe = this.nombres.contains(this.currentToken.tk);
            
            if (existe) {
                Token trelacion = clonarToken();
                if (!actualizarToken()) {
                    return trelacion;
                }
                if (this.currentToken != null && this.currentToken.nombre.equals("relacion")) {
                    this.serror = "Error operacion invalida";
                    this.elementos = -1;
                    return null;
                }
                return trelacion;
            }
            this.serror = "Relacion " + this.currentToken.tk + " no definida";
            this.elementos = -1;
            return null;
        }
        
        if (atom.equals("op_parentAbre")) {
            if (!actualizarToken()) {
                this.serror = "Fin inesperado de sentencia";
                return null;
            }
            Token trelacion = Sent();
            
            if (trelacion == null) {
                return null;
            }
            
            if (this.currentToken == null) {
                this.serror = "Se esperaba un parentesis ')'";
                this.elementos = -1;
                return null;
            }
            
            atom = this.currentToken.nombre;
            
            if (atom.equals("op_parentCierra")) {
                actualizarToken();
                return trelacion;
            }
            
            this.serror = "Se esperaba un parentesis ')'";
            this.elementos = -1;
            return null;
        }
        
        this.serror = "Se esperaba una expresion";
        this.elementos = -1;
        return null;
    }
    
    private Token union(Token op1, Token op2) {
        ArrayList<?> nod1 = op1.nodos;
        ArrayList<?> nod2 = op2.nodos;
        
        if (!comprobaruniverso(nod1, nod2, op1.matriz.length)) {
            return null;
        }
        
        boolean[][] mat1 = op1.matriz;
        boolean[][] mat2 = op2.matriz;
        
        if (mat1.length != mat2.length) {
            this.serror = "Las matrices son de diferente longitud.\nImposible hacer operaciones entre ellas";
            return null;
        }
        
        for (int k = 0; k < mat1.length; k++) {
            for (int i = 0; i < mat1.length; i++) {
                mat1[k][i] = (mat1[k][i] || mat2[k][i]);
            }
        }
        
        Token resultado = new Token("relacion", op1.tk, (short)0);
        resultado.matriz = mat1;
        resultado.nodos = op1.nodos;
        return resultado;
    }
    
    private Token interseccion(Token op1, Token op2) {
        ArrayList<?> nod1 = op1.nodos;
        ArrayList<?> nod2 = op2.nodos;
        
        if (!comprobaruniverso(nod1, nod2, op1.matriz.length)) {
            return null;
        }
        
        boolean[][] mat1 = op1.matriz;
        boolean[][] mat2 = op2.matriz;
        
        if (mat1.length != mat2.length) {
            this.serror = "Las matrices son de diferente longitud.\nImposible hacer operaciones entre ellas";
            return null;
        }
        
        for (int k = 0; k < mat1.length; k++) {
            for (int i = 0; i < mat1.length; i++) {
                mat1[k][i] = (mat1[k][i] && mat2[k][i]);
            }
        }
        
        Token resultado = new Token("relacion", op1.tk, (short)0);
        resultado.matriz = mat1;
        resultado.nodos = op1.nodos;
        return resultado;
    }
    
    private void transpuesta(Token t) {
        boolean[][] matriz = t.matriz;
        boolean[][] matriz2 = new boolean[matriz.length][matriz.length];
        
        for (int i = 0; i < matriz.length; i++) {
            for (int j = 0; j < matriz.length; j++) {
                matriz2[i][j] = matriz[j][i];
            }
        }
        t.matriz = matriz2;
    }
    
    private void negacion(Token t) {
        boolean[][] matriz = t.matriz;
        
        for (int i = 0; i < matriz.length; i++) {
            for (int j = 0; j < matriz.length; j++) {
                matriz[i][j] = !matriz[i][j];
            }
        }
        t.matriz = matriz;
    }
    
    private Token composicion(Token op1, Token op2) {
        ArrayList<?> nod1 = op1.nodos;
        ArrayList<?> nod2 = op2.nodos;
        
        if (!comprobaruniverso(nod1, nod2, op1.matriz.length)) {
            return null;
        }
        
        boolean[][] mat1 = op1.matriz;
        boolean[][] mat2 = op2.matriz;
        
        if (mat1.length != mat2.length) {
            this.serror = "Las matrices son de diferente longitud.\nImposible hacer operaciones entre ellas";
            return null;
        }
        
        boolean[][] mat3 = new boolean[mat1.length][mat1.length];
        
        for (int k = 0; k < mat1.length; k++) {
            for (int i = 0; i < mat1.length; i++) {
                boolean a = false;
                for (int j = 0; j < mat1.length; j++) {
                    a = (a || (mat1[j][k] && mat2[i][j]));
                }
                mat3[i][k] = a;
            }
        }
        
        Token resultado = new Token("relacion", op1.tk, (short)0);
        resultado.matriz = mat3;
        resultado.nodos = op1.nodos;
        return resultado;
    }
    
    private Token clonarToken() {
        String atom = this.currentToken.nombre;
        int indice = this.nombres.indexOf(this.currentToken.tk);
        Relacion r = this.relaciones.get(indice);
        
        boolean[][] real = r.getMatriz();
        boolean[][] copia = new boolean[r.size()][r.size()];
        
        for (int c = 0; c < copia.length; c++) {
            for (int f = 0; f < copia.length; f++) {
                copia[f][c] = real[f][c];
            }
        }
        
        ArrayList<?> nodosreales = r.getNodos();
        ArrayList<String> nodoscopia = new ArrayList<>();
        
        for (int in = 0; in < nodosreales.size(); in++) {
            String s = nodosreales.get(in).toString();
            nodoscopia.add(s);
        }
        
        Token t = new Token("relacion", atom, (short)0);
        t.matriz = copia;
        t.nodos = nodoscopia;
        return t;
    }
    
    private boolean comprobaruniverso(ArrayList<?> n1, ArrayList<?> n2, int elementos) {
        for (int i = 0; i < elementos; i++) {
            String a = n1.get(i).toString();
            String b = n2.get(i).toString();
            if (!a.equals(b)) {
                this.serror = "El universo de las matrices no es el mismo.\nImposible hacer operaciones entre ellas";
                return false;
            }
        }
        
        return true;
    }
}