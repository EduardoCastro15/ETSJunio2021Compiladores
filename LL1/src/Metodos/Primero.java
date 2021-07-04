package Metodos;

import java.util.ArrayList; //Libreria para crear listas
import java.util.Queue; //Libreria para crear colas
import java.util.HashSet; //Libreria para configurar tablas hash
import java.util.LinkedList; //Libreria para crear listas ligadas
import java.util.Set;  //Libreria para crear colecciones
import java.util.regex.Pattern; //Libreria para manejar expresiones regulares
import java.util.HashMap; //Libreria para crear tablas hash

public class Primero {
    //Declaracion de variables privadas constantes
    private final HashMap<String, HashMap<String, Set<String>>> valoresM;
    private final ArrayList<String> noTerminales;
    private final HashMap<String, Set<String>> primeros;
    private final HashMap<String, Queue<String>> nTPrimeros;
    private final HashMap<String, ArrayList<String>> producciones;
    
    //Cosntructor
    public Primero(ASintactico aSintactico) {
        this.primeros = new HashMap<>();
        this.nTPrimeros = new HashMap<>();
        this.valoresM = new HashMap<>();
        this.producciones = new HashMap<>();
        this.noTerminales = aSintactico.getNoTerminales();
        aSintactico.getProducciones().forEach((noTerminal, producciones) -> {
            this.producciones.put(noTerminal, producciones);
        });
        calcularVM(aSintactico);
        calcularNTPrimeros(aSintactico);
        calcularProducciones(aSintactico);
        construirPrimero(aSintactico);
        ciclos(aSintactico);
        epsilon(aSintactico);
    }
    
    //Getter Primeros: Retorna le valor de primeros
    public HashMap<String, Set<String>> getPrimeros() {
        return primeros;
    }

    //Getter ValoresM
    public HashMap<String, HashMap<String, Set<String>>> getValoresM() {
        return valoresM;
    }
    
    //Metodo calculo: Se encarga de imprimir los valores calculados para cada uno de los no terminales
    public void calculo() {
        System.out.println("---CALCULO DE PRIMERO(X) PARA TODOS LOS SIMBOLOS GRAMATICALES---");
        this.primeros.forEach((noTerminal, conjunto) -> {
            System.out.println("PRIMERO(" + noTerminal + ") = " + conjunto);
        });
        System.out.println();
    }

    //Metodo calcularVM: Crea la tabla hash donde se va a guardar los valores
    private void calcularVM(ASintactico aSintactico) {
        for (String noTerminal : aSintactico.getNoTerminales()) {
            HashMap<String, Set<String>> valor = new HashMap<>();
            ArrayList<String> producciones = aSintactico.getProducciones().get(noTerminal);
            for (String produccion : producciones) {
                valor.put(produccion, new HashSet<>());
            }
            this.valoresM.put(noTerminal, valor);
        }
    }

    //Metodo calcularNTPrimeros: Calcula los no terminales primeros
    private void calcularNTPrimeros(ASintactico aSintactico) {
        for (String noTerminal : aSintactico.getNoTerminales()) {
            this.nTPrimeros.put(noTerminal, new LinkedList<>());
        }
    }
    
    //Metodo epsilon: VErifica que cada primero de cada no terminal tenga epsilon si lo requiere
    private void epsilon(ASintactico aSintactico) {
        aSintactico.getProducciones().forEach((noTerminal, producciones) -> {
            int aux = 0;
            if (!producciones.contains("&")) {
                for (String prod : producciones) {
                    if (!terminal(prod.substring(0, 1))) {
                        for (char a : prod.toCharArray()) {
                            String sim = Character.toString(a);
                            if (!terminal(sim)) {
                                if (this.primeros.get(sim).contains("&")) {
                                    aux++;
                                }
                            }
                        }
                        if (aux < prod.length()) {
                            this.primeros.get(noTerminal).remove("&");
                            this.valoresM.get(noTerminal).get(prod).remove("&");
                        }
                    }
                }
            }
        });
    }

    //Metodo terminal: Verifica si es terminal o no
    private boolean terminal(String cadena) {
        return !Pattern.matches("[A-Z]", cadena);
    }
    
    //Metodo calcularPrimero: Culmina la construccion que construirPrimero comenzo
    private void calcularPrimero(String A, String produccion) {
        String prod = "";
        for (int i = 0; i < produccion.length(); i++) {
            String primeraCad = produccion.substring(i, i + 1);
            if (terminal(primeraCad)) {
                if (i == 0) {
                    this.primeros.get(A).add(primeraCad);
                } else {
                    prod += primeraCad;
                }
                Set<String> terminal = new HashSet<>();
                if (!primeraCad.equals("&")) {
                    terminal.add(primeraCad);
                }
                this.valoresM.get(A).put(produccion, terminal);
                break;
            } else {
                prod += primeraCad;
            }
        }
        this.nTPrimeros.get(A).add(prod);
    }

    //Metodo ciclos: Verifica los ciclos de cada primero
    private void ciclos(ASintactico aSintactico) {
        for (int j = 0; j < 2; j++) {
            int ultimaPos = this.noTerminales.size() - 1;
            for (int i = ultimaPos; i >= 0; i--) {
                String noTerminal = this.noTerminales.get(i);
                Queue<String> ciclo = this.nTPrimeros.get(noTerminal);
                Set<String> union = new HashSet<>();
                Set<String> A = this.primeros.get(noTerminal);
                union.addAll(A);
                for (String produccion : ciclo) {
                    for (char simb : produccion.toCharArray()) {
                        String sim = Character.toString(simb);
                        Set<String> B = new HashSet<>();
                        if (terminal(sim)) {
                            B.add(sim);
                            union.addAll(B);
                            relacionarVM(aSintactico, noTerminal, produccion, B);
                            break;
                        } else {
                            B = this.primeros.get(sim);
                            union.addAll(B);
                            relacionarVM(aSintactico, noTerminal, produccion, B);
                            if (!B.contains("&")) {
                                break;
                            }
                        }
                    }
                }
                this.primeros.get(noTerminal).addAll(union);
            }
        }
    }

    //Metodo calcularProducciones: Calcula las producciones de cada no terminal
    private void calcularProducciones(ASintactico aSintactico) {
        for (String noTerminal : aSintactico.getNoTerminales()) {
            this.primeros.put(noTerminal, new HashSet<>());
        }
    }

    //Metodo construirPrimero: Construye los valores primero para cada no terminal
    private void construirPrimero(ASintactico aSintactico) {
        for (String noTerminal : aSintactico.getNoTerminales()) {
            ArrayList<String> producciones = aSintactico.getProducciones().get(noTerminal);
            for (String prod : producciones) {
                calcularPrimero(noTerminal, prod);
            }
        }
    }
    
    //Metodo relacionarVM: RElaciona los valores Matriz con cada no terminal
    private void relacionarVM(ASintactico aSintactico, String noTerminal, String produccion, Set<String> B) {
        ArrayList<String> producciones = aSintactico.getProducciones().get(noTerminal);
        for (String prod : producciones) {
            if (prod.contains(produccion)) {
                this.valoresM.get(noTerminal).get(prod).addAll(B);
            }
        }
    }
}