# MainFrame.py

import tkinter as tk
from tkinter import ttk, messagebox, Menu
import os
from GraphScreen import GraphScreen
from VMatrizAdyacencia import VMatrizAdyacencia
from Relacion import Relacion
from Lexer import Lexer
from ParserC import ParserC
from Acercade import Acercade

class MainFrame(tk.Tk):
    MATRIX = 0
    GRAFO = 1
    PARES = 2

    def __init__(self):
        super().__init__()

        self.title("SIMAR - Sistema para el Manejo de Relaciones")
        self.geometry("960x700")
        self.resizable(False, False)

        self.update_idletasks()
        x = (self.winfo_screenwidth() - self.winfo_width()) // 2
        y = (self.winfo_screenheight() - self.winfo_height()) // 2
        self.geometry(f"+{x}+{y}")

        self.relaciones = []
        self.idrelaciones = []
        self.NOMBRES_RELACIONES = ['R', 'S', 'P', 'Q']
        self.img_nombres_relaciones = [False] * 4
        self.idr = 'R'
        self.relacionActual = 0
        self.lexer = Lexer()

        self.PROPIEDADES = [
            "Es cuando todo elemento de un conjunto A está relacionado consigo mismo...",
            "Es cuando ningún elemento del conjunto A está relacionado consigo mismo...",
            "Se dice que una relación R: A→B es simétrica cuando (a,b) Є R y (b,a) Є R.",
            "Una relación R de A en B es asimétrica si cuando (a,b) Є R entonces (b,a) ∉ R...",
            "Es cuando uno de los pares colocados simétricamente no está en la relación...",
            "Una relación de A en B tiene la propiedad de ser transitiva si cuando aRb y bRc entonces existe un par aRc.",
            "Es aquella que tiene las tres propiedades: reflexiva, simétrica y transitiva",
            "Es un subgrafo completo. Es un conjunto de clases de equivalencia...",
            "Para que una relación f de X a Y sea una función, el dominio de f debe ser igual a X...",
            "Una función f: A→B se llama suprayectiva, si el conjunto de los segundos elementos...",
            "Una función f: A→B se llama inyectiva, si para cada elemento distinto del conjunto A corresponde un elemento distinto del conjunto B.",
            "Cuando una función f es inyectiva y suprayectiva a la vez, se dice que es biyectiva",
            "Una función f: A→B es invertible si una relación inversa fˉ¹ es también una función."
        ]
        self.stetiquetas = [
            "Reflexiva", "Irreflexiva", "Simétrica", "Asimétrica", "Antisimétrica",
            "Transitiva", "Equivalencia", "Partición", "Función", "Función Suprayectiva",
            "Función Inyectiva", "Función Biyectiva", "Función Invertible"
        ]

        self.crearRelacionInicial()

        self.crearMenuBar()
        self.crearMenuRelaciones()
        self.crearBarraHerramientasPropiedades()
        self.crearMenuGrafos()
        self.crearBarraPares()
        self.crearEditor()
        self.crearCalculadora()
        self.crearHojaPropiedades()

        self.grid_columnconfigure(0, weight=1)
        self.grid_rowconfigure(0, weight=0)
        self.grid_rowconfigure(1, weight=0)
        self.grid_rowconfigure(2, weight=0)
        self.grid_rowconfigure(3, weight=0)
        self.grid_rowconfigure(4, weight=1)
        self.grid_rowconfigure(5, weight=0)
        self.grid_rowconfigure(6, weight=0)

        self.menu_relaciones.grid(row=1, column=0, sticky="ew", padx=20, pady=(30, 0))
        self.menu_herramientas_propiedades.grid(row=2, column=0, sticky="ew", padx=20, pady=5)
        self.menu_grafos.grid(row=3, column=0, sticky="ew", padx=20, pady=5)

        self.frame_grafo_matriz = tk.Frame(self)
        self.frame_grafo_matriz.grid(row=4, column=0, sticky="nsew", padx=20, pady=10)
        self.frame_grafo_matriz.grid_columnconfigure(0, weight=1)
        self.frame_grafo_matriz.grid_columnconfigure(1, weight=0)

        self.screen = GraphScreen(self.frame_grafo_matriz)
        self.screen.grid(row=0, column=0, sticky="nw", padx=(0, 20))

        self.tabla = VMatrizAdyacencia(self.frame_grafo_matriz)
        self.tabla.grid(row=0, column=1, sticky="ne")

        self.frame_pares_editor = tk.Frame(self)
        self.frame_pares_editor.grid(row=5, column=0, sticky="ew", padx=20, pady=5)
        self.frame_pares_editor.grid_columnconfigure(0, weight=1)
        self.frame_pares_editor.grid_columnconfigure(1, weight=0)

        self.scrollpares.grid(row=0, column=0, sticky="ew", padx=(0, 10))
        self.scroll.grid(row=0, column=1, sticky="ew")

        self.frame_calc_prop = tk.Frame(self)
        self.frame_calc_prop.grid(row=6, column=0, sticky="ew", padx=20, pady=5)
        self.frame_calc_prop.grid_columnconfigure(0, weight=1)
        self.frame_calc_prop.grid_columnconfigure(1, weight=0)

        self.menu_calculadora.grid(row=0, column=0, sticky="w")
        self.hojaPropiedades.grid(row=0, column=1, sticky="e")

        self.cambiarRelacion()

    def crearRelacionInicial(self):
        nodos = [chr(ord('a') + i) for i in range(9)]
        matriz = [[False for _ in range(9)] for _ in range(9)]
        rel = Relacion(matriz, nodos, 9)
        self.relaciones.append(rel)
        self.img_nombres_relaciones[0] = True
        self.idrelaciones.append('R')

    def crearMenuBar(self):
        menubar = Menu(self)
        self.config(menu=menubar)

        file_menu = Menu(menubar, tearoff=0)
        menubar.add_cascade(label="Archivo", menu=file_menu)
        file_menu.add_command(label="Nuevo", command=self.nuevaRelacion, accelerator="Ctrl+N")
        file_menu.add_command(label="Borrar", command=self.borrarRelacion, accelerator="Ctrl+D")
        file_menu.add_command(label="Generar", command=self.generarRelacion, accelerator="Ctrl+G")

        ver_menu = Menu(menubar, tearoff=0)
        menubar.add_cascade(label="Ver", menu=ver_menu)
        ver_menu.add_command(label="Matriz", command=lambda: self.cambiarVista(self.MATRIX))
        ver_menu.add_command(label="Grafo", command=lambda: self.cambiarVista(self.GRAFO))
        ver_menu.add_command(label="Pares", command=lambda: self.cambiarVista(self.PARES))

        ayuda_menu = Menu(menubar, tearoff=0)
        menubar.add_cascade(label="Ayuda", menu=ayuda_menu)
        ayuda_menu.add_command(label="Acerca de", command=self.acercaDe)
        ayuda_menu.add_command(label="Ayuda", command=self.lanzarAyuda)

        self.bind_all("<Control-n>", lambda e: self.nuevaRelacion())
        self.bind_all("<Control-d>", lambda e: self.borrarRelacion())
        self.bind_all("<Control-g>", lambda e: self.generarRelacion())

    def crearMenuRelaciones(self):
        self.menu_relaciones = tk.Frame(self, bg="light gray", height=40)
        self.menu_relaciones.pack_propagate(False)

        self.jbNuevaRelacion = tk.Button(self.menu_relaciones, text="Nueva Relación",
                                         command=self.nuevaRelacion)
        self.jbNuevaRelacion.place(x=0, y=5)

        self.jbGenerar = tk.Button(self.menu_relaciones, text="Generar",
                                   command=self.generarRelacion)
        self.jbGenerar.place(x=130, y=5)

        self.jbBorrar = tk.Button(self.menu_relaciones, text="Borrar",
                                  command=self.borrarRelacion)
        self.jbBorrar.place(x=480, y=5)

        self.jcbRelaciones = ttk.Combobox(self.menu_relaciones,
                                           values=[f"Relación {r}" for r in self.idrelaciones],
                                           state="readonly", width=15)
        self.jcbRelaciones.place(x=220, y=5)
        self.jcbRelaciones.current(0)
        self.jcbRelaciones.bind("<<ComboboxSelected>>", self.cambiarRelacionEvent)

        self.jcbModoOperacion = ttk.Combobox(self.menu_relaciones,
                                              values=["Matriz", "Grafo", "Pares"],
                                              state="readonly", width=15)
        self.jcbModoOperacion.place(x=350, y=5)
        self.jcbModoOperacion.current(0)
        self.jcbModoOperacion.bind("<<ComboboxSelected>>", self.cambiarVistaEvent)

    def crearBarraHerramientasPropiedades(self):
        self.menu_herramientas_propiedades = tk.Frame(self, bg="light gray", height=40)
        self.menu_herramientas_propiedades.pack_propagate(False)

        self.jcbPropiedades = ttk.Combobox(self.menu_herramientas_propiedades,
                                            values=[
                                                "Cerradura Transitiva",
                                                "Cerradura Reflexiva",
                                                "Cerradura Simétrica",
                                                "Diagrama de Hasse",
                                                "Clases de equivalencia",
                                                "Particiones"
                                            ],
                                            state="readonly", width=25)
        self.jcbPropiedades.place(x=0, y=5)
        self.jcbPropiedades.current(0)

        self.jbTransformacion = tk.Button(self.menu_herramientas_propiedades,
                                          text="Aplicar Transformación",
                                          command=self.aplicarTransformacion)
        self.jbTransformacion.place(x=200, y=5)

        self.jbdominio = tk.Button(self.menu_herramientas_propiedades,
                                    text="Ver Dominio",
                                    command=self.verDominio)
        self.jbdominio.place(x=350, y=5)

        self.jbcodominio = tk.Button(self.menu_herramientas_propiedades,
                                      text="Ver Codominio",
                                      command=self.verCodominio)
        self.jbcodominio.place(x=450, y=5)

    def crearMenuGrafos(self):
        self.menu_grafos = tk.Frame(self, bg="light gray", height=40)
        self.menu_grafos.pack_propagate(False)

        self.bnodo = tk.Button(self.menu_grafos, text="Nodo", width=8,
                               command=lambda: self.screen.cambiaOperacion(GraphScreen.NODE))
        self.bnodo.place(x=0, y=5)

        self.benlace = tk.Button(self.menu_grafos, text="Enlace", width=8,
                                 command=lambda: self.screen.cambiaOperacion(GraphScreen.CONECT))
        self.benlace.place(x=70, y=5)

        self.bseleccion = tk.Button(self.menu_grafos, text="Seleccionar", width=8,
                                    command=lambda: self.screen.cambiaOperacion(GraphScreen.SELECT))
        self.bseleccion.place(x=140, y=5)

        self.bborrar = tk.Button(self.menu_grafos, text="Borrar", width=8,
                                 command=lambda: self.screen.cambiaOperacion(GraphScreen.DELETE))
        self.bborrar.place(x=210, y=5)

        self.bnodo.config(state="disabled")
        self.benlace.config(state="disabled")
        self.bseleccion.config(state="disabled")
        self.bborrar.config(state="disabled")

    def crearBarraPares(self):
        fuente = ("Arial", 12)

        tk.Label(self, text="Pares ordenados", font=fuente).place(x=600, y=430)

        self.jta_Pares = tk.Text(self, height=3, width=50, font=fuente)
        self.jta_Pares.config(state="disabled", bg="light gray")
        self.scrollpares = tk.Scrollbar(self, orient="vertical", command=self.jta_Pares.yview)
        self.jta_Pares.configure(yscrollcommand=self.scrollpares.set)

    def crearEditor(self):
        self.jta_editor = tk.Text(self, height=2, width=50, font=("Arial", 14))
        self.scroll = tk.Scrollbar(self, orient="vertical", command=self.jta_editor.yview)
        self.jta_editor.configure(yscrollcommand=self.scroll.set)

    def crearCalculadora(self):
        self.menu_calculadora = tk.Frame(self, bg="light gray", width=350, height=200)
        self.menu_calculadora.pack_propagate(False)

        fuente = ("Arial", 14)

        self.brelaciones = []
        x = 80
        for i, nombre in enumerate(self.NOMBRES_RELACIONES):
            btn = tk.Button(self.menu_calculadora, text=nombre, font=fuente, width=2,
                            command=lambda n=nombre: self.jta_editor.insert(tk.END, n))
            btn.place(x=x, y=60)
            self.brelaciones.append(btn)
            x += 40
            if i != 0:
                btn.config(state="disabled")

        self.union = tk.Button(self.menu_calculadora, text="∪", font=fuente, width=2,
                               command=lambda: self.jta_editor.insert(tk.END, "∪"))
        self.union.place(x=0, y=20)

        self.interseccion = tk.Button(self.menu_calculadora, text="∩", font=fuente, width=2,
                                      command=lambda: self.jta_editor.insert(tk.END, "∩"))
        self.interseccion.place(x=40, y=20)

        self.inversa = tk.Button(self.menu_calculadora, text="ˉ¹", font=fuente, width=2,
                                 command=lambda: self.jta_editor.insert(tk.END, "ˉ¹"))
        self.inversa.place(x=80, y=20)

        self.complemento = tk.Button(self.menu_calculadora, text="'", font=fuente, width=2,
                                     command=lambda: self.jta_editor.insert(tk.END, "'"))
        self.complemento.place(x=120, y=20)

        self.composicion = tk.Button(self.menu_calculadora, text="°", font=fuente, width=2,
                                     command=lambda: self.jta_editor.insert(tk.END, "°"))
        self.composicion.place(x=160, y=20)

        self.parenta = tk.Button(self.menu_calculadora, text="(", font=fuente, width=2,
                                 command=lambda: self.jta_editor.insert(tk.END, "("))
        self.parenta.place(x=0, y=60)

        self.parentc = tk.Button(self.menu_calculadora, text=")", font=fuente, width=2,
                                 command=lambda: self.jta_editor.insert(tk.END, ")"))
        self.parentc.place(x=40, y=60)

        self.calcular = tk.Button(self.menu_calculadora, text="Calcular", font=fuente,
                                  command=self.calcularExpresion)
        self.calcular.place(x=263, y=22)

    def crearHojaPropiedades(self):
        self.hojaPropiedades = ttk.Notebook(self, width=350, height=230)

        self.jpgeneral = tk.Frame(self.hojaPropiedades)
        self.hojaPropiedades.add(self.jpgeneral, text="PROPIEDADES")

        self.jlpropiedades = []
        self.jcbpropiedades = []

        for i, texto in enumerate(self.stetiquetas):
            cb = tk.Checkbutton(self.jpgeneral, text="", state="disabled")
            cb.grid(row=i, column=0, sticky="w", padx=5, pady=2)
            self.jcbpropiedades.append(cb)

            btn = tk.Button(self.jpgeneral, text=texto, width=30,
                            command=lambda idx=i: self.mostrarPropiedad(idx))
            btn.grid(row=i, column=1, sticky="w", pady=2)
            self.jlpropiedades.append(btn)

    def cambiarVista(self, vista):
        self.jcbModoOperacion.current(vista)
        self.cambiarVistaEvent(None)

    def cambiarVistaEvent(self, event):
        vista = self.jcbModoOperacion.current()
        if vista == self.PARES:
            self.cambiarVistaPares()
        elif vista == self.GRAFO:
            self.cambiarVistaGrafo()
        else:
            self.cambiarVistaMatriz()

    def cambiarVistaPares(self):
        self.jta_Pares.config(state="normal", bg="white")
        self.tabla.setEnable(False)
        self.screen.setEnabled(False)
        self.bnodo.config(state="disabled")
        self.benlace.config(state="disabled")
        self.bborrar.config(state="disabled")
        self.bseleccion.config(state="disabled")

    def cambiarVistaMatriz(self):
        self.jta_Pares.config(state="disabled", bg="light gray")
        self.tabla.setEnable(True)
        self.screen.setEnabled(False)
        self.bnodo.config(state="disabled")
        self.benlace.config(state="disabled")
        self.bborrar.config(state="disabled")
        self.bseleccion.config(state="disabled")

        rel = self.relaciones[self.relacionActual]
        self.tabla.setRelacion(rel)

    def cambiarVistaGrafo(self):
        self.jta_Pares.config(state="disabled", bg="light gray")
        self.tabla.setEnable(False)
        self.screen.setEnabled(True)
        self.bnodo.config(state="normal")
        self.benlace.config(state="normal")
        self.bborrar.config(state="normal")
        self.bseleccion.config(state="normal")

        rel = self.relaciones[self.relacionActual]
        self.screen.setRelacion(rel)

    def nuevaRelacion(self):
        cantidad = len(self.idrelaciones)
        if cantidad >= 4:
            messagebox.showerror("Límite excedido", "No se permiten más de 4 relaciones")
            return

        for i, usado in enumerate(self.img_nombres_relaciones):
            if not usado:
                self.idr = self.NOMBRES_RELACIONES[i]
                self.img_nombres_relaciones[i] = True
                break

        nodos = [chr(ord('a') + i) for i in range(9)]
        matriz = [[False for _ in range(9)] for _ in range(9)]
        rel = Relacion(matriz, nodos, 9)
        self.relaciones.append(rel)
        self.idrelaciones.append(self.idr)

        self.jcbRelaciones['values'] = [f"Relación {r}" for r in self.idrelaciones]
        self.jcbRelaciones.current(len(self.idrelaciones)-1)

        for i, btn in enumerate(self.brelaciones):
            if btn["text"] == self.idr:
                btn.config(state="normal")
                break

        self.cambiarRelacion()

    def borrarRelacion(self):
        if len(self.idrelaciones) == 1:
            nodos = [chr(ord('a') + i) for i in range(9)]
            matriz = [[False for _ in range(9)] for _ in range(9)]
            rel = Relacion(matriz, nodos, 9)
            self.relaciones[0] = rel
            self.tabla.setRelacion(rel)
            self.screen.setRelacion(rel)
            self.screen.setEnabled(False)
            self.jta_Pares.config(state="normal")
            self.jta_Pares.delete(1.0, tk.END)
            self.jta_Pares.insert(tk.END, str(rel))
            self.jta_Pares.config(state="disabled", bg="light gray")
            return

        indice = self.jcbRelaciones.current()
        nombre = self.idrelaciones.pop(indice)
        self.relaciones.pop(indice)

        self.jcbRelaciones['values'] = [f"Relación {r}" for r in self.idrelaciones]
        self.jcbRelaciones.current(0)

        for i, btn in enumerate(self.brelaciones):
            if btn["text"] == nombre:
                btn.config(state="disabled")
                self.img_nombres_relaciones[i] = False
                break

        self.cambiarRelacion()

    def cambiarRelacionEvent(self, event):
        self.cambiarRelacion()

    def cambiarRelacion(self):
        self.relacionActual = self.jcbRelaciones.current()
        rel = self.relaciones[self.relacionActual]
        self.tabla.setRelacion(rel)
        self.jta_Pares.config(state="normal")
        self.jta_Pares.delete(1.0, tk.END)
        self.jta_Pares.insert(tk.END, str(rel))
        self.jta_Pares.config(state="disabled", bg="light gray")
        self.screen.setRelacion(rel)
        self.setProperties()

    def generarRelacion(self):
        vista = self.jcbModoOperacion.current()
        if vista == self.MATRIX:
            self.casoMatriz()
        elif vista == self.PARES:
            self.casoPares()
        else:
            self.casoGrafo()

    def casoMatriz(self):
        if not self.tabla.comprobarIntegridad():
            return
        matriz = self.tabla.getMatriz()
        nombres = self.tabla.getNodos()[:self.tabla.getSizeRelacion()]
        rel = Relacion(matriz, nombres, self.tabla.getSizeRelacion())
        self.relaciones[self.relacionActual] = rel
        self.jta_Pares.config(state="normal")
        self.jta_Pares.delete(1.0, tk.END)
        self.jta_Pares.insert(tk.END, str(rel))
        self.jta_Pares.config(state="disabled", bg="light gray")
        self.screen.setRelacion(rel)
        self.setProperties()

    def casoPares(self):
        texto = self.jta_Pares.get(1.0, tk.END).strip()
        if not texto:
            messagebox.showerror("Error", "No ha especificado ninguna relación")
            return

        lex = Lexer()
        lex.agregarExprReg(r"^\s+", "espacio")
        lex.agregarExprReg(r"^[a-zA-Z0-9]", "nodo")
        lex.agregarExprReg(r"^,", "coma")
        lex.agregarExprReg(r"^{", "op_llaveAbre")
        lex.agregarExprReg(r"^}", "op_llaveCierra")
        lex.agregarExprReg(r"^\(", "op_parentAbre")
        lex.agregarExprReg(r"^\)", "op_parentCierra")
        lex.cargarArchivo(texto)

        if not lex.analizarArchivo():
            messagebox.showerror("Error", "Error de sintaxis en los pares")
            return

        tokens = lex.getList()
        rel = Relacion()
        if rel.verificarPares(tokens):
            self.relaciones[self.relacionActual] = rel
            self.tabla.setRelacion(rel)
            self.screen.setRelacion(rel)
            self.jta_Pares.config(state="normal")
            self.jta_Pares.delete(1.0, tk.END)
            self.jta_Pares.insert(tk.END, str(rel))
            self.jta_Pares.config(state="disabled", bg="light gray")
            self.setProperties()
        else:
            messagebox.showerror(rel.getTipo(), rel.getError())

    def casoGrafo(self):
        rel = self.screen.getRelacion()
        rel.verificarPropiedades()
        self.relaciones[self.relacionActual] = rel
        self.jta_Pares.config(state="normal")
        self.jta_Pares.delete(1.0, tk.END)
        self.jta_Pares.insert(tk.END, str(rel))
        self.jta_Pares.config(state="disabled", bg="light gray")
        self.tabla.setRelacion(rel)
        self.setProperties()
        self.screen.cambiaOperacion(GraphScreen.SELECT)

    def aplicarTransformacion(self):
        rel = self.relaciones[self.relacionActual]
        opcion = self.jcbPropiedades.current()

        if opcion == 0:
            rel.aplicarCerraduraTransitiva()
        elif opcion == 1:
            rel.aplicarCerraduraReflexiva()
        elif opcion == 2:
            rel.aplicarCerraduraSimetrica()
        elif opcion == 3:
            if not rel.aplicarHasse():
                messagebox.showerror("Error", "La relación debe ser reflexiva, antisimétrica y transitiva")
                return
        elif opcion == 4:
            clases = rel.verEquivalencia()
            if clases:
                messagebox.showinfo("Clases de equivalencia", clases)
            else:
                messagebox.showinfo("Información", "No tiene clases de equivalencia")
            return
        elif opcion == 5:
            particion = rel.verParticion()
            if particion:
                messagebox.showinfo("Partición", particion)
            else:
                messagebox.showinfo("Información", "No tiene partición")
            return

        self.tabla.setRelacion(rel)
        self.jta_Pares.config(state="normal")
        self.jta_Pares.delete(1.0, tk.END)
        self.jta_Pares.insert(tk.END, str(rel))
        self.jta_Pares.config(state="disabled", bg="light gray")
        self.screen.setRelacion(rel)
        self.setProperties()

    def verDominio(self):
        rel = self.relaciones[self.relacionActual]
        dominio = rel.aplicarDominio()
        if dominio:
            messagebox.showinfo("Dominio", ", ".join([str(d) for d in dominio]))
        else:
            messagebox.showinfo("Dominio", "No hay dominio")

    def verCodominio(self):
        rel = self.relaciones[self.relacionActual]
        codominio = rel.aplicarCodominio()
        if codominio:
            messagebox.showinfo("Codominio", ", ".join([str(c) for c in codominio]))
        else:
            messagebox.showinfo("Codominio", "No hay codominio")

    def calcularExpresion(self):
        expresion = self.jta_editor.get(1.0, tk.END).strip()
        if not expresion:
            return

        parser = ParserC(self.relaciones, self.idrelaciones)
        rel_resultado = parser.ejecutar(expresion)

        if rel_resultado is None:
            messagebox.showerror("Error", parser.getErrorType())
            return

        indice = parser.getRelacion()
        self.relaciones[indice] = rel_resultado

        if indice != self.relacionActual:
            self.jcbRelaciones.current(indice)
            self.cambiarRelacion()
        else:
            self.tabla.setRelacion(rel_resultado)
            self.jta_Pares.config(state="normal")
            self.jta_Pares.delete(1.0, tk.END)
            self.jta_Pares.insert(tk.END, str(rel_resultado))
            self.jta_Pares.config(state="disabled", bg="light gray")
            self.screen.setRelacion(rel_resultado)
            self.jcbModoOperacion.current(0)
            self.setProperties()

        self.jta_editor.delete(1.0, tk.END)

    def setProperties(self):
        rel = self.relaciones[self.relacionActual]
        props = [
            rel.esReflexiva(), rel.esIrreflexiva(), rel.esSimetrica(),
            rel.esAsimetrica(), rel.esAntisimetrica(), rel.esTransitiva(),
            rel.esDeEquivalencia(), rel.esDeParticion(), rel.esFuncion(),
            rel.esSuprayectiva(), rel.esInyectiva(), rel.esBiyectiva(),
            rel.esInvertible()
        ]
        for i, valor in enumerate(props):
            if valor:
                self.jcbpropiedades[i].select()
            else:
                self.jcbpropiedades[i].deselect()

    def mostrarPropiedad(self, indice):
        messagebox.showinfo(self.stetiquetas[indice], self.PROPIEDADES[indice])

    def acercaDe(self):
        acerca = Acercade(self)
        acerca.crearGUI()

    def lanzarAyuda(self):
        ruta = "c:/simar/manual.pdf"
        if os.path.exists(ruta):
            os.startfile(ruta)
        else:
            messagebox.showerror("Error", f"No se encontró el manual en {ruta}")

if __name__ == "__main__":
    app = MainFrame()
    app.mainloop()