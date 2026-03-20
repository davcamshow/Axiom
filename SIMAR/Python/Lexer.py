# Lexer.py

import re

class Lexer:
    def __init__(self):
        self.li_exprReg = []
        self.li_nombre = []
        self.li_tipo = []
        self.li_Tokens = []
        self.li_PalabraReservada = []
        self.numero_Linea = 1
        self.texto = ""

        self.ST_EXPR = [
            r"^\n",
            r"^\s+",
            r"^[a-zA-Z]+_?[a-zA-Z0-9]*",
            r"^\d+\.\d*",
            r"^\d+",
            r"^[.]|^[+]|^-|^[*]|^[;]|^[=][=]|^[<]|^[>]|^[=]|^[!][=]|^[!]|^[#]|^[\"]|^[\\/]",
            r"^[(]",
            r"^[)]",
            r"^[{]",
            r"^[}]"
        ]
        self.ST_NOMBRE = [
            "Salto de linea",
            "ws",
            "identificador",
            "flotante",
            "entero",
            "operador",
            "op_parentAbre",
            "op_parentCierra",
            "op_llaveAbre",
            "op_llaveCierra"
        ]

    def cargarDefault(self):
        for i, expr in enumerate(self.ST_EXPR):
            self.li_exprReg.append(expr)
            self.li_nombre.append(self.ST_NOMBRE[i])
            self.li_tipo.append(i)

    def cargarArchivo(self, archivo):
        self.texto = archivo

    def analizarArchivo(self):
        self.preparar()
        elementos = len(self.li_exprReg)
        caracteres = len(self.texto)
        isOk = True
        texto_temp = self.texto

        while caracteres > 0 and isOk:
            encontrado = False
            for i in range(elementos):
                patron = self.li_exprReg[i]
                tipo = i
                match = re.match(patron, texto_temp)
                if match:
                    encontrado = True
                    indice = match.end()
                    stg = texto_temp[:indice]
                    texto_temp = texto_temp[indice:]
                    caracteres = len(texto_temp)
                    nombre = self.li_nombre[i]

                    if tipo == 0:
                        self.numero_Linea += 1
                        break
                    elif not stg.isspace() or stg == ' ':
                        if not (len(stg) == 1 and stg.isspace()):
                            self.li_Tokens.append(Token(nombre, stg, tipo))
                        break
                    else:
                        break
            if not encontrado:
                isOk = False

        self.texto = texto_temp
        return isOk

    def listarTokens(self):
        lista = ""
        for token in self.li_Tokens:
            lista += f"{token.tipo}\t{token.nombre}\t{token.tk}\n"
        return lista

    def agregarExprReg(self, regExpr, nombre):
        self.li_exprReg.append(regExpr)
        self.li_nombre.append(nombre)
        tipo = len(self.li_nombre) - 1
        self.li_tipo.append(tipo)

    def agregarPalabraReservada(self, st):
        match = re.match(self.ST_EXPR[2], st)
        if match and match.group() == st:
            if not self.esPalabraReservada(st):
                self.li_PalabraReservada.append(st)
                return True
        return False

    def Linea(self):
        return self.numero_Linea

    def esPalabraReservada(self, st):
        return st in self.li_PalabraReservada

    def preparar(self):
        self.numero_Linea = 0
        self.li_Tokens = []

    def getList(self):
        return self.li_Tokens

from Token import Token