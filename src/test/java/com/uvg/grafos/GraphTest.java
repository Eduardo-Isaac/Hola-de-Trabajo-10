package com.uvg.grafos;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.List;


public class GraphTest {

    private Graph graph;

    @Before
    public void setUp() {
        graph = new Graph(10);
        graph.addNode("a");
        graph.addNode("b");
        graph.addNode("c");
        graph.addNode("d");
        graph.addNode("e");

        graph.addEdge("a", "b", 1);
        graph.addEdge("b", "c", 2);
        graph.addEdge("c", "d", 2);
        graph.addEdge("d", "c", 3);
        graph.addEdge("c", "e", 4);
        graph.addEdge("e", "d", 5);
    }

// pruebas grafo

    @Test
    public void testAddNode_ShouldIncreaseSize() {
        Graph g = new Graph(5);
        assertEquals(0, g.getSize());
        g.addNode("Ciudad1");
        assertEquals(1, g.getSize());
        g.addNode("Ciudad2");
        assertEquals(2, g.getSize());
    }

    @Test
    public void testAddNode_DuplicateNotAllowed() {
        Graph g = new Graph(5);
        assertTrue(g.addNode("Mixco"));
        assertFalse(g.addNode("Mixco")); // duplicado
        assertEquals(1, g.getSize());
    }

    @Test
    public void testAddNode_CapacityLimit() {
        Graph g = new Graph(2);
        assertTrue(g.addNode("A"));
        assertTrue(g.addNode("B"));
        assertFalse(g.addNode("C")); // excede capacidad
    }

    @Test
    public void testNodeExists_ShouldReturnCorrectly() {
        assertTrue(graph.nodeExists("a"));
        assertTrue(graph.nodeExists("c"));
        assertFalse(graph.nodeExists("z"));
        assertFalse(graph.nodeExists(""));
    }

    @Test
    public void testAddEdge_ShouldSetCorrectWeight() {
        int[][] matrix = graph.getAdjMatrix();
        int ia = graph.getIndex("a");
        int ib = graph.getIndex("b");
        assertEquals(1, matrix[ia][ib]);
    }

    @Test
    public void testAddEdge_DiagonalIsZero() {
        int[][] matrix = graph.getAdjMatrix();
        for (int i = 0; i < graph.getSize(); i++) {
            assertEquals(0, matrix[i][i]);
        }
    }

    @Test
    public void testAddEdge_NoEdgeIsINF() {
        // a no tiene arco directo a c
        int[][] matrix = graph.getAdjMatrix();
        int ia = graph.getIndex("a");
        int ic = graph.getIndex("c");
        assertEquals(Graph.INF, matrix[ia][ic]);
    }

    @Test
    public void testAddEdge_InvalidNode_ReturnsFalse() {
        assertFalse(graph.addEdge("a", "NoExiste", 10));
        assertFalse(graph.addEdge("NoExiste", "b", 10));
    }

    @Test
    public void testAddEdge_NegativeWeight_ReturnsFalse() {
        Graph g = new Graph(5);
        g.addNode("X");
        g.addNode("Y");
        assertFalse(g.addEdge("X", "Y", -5));
        assertFalse(g.addEdge("X", "Y", 0));
    }

    @Test
    public void testRemoveEdge_ShouldSetINF() {
        boolean removed = graph.removeEdge("a", "b");
        assertTrue(removed);
        int[][] matrix = graph.getAdjMatrix();
        int ia = graph.getIndex("a");
        int ib = graph.getIndex("b");
        assertEquals(Graph.INF, matrix[ia][ib]);
    }

    @Test
    public void testRemoveEdge_NonExistentEdge_ReturnsFalse() {
        // a -> c no existe directamente
        assertFalse(graph.removeEdge("a", "c"));
    }

    @Test
    public void testRemoveEdge_InvalidNode_ReturnsFalse() {
        assertFalse(graph.removeEdge("a", "NoExiste"));
        assertFalse(graph.removeEdge("NoExiste", "b"));
    }

    @Test
    public void testGetIndex_ShouldReturnCorrectIndex() {
        int ia = graph.getIndex("a");
        int ib = graph.getIndex("b");
        assertTrue(ia >= 0 && ia < graph.getSize());
        assertTrue(ib >= 0 && ib < graph.getSize());
        assertNotEquals(ia, ib);
    }

    @Test
    public void testGetIndex_NonExistent_ReturnsMinusOne() {
        assertEquals(-1, graph.getIndex("xyz"));
    }

    @Test
    public void testGetVertices_ShouldContainAllAdded() {
        List<String> v = graph.getVertices();
        assertTrue(v.contains("a"));
        assertTrue(v.contains("b"));
        assertTrue(v.contains("c"));
        assertTrue(v.contains("d"));
        assertTrue(v.contains("e"));
        assertEquals(5, v.size());
    }

    // ===== PRUEBAS DE FLOYD =====

    @Test
    public void testFloyd_ShortestDistance_DirectPath() {
        Floyd f = new Floyd(graph);
        // a -> b directo = 1
        assertEquals(1, f.getShortestDistance("a", "b", graph));
    }

    @Test
    public void testFloyd_ShortestDistance_IndirectPath() {
        Floyd f = new Floyd(graph);
        // a -> b -> c -> d = 1+2+2 = 5
        assertEquals(5, f.getShortestDistance("a", "d", graph));
        // a -> b -> c = 1+2 = 3
        assertEquals(3, f.getShortestDistance("a", "c", graph));
    }

