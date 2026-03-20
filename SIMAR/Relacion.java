package SIMAR;

import java.awt.Container;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import javax.swing.JOptionPane;

public class Relacion {
    private String pares_ordenados;
    private ArrayList<String> nodos;
    private ArrayList<String> dominio;
    private ArrayList<String> codominio;
    private boolean[][] matriz;
    private String error = "";
    private String tipo = "";
    private GNodo[] gnodos;
    private int length;
    private boolean suprayectiva;
    private boolean inyectiva;
    private boolean biyectiva;
    private boolean invertible;
    private boolean reflexiva;
    private boolean irreflexiva;
    private boolean simetrica;
    private boolean asimetrica;
    private boolean antisimetrica;
    private boolean transitiva;
    private boolean equivalencia;
    private boolean particion;
    private boolean funcion;
    
    public Relacion(boolean[][] m, ArrayList<String> n, int l) {
        this.length = l;
        this.matriz = m;
        this.nodos = n;
        this.gnodos = new GNodo[9];
        generarPares();
        verificarPropiedades();
    }
    
    public Relacion() {
        this.gnodos = new GNodo[9];
        this.nodos = new ArrayList<>();
        this.dominio = new ArrayList<>();
        this.codominio = new ArrayList<>();
    }
    
    public int size() {
        return this.length;
    }
    
    public String getError() {
        return this.error;
    }
    
    public String getTipo() {
        return this.tipo;
    }
    
    public boolean esFuncion() {
        return this.funcion;
    }
    
    public boolean esSuprayectiva() {
        return this.suprayectiva;
    }
    
    public boolean esInyectiva() {
        return this.inyectiva;
    }
    
    public boolean esBiyectiva() {
        return this.biyectiva;
    }
    
    public boolean esInvertible() {
        return this.invertible;
    }
    
    public boolean esReflexiva() {
        return this.reflexiva;
    }
    
    public boolean esIrreflexiva() {
        return this.irreflexiva;
    }
    
    public boolean esSimetrica() {
        return this.simetrica;
    }
    
    public boolean esAsimetrica() {
        return this.asimetrica;
    }
    
    public boolean esAntisimetrica() {
        return this.antisimetrica;
    }
    
    public boolean esTransitiva() {
        return this.transitiva;
    }
    
    public boolean esDeEquivalencia() {
        return this.equivalencia;
    }
    
    public boolean esDeParticion() {
        return this.particion;
    }
    
    private void generarPares() {
        this.pares_ordenados = "{ ";
        
        for (int i = 0; i < this.length; i++) {
            this.pares_ordenados += this.nodos.get(i).toString();
            if (i + 1 < this.length)
                this.pares_ordenados += ", ";
        }
        this.pares_ordenados += " } \n{ ";
        
        int npares = 0;
        
        Object[] pares = null;
        ArrayList<String> tmppares = new ArrayList<>();
        
        for (int f = 0; f < this.matriz.length; f++) {
            for (int c = 0; c < this.matriz.length; c++) {
                if (this.matriz[f][c]) {
                    tmppares.add(this.nodos.get(c).toString() + this.nodos.get(f).toString());
                }
            }
        }
        
        pares = tmppares.toArray();
        Arrays.sort(pares);
        
        for (int j = 0; j < pares.length; j++) {
            if (npares % 9 == 0)
                this.pares_ordenados += "\n";
            String par = pares[j].toString();
            String sori = par.substring(0, 1);
            String sdes = par.substring(1);
            this.pares_ordenados += "( " + sori + ", " + sdes + " )";
            npares++;
            if (npares < pares.length) {
                this.pares_ordenados += ", ";
            }
        }
        this.pares_ordenados += " \n}";
    }
    
    public GNodo[] getGNodos() {
        return this.gnodos;
    }
    
    public ArrayList<String> getNodos() {
        return this.nodos;
    }
    
    public boolean[][] getMatriz() {
        return this.matriz;
    }
    
    public void setPares(String st) {
        this.pares_ordenados = st;
    }
    
