import networkx as nx
import sys
import os


def load_graph(filename: str) -> nx.DiGraph:
    """
    Lee el archivo guategrafo.txt y construye un DiGraph de NetworkX.
    Formato: Ciudad1 Ciudad2 KM
    """
    G = nx.DiGraph()

    if not os.path.exists(filename):
        print(f"  [ERROR] Archivo no encontrado: {filename}")
        sys.exit(1)

    edges_added = 0
    with open(filename, "r", encoding="utf-8") as f:
        for line_num, line in enumerate(f, 1):
            line = line.strip()
            if not line:
                continue

            parts = line.rsplit(" ", 1)  # Separar por el último espacio (el peso)
            if len(parts) < 2:
                print(f"  [WARN] Línea {line_num} ignorada: '{line}'")
                continue

            cities_part, weight_str = parts[0].strip(), parts[1].strip()

            try:
                weight = int(weight_str)
            except ValueError:
                print(f"  [WARN] Línea {line_num}: peso inválido '{weight_str}'")
                continue

            # Separar ciudad1 y ciudad2
            city_parts = cities_part.split()
            if len(city_parts) < 2:
                print(f"  [WARN] Línea {line_num}: ciudades inválidas")
                continue

            city1 = city_parts[0]
            city2 = " ".join(city_parts[1:])

            G.add_edge(city1, city2, weight=weight)
            edges_added += 1

    print(f"  Archivo leído: {edges_added} rutas cargadas.")
    print(f"  Nodos: {G.number_of_nodes()} ciudades | Arcos: {G.number_of_edges()}")
    return G


def compute_floyd(G: nx.DiGraph) -> dict:
    """
    Ejecuta Floyd-Warshall usando NetworkX.
    Retorna el diccionario de distancias más cortas: dist[u][v]
    """
    # floyd_warshall retorna dict de dicts con distancias
    dist = dict(nx.floyd_warshall(G, weight="weight"))
    return dist


def get_center(G: nx.DiGraph, dist: dict) -> str | None:
    """
    Calcula el centro del grafo dirigido usando la definición del libro:
      1. Para cada columna (vértice destino v), calcular excentricidad:
         max{ dist[w][v] } para todo w != v
      2. El centro es el vértice con excentricidad mínima.
    """
    INF = float("inf")
    min_ecc = INF
    center = None

    nodes = list(G.nodes())

    for v in nodes:
        max_dist = 0
        reachable = True

        for w in nodes:
            if w == v:
                continue
            d = dist.get(w, {}).get(v, INF)
            if d == INF:
                reachable = False
                break
            if d > max_dist:
                max_dist = d

        if not reachable:
            continue

        if max_dist < min_ecc:
            min_ecc = max_dist
            center = v

    return center


def print_apsp_matrix(G: nx.DiGraph, dist: dict):
    """Muestra la matriz APSP de Floyd-Warshall."""
    INF = float("inf")
    nodes = sorted(G.nodes())
    col_w = 14

    print("\n=== MATRIZ APSP (Floyd-Warshall) ===")
    print(f"{'':15}", end="")
    for n in nodes:
        label = n[:col_w - 1] if len(n) >= col_w else n
        print(f"{label:<{col_w}}", end="")
    print()
    print("-" * (15 + col_w * len(nodes)))

    for u in nodes:
        print(f"{u[:14]:<15}", end="")
        for v in nodes:
            d = dist.get(u, {}).get(v, INF)
            val = "INF" if d == INF else str(int(d))
            print(f"{val:<{col_w}}", end="")
        print()
    print()


def print_center_info(G: nx.DiGraph, dist: dict):
    """Muestra excentricidades y el centro del grafo."""
    INF = float("inf")
    nodes = sorted(G.nodes())

    print("\n=== CENTRO DEL GRAFO ===")
    print(f"  {'Ciudad':<25} {'Excentricidad'}")
    print("  " + "-" * 40)

    for v in nodes:
        max_dist = 0
        reachable = True
        for w in nodes:
            if w == v:
                continue
            d = dist.get(w, {}).get(v, INF)
            if d == INF:
                reachable = False
                break
            if d > max_dist:
                max_dist = d

        ecc_str = str(int(max_dist)) if reachable else "∞ (no alcanzable)"
        print(f"  {v:<25} {ecc_str}")

    center = get_center(G, dist)
    print()
    if center:
        print(f"  ► Centro del grafo: {center}")
    else:
        print("  ► No se puede determinar el centro (grafo no fuertemente conexo).")
    print()