    @Test
    public void testFloyd_ShortestDistance_SameNode_IsZero() {
        Floyd f = new Floyd(graph);
        assertEquals(0, f.getShortestDistance("a", "a", graph));
        assertEquals(0, f.getShortestDistance("d", "d", graph));
    }

    @Test
    public void testFloyd_ShortestDistance_NoPath_IsINF() {
        Floyd f = new Floyd(graph);
        // Desde a no hay forma de llegar a... revisemos, a es fuente sin camino de retorno
        // a no puede recibir ningún nodo (nadie apunta a a)
        assertEquals(Graph.INF, f.getShortestDistance("b", "a", graph));
        assertEquals(Graph.INF, f.getShortestDistance("e", "a", graph));
    }

    @Test
    public void testFloyd_ShortestDistance_InvalidNode_IsINF() {
        Floyd f = new Floyd(graph);
        assertEquals(Graph.INF, f.getShortestDistance("a", "NoExiste", graph));
        assertEquals(Graph.INF, f.getShortestDistance("NoExiste", "b", graph));
    }

    @Test
    public void testFloyd_PathReconstruction_DirectEdge() {
        Floyd f = new Floyd(graph);
        List<String> path = f.getPath("a", "b", graph);
        assertEquals(2, path.size());
        assertEquals("a", path.get(0));
        assertEquals("b", path.get(1));
    }

    @Test
    public void testFloyd_PathReconstruction_MultiHop() {
        Floyd f = new Floyd(graph);
        // a -> b -> d (shortest)
        List<String> path = f.getPath("a", "d", graph);
        assertTrue(path.size() >= 2);
        assertEquals("a", path.get(0));
        assertEquals("d", path.get(path.size() - 1));
    }

    @Test
    public void testFloyd_PathReconstruction_NoPath_IsEmpty() {
        Floyd f = new Floyd(graph);
        List<String> path = f.getPath("b", "a", graph);
        assertTrue(path.isEmpty());
    }

    @Test
    public void testFloyd_SameNodePath_HasOneElement() {
        Floyd f = new Floyd(graph);
        List<String> path = f.getPath("c", "c", graph);
        assertEquals(1, path.size());
        assertEquals("c", path.get(0));
    }

    @Test
    public void testFloyd_AfterRemoveEdge_UpdatesCorrectly() {
        // Remover c->d y recalcular
        graph.removeEdge("c", "d");
        Floyd f = new Floyd(graph);
        // Sin c->d: a->b->c->e->d = 1+2+4+5 = 12
        assertEquals(12, f.getShortestDistance("a", "d", graph));
    }

    @Test
    public void testFloyd_AfterAddEdge_UpdatesCorrectly() {
        // Agregar a->e con peso 1 y recalcular
        graph.addNode("x"); // nodo extra no necesario pero valid
        graph.addEdge("a", "e", 1);
        Floyd f = new Floyd(graph);
        assertEquals(1, f.getShortestDistance("a", "e", graph));
    }

    // ===== PRUEBAS DE CENTRO =====

    @Test
    public void testCenter_BookExample_IsD() {
        // Basado en Example 6.11 del libro adjunto
        Floyd f = new Floyd(graph);
        String center = f.getCenter();
        // El centro debe ser 'd' con excentricidad 5
        assertEquals("d", center);
    }

    @Test
    public void testCenter_SingleNode_IsItself() {
        Graph g = new Graph(5);
        g.addNode("Solo");
        Floyd f = new Floyd(g);
        // Un solo nodo, excentricidad 0
        String center = f.getCenter();
        assertEquals("Solo", center);
    }

    @Test
    public void testCenter_NotStronglyConnected_ReturnsNull() {
        // Grafo donde algunos nodos no son alcanzables
        Graph g = new Graph(5);
        g.addNode("A");
        g.addNode("B");
        g.addNode("C");
        g.addEdge("A", "B", 5);
        // C está aislado, B no puede llegar a A
        Floyd f = new Floyd(g);
        // El centro puede ser null o algún nodo con excentricidad finita
        // En este caso B tiene excentricidad ∞ (C no alcanzable)
        // A tiene excentricidad ∞ (nadie llega a A)
        // Resultado esperado: null (ninguno es fuertemente conexo a todos)
        assertNull(f.getCenter());
    }

    @Test
    public void testCenter_SimpleLinearGraph() {
        // A -> B -> C (lineal)
        Graph g = new Graph(5);
        g.addNode("A");
        g.addNode("B");
        g.addNode("C");
        g.addEdge("A", "B", 1);
        g.addEdge("B", "C", 1);
        Floyd f = new Floyd(g);
        // Solo A puede llegar a todos... pero nadie llega de vuelta a A
        // El centro sería null en grafo no fuertemente conexo
        String center = f.getCenter();
        // En este grafo dirigido sin ciclos, el centro es null
        assertNull(center);
    }

    @Test
    public void testCenter_FullyConnectedSmall() {
        // Grafo bidireccional simple: A<->B<->C
        Graph g = new Graph(5);
        g.addNode("A");
        g.addNode("B");
        g.addNode("C");
        g.addEdge("A", "B", 1);
        g.addEdge("B", "A", 1);
        g.addEdge("B", "C", 2);
        g.addEdge("C", "B", 2);
        g.addEdge("A", "C", 3);
        g.addEdge("C", "A", 3);
        Floyd f = new Floyd(g);
        // B está más cerca de todos
        String center = f.getCenter();
        assertNotNull(center);
        assertEquals("B", center);
    }
}