    public boolean verificarPares(ArrayList<Token> tokens) {
        boolean f = true;
        char digito = 'n';
        int estado = 0;
        String par = "";
        int fila = 0, columna = 0;
        ArrayList<String> indices = new ArrayList<>();
        HashMap<String, String> key_nodos = new HashMap<>();
        HashMap<String, String> key_pares = new HashMap<>();
        char letras = 'a';
        char numeros = '1';
        
        for (int i = 0; i < tokens.size(); i++) {
            if (!f) {
                break;
            }
            Token token = tokens.get(i);
            
            switch (estado) {
                case 0:
                    if (!token.nombre.equals("op_llaveAbre")) {
                        f = false;
                        break;
                    }
                    estado++;
                    break;
                case 1:
                    if (!token.nombre.equals("nodo")) {
                        f = false;
                        break;
                    }
                    estado++;
                    
                    if (key_nodos.size() < 9) {
                        char ch = token.tk.charAt(0);
                        
                        if (digito == 'n') {
                            if (Character.isDigit(ch)) {
                                digito = 'd';
                                if (ch != numeros) {
                                    f = false;
                                    this.error = "Escriba los numeros en orden ascendente\npartiendo del numero '1'";
                                    this.tipo = "Error de definicion";
                                    break;
                                }
                                numeros = (char)(numeros + 1);
                            } else {
                                if (Character.isUpperCase(ch)) {
                                    f = false;
                                    this.error = "Imposible utilizar letras mayusculas";
                                    this.tipo = "Error de definicion";
                                    break;
                                }
                                digito = 'c';
                                if (ch != letras) {
                                    f = false;
                                    this.error = "Escriba los caracteres en orden alfabetico\npartiendo de la letra 'a'";
                                    this.tipo = "Error de definicion";
                                    break;
                                }
                                letras = (char)(letras + 1);
                            }
                        } else if (digito == 'd') {
                            if (Character.isLetter(ch)) {
                                f = false;
                                this.error = "Imposible mezclar numeros y letras";
                                this.tipo = "Error de definicion";
                                break;
                            }
                            if (ch != numeros) {
                                f = false;
                                this.error = "Por favor, ingrese los numeros en el orden natural (1,2,3...)";
                                this.tipo = "Error de orden";
                                break;
                            }
                            numeros = (char)(numeros + 1);
                        } else {
                            if (Character.isDigit(ch)) {
                                f = false;
                                this.error = "Imposible mezclar numeros y letras";
                                this.tipo = "Error de definicion";
                                break;
                            }
                            if (ch != letras) {
                                f = false;
                                this.error = "Por favor, ingrese los caracteres en orden alfabético";
                                this.tipo = "Error de orden";
                                break;
                            }
                            letras = (char)(letras + 1);
                        }
                        
                        if (!key_nodos.containsKey(token.tk)) {
                            key_nodos.put(token.tk, token.tk);
                            indices.add(token.tk);
                            break;
                        }
                        f = false;
                        this.error = "El nodo: ' " + token.tk + "' ya ha sido definido";
                        this.tipo = "Inconsistencia";
                        break;
                    }
                    f = false;
                    this.error = "No se permiten más de 9 nodos.";
                    this.tipo = "Limite excedido";
                    break;
                    
                case 2:
                    if (token.nombre.equals("coma")) {
                        estado = 1;
                        break;
                    }
                    if (token.nombre.equals("op_llaveCierra")) {
                        this.matriz = new boolean[key_nodos.size()][key_nodos.size()];
                        estado++;
                        break;
                    }
                    f = false;
                    this.error = "Expresión inválida.\nSe esperaba un caracter ' , ' o ' } ',\nantes de ' " + token.tk + " '.";
                    this.tipo = "Verifique su sintaxis";
                    break;
                    
                case 3:
                    if (token.nombre.equals("op_llaveAbre")) {
                        estado = 5;
                        break;
                    }
                    f = false;
                    this.error = "Expresión inválida.\nSe esperaba un caracter ' { ' ,\nantes de ' " + token.tk + " '.";
                    this.tipo = "Verifique su sintaxis";
                    break;
                    
                case 4:
                    if (token.nombre.equals("coma")) {
                        estado++;
                        break;
                    }
                    if (token.nombre.equals("op_llaveCierra")) {
                        estado = 0;
                        if (i > tokens.size() - 1) {
                            f = false;
                            this.error = "Posición erronea de terminador de cadena";
                            this.tipo = "Verifique su sintaxis";
                        }
                        break;
                    }
                    f = false;
                    this.error = "Se esperaba un caracter ' } '";
                    this.tipo = "Verifique su sintaxis";
                    break;
                    
                case 5:
                    if (token.nombre.equals("op_parentAbre")) {
                        estado++;
                        break;
                    }
                    if (token.nombre.equals("op_llaveCierra")) {
                        estado = 0;
                        if (i + 2 == tokens.size()) {
                            this.error = "Posicion erronea de terminador de cadena.\nNo se esperaban mas elementos.";
                            this.tipo = "Error de sintaxis";
                            f = false;
                        }
                        break;
                    }
                    f = false;
                    this.error = "Fin invalido de expresión.\nDebe terminar con el caracter ' } '.";
                    this.tipo = "Verifique su sintaxis";
                    break;
                    
                case 6:
                    if (token.nombre.equals("nodo")) {
                        if (!key_nodos.containsKey(token.tk)) {
                            this.error = "El nodo: ' " + token.tk + " ' no ha sido definido";
                            this.tipo = "Inconsistencia";
                            f = false;
                            break;
                        }
                        columna = indices.indexOf(token.tk);
                        estado++;
                        par = token.tk;
                        break;
                    }
                    f = false;
                    this.error = "Se esperaba un nodo";
                    this.tipo = "Verifique su sintaxis";
                    break;
                    
                case 7:
                    if (token.nombre.equals("coma")) {
                        estado++;
                        break;
                    }
                    f = false;
                    this.error = "Se esperaba un separador ' , '";
                    this.tipo = "Verifique su sintaxis";
                    break;
                    
                case 8:
                    if (token.nombre.equals("nodo")) {
                        estado++;
                        
                        if (!key_nodos.containsKey(token.tk)) {
                            this.error = "El nodo: ' " + token.tk + " ' no ha sido definido";
                            this.tipo = "Inconsistencia";
                            f = false;
                            break;
                        }
                        par = par + ", " + token.tk;
                        fila = indices.indexOf(token.tk);
                        
                        this.matriz[fila][columna] = true;
                        
                        if (key_pares.containsKey(par)) {
                            this.error = "El par: ( " + par + " ) esta duplicado.";
                            this.tipo = "Inconsistencia";
                            f = false;
                            break;
                        }
                        key_pares.put(par, par);
                        break;
                    }
                    this.error = "Se esperaba un nodo";
                    this.tipo = "Verifique su sintaxis";
                    f = false;
                    break;
                    
                case 9:
                    if (token.nombre.equals("op_parentCierra")) {
                        estado = 4;
                        break;
                    }
                    this.error = "Se esperaba un caracter ' ) '";
                    this.tipo = "Verifique su sintaxis";
                    f = false;
                    break;
            }
        }
        
        if (estado != 0 || tokens.size() == 0) {
            if (estado == 0) {
                this.error = "Inicio invalido de expresión.\nDebe iniciar con el caracter ' { '.";
                this.tipo = "Verifique su sintaxis";
            } else if (estado == 1) {
                this.error = "Expresión inválida.\nDebe definir un conjunto.";
                this.tipo = "Verifique su sintaxis";
            }
            f = false;
        }
        
        if (this.error.equals("")) {
            this.error = "Ha ocurrido un error mientras se generaba la relacion\nverifique los datos que ingresó";
            this.tipo = "Error de sintaxis";
        }
        
        if (f == true) {
            this.length = key_nodos.size();
            verificarPropiedades();
            
            if (digito == 'd') {
                for (char c = numeros; c < ':'; c = (char)(c + 1)) {
                    indices.add(String.valueOf(c));
                }
            } else {
                for (char c = letras; c < 'j'; c = (char)(c + 1)) {
                    indices.add(String.valueOf(c));
                }
            }
            
            this.nodos = indices;
        }
        
        return f;
    }
    
