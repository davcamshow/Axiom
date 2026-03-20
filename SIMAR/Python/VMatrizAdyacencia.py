# VMatrizAdyacencia.py

import tkinter as tk
from tkinter import messagebox
from Relacion import Relacion

class VMatrizAdyacencia(tk.Frame):
    def __init__(self, parent):
        super().__init__(parent, width=370, height=320, bg="light gray")
        self.parent = parent
        self.pack_propagate(False)

        self.CELDAS = 9
        self.ids = ['a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i']
        self.bids = [False] * 9
        self.nombres = []
        self.length = 9
        self.imagen = [[False for _ in range(9)] for _ in range(9)]
        self.relacion = None

        self.jb_nodos = [[None, None] for _ in range(9)]
        self.jb_celdas = [[None for _ in range(9)] for _ in range(9)]
        self.jchb_switches = [None] * 8

        self.crearCeldas()

        titulo = tk.Label(self, text="Matriz de Adyacencia", font=("Arial", 12, "bold"), bg="light gray")
        titulo.place(x=130, y=0)

    def crearCeldas(self):
        fuente = ("Arial", 10, "bold")

        for i in range(9):
            for j in range(9):
                btn = tk.Button(self, text="0", width=2, height=1, font=fuente,
                                bg="white", relief="raised", bd=1,
                                command=lambda f=i, c=j: self.celda_click(f, c))
                btn.place(x=i*30 + 60, y=j*30 + 50)
                self.jb_celdas[i][j] = btn

        for i in range(9):
            self.bids[i] = True
            btn_sup = tk.Button(self, text=self.ids[i], width=2, height=1, font=fuente,
                                bg="#1188FF", fg="white", relief="flat",
                                command=lambda idx=i: self.editar_nombre(idx))
            btn_sup.place(x=i*30 + 60, y=20)
            self.jb_nodos[i][0] = btn_sup

            btn_izq = tk.Button(self, text=self.ids[i], width=2, height=1, font=fuente,
                                bg="#1188FF", fg="white", relief="flat")
            btn_izq.place(x=30, y=i*30 + 50)
            self.jb_nodos[i][1] = btn_izq

        for i in range(8):
            cb = tk.Checkbutton(self, bg="light gray", activebackground="light gray",
                                command=lambda idx=i: self.switch_click(idx))
            cb.place(x=5, y=i*30 + 85)
            cb.select()
            self.jchb_switches[i] = cb

    def celda_click(self, fila, columna):
        btn = self.jb_celdas[fila][columna]
        if btn["text"] == "0":
            btn.config(text="1", font=("Arial", 10, "bold"))
        else:
            btn.config(text="0", font=("Arial", 10))

    def editar_nombre(self, indice):
        dialogo = tk.Toplevel(self)
        dialogo.title("Editar nombre")
        dialogo.geometry("300x150")
        dialogo.transient(self)
        dialogo.grab_set()

        tk.Label(dialogo, text=f"Editar nombre del nodo {self.ids[indice]}:").pack(pady=10)
        entry = tk.Entry(dialogo, width=10)
        entry.pack(pady=5)
        entry.focus()

        def aceptar():
            nuevo = entry.get().strip()
            if nuevo and len(nuevo) == 1:
                if nuevo.isalpha() or nuevo.isdigit():
                    if nuevo.isalpha():
                        self.cambiar_nodos_a_letras()
                    else:
                        self.cambiar_nodos_a_numeros()
                else:
                    messagebox.showerror("Error", "Solo se permiten letras o números")
            dialogo.destroy()

        tk.Button(dialogo, text="Aceptar", command=aceptar).pack(pady=10)

    def cambiar_nodos_a_letras(self):
        letras = ['a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i']
        for i in range(9):
            self.jb_nodos[i][0].config(text=letras[i])
            self.jb_nodos[i][1].config(text=letras[i])

    def cambiar_nodos_a_numeros(self):
        for i in range(9):
            self.jb_nodos[i][0].config(text=str(i+1))
            self.jb_nodos[i][1].config(text=str(i+1))

    def switch_click(self, indice):
        seleccionados = 0
        ultimo_deseleccionado = -1
        for i, cb in enumerate(self.jchb_switches):
            if cb.getvar(cb["variable"]) == 1:
                seleccionados += 1
            else:
                ultimo_deseleccionado = i

        if seleccionados == 7 and ultimo_deseleccionado != -1:
            self.length = ultimo_deseleccionado + 1
            for i in range(9):
                for j in range(9):
                    if i >= self.length or j >= self.length:
                        self.jb_celdas[i][j].config(bg="black", state="disabled")
                    else:
                        self.jb_celdas[i][j].config(bg="white", state="normal")
            for i, cb in enumerate(self.jchb_switches):
                if i != ultimo_deseleccionado:
                    cb.config(state="disabled")
        else:
            self.length = 9
            for i in range(9):
                for j in range(9):
                    self.jb_celdas[i][j].config(bg="white", state="normal")
            for cb in self.jchb_switches:
                cb.config(state="normal")
                cb.select()

    def setRelacion(self, rel):
        self.relacion = rel
        self.limpiar()
        self.imagen = rel.getMatriz()
        self.nombres = rel.getNodos()
        self.length = rel.size()

        for i in range(len(self.nombres)):
            nombre = str(self.nombres[i])
            self.jb_nodos[i][0].config(text=nombre)
            self.jb_nodos[i][1].config(text=nombre)

        for i in range(self.length):
            for j in range(self.length):
                if self.imagen[i][j]:
                    self.jb_celdas[i][j].config(text="1", font=("Arial", 10, "bold"))
                else:
                    self.jb_celdas[i][j].config(text="0", font=("Arial", 10))

        if self.length == 9:
            for cb in self.jchb_switches:
                cb.config(state="normal")
                cb.select()
        else:
            for i, cb in enumerate(self.jchb_switches):
                if i == self.length - 1:
                    cb.config(state="normal")
                    cb.deselect()
                else:
                    cb.config(state="disabled")

    def limpiar(self):
        for i in range(9):
            for j in range(9):
                self.jb_celdas[i][j].config(text="0", font=("Arial", 10),
                                            bg="white", state="normal")
        for i in range(8):
            self.jchb_switches[i].config(state="normal")
            self.jchb_switches[i].select()
        self.length = 9

    def getMatriz(self):
        matriz = [[False for _ in range(self.length)] for _ in range(self.length)]
        for i in range(self.length):
            for j in range(self.length):
                matriz[i][j] = (self.jb_celdas[i][j]["text"] == "1")
        return matriz

    def getNodos(self):
        nodos = []
        for i in range(9):
            nodos.append(self.jb_nodos[i][0]["text"])
        return nodos

    def getSizeRelacion(self):
        return self.length

    def setEnable(self, enabled):
        state = "normal" if enabled else "disabled"
        for i in range(9):
            for j in range(9):
                self.jb_celdas[i][j].config(state=state)
            self.jb_nodos[i][0].config(state=state)
        if enabled:
            if self.length == 9:
                for cb in self.jchb_switches:
                    cb.config(state="normal")
                    cb.select()
            else:
                for i, cb in enumerate(self.jchb_switches):
                    if i == self.length - 1:
                        cb.config(state="normal")
                        cb.deselect()
                    else:
                        cb.config(state="disabled")
        else:
            for cb in self.jchb_switches:
                cb.config(state="disabled")

    def comprobarIntegridad(self):
        for i in range(self.length):
            nombre = self.jb_nodos[i][0]["text"]
            if nombre == "$" or len(nombre) != 1 or not (nombre.isalnum()):
                messagebox.showerror("Inconsistencia",
                                     f"El nombre '{nombre}' no es válido.\nUse una letra o un número.")
                return False
        return True