package com.uvg.grafos;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Graph {

    public static final int INF = Integer.MAX_VALUE / 2; // Infinito (evitar overflow)

    private List<String> vertices;           // Lista de nombres de vértices
    private Map<String, Integer> indexMap;  // Mapeo nombre -> índice
    private int[][] adjMatrix;              // Matriz de adyacencia
    private int size;                       // Número de vértices
    private int capacity;                   // Capacidad máxima

    public Graph(int capacity) {
        this.capacity = capacity;
        this.size = 0;
        this.vertices = new ArrayList<>();
        this.indexMap = new HashMap<>();
        this.adjMatrix = new int[capacity][capacity];

        // Inicializar matriz con INF (sin conexión) y 0 en diagonal
        for (int i = 0; i < capacity; i++) {
            for (int j = 0; j < capacity; j++) {
                adjMatrix[i][j] = (i == j) ? 0 : INF;
            }
        }
    }

    /**
     * Agrega un nodo (ciudad) al grafo.
     * @param name Nombre de la ciudad
     * @return true si se agregó exitosamente, false si ya existía o no hay capacidad
     */
    public boolean addNode(String name) {
        if (indexMap.containsKey(name)) {
            System.out.println("  [!] El nodo '" + name + "' ya existe.");
            return false;
        }
        if (size >= capacity) {
            System.out.println("capacidad maxima alcanzada");
            return false;
        }
        indexMap.put(name, size);
        vertices.add(name);
        // La matriz ya está inicializada; solo la diagonal necesita 0
        adjMatrix[size][size] = 0;
        size++;
        return true;
    }

    /**
     * Agrega un arco dirigido entre dos ciudades con una distancia.
     * @param from  Ciudad origen
     * @param to    Ciudad destino
     * @param weight Distancia en KM
     * @return true si se agregó exitosamente
     */
    public boolean addEdge(String from, String to, int weight) {
        if (!indexMap.containsKey(from)) {
            System.out.println("ciudad origen '" + from + "' no existe");
            return false;
        }
        if (!indexMap.containsKey(to)) {
            System.out.println("ciudad destino '" + to + "' no existe");
            return false;
        }
        if (weight <= 0) {
            System.out.println("el peso debe ser positivo");
            return false;
        }
        int i = indexMap.get(from);
        int j = indexMap.get(to);
        adjMatrix[i][j] = weight;
        return true;
    }

    /**
     * Elimina el arco entre dos ciudades (interrumpe el tráfico).
     * @param from Ciudad origen
     * @param to   Ciudad destino
     * @return true si se eliminó exitosamente
     */
    public boolean removeEdge(String from, String to) {
        if (!indexMap.containsKey(from) || !indexMap.containsKey(to)) {
            System.out.println("una o ambas ciudades no existen");
            return false;
        }
        int i = indexMap.get(from);
        int j = indexMap.get(to);
        if (adjMatrix[i][j] == INF) {
            System.out.println("no existe conexion directa entre '" + from + "' y '" + to + "'.");
            return false;
        }
        adjMatrix[i][j] = INF;
        System.out.println("arco eliminado: " + from + " -> " + to);
        return true;
    }

    /**
     * Devuelve una copia de la matriz de adyacencia actual.
     */
    public int[][] getAdjMatrix() {
        int[][] copy = new int[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                copy[i][j] = adjMatrix[i][j];
            }
        }
        return copy;
    }

    /** Retorna el número de vértices activos */
    public int getSize() { return size; }

    /** Retorna lista de nombres de vértices */
    public List<String> getVertices() { return new ArrayList<>(vertices); }

    /** Retorna el índice de un vértice dado su nombre */
    public int getIndex(String name) {
        return indexMap.getOrDefault(name, -1);
    }

    /** Retorna el nombre de un vértice dado su índice */
    public String getVertex(int index) {
        if (index < 0 || index >= size) return null;
        return vertices.get(index);
    }

    /** Verifica si un nodo existe */
    public boolean nodeExists(String name) {
        return indexMap.containsKey(name);
    }

    /**
     * Muestra la matriz de adyacencia en consola con formato legible.
     */
    public void printAdjMatrix() {
        System.out.println("\nmatriz de adyacencia:");
        int colWidth = 12;

        // Encabezado
        System.out.printf("%-15s", "");
        for (int j = 0; j < size; j++) {
            String label = vertices.get(j);
            if (label.length() > colWidth - 1) label = label.substring(0, colWidth - 1);
            System.out.printf("%-" + colWidth + "s", label);
        }
        System.out.println();

        // Separador
        System.out.println("-".repeat(15 + colWidth * size));

        // Filas
        for (int i = 0; i < size; i++) {
            String rowLabel = vertices.get(i);
            if (rowLabel.length() > 14) rowLabel = rowLabel.substring(0, 14);
            System.out.printf("%-15s", rowLabel);
            for (int j = 0; j < size; j++) {
                String val = (adjMatrix[i][j] == INF) ? "INF" : String.valueOf(adjMatrix[i][j]);
                System.out.printf("%-" + colWidth + "s", val);
            }
            System.out.println();
        }
        System.out.println();
    }
}