    public void verificarPropiedades() {
        Funcion();
        Suprayectiva();
        Inyectiva();
        Reflexiva();
        Irreflexiva();
        Simetrica();
        Asimetrica();
        Antisimetrica();
        Transitiva();
        Equivalencia();
        Particion();
        Biyectiva();
        Invertible();
    }
    
    private void Inyectiva() {
        int cont1 = 0, cont2 = 0, unos = 0, colum = 0;
        while (cont1 < this.length) {
            cont2 = 0;
            unos = 0;
            while (cont2 < this.length) {
                if (this.matriz[cont1][cont2] == true)
                    unos++;
                cont2++;
            }
            if (unos < 2)
                colum++;
            cont1++;
        }
        boolean regreso = false;
        if (colum == this.length && this.funcion == true) {
            regreso = true;
        } else if (!this.funcion || colum != this.length) {
            regreso = false;
        }
        this.inyectiva = regreso;
    }
    
    private void Funcion() {
        int cont1 = 0, cont2 = 0, unos = 0, filas = 0;
        while (cont2 < this.length) {
            cont1 = 0;
            unos = 0;
            while (cont1 < this.length) {
                if (this.matriz[cont1][cont2] == true)
                    unos++;
                cont1++;
            }
            if (unos == 1)
                filas++;
            cont2++;
        }
        if (filas == this.length) {
            this.funcion = true;
        } else {
            this.funcion = false;
        }
    }
    
