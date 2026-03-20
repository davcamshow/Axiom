# GNodo.py

import tkinter as tk

class GNodo:
    def __init__(self, x1, y1, id_nodo, seleccionado=False):
        self.x = x1
        self.y = y1
        self.ancho = 35
        self.id_nodo = id_nodo
        self.seleccionado = seleccionado
        self.circulo = None
        self.texto = None

    def getPoint(self):
        return (self.x, self.y)

    def setSelected(self, b):
        self.seleccionado = b

    def dibujar(self, canvas):
        if self.circulo:
            canvas.delete(self.circulo)
        if self.texto:
            canvas.delete(self.texto)

        color_borde = "red" if self.seleccionado else "blue"
        self.circulo = canvas.create_oval(self.x, self.y, self.x + self.ancho, self.y + self.ancho,
                                           outline=color_borde, width=3, fill="yellow")
        self.texto = canvas.create_text(self.x + self.ancho//2, self.y + self.ancho//2,
                                         text=self.id_nodo, font=("Arial", 12, "bold"))

    def dibujarUnselectedNodo(self, canvas):
        self.seleccionado = False
        self.dibujar(canvas)

    def dibujarSelectedNodo(self, canvas):
        self.seleccionado = True
        self.dibujar(canvas)

    def getId(self):
        return self.id_nodo

    def getCenter(self):
        return (self.x + self.ancho//2, self.y + self.ancho//2)

    def getMinX(self):
        return self.x

    def getMinY(self):
        return self.y

    def getMaxX(self):
        return self.x + self.ancho

    def getMaxY(self):
        return self.y + self.ancho

    def set(self, x, y):
        self.x = x
        self.y = y

    def isSelected(self):
        return self.seleccionado

    def contains(self, xi, yi):
        cx = self.x + self.ancho//2
        cy = self.y + self.ancho//2
        radio = self.ancho//2
        return ((xi - cx)**2 + (yi - cy)**2) <= radio**2

    def setId(self, c):
        self.id_nodo = c