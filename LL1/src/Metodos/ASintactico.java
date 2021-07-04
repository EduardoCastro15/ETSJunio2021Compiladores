package Metodos;

import java.util.HashSet; //Libreria para configurar tablas hash
import java.util.Iterator; //Libreria para crear interadores
import java.util.ArrayList; //Libreria para crear listas
import java.util.HashMap; //Libreria para crear tablas hash
import java.util.Set; //Libreria para crear colecciones

public class ASintactico {
    //Declaracion de variables privadas constantes
    private ArrayList<String> terminales;
    private final String nTInicial;
    private HashMap<String, ArrayList<String>> producciones;
    private final ArrayList<String> noTerminales;

    //Constructor
    public ASintactico(Gramatica gramaticaLimpia) {
        this.terminales = gramaticaLimpia.getTerminales();
        this.nTInicial = gramaticaLimpia.getnTInicial();
        this.noTerminales = gramaticaLimpia.getNoTerminales();
        this.producciones = new HashMap<>();
        gramaticaLimpia.getProducciones().forEach((noTerminal, producciones) -> {
            if (recursivo(noTerminal, producciones)) {
                noRecursivo(noTerminal, producciones);
            } else {
                this.producciones.put(noTerminal, producciones);
            }
        });
        factorizacion();
        construirTerminales();
    }
    
    //Metodo relacionarFactores: Culmina el trabajo que comenzo factorizar agregando & para epsilon
    private void relacionarFactores(String A, ArrayList<String> producciones, Set<Integer> indicesF, String cadenaMax) {
        String AP = nuevoNoTerminal();
        ArrayList<String> prodANueva = new ArrayList<>();
        prodANueva.add(cadenaMax + AP);
        ArrayList<String> prodAPNueva = new ArrayList<>();
        for (int j = 0; j < producciones.size(); j++) {
            if (!indicesF.contains(j)) {
                prodANueva.add(producciones.get(j));
            } else {
                producciones.set(j, producciones.get(j).replace(cadenaMax, ""));
                if (producciones.get(j).equals("")) {
                    producciones.set(j, "&");
                }
                prodAPNueva.add(producciones.get(j));
            }
        }
        this.producciones.put(A, prodANueva);
        this.producciones.put(AP, prodAPNueva);
        int indiceA = this.noTerminales.indexOf(A);
        this.noTerminales.add(indiceA + 1, AP);
    }
    
    //Getter : Retorna el valor de terminales
    public ArrayList<String> getTerminales() {
        return terminales;
    }

    //Getter NoTerminales: Retorna el valor de noTerminales
    public ArrayList<String> getNoTerminales() {
        return noTerminales;
    }

    //Getter nTermInicial: Retorna el valor de nTInicial
    public String getnTermInicial() {
        return nTInicial;
    }

    //Getter Producciones: Retorna el valor de producciones
    public HashMap<String, ArrayList<String>> getProducciones() {
        return producciones;
    }
    
    //Metodo encargado de imprimir todo lo relacionado con el analisis de la gramatica sin recursividad y factorizada
    public void calculo() {
        System.out.println("---ANALISIS DE LA GRAMÁTICA LIMPIA (SIN RECURSIVIDAD Y FACTORIZADA)---");
        System.out.println("No Terminal inicial: " + this.nTInicial);
        System.out.println("Terminales: " + this.terminales);
        System.out.println("No terminales: " + this.noTerminales);
        System.out.println("Producciones:");
        this.producciones.forEach((noTerminal, produccion) -> {
            System.out.println("\t" + noTerminal + "->" + produccion);
        });
        System.out.println();
    }

    //Metodo recursivo: Retorna si una produccion es recursiva
    private boolean recursivo(String noTerminal, ArrayList<String> producciones) {
        int i = 0;
        while (i < producciones.size()) {
            if (producciones.get(i).substring(0, 1).equals(noTerminal)) {
                return true;
            }
            i++;
        }
        return false;
    }

    //Metodo noRecursivo: Aplica el algoritmo para eliminar la recursividad en una gramatica
    private void noRecursivo(String A, ArrayList<String> producciones) {
        ArrayList<String> alfa = new ArrayList<>();
        ArrayList<String> beta = new ArrayList<>();
        for (String produccion : producciones) {
            if (A.equals(produccion.substring(0, 1))) {
                int tamañoProd = produccion.length();
                alfa.add(produccion.substring(1, tamañoProd));
            } else {
                beta.add(produccion);
            }
        }
        relacionarNoRecursivo(A, alfa, beta);
    }

