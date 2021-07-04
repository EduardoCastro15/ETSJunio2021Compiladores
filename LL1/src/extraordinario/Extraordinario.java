package extraordinario;

import Metodos.Ll1; //Llamada a la clase Ll1 dentro del paquete Metodos
import java.io.IOException; //Libreria para la detección de excepciones
import java.util.Scanner; //Libreria para leer

public class  Extraordinario{
    //Función main: Encargada de inicializar el programa
    public static void main(String[] args) throws IOException{
        Ll1 ll1 = new Ll1(gramaticaLimpia());
        ll1.calculo();
        while (true) {
            Scanner lectura = new Scanner(System.in);
            System.out.println("INGRESA LA CADENA A COMPROBAR: ");
            String cadena = lectura.nextLine();
            ll1.comprobarCadena(cadena);
        }
    }
    //Metodo gramaticaLimpia: Retorna la gramatica a utilizar
    private static String gramaticaLimpia(){
        return    "E->TA\n"
                + "A->+TA\n"
                + "A->&\n"
                + "T->FB\n"
                + "B->*FB\n"
                + "B->&\n"
                + "F->(E)\n"
                + "F->a";
        /*return    "E->E+T\n"
                + "E->T\n"
                + "T->T*F\n"
                + "T->F\n"
                + "F->a\n"
                + "F->(E)";
        return    "A->Aa\n"
                + "A->BCD\n"
                + "B->b\n"
                + "B->&\n"
                + "C->c\n"
                + "C->&\n"
                + "D->d\n"
                + "D->Ce";*/
    }
}