    private void Biyectiva() {
        boolean regreso = false;
        if (this.inyectiva == true && this.suprayectiva == true)
            regreso = true;
        this.biyectiva = regreso;
    }
    
    private void Invertible() {
        if (this.biyectiva == true) {
            this.invertible = true;
        } else {
            this.invertible = false;
        }
    }
    
    private void Reflexiva() {
        int ind = 0;
        while (ind < this.length && this.matriz[ind][ind]) {
            ind++;
        }
        if (ind == this.length) {
            this.reflexiva = true;
        } else {
            this.reflexiva = false;
        }
    }
    
    private void Suprayectiva() {
        int cont1 = 0, cont2 = 0, unos = 0, colum = 0;
        while (cont1 < this.length) {
            cont2 = 0;
            unos = 0;
            while (cont2 < this.length) {
                if (this.matriz[cont1][cont2] == true)
                    unos++;
                cont2++;
            }
            if (unos >= 1)
                colum++;
            cont1++;
        }
        boolean regreso = false;
        if (colum == this.length && this.funcion == true) {
            regreso = true;
        } else if (!this.funcion || colum != this.length) {
            regreso = false;
        }
        this.suprayectiva = regreso;
    }
    
    private void Irreflexiva() {
        int ind = 0;
        while (ind < this.length && !this.matriz[ind][ind]) {
            ind++;
        }
        if (ind == this.length) {
            this.irreflexiva = true;
        } else {
            this.irreflexiva = false;
        }
    }
    
    private void Simetrica() {
        this.simetrica = true;
        for (int x = 0; x < this.length; x++) {
            for (int y = 0; y < this.length; y++) {
                if (this.matriz[x][y] != this.matriz[y][x]) {
                    this.simetrica = false;
                }
            }
        }
    }
    
    private void Asimetrica() {
        this.asimetrica = true;
        for (int i = 0; i < this.length; i++) {
            for (int j = 0; j < this.length; j++) {
                if (this.matriz[i][j] && this.matriz[j][i]) {
                    this.asimetrica = false;
                }
            }
        }
    }
    
    private void Antisimetrica() {
        this.antisimetrica = true;
        for (int i = 0; i < this.length; i++) {
            for (int j = 0; j < this.length; j++) {
                if (i != j && this.matriz[i][j] && this.matriz[j][i]) {
                    this.antisimetrica = false;
                }
            }
        }
    }
    
    private void Transitiva() {
        this.transitiva = true;
        for (int x = 0; x < this.length; x++) {
            for (int y = 0; y < this.length; y++) {
                if (this.matriz[x][y]) {
                    for (int z = 0; z < this.length; z++) {
                        if (this.matriz[y][z] && !this.matriz[x][z]) {
                            this.transitiva = false;
                        }
                    }
                }
            }
        }
    }
    
    private void Equivalencia() {
        if (this.transitiva && this.reflexiva && this.simetrica) {
            this.equivalencia = true;
        } else {
            this.equivalencia = false;
        }
    }
    
