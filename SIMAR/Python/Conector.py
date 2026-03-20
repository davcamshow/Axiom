# Conector.py

import tkinter as tk

class Conector:
    def __init__(self, xi, yi, xc, yc, xf, yf, origen, destino):
        self.xi = xi
        self.yi = yi
        self.xc = xc
        self.yc = yc
        self.xf = xf
        self.yf = yf
        self.origen = origen
        self.destino = destino
        self.selected = False
        self.linea_id = None
        self.circulo_id = None

    def dibujarConectores(self, canvas):
        if self.linea_id:
            canvas.delete(self.linea_id)
        if self.circulo_id:
            canvas.delete(self.circulo_id)

        if self.selected:
            color_linea = "red"
            color_circulo = "black"
        else:
            color_linea = "black"
            color_circulo = "red"

        self.linea_id = canvas.create_line(self.xi, self.yi, self.xc, self.yc, self.xf, self.yf,
                                           smooth=1, width=2, fill=color_linea)
        self.circulo_id = canvas.create_oval(self.xi-3, self.yi-3, self.xi+3, self.yi+3,
                                              fill=color_circulo, outline="")

    def setCurve(self, otro_conector):
        self.xi = otro_conector.xi
        self.yi = otro_conector.yi
        self.xc = otro_conector.xc
        self.yc = otro_conector.yc
        self.xf = otro_conector.xf
        self.yf = otro_conector.yf

    def getOrigen(self):
        return self.origen

    def getDestino(self):
        return self.destino

    def setOrigen(self, c):
        self.origen = c

    def setDestino(self, c):
        self.destino = c

    def setSelected(self, b):
        self.selected = b

    def isSelected(self):
        return self.selected

    def intersects(self, x, y, margen=6):
        return False