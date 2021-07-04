package Metodos;

import java.util.HashMap; //Libreria para crear tablas hash

public class TablaASP {
    //Declaracion de variable privada constante
    private final HashMap<String, HashMap<String, String>> tablaASP;

    //Constructor
    public TablaASP(ASintactico aSintactico, Primero primeros, Siguiente siguientes) {
        this.tablaASP = new HashMap<>();
        construirTablaASP(aSintactico);
        calcularTablaASP(primeros, siguientes);
    }
    
    //Metodo calculo: Imprime la tabla de Analisis Sintactico Predictivo
    public void calculo() {
        System.out.println("---TABLA DE ANALISIS SINTACTICO PREDICTIVO---");
        this.tablaASP.forEach((noTerminal, conjunto) -> {
            System.out.println(noTerminal + ": " + conjunto);
        });
        System.out.println();
    }
    
    //Metodo calcularTablaASP: Calcula cada una de las relaciones de la Tabla de Anlisis Sintactico Predictivo
    private void calcularTablaASP(Primero primeros, Siguiente siguientes) {
        primeros.getPrimeros().forEach((noTerminal, terminales) -> {
            for (String terminal : terminales) {
                primeros.getValoresM().get(noTerminal).forEach((produccion, produce) -> {
                    if (produce.contains("&")) {
                        for (String simbolo : siguientes.getSiguientes().get(noTerminal)) {
                            String aux = noTerminal + "->" + produccion;
                            this.tablaASP.get(noTerminal).put(simbolo, aux);
                        }
                    }
                    if (!produccion.equals("&")) {
                        if (produce.contains(terminal)) {
                            String aux = noTerminal + "->" + produccion;
                            this.tablaASP.get(noTerminal).put(terminal, aux);
                        }
                    } else {
                        for (String vacio : siguientes.getSiguientes().get(noTerminal)) {
                            String aux = noTerminal + "->&";
                            this.tablaASP.get(noTerminal).put(vacio, aux);
                        }
                    }
                });
            }
        });
    }
    
    //Getter TablaASP: Devuelve la cadena con el valor dentro de la Tabla (noTerminal, terminal)
    public String getTablaASP(String noTerminal, String terminal) {
        return tablaASP.get(noTerminal).get(terminal);
    }
    
    //Metodo construirTablaASP: Construye la Tabla de Analisis Sintactico Predictivo
    private void construirTablaASP(ASintactico aSintactico) {
        for (String noTerminal : aSintactico.getNoTerminales()) {
            this.tablaASP.put(noTerminal, new HashMap<>());
            for (String terminal : aSintactico.getTerminales()) {
                this.tablaASP.get(noTerminal).put(terminal, "");
            }
            this.tablaASP.get(noTerminal).put("$", "");
        }
    }
    
    //Getter TablaASP: Devuelve la tabla de Analisis Sintactico Predictivo
    public HashMap<String, HashMap<String, String>> getTablaASP() {
        return tablaASP;
    }
}
