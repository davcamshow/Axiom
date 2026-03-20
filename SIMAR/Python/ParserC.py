# ParserC.py

from Lexer import Lexer
from Token import Token
from Relacion import Relacion

class ParserC:
    def __init__(self, r, nom):
        self.relaciones = r
        self.nombres = nom
        self.lexer = None
        self.lista = []
        self.currentToken = None
        self.tablasimbolos = {}
        self.serror = ""
        self.elementos = 0
        self.nrelacion = ""

    def actualizarToken(self):
        self.elementos += 1
        if self.elementos < len(self.lista):
            self.currentToken = self.lista[self.elementos]
            return True
        return False

    def ConfigurarLexer(self):
        self.lexer = Lexer()
        self.lexer.agregarExprReg(r"^\n", "salto linea")
        self.lexer.agregarExprReg(r"^\s+", "espacio")
        self.lexer.agregarExprReg(r"^[RrSsPpQq]", "relacion")
        self.lexer.agregarExprReg(r"^=", "asignacion")
        self.lexer.agregarExprReg(r"^∩", "interseccion")
        self.lexer.agregarExprReg(r"^∪", "union")
        self.lexer.agregarExprReg(r"^ˉ¹", "transpuesta")
        self.lexer.agregarExprReg(r"^'", "negacion")
        self.lexer.agregarExprReg(r"^°", "composicion")
        self.lexer.agregarExprReg(r"^\(", "op_parentAbre")
        self.lexer.agregarExprReg(r"^\)", "op_parentCierra")

    def getErrorType(self):
        return self.serror

    def getRelacion(self):
        return self.nombres.index(self.nrelacion)

    def ejecutar(self, codigo):
        self.ConfigurarLexer()
        self.lexer.cargarArchivo(codigo)
        b = self.lexer.analizarArchivo()

        if b:
            self.elementos = 0
            self.lista = self.lexer.getList()

            if len(self.lista) == 0:
                self.serror = "No se especificó ninguna operación"
                return None

            self.currentToken = self.lista[0]

            if self.currentToken.nombre == "relacion":
                self.nrelacion = self.currentToken.tk.upper()
                if self.nrelacion not in self.nombres:
                    self.serror = f"Relación {self.nrelacion} no definida"
                    return None

                if not self.actualizarToken():
                    self.serror = "Expresión incompleta después de la relación"
                    return None

                if self.currentToken.nombre != "asignacion":
                    self.serror = "El resultado debe ser asignado con '='"
                    return None

                if not self.actualizarToken():
                    self.serror = "Expresión incompleta después de '='"
                    return None
            else:
                self.serror = "Inicio incorrecto de expresión. Debe ser 'R = ...'"
                return None

            tk_final = self.Sent()
            if tk_final is None:
                return None

            if tk_final.matriz is None or tk_final.nodos is None:
                self.serror = "Error interno: Token final sin matriz o nodos"
                return None

            matriz_final = [fila[:] for fila in tk_final.matriz]
            r = Relacion(matriz_final, tk_final.nodos[:], len(matriz_final))
            return r

        else:
            self.serror = f"Error, caracteres inválidos en la línea {self.lexer.Linea() + 1}"
            return None

    def Sent(self):
        return self.union_interseccion()

    def union_interseccion(self):
        t1 = self.composicion()
        if t1 is None:
            return None

        while self.currentToken and (self.currentToken.nombre == "union" or self.currentToken.nombre == "interseccion"):
            op = self.currentToken.nombre
            if not self.actualizarToken():
                self.serror = "Error, se esperaba un operando después del operador"
                return None

            t2 = self.composicion()
            if t2 is None:
                return None

            if op == "union":
                t1 = self.union(t1, t2)
            else:
                t1 = self.interseccion(t1, t2)

            if t1 is None:
                return None
        return t1

    def composicion(self):
        t1 = self.transpuesta_negacion()
        if t1 is None:
            return None

        while self.currentToken and self.currentToken.nombre == "composicion":
            if not self.actualizarToken():
                self.serror = "Error, se esperaba un operando después de '°'"
                return None
            t2 = self.transpuesta_negacion()
            if t2 is None:
                return None
            t1 = self.composicion_operacion(t1, t2)
            if t1 is None:
                return None
        return t1

    def transpuesta_negacion(self):
        t_relacion = self.atom()
        if t_relacion is None:
            return None

        while self.currentToken and (self.currentToken.nombre == "transpuesta" or self.currentToken.nombre == "negacion"):
            if self.currentToken.nombre == "transpuesta":
                self.transpuesta(t_relacion)
            else:
                self.negacion(t_relacion)
            if not self.actualizarToken():
                break
        return t_relacion

    def atom(self):
        if not self.currentToken:
            self.serror = "Fin inesperado de sentencia"
            return None

        atom_nombre = self.currentToken.nombre

        if atom_nombre == "relacion":
            nombre_rel = self.currentToken.tk.upper()
            existe = nombre_rel in self.nombres
            if existe:
                t_relacion = self.clonarToken()
                if not self.actualizarToken():
                    return t_relacion
                if self.currentToken and self.currentToken.nombre == "relacion":
                    self.serror = "Error, operación inválida (dos relaciones seguidas)"
                    self.elementos = -1
                    return None
                return t_relacion
            else:
                self.serror = f"Relación {self.currentToken.tk} no definida"
                self.elementos = -1
                return None

        elif atom_nombre == "op_parentAbre":
            if not self.actualizarToken():
                self.serror = "Fin inesperado después de '('"
                return None
            t_relacion = self.Sent()
            if t_relacion is None:
                return None

            if not self.currentToken or self.currentToken.nombre != "op_parentCierra":
                self.serror = "Se esperaba ')'"
                self.elementos = -1
                return None

            self.actualizarToken()
            return t_relacion

        else:
            self.serror = f"Se esperaba una relación o '(' en lugar de '{atom_nombre}'"
            self.elementos = -1
            return None

    def union(self, op1, op2):
        if not self.comprobaruniverso(op1.nodos, op2.nodos, len(op1.matriz)):
            return None
        if len(op1.matriz) != len(op2.matriz):
            self.serror = "Las matrices son de diferente longitud"
            return None

        mat1 = [fila[:] for fila in op1.matriz]
        mat2 = op2.matriz

        for i in range(len(mat1)):
            for j in range(len(mat1)):
                mat1[i][j] = mat1[i][j] or mat2[i][j]

        resultado = Token("relacion", op1.tk, 0)
        resultado.matriz = mat1
        resultado.nodos = op1.nodos[:]
        return resultado

    def interseccion(self, op1, op2):
        if not self.comprobaruniverso(op1.nodos, op2.nodos, len(op1.matriz)):
            return None
        if len(op1.matriz) != len(op2.matriz):
            self.serror = "Las matrices son de diferente longitud"
            return None

        mat1 = [fila[:] for fila in op1.matriz]
        mat2 = op2.matriz

        for i in range(len(mat1)):
            for j in range(len(mat1)):
                mat1[i][j] = mat1[i][j] and mat2[i][j]

        resultado = Token("relacion", op1.tk, 0)
        resultado.matriz = mat1
        resultado.nodos = op1.nodos[:]
        return resultado

    def transpuesta(self, t):
        mat = t.matriz
        tam = len(mat)
        mat2 = [[False for _ in range(tam)] for _ in range(tam)]
        for i in range(tam):
            for j in range(tam):
                mat2[i][j] = mat[j][i]
        t.matriz = mat2

    def negacion(self, t):
        mat = t.matriz
        for i in range(len(mat)):
            for j in range(len(mat)):
                mat[i][j] = not mat[i][j]

    def composicion_operacion(self, op1, op2):
        if not self.comprobaruniverso(op1.nodos, op2.nodos, len(op1.matriz)):
            return None
        if len(op1.matriz) != len(op2.matriz):
            self.serror = "Las matrices son de diferente longitud"
            return None

        mat1 = op1.matriz
        mat2 = op2.matriz
        tam = len(mat1)
        mat3 = [[False for _ in range(tam)] for _ in range(tam)]

        for i in range(tam):
            for k in range(tam):
                a = False
                for j in range(tam):
                    a = a or (mat1[j][k] and mat2[i][j])
                mat3[i][k] = a

        resultado = Token("relacion", op1.tk, 0)
        resultado.matriz = mat3
        resultado.nodos = op1.nodos[:]
        return resultado

    def clonarToken(self):
        nombre_rel = self.currentToken.tk.upper()
        if nombre_rel not in self.nombres:
            self.serror = f"Relación {nombre_rel} no encontrada"
            return None
            
        idx = self.nombres.index(nombre_rel)
        r = self.relaciones[idx]

        matriz_real = r.getMatriz()
        tam = r.size()
        copia_matriz = [[False for _ in range(tam)] for _ in range(tam)]
        for f in range(tam):
            for c in range(tam):
                copia_matriz[f][c] = matriz_real[f][c]

        nodos_reales = r.getNodos()
        copia_nodos = [str(n) for n in nodos_reales[:tam]]

        t = Token("relacion", self.currentToken.tk, 0)
        t.matriz = copia_matriz
        t.nodos = copia_nodos
        return t

    def comprobaruniverso(self, n1, n2, elementos):
        for i in range(elementos):
            if str(n1[i]) != str(n2[i]):
                self.serror = "El universo de las matrices no es el mismo."
                return False
        return True