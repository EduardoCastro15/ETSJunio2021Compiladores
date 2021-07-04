package Metodos;

import java.util.Stack; //Libreria para crear pilas
import java.util.regex.Pattern; //Libreria para manejar expresiones regulares
import java.io.IOException; //Libreria para la detección de excepciones
import java.util.HashMap; //Libreria para crear tablas hash
import java.util.Iterator; //Libreria para crear interadores

public class Ll1{
    //Declaracion de variables privadas constantes
    private final Primero primeros;
    private final Siguiente siguientes;
    private final TablaASP tablaASP;
    private final Gramatica gramatica;
    private final ASintactico aSintactico;
    
    //Constructor
    public Ll1(String gramaticaLimpia) throws IOException{
        this.gramatica = new Gramatica(gramaticaLimpia);
        this.aSintactico = new ASintactico(gramatica);
        this.primeros = new Primero(aSintactico);
        this.siguientes = new Siguiente(aSintactico, primeros);
        this.tablaASP = new TablaASP(aSintactico, primeros, siguientes);
    }
    
    //Metodo calculo: Llama a los demas metodos del paquete Metodos
    public void calculo(){
        this.gramatica.calculo();
        this.aSintactico.calculo();
        this.primeros.calculo();
        this.siguientes.calculo();
        this.tablaASP.calculo();
    }
    
    //Metodo comprobarCadena: Comprueba que la cadena sea valida para la gramatica
    public void comprobarCadena(String cadena){
        String revisados = cadena + "$";
        revisados = revisados.replaceAll("&", "");
        Stack<String> pila = new Stack<>();
        pila.push("$");
        pila.push(this.aSintactico.getnTermInicial());
        String auxPila, auxRevisados, auxCoincidencia = "";
        System.out.println("COINCIDENCIA        "
                         + "PILA               "
                         + "ENTRADA                "
                         + "ACCION");
        String aux;
        do{
            auxRevisados = revisados.charAt(0) + "";
            auxPila = pila.peek();
            if(terminal(auxPila + "") || auxPila.equals("$")){
                if(auxPila.equals(auxRevisados)){
                    auxCoincidencia += pila.peek();
                    aux = espacios(auxCoincidencia, getPila(pila), revisados, "RELACIONAR " + auxPila);
                    System.out.println(aux); //Imprime la columna de accion (Relacionar)
                    pila.pop();
                    revisados = revisados.substring(1);
                }else{
                    aux = espacios("", getPila(pila), revisados, "ERROR");
                    System.out.println(aux);
                    break;
                }
            }else{
                HashMap<String, String> Mapa = tablaASP.getTablaASP().get(auxPila + "");
                String producciones = Mapa.get(auxRevisados + "");
                if(producciones == null){
                    aux = espacios("", getPila(pila), revisados, "ERROR");
                    System.out.println(aux);
                    break;
                }
                aux = espacios(auxCoincidencia, getPila(pila), revisados, "EMITIR " + producciones);
                System.out.println(aux); //Imprime la columna de accion (Emitir)
                if(!producciones.equals("")){
                    String f = producciones.charAt(0) + "";
                    if(f.equals(auxPila) || (f.concat("'").equals(auxPila))){
                        pila.pop();
                        String auxSubStr;
                        String auxProd = producciones.charAt(1) + "";
                        if(auxProd.equals("'"))
                            auxSubStr = producciones.substring(4);
                        else
                            auxSubStr = producciones.substring(3);
                        boolean sw = true;
                        if(!auxSubStr.equals("&")){
                            for(int i = auxSubStr.length() - 1; i >= 0; i--){
                                String comp = auxSubStr.charAt(i) + "";
                                if(comp.equals("'"))
                                    sw = false;
                                else if(sw == false){
                                    pila.push(auxSubStr.charAt(i) + "'");
                                    sw = true;
                                }else
                                    pila.push(auxSubStr.charAt(i) + "");
                            }
                        }
                    }else{
                        aux = espacios("", getPila(pila), revisados, "ERROR");
                        System.out.println(aux);
                        break;
                    }
                }else{
                    aux = espacios("", getPila(pila), revisados, "ERROR");
                    System.out.println(aux);
                    break;
                }
            }
        }while(!auxPila.equals("$"));
        if(auxPila.equals("$") && pila.empty())
            System.out.println("CADENA VERIFICADA Y ACEPTADA PARA LA GRAMATICA...\n");
    }
    
    //Metodo terminal: Verifica si es terminal o no
    private boolean terminal(String terminal){
        return !Pattern.matches("[A-Z]", terminal);
    }
    
    //Getter Pila: Devuelve el valor que existe dentro de la pila en forma de cadena
    public String getPila(Stack<String> cadena){
        Iterator valor = cadena.iterator();
        String aux = "";
        while (valor.hasNext()){
            aux += valor.next();
        }
        return aux;
    }
    
    //Metodo espacios: Devuelve el renglon con la Pila, la Entrada y la Salida
    private String espacios(String coincidencia, String pila, String revisados, String producciones){
        String aux = coincidencia;
        for(int i = 0; i < 20 - coincidencia.length(); i++)
            aux += " ";
        aux += pila;
        for(int i = 0; i < 20 - pila.length(); i++)
            aux += " ";
        aux += revisados;
        for(int i = 0; i < 20 - revisados.length(); i++)
            aux += " ";
        aux += producciones;
        return aux;
    }
}