package com.uvg.grafos;
import java.util.*;

public class Main {

    private static Graph graph;
    private static Floyd floyd;
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        // Cargar grafo desde archivo
        String filename = "demo/guategrafo.txt";
        System.out.println("Cargando grafo desde: " + filename);
        graph = GraphFileReader.readGraph(filename, 100);

        if (graph == null || graph.getSize() == 0) {
            System.out.println("No se pudo cargar el grafo vea el archivo :))'" + filename + "'.");
            System.exit(1);
        }

        // Mostrar matriz de adyacencia inicial
        graph.printAdjMatrix();

        // Calcular Floyd-Warshall inicial
        recalculate();

        // Menú principal
        mainMenu();

        System.out.println("\n Adios");
        scanner.close();
    }

    /** Ejecuta (o re-ejecuta) Floyd-Warshall y muestra la matriz APSP */
    private static void recalculate() {
        System.out.println("ejecutando");
        floyd = new Floyd(graph);
        floyd.printAPSPMatrix();
        System.out.println("centro del grafo: " + floydCenter());
        System.out.println();
    }

    private static String floydCenter() {
        String c = floyd.getCenter();
        return (c != null) ? c : "no determinable";
    }

    private static void mainMenu() {
        boolean running = true;
        while (running) {
            System.out.println("================================================");
            System.out.println("     RED VIAL - CENTRO RESPUESTA COVID-19   ");
            System.out.println("================================================");
            System.out.println("1. Consultar ruta más corta entre ciudades  ");
            System.out.println("2. Ver centro del grafo                     ");
            System.out.println("3. Modificar grafo (agregar/eliminar arco)  ");
            System.out.println("4. Ver matrices (Adyacencia y APSP)         ");
            System.out.println("5. Listar todas las ciudades                ");
            System.out.println("6. Salir                                    ");
            System.out.println("================================================");
            System.out.print("  Seleccione una opción: ");

            String input = scanner.nextLine().trim();

            switch (input) {
                case "1": menuShortestPath(); break;
                case "2": menuCenter();       break;
                case "3": menuModifyGraph();  break;
                case "4": menuMatrices();     break;
                case "5": listCities();       break;
                case "6": running = false;    break;
                default:
                    System.out.println("opcion invalida.\n");
            }
        }
    }

    /** Opción 1: Consultar ruta más corta */
    private static void menuShortestPath() {
        System.out.println("\n ruta mas corta");
        System.out.print("ciudad origen: ");
        String from = scanner.nextLine().trim();
        System.out.print("Ciudad destino: ");
        String to = scanner.nextLine().trim();

        if (!graph.nodeExists(from)) {
            System.out.println("ciudad origen '" + from + "' no existe en el grafo\n");
            return;
        }
        if (!graph.nodeExists(to)) {
            System.out.println("ciudad destino '" + to + "' no existe en el grafo\n");
            return;
        }

        floyd.printShortestPath(from, to, graph);
    }

    /** Opción 2: Ver centro del grafo */
    private static void menuCenter() {
        floyd.printCenter();
    }

    /** Opción 3: Modificar grafo */
    private static void menuModifyGraph() {
        System.out.println("\nmodificar grafo");
        System.out.println("a Interrumpir trafico entre dos ciudades");
        System.out.println("b Establecer nueva conexión entre dos ciudades");
        System.out.print("seleccione: ");
        String choice = scanner.nextLine().trim().toLowerCase();

        if (choice.equals("a")) {
            System.out.print("ciudad origen:");
            String from = scanner.nextLine().trim();
            System.out.print("ciudad destino:");
            String to = scanner.nextLine().trim();
            graph.removeEdge(from, to);
        } else if (choice.equals("b")) {
            System.out.print("ciudad origen:");
            String from = scanner.nextLine().trim();
            System.out.print("ciudad destino:");
            String to = scanner.nextLine().trim();
            System.out.print("distancia:");
            String weightStr = scanner.nextLine().trim();
            try {
                int weight = Integer.parseInt(weightStr);
                // Agregar nodos si no existen
                if (!graph.nodeExists(from)) {
                    System.out.println("ciudad '" + from + "' no existe agregando al grafo");
                    graph.addNode(from);
                }
                if (!graph.nodeExists(to)) {
                    System.out.println("ciudad '" + to + "' no existe agregando al grafo");
                    graph.addNode(to);
                }
                if (graph.addEdge(from, to, weight)) {
                    System.out.println("conexión agregada: " + from + " -> " + to + " (" + weight + " KM)");
                }
            } catch (NumberFormatException e) {
                System.out.println("distancia invalida");
                return;
            }
        } else {
            System.out.println("opción invalida");
            return;
        }

        // Recalcular Floyd después de la modificación
        System.out.println("\nrecalculando rutas mas cortas y centro del grafo");
        recalculate();
    }

    /** Opción 4: Ver matrices */
    private static void menuMatrices() {
        graph.printAdjMatrix();
        floyd.printAPSPMatrix();
    }

    /** Opción 5: Listar ciudades */
    private static void listCities() {
        System.out.println("\nciudades en el grafo (" + graph.getSize() + ") : ");
        List<String> cities = graph.getVertices();
        for (int i = 0; i < cities.size(); i++) {
            System.out.printf("  %3d. %s%n", i + 1, cities.get(i));
        }
        System.out.println();
    }
}
