package com.uvg.grafos;
import java.io.*;
import java.util.*;


public class GraphFileReader {

    /**
     * Lee el archivo y construye el grafo.
     * @param filename Ruta al archivo .txt
     * @param capacity Capacidad máxima del grafo
     * @return Graph construido, o null si ocurre un error
     */
    public static Graph readGraph(String filename, int capacity) {
        Graph graph = new Graph(capacity);
        File file = new File(filename);

        if (!file.exists()) {
            System.out.println("archivo no encontrado: " + filename);
            return null;
        }

        int linesRead = 0;
        int edgesAdded = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNum = 0;

            while ((line = br.readLine()) != null) {
                lineNum++;
                line = line.trim();
                if (line.isEmpty()) continue;

                // Parsear: "Ciudad1 Ciudad2 KM"
                // El peso siempre es el ÚLTIMO token; las ciudades pueden tener espacios
                // Estrategia: separar el último token como peso, el resto como "Ciudad1 Ciudad2"
                int lastSpace = line.lastIndexOf(' ');
                if (lastSpace == -1) {
                    System.out.println("linea " + lineNum + " ignorada: " + line);
                    continue;
                }

                String citiesPart = line.substring(0, lastSpace).trim();
                String weightStr = line.substring(lastSpace + 1).trim();

                int weight;
                try {
                    weight = Integer.parseInt(weightStr);
                } catch (NumberFormatException e) {
                    System.out.println("linea " + lineNum + ": peso invalido '" + weightStr + "', ignorada");
                    continue;
                }


                String[] parts = citiesPart.split("\\s+");
                if (parts.length < 2) {
                    System.out.println("linea " + lineNum + ": no se pudieron separar las ciudades");
                    continue;
                }

                // Si hay exactamente 2 partes, cada una es una ciudad
                // Si hay más, la primera mitad es ciudad1 y la segunda ciudad2
                String city1, city2;
                if (parts.length == 2) {
                    city1 = parts[0];
                    city2 = parts[1];
                } else {
                    // Asumir primera palabra = ciudad1, resto = ciudad2
                    city1 = parts[0];
                    StringBuilder sb = new StringBuilder();
                    for (int i = 1; i < parts.length; i++) {
                        sb.append(parts[i]);
                        if (i < parts.length - 1) sb.append(" ");
                    }
                    city2 = sb.toString();
                }

                // Agregar nodos si no existen
                if (!graph.nodeExists(city1)) graph.addNode(city1);
                if (!graph.nodeExists(city2)) graph.addNode(city2);

                // Agregar arco
                if (graph.addEdge(city1, city2, weight)) {
                    edgesAdded++;
                }
                linesRead++;
            }

        } catch (IOException e) {
            System.out.println("error leyendo archivo: " + e.getMessage());
            return null;
        }

        System.out.println("archivo leido: " + linesRead + " rutas cargadas");
        System.out.println("nodos: " + graph.getSize() + "ciudades | arcos: " + edgesAdded);
        return graph;
    }
}
