#Clase con los metodos de cada Estado
class Estado:
	#Declaración del constructor
	def __init__(self, Num, Inicial, Final):
		self.Num = Num
		self.Inicial = Inicial
		self.Final = Final
		self.Trans = []
	#Declaración de metodo para agregar Transición
	def AgregarTrans(self, Letra, Siguiente):
		Aux = [Letra,Siguiente]
		self.Trans.append(Aux)
	#Declaración de metodo para agregar Transición siguiente
	def Siguiente(self, Letra):
		Aux = -1
		for i in range(len(self.Trans)):
			if self.Trans[i][0] == Letra:
				Aux = self.Trans[i][1]
		return Aux
