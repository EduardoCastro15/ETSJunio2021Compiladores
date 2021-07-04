package Metodos;

import java.util.ArrayList; //Libreria para crear listas
import java.util.HashSet; //Libreria para configurar tablas hash
import java.util.regex.Pattern; //Libreria para manejar expresiones regulares
import java.util.HashMap; //Libreria para crear tablas hash
import java.util.Set; //Libreria para crear colecciones

public class Siguiente {
    //Declaracion de variables privadas constantes
    private HashMap<String, ArrayList<String>> producciones;
    private HashMap<String, Set<String>> nTSiguientes;
    private ArrayList<String> noTerminales;
    private HashMap<String, Set<String>> siguientes;
    
    //Constructor
    public Siguiente(ASintactico aSintactico, Primero primeros) {
        this.siguientes = new HashMap<>();
        this.nTSiguientes = new HashMap<>();
        this.producciones = aSintactico.getProducciones();
        this.noTerminales = aSintactico.getNoTerminales();
        calcularNTSiguientes(aSintactico);
        construirSiguiente(aSintactico, primeros);
        calcularSiguiente(aSintactico);
    }
    
    //Metodo calculo: Se ecarga de imprimir todo el calculo de SIGUIENTES
    public void calculo() {
        System.out.println("---CALCULO DE SIGUIENTE(X) PARA TODOS LOS SIMBOLOS GRAMATICALES---");
        this.siguientes.forEach((noTerminal, conjunto) -> {
            System.out.println("SIGUIENTE(" + noTerminal + ") = " + conjunto);
        });
        System.out.println();
    }
    
    //Metodo calcularSiguiente: Culmina la tarea de calcular siguientes, que constuirSiguiente comenzo
    private void calcularSiguiente(ASintactico aSintactico) {
        for (int j = 0; j < 2; j++) {
            for (String noTerminal : aSintactico.getNoTerminales()) {
                Set<String> ciclo = this.nTSiguientes.get(noTerminal);
                Set<String> aux = new HashSet<>();
                Set<String> A = this.siguientes.get(noTerminal);
                aux.addAll(A);
                for (String sim : ciclo) {
                    Set<String> B = this.siguientes.get(sim);
                    aux.addAll(B);
                }
                this.siguientes.get(noTerminal).addAll(aux);
            }
        }
    }

    //Metodo terminal: Verifica si es terminal o no
    private boolean terminal(String cadena) {
        return !Pattern.matches("[A-Z]", cadena);
    }
    
    //Metodo caluclarNTSsiguientes: Guarda en las tablas hash los no terminales siguientes
    private void calcularNTSiguientes(ASintactico aSintactico) {
        for (String noTerminal : aSintactico.getNoTerminales()) {
            this.nTSiguientes.put(noTerminal, new HashSet<>());
            this.siguientes.put(noTerminal, new HashSet<>());
        }
        String inicial = aSintactico.getNoTerminales().get(0);
        this.siguientes.get(inicial).add("$");
    }

    //Metodo construirSiguiente: Aplica las 3 reglas vistas para producir los siguientes de cada no terminal
    private void construirSiguiente(ASintactico aSintactico, Primero primeros) {
        for (String noTerminal : aSintactico.getNoTerminales()) {
            for (String noTermAux : aSintactico.getNoTerminales()) {
                ArrayList<String> producciones = aSintactico.getProducciones().get(noTermAux);
                for (String produccion : producciones) {
                    Set<String> aux = calcularProducciones(produccion, noTerminal, noTermAux, primeros);
                    if (!aux.isEmpty()) {
                        for (String prod : aux) {
                            for (int i = 0; i < prod.length(); i++) {
                                String sim = prod.substring(i, i + 1);
                                if (terminal(sim)) {
                                    this.siguientes.get(noTerminal).add(sim);
                                    break;
                                } else {
                                    Set<String> conjPrimero = primeros.getPrimeros().get(sim);
                                    if (!conjPrimero.contains("&")) {
                                        this.siguientes.get(noTerminal).addAll(conjPrimero);
                                        break;
                                    } else {
                                        if (prod.length() == 1) {
                                            this.siguientes.get(noTerminal).addAll(conjPrimero);
                                            this.nTSiguientes.get(noTerminal).add(noTermAux);
                                        } else {
                                            this.siguientes.get(noTerminal).addAll(conjPrimero);
                                            if (i == prod.length() - 1) {
                                                this.nTSiguientes.get(noTerminal).add(noTermAux);
                                            }
                                        }
                                        this.siguientes.get(noTerminal).remove("&");
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    //Getter Siguientes: Retorna el valor de siguientes
    public HashMap<String, Set<String>> getSiguientes() {
        return siguientes;
    }

    //Metodo calcularProducciones: Retorna la coleccion con las producciones
    private Set<String> calcularProducciones(String produccion, String B, String A, Primero primeros) {
        Set<String> aux = new HashSet<>();
        while (produccion.contains(B)) {
            produccion = produccion.substring(produccion.indexOf(B) + 1, produccion.length());
            if (produccion.equals("")) {
                this.nTSiguientes.get(B).add(A);
                break;
            }
            aux.add(produccion);
        }
        return aux;
    }
}