    private void Particion() {
        if (this.simetrica && this.reflexiva && this.transitiva) {
            ArrayList<String> listaclases = new ArrayList<>();
            
            for (int x = 0; x < this.length; x++) {
                ArrayList<String> listafilas = new ArrayList<>();
                for (int y = 0; y < this.length; y++) {
                    if (this.matriz[x][y]) {
                        listafilas.add(Integer.toString(y + 1));
                    }
                }
                int taman = listafilas.size();
                StringBuilder conca = new StringBuilder("");
                for (int p = 0; p < taman; p++) {
                    conca.append(listafilas.get(p));
                }
                
                boolean noesta = true;
                for (int v = 0; v < listaclases.size(); v++) {
                    if (conca.toString().equals(listaclases.get(v))) {
                        v = listaclases.size() + 1;
                        noesta = false;
                    }
                }
                if (noesta) {
                    listaclases.add(conca.toString());
                }
            }
            if (listaclases.size() < 2) {
                this.particion = false;
            } else {
                this.particion = true;
                for (int r = 1; r < this.length; r++) {
                    ArrayList<String> cuantos = new ArrayList<>();
                    for (int l = 0; l < listaclases.size(); l++) {
                        if (listaclases.get(0).indexOf(String.valueOf(r)) > -1)
                            cuantos.add("");
                    }
                    if (cuantos.size() > 1) {
                        this.particion = false;
                    }
                }
            }
        } else {
            this.particion = false;
        }
    }
    
    public void aplicarHasse(Container c) {
        if (!this.transitiva || !this.antisimetrica || !this.reflexiva) {
            JOptionPane.showMessageDialog(c, "La relación actual no presenta diagrama de Hasse ya que necesita ser:\n reflexiva, antisimétrica y transitiva", "Imposible crear relación", 1);
            return;
        }
        
        for (int j = 0; j < this.length; j++) {
            if (this.matriz[j][j])
                this.matriz[j][j] = false;
        }
        for (int i = 0; i < this.length; i++) {
            for (int k = 0; k < this.length; k++) {
                if (i != k && this.matriz[i][k] && this.matriz[k][i]) {
                    this.matriz[i][k] = false;
                }
            }
        }
        for (int x = 0; x < this.length; x++) {
            for (int y = 0; y < this.length; y++) {
                if (this.matriz[x][y]) {
                    for (int z = 0; z < this.length; z++) {
                        if (this.matriz[y][z] && this.matriz[x][z]) {
                            this.matriz[x][z] = false;
                        }
                    }
                }
            }
        }
        verificarPropiedades();
        generarPares();
    }
    
    public void aplicarCerraduraTransitiva() {
        boolean cambio;
        do {
            cambio = false;
            for (int x = 0; x < this.length; x++) {
                for (int y = 0; y < this.length; y++) {
                    if (this.matriz[x][y]) {
                        for (int z = 0; z < this.length; z++) {
                            if (this.matriz[y][z] && !this.matriz[x][z]) {
                                this.matriz[x][z] = true;
                                cambio = true;
                            }
                        }
                    }
                }
            }
        } while (cambio);
        verificarPropiedades();
        generarPares();
    }
    
    public void aplicarCerraduraReflexiva() {
        for (int x = 0; x < this.length; x++) {
            if (!this.matriz[x][x]) {
                this.matriz[x][x] = true;
            }
        }
        verificarPropiedades();
        generarPares();
    }
    
    public void aplicarCerraduraSimetrica() {
        for (int x = 0; x < this.length; x++) {
            for (int y = 0; y < this.length; y++) {
                if (this.matriz[x][y] && !this.matriz[y][x]) {
                    this.matriz[y][x] = true;
                }
            }
        }
        verificarPropiedades();
        generarPares();
    }
    
    public void aplicarDominio(Container c) {
        int cont1 = 0, cont2 = 0;
        this.dominio = new ArrayList<>();
        while (cont2 < this.length) {
            cont1 = 0;
            while (cont1 < this.length) {
                if (this.matriz[cont1][cont2] == true) {
                    cont1 = this.length;
                    this.dominio.add(this.nodos.get(cont2));
                    continue;
                }
                cont1++;
            }
            cont2++;
        }
        imprimeLista(1, c);
    }
    