    //Metodo relacionarNoRecursivo: Se encarga de culminar la tarea del metodo noRecursivo, relacionando cada una de las producciones
    private void relacionarNoRecursivo(String A, ArrayList<String> alfa, ArrayList<String> beta) {
        String AP = nuevoNoTerminal();
        ArrayList<String> noRA = new ArrayList<>();
        for (String produccion : beta) {
            noRA.add(produccion + AP);
        }
        if (beta.isEmpty()) {
            noRA.add(AP);
        }
        ArrayList<String> noRAP = new ArrayList<>();
        for (String produccion : alfa) {
            noRAP.add(produccion + AP);
        }
        noRAP.add("&");
        this.producciones.put(A, noRA);
        this.producciones.put(AP, noRAP);
        int indiceA = this.noTerminales.indexOf(A);
        this.noTerminales.add(indiceA + 1, AP);
    }

    //Metodo factorizacion: Aplica recursividad para factorizar las producciones que asi lo necesiten
    private void factorizacion() {
        boolean aux = false;
        for (String noTerminal : this.noTerminales) {
            ArrayList<String> producciones = this.producciones.get(noTerminal);
            Set<Integer> indicesF = new HashSet<>();
            do {
                indicesF = factorizable(noTerminal, producciones);
                if (!indicesF.isEmpty()) {
                    factorizar(noTerminal, producciones, indicesF);
                    aux = true;
                    break;
                } else {
                    aux = false;
                }
            } while (!indicesF.isEmpty());
            if (aux) {
                break;
            }
        }
        if (aux) {
            factorizacion();
        }
    }
    
    //Metodo nuevoNoTerminal: Retorna los no terminales
    private String nuevoNoTerminal() {
        for (char A = 'A'; A <= 'Z'; A++) {
            String noTerminal = Character.toString(A);
            if (!this.noTerminales.contains(noTerminal)) {
                return noTerminal;
            }
        }
        return "A";
    }

    //Metodo construirTerminales: Se encarga de construir los terminales de la gramatica
    private void construirTerminales() {
        this.terminales = new ArrayList<>();
        for (String noTerminal : this.noTerminales) {
            ArrayList<String> gramaticaLimpia = this.producciones.get(noTerminal);
            for (String expresion : gramaticaLimpia) {
                String cadenaTerminales = expresion.replaceAll("([A-Z]'*)", "");
                for (int i = 0; i < cadenaTerminales.length(); i++) {
                    String simbolo = cadenaTerminales.substring(i, i + 1);
                    if (!simbolo.equals("&") && !this.terminales.contains(simbolo)) {
                        this.terminales.add(simbolo);
                    }
                }
            }
        }
    }
    
    //Metodo factorizable: Retorna una coleccion de indices factorizables
    private Set<Integer> factorizable(String A, ArrayList<String> producciones) {
        Set<Integer> indicesF = new HashSet<>();
        int i = 0;
        while (i < producciones.size()) {
            indicesF.add(i);
            for (int j = 0; j < producciones.size(); j++) {
                if (j == i) {
                    continue;
                }
                String primeroI = producciones.get(i).substring(0, 1);
                String primeroJ = producciones.get(j).substring(0, 1);
                if (primeroI.equals(primeroJ)) {
                    indicesF.add(j);
                }
            }
            if (indicesF.size() > 1) {
                break;
            } else {
                indicesF.removeAll(indicesF);
            }
            i++;
        }
        return indicesF;
    }

    //Metodo factorizar: Se encarga de factorizar los indices calculados en factorizable
    private void factorizar(String A, ArrayList<String> producciones, Set<Integer> indicesF) {
        Iterator iter = indicesF.iterator();
        Integer primeraPos = (Integer) iter.next();
        String primerProd = producciones.get(primeraPos);
        String cadenaMax = "";
        int i = 0;
        boolean cadenaNoTer = true;
        while (cadenaNoTer) {
            int iguales = 0;
            for (Integer indice : indicesF) {
                if (i >= primerProd.length() || i >= producciones.get(indice).length()) {
                    break;
                }
                if (primerProd.equals(producciones.get(indice))) {
                    continue;
                }
                String compararP = primerProd.substring(0, i + 1);
                String compararS = producciones.get(indice).substring(0, i + 1);
                if (compararP.equals(compararS)) {
                    iguales++;
                }
            }
            if (iguales == indicesF.size() - 1) {
                cadenaMax = primerProd.substring(0, i + 1);
            } else {
                cadenaNoTer = false;
            }
            i++;
        }
        relacionarFactores(A, producciones, indicesF, cadenaMax);
    }
}
