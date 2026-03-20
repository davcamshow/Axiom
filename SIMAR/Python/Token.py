# Token.py

class Token:
    def __init__(self, nombre, tk, tipo):
        """
        nombre: str, el nombre descriptivo del token (ej. "relacion", "union")
        tk: str, el texto real del token (ej. "R", "∪")
        tipo: int/short, el código numérico del token
        """
        self.nombre = nombre
        self.tk = tk
        self.tipo = tipo
        # Los siguientes atributos se usarán en el Parser
        self.matriz = None
        self.nodos = None