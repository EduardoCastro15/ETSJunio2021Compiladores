package Metodos;

import java.util.ArrayList; //Libreria para crear listas
import java.util.HashMap; //Libreria para crear tablas hash
import java.io.IOException; //Libreria para la detección de excepciones

public class Gramatica {
    //Declaracion de variables privadas constantes
    private final String nTInicial;
    private final HashMap<String, ArrayList<String>> producciones;
    private final ArrayList<String> terminales;
    private final ArrayList<String> noTerminales;
    
    //Constructor
    public Gramatica(String gramaticaLimpia) throws IOException {
        this.noTerminales = new ArrayList<>();
        this.producciones = new HashMap<>();
        this.terminales = new ArrayList<>();
        String[] lineas = gramaticaLimpia.split("\n");
        for (String renglon : lineas) {
            Terminales(renglon);
            NoTerminales(renglon);
            Producciones(renglon);
        }
        this.nTInicial = this.noTerminales.get(0);
    }
    
    //Metodo calculo: Imprime toda la parte del analisis de la gramática
    public void calculo() {
        System.out.println("---ANALISIS DE LA GRAMÁTICA LIMPIA---");
        System.out.println("No Terminal inicial: " + this.nTInicial);
        System.out.println("Terminales: " + this.terminales);
        System.out.println("No terminales: " + this.noTerminales);
        System.out.println("Producciones:");
        this.producciones.forEach((noTerminal, produccion) -> {
            System.out.println("\t" + noTerminal + "->" + produccion);
        });
        System.out.println();
    }

    //Metodo Terminales: Separa en cadenas separadas los Terminales
    private void Terminales(String renglon) {
        int indiceProduce = renglon.indexOf(">");
        String aux = renglon.substring(indiceProduce + 1, renglon.length());
        aux = aux.replaceAll("([A-Z])", "");
        for (int i = 0; i < aux.length(); i++) {
            String simbolo = aux.substring(i, i + 1);
            if (!simbolo.equals("&") && !this.terminales.contains(simbolo)) {
                this.terminales.add(simbolo);
            }
        }
    }

    //Metodo NoTerminales: Separa en cadenas separadas los no terminales
    private void NoTerminales(String renglon) throws IOException {
        int indiceNTerminal = renglon.indexOf("-");
        String aux = renglon.substring(0, indiceNTerminal);
        if (!this.noTerminales.contains(aux)) {
            this.noTerminales.add(aux);
        }
    }

    //Metodo Producciones: Separa en cadenas separadas las producciones de cada no terminal
    private void Producciones(String renglon) throws IOException {
        String[] aux = renglon.split("->");
        if (!this.producciones.containsKey(aux[0])) {
            this.producciones.put(aux[0], new ArrayList<>());
            this.producciones.get(aux[0]).add(aux[1]);
        } else {
            this.producciones.get(aux[0]).add(aux[1]);
        }
    }
    
    //Getter Terminales: Retorna el valor de terminales
    public ArrayList<String> getTerminales() {
        return terminales;
    }

    //Getter NoTerminales: Retorna el valor de noTerminales
    public ArrayList<String> getNoTerminales() {
        return noTerminales;
    }

    //Getter nTInicial: Retorna el valor de nTInicial
    public String getnTInicial() {
        return nTInicial;
    }

    //Getter Producciones: Retorna el valor de producciones
    public HashMap<String, ArrayList<String>> getProducciones() {
        return producciones;
    }
}