def shortest_path_menu(G: nx.DiGraph, dist: dict):
    """Consulta ruta más corta entre dos ciudades."""
    INF = float("inf")
    from_city = input("  Ciudad origen:  ").strip()
    to_city = input("  Ciudad destino: ").strip()

    if from_city not in G.nodes():
        print(f"  [!] Ciudad '{from_city}' no existe en el grafo.\n")
        return
    if to_city not in G.nodes():
        print(f"  [!] Ciudad '{to_city}' no existe en el grafo.\n")
        return

    d = dist.get(from_city, {}).get(to_city, INF)

    print("\n=== RUTA MÁS CORTA ===")
    print(f"  Origen:  {from_city}")
    print(f"  Destino: {to_city}")

    if d == INF:
        print("  No existe ruta entre estas ciudades.")
    else:
        print(f"  Distancia total: {int(d)} KM")
        try:
            path = nx.dijkstra_path(G, from_city, to_city, weight="weight")
            print(f"  Ruta: {' -> '.join(path)}")
        except nx.NetworkXNoPath:
            print("  (No se pudo reconstruir la ruta)")
    print()


def modify_graph_menu(G: nx.DiGraph) -> dict:
    """Modifica el grafo y retorna nueva matriz dist."""
    print("\n--- Modificar Grafo ---")
    print("  a. Interrumpir tráfico (eliminar arco)")
    print("  b. Establecer nueva conexión (agregar arco)")
    choice = input("  Seleccione: ").strip().lower()

    if choice == "a":
        from_city = input("  Ciudad origen:  ").strip()
        to_city = input("  Ciudad destino: ").strip()
        if G.has_edge(from_city, to_city):
            G.remove_edge(from_city, to_city)
            print(f"  Arco eliminado: {from_city} -> {to_city}")
        else:
            print(f"  [!] No existe conexión directa entre '{from_city}' y '{to_city}'.")
    elif choice == "b":
        from_city = input("  Ciudad origen:  ").strip()
        to_city = input("  Ciudad destino: ").strip()
        weight_str = input("  Distancia (KM): ").strip()
        try:
            weight = int(weight_str)
            G.add_edge(from_city, to_city, weight=weight)
            print(f"  Conexión agregada: {from_city} -> {to_city} ({weight} KM)")
        except ValueError:
            print("  [!] Distancia inválida.")
            return compute_floyd(G)
    else:
        print("  [!] Opción inválida.")
        return compute_floyd(G)

    print("\n  Recalculando Floyd-Warshall...")
    dist = compute_floyd(G)
    center = get_center(G, dist)
    print(f"centro del grafo: {center if center else 'no determinable'}")
    return dist


def list_cities(G: nx.DiGraph):
    nodes = sorted(G.nodes())
    print(f"\nciudades en el grafo ({len(nodes)}) ===")
    for i, city in enumerate(nodes, 1):
        print(f"  {i:3}. {city}")
    print()


def main():
    filename = "data/guategrafo.txt"
    print(f"Cargando grafo desde: {filename}")
    G = load_graph(filename)

    print("\nEjecutando Floyd-Warshall con NetworkX...")
    dist = compute_floyd(G)
    print_apsp_matrix(G, dist)
    print(f"  Centro del grafo: {get_center(G, dist) or 'No determinable'}\n")

    while True:
        print("=============================================")
        print("                    red vial                 ")
        print("=============================================")
        print("1. Consultar ruta más corta entre ciudades  ")
        print("2. Ver centro del grafo                     ")
        print("3. Modificar grafo (agregar/eliminar arco)  ")
        print("4. Ver matriz APSP                          ")
        print("5. Listar todas las ciudades                ")
        print("6. Salir                                    ")
        print("=============================================")

        option = input("  Seleccione una opción: ").strip()

        if option == "1":
            shortest_path_menu(G, dist)
        elif option == "2":
            print_center_info(G, dist)
        elif option == "3":
            dist = modify_graph_menu(G)
        elif option == "4":
            print_apsp_matrix(G, dist)
        elif option == "5":
            list_cities(G)
        elif option == "6":
            print("\nadios")
            break
        else:
            print("opcion invalida.\n")


if __name__ == "__main__":
    main()
