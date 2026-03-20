# Acercade.py

import tkinter as tk
from tkinter import scrolledtext

class Acercade(tk.Toplevel):
    def __init__(self, parent):
        super().__init__(parent)
        self.parent = parent
        self.title("Acerca de SIMAR")
        self.geometry("450x460")
        self.resizable(False, False)
        self.transient(parent)
        self.grab_set()

        self.pantalla = 0

        self.infosimar = """
ACERCA DE SIMAR

SIMAR (Sistema para el Manejo de Relaciones) es un sistema pensado para ser utilizado como apoyo para los alumnos de la materia Matemáticas para Computadoras impartida en el Instituto Tecnológico de Morelia (ITM).

SIMAR es capaz de construir una relación matemática a través de la definición de su matriz, de su grafo o de la introducción directa de los pares que la conforman, así como de realizar operaciones entre relaciones, obtener las cerraduras y ver las propiedades de las relaciones resultantes en todo momento.

SIMAR fue desarrollado por alumnos de Ingeniería en Sistemas Computacionales del ITM como proyecto final para la materia de Planificación y Modelado.
"""

        self.infoadrian = """
Víctor Adrián Valle Rivera

Nacido en Chilpancingo GRO en el año de 1987. Participó en la elaboración del proyecto SIMAR fungiendo como programador y diseñador principal.

Es un apasionado de la computación y de las matemáticas. A la fecha, se encuentra estudiando la carrera de Ingeniería en Sistemas Computacionales en el ITM.

Contáctame: extradiable@gmail.com
"""

        self.infomiriam = """
Eréndira Miriam Jiménez Hernández

Mexicana. Originaria de Apizaco, Tlaxcala. Nacida el 29 de Septiembre de 1987.

Durante la creación de SIMAR fungió como Ingeniero de Requerimientos además de programar algunos módulos.

Contáctame: miriam16_eren@hotmail.com
"""

        self.infoantonio = """
Antonio Garcia Gil

Nacido en Morelia, Mich. el 9 de Mayo de 1987.

Participó en la elaboración del proyecto SIMAR fungiendo como programador.

Contáctame: byakugan.hyuga@gmail.com
"""

        self.crearGUI()

    def crearGUI(self):
        self.text_area = scrolledtext.ScrolledText(self, wrap=tk.WORD, width=50, height=20)
        self.text_area.pack(padx=10, pady=10)
        self.text_area.insert(tk.END, self.infosimar)
        self.text_area.config(state="disabled")

        frame_botones = tk.Frame(self)
        frame_botones.pack(pady=5)

        self.btn_anterior = tk.Button(frame_botones, text="<<", command=self.anterior)
        self.btn_anterior.pack(side=tk.LEFT, padx=5)

        self.btn_siguiente = tk.Button(frame_botones, text=">>", command=self.siguiente)
        self.btn_siguiente.pack(side=tk.LEFT, padx=5)

        self.btn_aceptar = tk.Button(frame_botones, text="Aceptar", command=self.destroy)
        self.btn_aceptar.pack(side=tk.LEFT, padx=5)

        self.btn_anterior.config(state="disabled")

    def anterior(self):
        if self.pantalla == 1:
            self.text_area.config(state="normal")
            self.text_area.delete(1.0, tk.END)
            self.text_area.insert(tk.END, self.infosimar)
            self.text_area.config(state="disabled")
            self.pantalla = 0
            self.btn_anterior.config(state="disabled")
        elif self.pantalla == 2:
            self.text_area.config(state="normal")
            self.text_area.delete(1.0, tk.END)
            self.text_area.insert(tk.END, self.infoantonio)
            self.text_area.config(state="disabled")
            self.pantalla = 1
        elif self.pantalla == 3:
            self.text_area.config(state="normal")
            self.text_area.delete(1.0, tk.END)
            self.text_area.insert(tk.END, self.infomiriam)
            self.text_area.config(state="disabled")
            self.pantalla = 2
            self.btn_siguiente.config(state="normal")

    def siguiente(self):
        if self.pantalla == 0:
            self.text_area.config(state="normal")
            self.text_area.delete(1.0, tk.END)
            self.text_area.insert(tk.END, self.infoantonio)
            self.text_area.config(state="disabled")
            self.pantalla = 1
            self.btn_anterior.config(state="normal")
        elif self.pantalla == 1:
            self.text_area.config(state="normal")
            self.text_area.delete(1.0, tk.END)
            self.text_area.insert(tk.END, self.infomiriam)
            self.text_area.config(state="disabled")
            self.pantalla = 2
        elif self.pantalla == 2:
            self.text_area.config(state="normal")
            self.text_area.delete(1.0, tk.END)
            self.text_area.insert(tk.END, self.infoadrian)
            self.text_area.config(state="disabled")
            self.pantalla = 3
            self.btn_siguiente.config(state="disabled")