    public void aplicarCodominio(Container c) {
        int cont1 = 0, cont2 = 0;
        this.codominio = new ArrayList<>();
        while (cont1 < this.length) {
            cont2 = 0;
            while (cont2 < this.length) {
                if (this.matriz[cont1][cont2] == true) {
                    cont2 = this.length;
                    this.codominio.add(this.nodos.get(cont1));
                    continue;
                }
                cont2++;
            }
            cont1++;
        }
        imprimeLista(2, c);
    }
    
    private void imprimeLista(int id, Container c) {
        StringBuilder pantalla = new StringBuilder();
        if (id == 1) {
            for (int i = 0; i < this.dominio.size(); i++)
                pantalla.append(" ").append(this.dominio.get(i).toString());
            if (pantalla.length() == 0) {
                JOptionPane.showMessageDialog(null, "NO HAY DOMINIO EN LA RELACION", "DOMINIO", 1);
            } else {
                JOptionPane.showMessageDialog(null, pantalla.toString(), "DOMINIO", 1);
            }
        }
        if (id == 2) {
            for (int i = 0; i < this.codominio.size(); i++)
                pantalla.append(" ").append(this.codominio.get(i).toString());
            if (pantalla.length() == 0) {
                JOptionPane.showMessageDialog(null, "NO HAY CODOMINIO EN LA RELACION", "CODOMINIO", 1);
            } else {
                JOptionPane.showMessageDialog(null, pantalla.toString(), "CODOMINIO", 1);
            }
        }
    }
    
    public String toString() {
        return this.pares_ordenados;
    }
    
    public void verEquivalencia(Container c) {
        if (this.equivalencia) {
            ArrayList<String> listaclases = new ArrayList<>();
            for (int x = 0; x < this.length; x++) {
                ArrayList<String> listafilas = new ArrayList<>();
                for (int y = 0; y < this.length; y++) {
                    if (this.matriz[x][y])
                        listafilas.add(this.nodos.get(y).toString());
                }
                int taman = listafilas.size();
                StringBuilder conca = new StringBuilder();
                conca.append("[").append(this.nodos.get(x)).append("] = ");
                for (int p = 0; p < taman; p++) {
                    if (conca.length() == 6)
                        conca.append("{");
                    if (conca.length() > 7)
                        conca.append(", ");
                    conca.append(listafilas.get(p));
                    if (p == taman - 1) {
                        conca.append("}");
                    }
                }
                listaclases.add(conca.toString());
            }
            
            int t = listaclases.size();
            StringBuilder clases = new StringBuilder();
            for (int i = 0; i < t; i++) {
                clases.append(listaclases.get(i)).append("\n");
            }
            JOptionPane.showMessageDialog(c, clases, "Clases de equivalencia", 1);
        } else {
            JOptionPane.showMessageDialog(c, "No tiene clases de equivalencia", "EQUIVALENCIA", 1);
        }
    }
    
    public void verParticion(Container c) {
        if (this.particion) {
            ArrayList<String> listaclases = new ArrayList<>();
            for (int x = 0; x < this.length; x++) {
                ArrayList<String> listafilas = new ArrayList<>();
                for (int y = 0; y < this.length; y++) {
                    if (this.matriz[x][y])
                        listafilas.add(this.nodos.get(y).toString());
                }
                int taman = listafilas.size();
                StringBuilder conca = new StringBuilder();
                for (int p = 0; p < taman; p++) {
                    if (conca.length() == 0)
                        conca.append("{");
                    if (conca.length() > 1)
                        conca.append(", ");
                    conca.append(listafilas.get(p));
                    if (p == taman - 1) {
                        conca.append("}");
                    }
                }
                boolean noesta = true;
                for (int v = 0; v < listaclases.size(); v++) {
                    if (conca.toString().equals(listaclases.get(v))) {
                        v = listaclases.size() + 1;
                        noesta = false;
                    }
                }
                if (noesta) {
                    listaclases.add(conca.toString());
                }
            }
            int t = listaclases.size();
            StringBuilder clases = new StringBuilder();
            for (int i = 0; i < t; i++) {
                clases.append(listaclases.get(i)).append("\n");
            }
            JOptionPane.showMessageDialog(c, clases, "Particion", 1);
        } else {
            JOptionPane.showMessageDialog(c, "No tiene partición", "PARTICION", 1);
        }
    }
}