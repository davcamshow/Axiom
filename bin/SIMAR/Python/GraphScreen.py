# GraphScreen.py

import tkinter as tk
from tkinter import messagebox, Menu
import math
from GNodo import GNodo
from Conector import Conector
from Relacion import Relacion

class GraphScreen(tk.Canvas):
    NULL = 0
    NODE = 1
    CONECT = 2
    SELECT = 3
    SELECTED = 4
    DELETE = 5
    ARC = 6
    CONECTING = 7

    def __init__(self, parent, relacion_inicial=None):
        super().__init__(parent, width=550, height=300, bg="white", highlightthickness=0)
        self.parent = parent

        self.matriz = [[False for _ in range(9)] for _ in range(9)]
        self.nodos = [None] * 9
        self.lazos = []
        self.totalNodos = 0
        self.nodoActual = -1
        self.nombres = ['a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i']
        self.imgnombres = [False] * 9
        self.key = 0
        self.relacion = None
        self.nuevo = False
        self.operacion = self.SELECT
        self.xi = self.yi = self.xf = self.yf = 0
        self.prevx = self.prevy = 0
        self.origen = -1
        self.destino = -1
        self.enabled = False
        self.FACTOR = 0.35

        self.bind("<Button-1>", self.mouse_pressed)
        self.bind("<B1-Motion>", self.mouse_dragged)
        self.bind("<ButtonRelease-1>", self.mouse_released)
        self.bind("<Button-3>", self.mouse_clicked)
        self.bind("<Enter>", self.mouse_entered)
        self.bind("<Key>", self.key_released)
        self.focus_set()

        if relacion_inicial:
            self.setRelacion(relacion_inicial)
        else:
            nodos_vacios = [str(c) for c in self.nombres]
            matriz_vacia = [[False for _ in range(9)] for _ in range(9)]
            self.relacion = Relacion(matriz_vacia, nodos_vacios, 9)
            self.setRelacion(self.relacion)

    def setEnabled(self, b):
        self.enabled = b
        if b:
            self.config(bg="white")
            self.operacion = self.SELECT
            self.dibujarGrafo()
        else:
            self.config(bg="light gray")
            self.operacion = self.NULL
            self.nodoActual = -1
            if self.nodos[0] is not None:
                for nodo in self.nodos:
                    if nodo:
                        nodo.setSelected(False)
                self.borraSeleccionConectores()
                self.dibujarGrafo()

    def mouse_clicked(self, event):
        if not self.enabled:
            return
        if event.num == 3:
            self.operacion = self.SELECT
            isns = (self.nodoActual > -1)
            isls = -1
            if not isns:
                for i, con in enumerate(self.lazos):
                    if con.isSelected():
                        isls = i
                        break

            popup = Menu(self, tearoff=0)
            eliminate = "Eliminar Nodo" if isns else "Eliminar lazo"
            popup.add_command(label=eliminate,
                              command=lambda: self.borrarNodo() if isns else self.borrarConector())
            popup.add_command(label="Cambiar Números/Letras", command=self.cambiarNombres)
            if isns or isls > -1:
                popup.add_command(label="Borrar asociaciones", command=self.borrarAsociaciones)

            popup.post(event.x_root, event.y_root)

    def mouse_entered(self, event):
        if self.enabled:
            self.focus_set()

    def mouse_pressed(self, event):
        if not self.enabled:
            return
        self.xf = self.xi = event.x
        self.yf = self.yi = event.y

        if self.operacion == self.NODE:
            self.nuevoNodo()
        elif self.operacion == self.SELECT:
            self.comprobarSeleccionNodos()
            if self.nodoActual < 0:
                self.comprobarSeleccionConectores(event.x, event.y)
            self.dibujarGrafo()
        elif self.operacion == self.CONECT:
            self.borraSeleccionConectores()
            self.origen = self.esPosicionValida(event.x, event.y)
            if self.origen > -1:
                self.operacion = self.CONECTING

    def mouse_dragged(self, event):
        self.xf = event.x
        self.yf = event.y

        if self.operacion == self.SELECTED:
            if self.nodoActual > -1:
                self.actualizarPosNodo()
                self.dibujarGrafo()
                self.actualizarPosConectores()
        elif self.operacion == self.CONECTING:
            self.dibujarLineaTemporal()

    def mouse_released(self, event):
        if not self.enabled:
            return
        self.xf = event.x
        self.yf = event.y

        if self.operacion == self.CONECTING:
            self.destino = self.esPosicionValida(self.xf, self.yf)
            if self.destino > -1:
                self.creaConexion()
            else:
                self.destino = -1
                self.origen = -1
            self.operacion = self.CONECT
            self.dibujarGrafo()
        elif self.nodoActual > -1:
            if self.xf > 535 or self.yf > 285 or self.xf < 0 or self.yf < 0:
                messagebox.showerror("Error de dibujo", "Imposible dibujar en esa región")
                if self.nuevo:
                    self.borrarNodo()
                    self.nuevo = False
                    self.operacion = self.SELECT
                else:
                    self.cancelaPosicionamiento()
                    self.actualizarPosConectores()
                    self.dibujarGrafo()
            elif self.traslape():
                messagebox.showerror("Error de dibujo", "Evitar traslapes entre nodos")
                if self.nuevo:
                    self.borrarNodo()
                    self.nuevo = False
                    self.operacion = self.SELECT
                else:
                    self.cancelaPosicionamiento()
                    self.actualizarPosConectores()
                    self.dibujarGrafo()
            else:
                self.nuevo = False
                self.operacion = self.SELECT
                self.actualizarPosConectores()
                self.dibujarGrafo()

    def key_released(self, event):
        if self.nodoActual > -1 and event.keysym == "Delete":
            self.borrarNodo()
            self.operacion = self.SELECT
        elif event.keysym == "Delete":
            self.borrarConector()
            self.dibujarGrafo()

    def dibujarGrafo(self):
        self.delete("all")
        self.dibujarConexiones()
        self.dibujarNodos()

    def dibujarNodos(self):
        for nodo in self.nodos:
            if nodo is not None:
                nodo.dibujar(self)

    def dibujarConexiones(self):
        for lazo in self.lazos:
            lazo.dibujarConectores(self)

    def dibujarLineaTemporal(self):
        self.dibujarGrafo()
        self.create_line(self.xi, self.yi, self.xf, self.yf, fill="black", width=2)

    def nuevoNodo(self):
        self.nuevo = True
        if self.totalNodos < 9:
            self.operacion = self.SELECTED
            self.nodoActual = self.totalNodos
            self.totalNodos += 1
            nodo = GNodo(self.xi, self.yi, self.nombres[self.key], True)
            self.nodos[self.nodoActual] = nodo
            self.recalcularKey()
            self.dibujarGrafo()
        else:
            messagebox.showerror("Desbordamiento", "Máximo 9 nodos permitidos")
            self.operacion = self.SELECT

    def borrarNodo(self):
        if self.totalNodos == 1:
            messagebox.showerror("Error de inconsistencia", "Imposible eliminar el último nodo de la relación")
            return
        self.borrarConectoresAsociados()
        self.recuperarKey(self.nodos[self.nodoActual].getId())
        self.nodos[self.nodoActual] = None
        for i in range(self.nodoActual, len(self.nodos)-1):
            self.nodos[i] = self.nodos[i+1]
        self.nodos[len(self.nodos)-1] = None
        self.renombrarNodos()
        self.nodoActual = -1
        self.totalNodos -= 1
        self.dibujarGrafo()

    def borrarConectoresAsociados(self):
        if self.nodoActual < 0:
            return
        ch = self.nodos[self.nodoActual].getId()
        i = 0
        while i < len(self.lazos):
            c = self.lazos[i]
            if c.getOrigen() == ch or c.getDestino() == ch:
                ori_idx = -1
                des_idx = -1
                for j, nodo in enumerate(self.nodos):
                    if nodo and nodo.getId() == c.getOrigen():
                        ori_idx = j
                    if nodo and nodo.getId() == c.getDestino():
                        des_idx = j
                if ori_idx != -1 and des_idx != -1:
                    self.matriz[ori_idx][des_idx] = False
                self.lazos.pop(i)
            else:
                i += 1

    def borrarConector(self):
        i = 0
        while i < len(self.lazos):
            c = self.lazos[i]
            if c.isSelected():
                ori_char = c.getOrigen()
                des_char = c.getDestino()
                ori_idx = -1
                des_idx = -1
                for j, nombre in enumerate(self.nombres):
                    if nombre == ori_char:
                        ori_idx = j
                    if nombre == des_char:
                        des_idx = j
                if ori_idx != -1 and des_idx != -1:
                    self.matriz[ori_idx][des_idx] = False
                self.lazos.pop(i)
            else:
                i += 1
        self.operacion = self.SELECT

    def borrarAsociaciones(self):
        if self.nodoActual < 0:
            return
        ch = self.nodos[self.nodoActual].getId()
        i = 0
        while i < len(self.lazos):
            c = self.lazos[i]
            if c.getOrigen() == ch or c.getDestino() == ch:
                ori_idx = -1
                des_idx = -1
                for j, nodo in enumerate(self.nodos):
                    if nodo and nodo.getId() == c.getOrigen():
                        ori_idx = j
                    if nodo and nodo.getId() == c.getDestino():
                        des_idx = j
                if ori_idx != -1 and des_idx != -1:
                    self.matriz[ori_idx][des_idx] = False
                self.lazos.pop(i)
            else:
                i += 1
        self.dibujarGrafo()

    def borraSeleccionConectores(self):
        for c in self.lazos:
            c.setSelected(False)
        self.dibujarGrafo()

    def cancelaPosicionamiento(self):
        self.nuevo = False
        self.operacion = self.SELECT
        if self.nodoActual != -1:
            self.nodos[self.nodoActual].set(self.prevx, self.prevy)
        self.dibujarGrafo()

    def actualizarPosNodo(self):
        if self.nodoActual != -1:
            self.nodos[self.nodoActual].set(self.xf, self.yf)

    def actualizarPosConectores(self):
        if self.nodoActual == -1:
            return
        nodo_movido_id = self.nodos[self.nodoActual].getId()
        for c in self.lazos:
            if c.getOrigen() == nodo_movido_id or c.getDestino() == nodo_movido_id:
                ori_idx = -1
                des_idx = -1
                for j, nodo in enumerate(self.nodos):
                    if nodo and nodo.getId() == c.getOrigen():
                        ori_idx = j
                    if nodo and nodo.getId() == c.getDestino():
                        des_idx = j
                if ori_idx != -1 and des_idx != -1:
                    if ori_idx == des_idx:
                        xn = self.nodos[ori_idx].getMinX() + 10
                        yn = self.nodos[ori_idx].getMinY()
                        xn2 = xn + 15
                        pcx = xn + 5
                        pcy = yn - 35
                        c.xi = xn
                        c.yi = yn
                        c.xc = pcx
                        c.yc = pcy
                        c.xf = xn2
                        c.yf = yn
                    else:
                        ori_center = self.nodos[ori_idx].getCenter()
                        des_center = self.nodos[des_idx].getCenter()
                        if ori_center[0] == des_center[0]:
                            ori_center = (ori_center[0] + 1, ori_center[1])

                        nori = self.resolverEcuacion(ori_center[0], ori_center[1],
                                                      des_center[0], des_center[1], True)
                        ndes = self.resolverEcuacion(des_center[0], des_center[1],
                                                      ori_center[0], ori_center[1], False)

                        xc = (nori[0] + ndes[0]) // 2
                        yc = (nori[1] + ndes[1]) // 2

                        dx = abs(nori[0] - ndes[0])
                        dy = abs(nori[1] - ndes[1])
                        incx = int(dx * self.FACTOR)
                        incy = int(dy * self.FACTOR)

                        if ori_idx > des_idx:
                            pcx = xc + incy
                            pcy = yc + incx
                        else:
                            pcx = xc - incy
                            pcy = yc - incx

                        c.xi = nori[0]
                        c.yi = nori[1]
                        c.xc = pcx
                        c.yc = pcy
                        c.xf = ndes[0]
                        c.yf = ndes[1]

    def creaConexion(self):
        if self.matriz[self.origen][self.destino]:
            messagebox.showinfo("Información redundante", "La relación ya existe")
        else:
            self.matriz[self.origen][self.destino] = True
            if self.origen == self.destino:
                xn = self.nodos[self.origen].getMinX() + 10
                yn = self.nodos[self.origen].getMinY()
                xn2 = xn + 15
                pcx = xn + 5
                pcy = yn - 35
                c = Conector(xn, yn, pcx, pcy, xn2, yn,
                             self.nodos[self.origen].getId(),
                             self.nodos[self.destino].getId())
                self.lazos.append(c)
            else:
                o_char = self.nodos[self.origen].getId()
                d_char = self.nodos[self.destino].getId()
                ori_center = self.nodos[self.origen].getCenter()
                des_center = self.nodos[self.destino].getCenter()
                if ori_center[0] == des_center[0]:
                    ori_center = (ori_center[0] + 1, ori_center[1])

                nori = self.resolverEcuacion(ori_center[0], ori_center[1],
                                              des_center[0], des_center[1], True)
                ndes = self.resolverEcuacion(des_center[0], des_center[1],
                                              ori_center[0], ori_center[1], False)

                xc = (nori[0] + ndes[0]) // 2
                yc = (nori[1] + ndes[1]) // 2
                dx = abs(nori[0] - ndes[0])
                dy = abs(nori[1] - ndes[1])
                incx = int(dx * self.FACTOR)
                incy = int(dy * self.FACTOR)

                if o_char > d_char:
                    pcx = xc + incy
                    pcy = yc + incx
                else:
                    pcx = xc - incy
                    pcy = yc - incx

                c = Conector(nori[0], nori[1], pcx, pcy, ndes[0], ndes[1], o_char, d_char)
                self.lazos.append(c)

        self.destino = -1
        self.origen = -1
        self.dibujarGrafo()

    def comprobarSeleccionNodos(self):
        self.nodoActual = -1
        for i, nodo in enumerate(self.nodos):
            if nodo and nodo.contains(self.xi, self.yi):
                self.nodoActual = i
                nodo.setSelected(True)
                self.operacion = self.SELECTED
                self.prevx, self.prevy = nodo.getPoint()
                self.seleccionaConectores()
                break

    def comprobarSeleccionConectores(self, x, y):
        for c in self.lazos:
            d1 = math.hypot(c.xi - x, c.yi - y)
            d2 = math.hypot(c.xf - x, c.yf - y)
            d3 = math.hypot(c.xc - x, c.yc - y)
            if d1 < 6 or d2 < 6 or d3 < 6:
                c.setSelected(True)
                for otro in self.lazos:
                    if otro != c:
                        otro.setSelected(False)
                return
        for c in self.lazos:
            c.setSelected(False)

    def seleccionaConectores(self):
        if self.nodoActual == -1:
            return
        n_char = self.nodos[self.nodoActual].getId()
        for c in self.lazos:
            if c.getOrigen() == n_char or c.getDestino() == n_char:
                c.setSelected(True)

    def esPosicionValida(self, x, y):
        for i, nodo in enumerate(self.nodos):
            if nodo and nodo.contains(x, y):
                return i
        return -1

    def traslape(self):
        if self.totalNodos == 0:
            return False
        for i, nodo in enumerate(self.nodos):
            if nodo and i != self.nodoActual:
                px, py = nodo.getPoint()
                if abs(px - self.xf) < 45 and abs(py - self.yf) < 45:
                    return True
        return False

    def cambiarNombres(self):
        if self.totalNodos == 0:
            return
        primero = self.nodos[0].getId()
        if primero.isalpha():
            resta = ord('0')
            for nodo in self.nodos:
                if nodo:
                    nodo.setId(chr(ord(nodo.getId()) - resta))
            for con in self.lazos:
                con.setOrigen(chr(ord(con.getOrigen()) - resta))
                con.setDestino(chr(ord(con.getDestino()) - resta))
            for i in range(len(self.nombres)):
                self.nombres[i] = chr(ord(self.nombres[i]) - resta)
        else:
            adicion = ord('0')
            for nodo in self.nodos:
                if nodo:
                    nodo.setId(chr(ord(nodo.getId()) + adicion))
            for con in self.lazos:
                con.setOrigen(chr(ord(con.getOrigen()) + adicion))
                con.setDestino(chr(ord(con.getDestino()) + adicion))
            for i in range(len(self.nombres)):
                self.nombres[i] = chr(ord(self.nombres[i]) + adicion)
        self.dibujarGrafo()

    def recalcularKey(self):
        self.imgnombres[self.key] = True
        for i, usado in enumerate(self.imgnombres):
            if not usado:
                self.key = i
                return

    def recuperarKey(self, c):
        self.imgnombres = [False] * 9
        for i, nodo in enumerate(self.nodos):
            if nodo and i < self.totalNodos:
                self.imgnombres[i] = True
        self.key = self.totalNodos - 1

    def renombrarNodos(self):
        for i in range(self.nodoActual, len(self.nodos)):
            if self.nodos[i] is None:
                break
            nuevo_id = chr(ord(self.nodos[i].getId()) - 1)
            self.nodos[i].setId(nuevo_id)

        if self.nodoActual != -1 and self.nodos[self.nodoActual] is not None:
            c = self.nodos[self.nodoActual].getId()
            c = chr(ord(c) + self.nodoActual)
            for con in self.lazos:
                if con.getDestino() > c:
                    con.setDestino(chr(ord(con.getDestino()) - 1))
                if con.getOrigen() > c:
                    con.setOrigen(chr(ord(con.getOrigen()) - 1))

    def cambiaOperacion(self, op):
        self.operacion = op
        if self.nodoActual > -1:
            if op == self.DELETE:
                self.borrarNodo()
                self.operacion = self.SELECT
            else:
                self.nodos[self.nodoActual].setSelected(False)
                self.nodoActual = -1
        elif op == self.DELETE:
            self.borrarConector()
        self.borraSeleccionConectores()
        self.dibujarGrafo()

    def getRelacion(self):
        nodos_lista = []
        for i in range(self.totalNodos):
            nodos_lista.append(str(self.nodos[i].getId()))
        for i in range(len(nodos_lista), 9):
            nodos_lista.append(self.nombres[i])

        matriz_pequena = [[False for _ in range(self.totalNodos)] for _ in range(self.totalNodos)]
        for i in range(self.totalNodos):
            for j in range(self.totalNodos):
                matriz_pequena[i][j] = self.matriz[i][j]

        rel = Relacion(matriz_pequena, nodos_lista[:self.totalNodos], self.totalNodos)
        self.relacion = rel
        return rel

    def setRelacion(self, r):
        self.relacion = r
        self.totalNodos = r.size()
        self.nodos = [None] * 9
        self.matriz = [[False for _ in range(9)] for _ in range(9)]
        self.lazos = []
        self.nombres = [str(n) for n in r.getNodos()] + [chr(ord('a')+i) for i in range(len(r.getNodos()), 9)]

        self.automatizarDibujado()

    def automatizarDibujado(self):
        total = self.totalNodos
        if total == 1:
            self.dibujarUno()
        elif total == 2:
            self.dibujarDos()
        elif total == 3:
            self.dibujarTres()
        elif total == 4:
            self.dibujarCuatro()
        elif total == 5:
            self.dibujarCinco()
        elif total == 6:
            self.dibujarSeis()
        elif total == 7:
            self.dibujarSiete()
        elif total == 8:
            self.dibujarOcho()
        elif total == 9:
            self.dibujarNueve()
        self.crearConectoresDesdeMatriz()
        self.dibujarGrafo()

    def crearConectoresDesdeMatriz(self):
        self.lazos = []
        for i in range(self.totalNodos):
            for j in range(self.totalNodos):
                if self.matriz[i][j]:
                    if i == j:
                        xn = self.nodos[i].getMinX() + 10
                        yn = self.nodos[i].getMinY()
                        xn2 = xn + 15
                        pcx = xn + 5
                        pcy = yn - 35
                        c = Conector(xn, yn, pcx, pcy, xn2, yn,
                                     self.nodos[i].getId(), self.nodos[i].getId())
                        c.setSelected(False)
                        self.lazos.append(c)
                    else:
                        o_char = self.nodos[j].getId()
                        d_char = self.nodos[i].getId()
                        ori_center = self.nodos[j].getCenter()
                        des_center = self.nodos[i].getCenter()
                        if ori_center[0] == des_center[0]:
                            ori_center = (ori_center[0] + 1, ori_center[1])

                        nori = self.resolverEcuacion(ori_center[0], ori_center[1],
                                                      des_center[0], des_center[1], True)
                        ndes = self.resolverEcuacion(des_center[0], des_center[1],
                                                      ori_center[0], ori_center[1], False)

                        xc = (nori[0] + ndes[0]) // 2
                        yc = (nori[1] + ndes[1]) // 2
                        dx = abs(nori[0] - ndes[0])
                        dy = abs(nori[1] - ndes[1])
                        incx = int(dx * self.FACTOR)
                        incy = int(dy * self.FACTOR)

                        if o_char > d_char:
                            pcx = xc + incy
                            pcy = yc + incx
                        else:
                            pcx = xc - incy
                            pcy = yc - incx

                        c = Conector(nori[0], nori[1], pcx, pcy, ndes[0], ndes[1], o_char, d_char)
                        c.setSelected(False)
                        self.lazos.append(c)

    def dibujarNueve(self):
        xcoords = [120, 210, 300, 390, 80, 430, 160, 250, 340]
        ycoords = [50, 50, 50, 50, 130, 130, 220, 220, 220]
        for i in range(9):
            self.nodos[i] = GNodo(xcoords[i], ycoords[i], self.nombres[self.key], False)
            self.recalcularKey()

    def dibujarOcho(self):
        xcoords = [120, 210, 300, 390, 120, 210, 300, 390]
        ycoords = [65, 65, 65, 65, 200, 200, 200, 200]
        for i in range(8):
            self.nodos[i] = GNodo(xcoords[i], ycoords[i], self.nombres[self.key], False)
            self.recalcularKey()

    def dibujarSiete(self):
        xcoords = [120, 210, 300, 390, 160, 250, 340]
        ycoords = [65, 65, 65, 65, 200, 200, 200]
        for i in range(7):
            self.nodos[i] = GNodo(xcoords[i], ycoords[i], self.nombres[self.key], False)
            self.recalcularKey()

    def dibujarSeis(self):
        xcoords = [260, 165, 345, 165, 345, 260]
        ycoords = [30, 90, 90, 180, 180, 250]
        for i in range(6):
            self.nodos[i] = GNodo(xcoords[i], ycoords[i], self.nombres[self.key], False)
            self.recalcularKey()

    def dibujarCinco(self):
        xcoords = [260, 165, 365, 165, 365]
        ycoords = [30, 90, 90, 210, 210]
        for i in range(5):
            self.nodos[i] = GNodo(xcoords[i], ycoords[i], self.nombres[self.key], False)
            self.recalcularKey()

    def dibujarCuatro(self):
        self.nodos[0] = GNodo(175, 60, self.nombres[self.key], False); self.recalcularKey()
        self.nodos[1] = GNodo(355, 60, self.nombres[self.key], False); self.recalcularKey()
        self.nodos[2] = GNodo(175, 190, self.nombres[self.key], False); self.recalcularKey()
        self.nodos[3] = GNodo(355, 190, self.nombres[self.key], False); self.recalcularKey()

    def dibujarTres(self):
        self.nodos[0] = GNodo(195, 50, self.nombres[self.key], False); self.recalcularKey()
        self.nodos[1] = GNodo(355, 50, self.nombres[self.key], False); self.recalcularKey()
        self.nodos[2] = GNodo(270, 220, self.nombres[self.key], False); self.recalcularKey()

    def dibujarDos(self):
        self.nodos[0] = GNodo(175, 130, self.nombres[self.key], False); self.recalcularKey()
        self.nodos[1] = GNodo(355, 130, self.nombres[self.key], False); self.recalcularKey()

    def dibujarUno(self):
        self.nodos[0] = GNodo(260, 130, self.nombres[self.key], False); self.recalcularKey()

    def resolverEcuacion(self, x1, y1, x2, y2, bxf):
        dx = x1 - x2
        dy = y1 - y2
        dist = math.hypot(dx, dy)
        if dist == 0:
            return (x2, y2)
        
        radio = 21
        factor = radio / dist
        
        if bxf:
            nx = x2 + dx * factor
            ny = y2 + dy * factor
        else:
            nx = x1 - dx * factor
            ny = y1 - dy * factor
        
        return (int(nx), int(ny))