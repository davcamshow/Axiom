# Relacion.py

class Relacion:
    def __init__(self, matriz=None, nodos=None, length=0):
        self.pares_ordenados = ""
        self.nodos = nodos if nodos is not None else []
        self.dominio = []
        self.codominio = []
        self.matriz = matriz if matriz is not None else []
        self.error = ""
        self.tipo = ""
        self.gnodos = [None] * 9
        self.length = length

        # Propiedades booleanas
        self.suprayectiva = False
        self.inyectiva = False
        self.biyectiva = False
        self.invertible = False
        self.reflexiva = False
        self.irreflexiva = False
        self.simetrica = False
        self.asimetrica = False
        self.antisimetrica = False
        self.transitiva = False
        self.equivalencia = False
        self.particion = False
        self.funcion = False

        if self.matriz and self.nodos and self.length > 0:
            self.generarPares()
            self.verificarPropiedades()

    def size(self):
        return self.length

    def getError(self):
        return self.error

    def getTipo(self):
        return self.tipo

    def esFuncion(self): 
        return self.funcion
    
    def esSuprayectiva(self): 
        return self.suprayectiva
    
    def esInyectiva(self): 
        return self.inyectiva
    
    def esBiyectiva(self): 
        return self.biyectiva
    
    def esInvertible(self): 
        return self.invertible
    
    def esReflexiva(self): 
        return self.reflexiva
    
    def esIrreflexiva(self): 
        return self.irreflexiva
    
    def esSimetrica(self): 
        return self.simetrica
    
    def esAsimetrica(self): 
        return self.asimetrica
    
    def esAntisimetrica(self): 
        return self.antisimetrica
    
    def esTransitiva(self): 
        return self.transitiva
    
    def esDeEquivalencia(self): 
        return self.equivalencia
    
    def esDeParticion(self): 
        return self.particion

    def generarPares(self):
        self.pares_ordenados = "{ "
        for i in range(self.length):
            self.pares_ordenados += str(self.nodos[i])
            if i + 1 < self.length:
                self.pares_ordenados += ", "
        self.pares_ordenados += " } \n{ "

        pares = []
        for f in range(self.length):
            for c in range(self.length):
                if self.matriz[f][c]:
                    pares.append(f"{self.nodos[f]},{self.nodos[c]}")

        pares.sort()
        num_pares = len(pares)
        for i, par_str in enumerate(pares):
            if i % 9 == 0 and i != 0:
                self.pares_ordenados += "\n"
            origen, destino = par_str.split(',')
            self.pares_ordenados += f"( {origen}, {destino} )"
            if i < num_pares - 1:
                self.pares_ordenados += ", "
        self.pares_ordenados += " \n}"

    def getGNodos(self):
        return self.gnodos

    def getNodos(self):
        return self.nodos

    def getMatriz(self):
        return self.matriz

    def setPares(self, st):
        self.pares_ordenados = st

    def verificarPares(self, tokens):
        f = True
        digito = 'n'
        estado = 0
        par = ""
        fila, columna = 0, 0
        indices = []
        key_nodos = {}
        key_pares = {}
        letras = 'a'
        numeros = '1'

        i = 0
        while i < len(tokens):
            if not f:
                break
            token = tokens[i]

            if estado == 0:
                if token.nombre != "op_llaveAbre":
                    f = False
                    self.error = "Se esperaba '{'"
                    self.tipo = "Error de sintaxis"
                else:
                    estado = 1

            elif estado == 1:
                if token.nombre != "nodo":
                    f = False
                    self.error = "Se esperaba un nodo (letra o número)"
                    self.tipo = "Error de sintaxis"
                else:
                    estado = 2
                    if len(key_nodos) < 9:
                        ch = token.tk[0]

                        if digito == 'n':
                            if ch.isdigit():
                                digito = 'd'
                                if ch != numeros:
                                    f = False
                                    self.error = "Escriba los números en orden ascendente partiendo del '1'"
                                    self.tipo = "Error de definicion"
                                else:
                                    numeros = chr(ord(numeros) + 1)
                            else:
                                if ch.isupper():
                                    f = False
                                    self.error = "Imposible utilizar letras mayúsculas"
                                    self.tipo = "Error de definicion"
                                else:
                                    digito = 'c'
                                    if ch != letras:
                                        f = False
                                        self.error = "Escriba los caracteres en orden alfabético partiendo de la 'a'"
                                        self.tipo = "Error de definicion"
                                    else:
                                        letras = chr(ord(letras) + 1)
                        elif digito == 'd':
                            if not ch.isdigit():
                                f = False
                                self.error = "Imposible mezclar números y letras"
                                self.tipo = "Error de definicion"
                            elif ch != numeros:
                                f = False
                                self.error = "Ingrese los números en orden natural (1,2,3...)"
                                self.tipo = "Error de orden"
                            else:
                                numeros = chr(ord(numeros) + 1)
                        else:
                            if not ch.isalpha():
                                f = False
                                self.error = "Imposible mezclar números y letras"
                                self.tipo = "Error de definicion"
                            elif ch != letras:
                                f = False
                                self.error = "Ingrese los caracteres en orden alfabético"
                                self.tipo = "Error de orden"
                            else:
                                letras = chr(ord(letras) + 1)

                        if token.tk not in key_nodos:
                            key_nodos[token.tk] = token.tk
                            indices.append(token.tk)
                        else:
                            f = False
                            self.error = f"El nodo: '{token.tk}' ya ha sido definido"
                            self.tipo = "Inconsistencia"
                    else:
                        f = False
                        self.error = "No se permiten más de 9 nodos."
                        self.tipo = "Limite excedido"

            elif estado == 2:
                if token.nombre == "coma":
                    estado = 1
                elif token.nombre == "op_llaveCierra":
                    self.matriz = [[False for _ in range(len(key_nodos))] for _ in range(len(key_nodos))]
                    self.length = len(key_nodos)
                    estado = 3
                else:
                    f = False
                    self.error = f"Expresión inválida. Se esperaba ',' o '}}' antes de '{token.tk}'."
                    self.tipo = "Verifique su sintaxis"

            elif estado == 3:
                if token.nombre == "op_llaveAbre":
                    estado = 5
                else:
                    f = False
                    self.error = f"Expresión inválida. Se esperaba '{{' antes de '{token.tk}'."
                    self.tipo = "Verifique su sintaxis"

            elif estado == 4:
                if token.nombre == "coma":
                    estado = 5
                elif token.nombre == "op_llaveCierra":
                    estado = 0
                else:
                    f = False
                    self.error = f"Se esperaba ',' o '}}'."
                    self.tipo = "Verifique su sintaxis"

            elif estado == 5:
                if token.nombre == "op_parentAbre":
                    estado = 6
                elif token.nombre == "op_llaveCierra":
                    estado = 0
                    if i + 2 == len(tokens):
                        f = False
                        self.error = "Posición errónea de terminador de cadena."
                        self.tipo = "Error de sintaxis"
                else:
                    f = False
                    self.error = f"Fin inválido de expresión. Se esperaba '(' o '}}'."
                    self.tipo = "Verifique su sintaxis"

            elif estado == 6:
                if token.nombre == "nodo":
                    if token.tk not in key_nodos:
                        f = False
                        self.error = f"El nodo: '{token.tk}' no ha sido definido"
                        self.tipo = "Inconsistencia"
                    else:
                        columna = indices.index(token.tk)
                        estado = 7
                        par = token.tk
                else:
                    f = False
                    self.error = f"Se esperaba un nodo."
                    self.tipo = "Verifique su sintaxis"

            elif estado == 7:
                if token.nombre == "coma":
                    estado = 8
                else:
                    f = False
                    self.error = f"Se esperaba una ','."
                    self.tipo = "Verifique su sintaxis"

            elif estado == 8:
                if token.nombre == "nodo":
                    estado = 9
                    if token.tk not in key_nodos:
                        f = False
                        self.error = f"El nodo: '{token.tk}' no ha sido definido"
                        self.tipo = "Inconsistencia"
                    else:
                        par = par + ", " + token.tk
                        fila = indices.index(token.tk)
                        self.matriz[fila][columna] = True
                        if par in key_pares:
                            f = False
                            self.error = f"El par: ( {par} ) está duplicado."
                            self.tipo = "Inconsistencia"
                        else:
                            key_pares[par] = par
                else:
                    f = False
                    self.error = f"Se esperaba un nodo."
                    self.tipo = "Verifique su sintaxis"

            elif estado == 9:
                if token.nombre == "op_parentCierra":
                    estado = 4
                else:
                    f = False
                    self.error = f"Se esperaba ')'."
                    self.tipo = "Verifique su sintaxis"
            i += 1

        if estado != 0 or len(tokens) == 0:
            if estado == 1:
                self.error = "Expresión inválida. Debe definir un conjunto."
                self.tipo = "Verifique su sintaxis"
                f = False
            elif estado != 0:
                self.error = "Expresión incompleta."
                self.tipo = "Verifique su sintaxis"
                f = False

        if self.error == "":
            self.error = "Ha ocurrido un error mientras se generaba la relación."
            self.tipo = "Error de sintaxis"

        if f:
            self.length = len(key_nodos)
            self.verificarPropiedades()
            if digito == 'd':
                for c in range(ord(numeros), ord('9') + 1):
                    indices.append(chr(c))
            else:
                for c in range(ord(letras), ord('i') + 1):
                    indices.append(chr(c))
            self.nodos = indices

        return f

    def verificarPropiedades(self):
        self.Funcion()
        self.Suprayectiva()
        self.Inyectiva()
        self.Reflexiva()
        self.Irreflexiva()
        self.Simetrica()
        self.Asimetrica()
        self.Antisimetrica()
        self.Transitiva()
        self.Equivalencia()
        self.Particion()
        self.Biyectiva()
        self.Invertible()

    def Inyectiva(self):
        cont1, cont2, unos, colum = 0, 0, 0, 0
        while cont1 < self.length:
            cont2 = 0
            unos = 0
            while cont2 < self.length:
                if self.matriz[cont1][cont2]:
                    unos += 1
                cont2 += 1
            if unos < 2:
                colum += 1
            cont1 += 1
        self.inyectiva = (colum == self.length and self.funcion)

    def Funcion(self):
        cont1, cont2, unos, filas = 0, 0, 0, 0
        while cont2 < self.length:
            cont1 = 0
            unos = 0
            while cont1 < self.length:
                if self.matriz[cont1][cont2]:
                    unos += 1
                cont1 += 1
            if unos == 1:
                filas += 1
            cont2 += 1
        self.funcion = (filas == self.length)

    def Biyectiva(self):
        self.biyectiva = (self.inyectiva and self.suprayectiva)

    def Invertible(self):
        self.invertible = self.biyectiva

    def Reflexiva(self):
        ind = 0
        while ind < self.length and self.matriz[ind][ind]:
            ind += 1
        self.reflexiva = (ind == self.length)

    def Suprayectiva(self):
        cont1, cont2, unos, colum = 0, 0, 0, 0
        while cont1 < self.length:
            cont2 = 0
            unos = 0
            while cont2 < self.length:
                if self.matriz[cont1][cont2]:
                    unos += 1
                cont2 += 1
            if unos >= 1:
                colum += 1
            cont1 += 1
        self.suprayectiva = (colum == self.length and self.funcion)

    def Irreflexiva(self):
        ind = 0
        while ind < self.length and not self.matriz[ind][ind]:
            ind += 1
        self.irreflexiva = (ind == self.length)

    def Simetrica(self):
        self.simetrica = True
        for x in range(self.length):
            for y in range(self.length):
                if self.matriz[x][y] != self.matriz[y][x]:
                    self.simetrica = False
                    return

    def Asimetrica(self):
        self.asimetrica = True
        for i in range(self.length):
            for j in range(self.length):
                if self.matriz[i][j] and self.matriz[j][i]:
                    self.asimetrica = False
                    return

    def Antisimetrica(self):
        self.antisimetrica = True
        for i in range(self.length):
            for j in range(self.length):
                if i != j and self.matriz[i][j] and self.matriz[j][i]:
                    self.antisimetrica = False
                    return

    def Transitiva(self):
        self.transitiva = True
        for x in range(self.length):
            for y in range(self.length):
                if self.matriz[x][y]:
                    for z in range(self.length):
                        if self.matriz[y][z] and not self.matriz[x][z]:
                            self.transitiva = False
                            return

    def Equivalencia(self):
        self.equivalencia = self.transitiva and self.reflexiva and self.simetrica

    def Particion(self):
        if self.simetrica and self.reflexiva and self.transitiva:
            listaclases = []
            for x in range(self.length):
                listafilas = []
                for y in range(self.length):
                    if self.matriz[x][y]:
                        listafilas.append(str(y + 1))
                conca = "".join(listafilas)
                if conca not in listaclases:
                    listaclases.append(conca)

            if len(listaclases) < 2:
                self.particion = False
                return

            self.particion = True
            for r in range(1, self.length + 1):
                cuantos = 0
                for clase in listaclases:
                    if str(r) in clase:
                        cuantos += 1
                if cuantos > 1:
                    self.particion = False
                    return
        else:
            self.particion = False

    def aplicarHasse(self):
        if not self.transitiva or not self.antisimetrica or not self.reflexiva:
            return False

        for j in range(self.length):
            self.matriz[j][j] = False

        for i in range(self.length):
            for k in range(self.length):
                if i != k and self.matriz[i][k] and self.matriz[k][i]:
                    self.matriz[i][k] = False

        for x in range(self.length):
            for y in range(self.length):
                if self.matriz[x][y]:
                    for z in range(self.length):
                        if self.matriz[y][z] and self.matriz[x][z]:
                            self.matriz[x][z] = False

        self.verificarPropiedades()
        self.generarPares()
        return True

    def aplicarCerraduraTransitiva(self):
        for k in range(self.length):
            for i in range(self.length):
                for j in range(self.length):
                    if self.matriz[i][k] and self.matriz[k][j]:
                        self.matriz[i][j] = True
        self.verificarPropiedades()
        self.generarPares()

    def aplicarCerraduraReflexiva(self):
        for i in range(self.length):
            self.matriz[i][i] = True
        self.verificarPropiedades()
        self.generarPares()

    def aplicarCerraduraSimetrica(self):
        for i in range(self.length):
            for j in range(self.length):
                if self.matriz[i][j]:
                    self.matriz[j][i] = True
        self.verificarPropiedades()
        self.generarPares()

    def aplicarDominio(self):
        self.dominio = []
        for col in range(self.length):
            for fila in range(self.length):
                if self.matriz[fila][col]:
                    self.dominio.append(self.nodos[col])
                    break
        return self.dominio

    def aplicarCodominio(self):
        self.codominio = []
        for fila in range(self.length):
            for col in range(self.length):
                if self.matriz[fila][col]:
                    self.codominio.append(self.nodos[fila])
                    break
        return self.codominio

    def __str__(self):
        return self.pares_ordenados
    
    def toString(self):
        return self.__str__()

    def verEquivalencia(self):
        if self.equivalencia:
            clases = []
            for x in range(self.length):
                elementos_relacionados = []
                for y in range(self.length):
                    if self.matriz[x][y]:
                        elementos_relacionados.append(str(self.nodos[y]))
                clase_str = f"[{self.nodos[x]}] = {{{', '.join(elementos_relacionados)}}}"
                if clase_str not in clases:
                    clases.append(clase_str)
            return "\n".join(clases)
        else:
            return None

    def verParticion(self):
        if self.particion:
            clases = []
            for x in range(self.length):
                elementos_relacionados = []
                for y in range(self.length):
                    if self.matriz[x][y]:
                        elementos_relacionados.append(str(self.nodos[y]))
                clase_str = f"{{{', '.join(elementos_relacionados)}}}"
                if clase_str not in clases:
                    clases.append(clase_str)
            return "\n".join(clases)
        else:
            return None