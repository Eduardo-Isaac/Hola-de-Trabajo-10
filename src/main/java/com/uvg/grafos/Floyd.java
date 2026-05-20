package com.uvg.grafos;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación del Algoritmo de Floyd-Warshall (All-Pairs Shortest Path).
 * Calcula la ruta más corta entre TODOS los pares de vértices del grafo.
 *
 * CC2003 - Algoritmos y Estructura de Datos
 * Hoja de Trabajo No. 10
 */
public class Floyd {

    private int[][] dist;       // Matriz de distancias más cortas (APSP)
    private int[][] next;       // Matriz para reconstrucción del camino
    private int size;
    private List<String> vertices;

    /**
     * Ejecuta el algoritmo de Floyd-Warshall sobre el grafo dado.
     * @param graph Grafo dirigido ponderado
     */
    public Floyd(Graph graph) {
        this.size = graph.getSize();
        this.vertices = graph.getVertices();
        this.dist = graph.getAdjMatrix();       // Copia de la matriz de adyacencia
        this.next = new int[size][size];

        // Inicializar matriz 'next' para reconstrucción de rutas
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (i == j || dist[i][j] == Graph.INF) {
                    next[i][j] = -1;
                } else {
                    next[i][j] = j; // El siguiente paso directo es j
                }
            }
        }

        // ===== ALGORITMO DE FLOYD-WARSHALL =====
        // Para cada vértice intermedio k
        for (int k = 0; k < size; k++) {
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    // Si pasar por k mejora la distancia de i a j
                    if (dist[i][k] != Graph.INF && dist[k][j] != Graph.INF) {
                        if (dist[i][k] + dist[k][j] < dist[i][j]) {
                            dist[i][j] = dist[i][k] + dist[k][j];
                            next[i][j] = next[i][k]; // El camino pasa por k
                        }
                    }
                }
            }
        }
    }

    /**
     * Retorna la distancia más corta entre dos ciudades.
     * @param from Ciudad origen
     * @param to   Ciudad destino
     * @return Distancia en KM, o INF si no hay ruta
     */
    public int getShortestDistance(String from, String to, Graph graph) {
        int i = graph.getIndex(from);
        int j = graph.getIndex(to);
        if (i == -1 || j == -1) return Graph.INF;
        return dist[i][j];
    }

    /**
     * Reconstruye el camino más corto entre dos ciudades.
     * @param from  Ciudad origen
     * @param to    Ciudad destino
     * @param graph Grafo de referencia
     * @return Lista ordenada de ciudades que forman la ruta, vacía si no existe
     */
    public List<String> getPath(String from, String to, Graph graph) {
        List<String> path = new ArrayList<>();
        int i = graph.getIndex(from);
        int j = graph.getIndex(to);

        if (i == -1 || j == -1 || dist[i][j] == Graph.INF) {
            return path; // No hay ruta
        }

        // Reconstruir camino usando la matriz 'next'
        path.add(vertices.get(i));
        while (i != j) {
            i = next[i][j];
            if (i == -1) {
                path.clear();
                return path; // Error en reconstrucción
            }
            path.add(vertices.get(i));
        }
        return path;
    }

    /**
     * Imprime en consola el resultado de la ruta más corta entre dos ciudades.
     */
    public void printShortestPath(String from, String to, Graph graph) {
        int distance = getShortestDistance(from, to, graph);
        List<String> path = getPath(from, to, graph);

        System.out.println("\n-ruta mas corta-");
        System.out.println("origen:  " + from);
        System.out.println("destino: " + to);

        if (distance == Graph.INF || path.isEmpty()) {
            System.out.println("no existe ruta entre estas ciudades");
        } else {
            System.out.println("distancia total: " + distance + "km");
            System.out.print("ruta: ");
            System.out.println(String.join(" -> ", path));
        }
        System.out.println();
    }

    /**
     * Calcula el centro del grafo.

     * @return 
     */
    public String getCenter() {
        int minEccentricity = Graph.INF;
        int centerIndex = -1;

        for (int j = 0; j < size; j++) {          // para cada columna 
            int maxInColumn = 0;                    // max de la columna :))
            boolean reachable = true;

            for (int i = 0; i < size; i++) {
                if (i == j) continue;
                if (dist[i][j] == Graph.INF) {
                    reachable = false;
                    break;
                }
                if (dist[i][j] > maxInColumn) {
                    maxInColumn = dist[i][j];
                }
            }

            if (!reachable) continue; // Excentricidad infinita, se descarta

            if (maxInColumn < minEccentricity) {
                minEccentricity = maxInColumn;
                centerIndex = j;
            }
        }

        return (centerIndex == -1) ? null : vertices.get(centerIndex);
    }

    /**
     * Imprime las excentricidades de todos los vértices y el centro del grafo.
     */
    public void printCenter() {
        System.out.println("\ncentro del grafo:");
        System.out.printf("  %-25s %s%n", "ciudad", "excentricidad");
        System.out.println("  " + "-".repeat(40));

        for (int j = 0; j < size; j++) {
            int maxInColumn = 0;
            boolean reachable = true;

            for (int i = 0; i < size; i++) {
                if (i == j) continue;
                if (dist[i][j] == Graph.INF) {
                    reachable = false;
                    break;
                }
                if (dist[i][j] > maxInColumn) {
                    maxInColumn = dist[i][j];
                }
            }

            String eccStr = reachable ? String.valueOf(maxInColumn) : "no alcanzable";
            System.out.printf("  %-25s %s%n", vertices.get(j), eccStr);
        }

        String center = getCenter();
        System.out.println();
        if (center != null) {
            System.out.println("centro del grafo: " + center);
        } else {
            System.out.println("no se puede determinar el centro");
        }
        System.out.println();
    }

    /**
     * muestra la matriz apsp resultante de Floyd.
     */
    public void printAPSPMatrix() {
        System.out.println("\nmatriz apsp");
        int colWidth = 12;

        System.out.printf("%-15s", "");
        for (int j = 0; j < size; j++) {
            String label = vertices.get(j);
            if (label.length() > colWidth - 1) label = label.substring(0, colWidth - 1);
            System.out.printf("%-" + colWidth + "s", label);
        }
        System.out.println();
        System.out.println("-".repeat(15 + colWidth * size));

        for (int i = 0; i < size; i++) {
            String rowLabel = vertices.get(i);
            if (rowLabel.length() > 14) rowLabel = rowLabel.substring(0, 14);
            System.out.printf("%-15s", rowLabel);
            for (int j = 0; j < size; j++) {
                String val = (dist[i][j] == Graph.INF) ? "INF" : String.valueOf(dist[i][j]);
                System.out.printf("%-" + colWidth + "s", val);
            }
            System.out.println();
        }
        System.out.println();
    }

    // Getters para pruebas unitarias
    public int[][] getDistMatrix() { return dist; }
    public int[][] getNextMatrix() { return next; }
    public int getSize() { return size; }
}
