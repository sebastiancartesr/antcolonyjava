package org.example.dto;

import lombok.Getter;
import org.example.Parameters;

import java.util.*;

@Getter
public class MyMap {
    private final String name;
    private final int[][] distancesMap;
    private final List<List<Integer>> nodesCandidates;
    private List<List<Double>> probabilidadInicial;
    private List<List<Integer>> candidatosDeNodo;
    public MyMap(final String name, final int[][] distancesMap) {
        this.name = name;
        this.distancesMap = distancesMap;
        this.probabilidadInicial = computarProbabilidades(distancesMap);
        this.nodesCandidates = this.computeCandidates(distancesMap);
    }
    @Getter
    private static class Pair {
        int distance;
        int node;

        Pair(int distance, int node) {
            this.distance = distance;
            this.node = node;
        }

    }
    public int getNodesDistance(Node a, Node b) {
        return this.distancesMap[a.id][b.id];
    }
    public int getCost(int a, int b) {
        return this.distancesMap[a][b];
    }
    public int getLength() {
        return this.distancesMap.length;
    }
    public List<Integer> getNodeCandidates(int nodeId) {
        return this.nodesCandidates.get(nodeId);
    }

    //computar tipo gustavo
    public List<List<Double>> computarProbabilidades( int[][] data) {
        int n = data.length;
        probabilidadInicial = new ArrayList<>(n);

        for (int i = 0; i < n; i++) {
            probabilidadInicial.add(new ArrayList<>(n));
            for (int j = 0; j < n; j++) {
                probabilidadInicial.get(i).add(0.0);
            }
        }

        for (int i = 0; i < n - 1; i++) {
            for (int j = i + 1; j < n; j++) {
                double p = 1.0 / Math.pow(data[i][j], Parameters.GAMMA);
                probabilidadInicial.get(i).set(j, p);
                probabilidadInicial.get(j).set(i, p);
            }
        }

        return probabilidadInicial;
    }
    public void computarCandidatos(int TAMVEC) {
        int n = distancesMap.length;
        candidatosDeNodo = new ArrayList<>(n);

        for (int i = 0; i < n; i++) {
            candidatosDeNodo.add(new ArrayList<>());
            PriorityQueue<Pair> cola = new PriorityQueue<>(Comparator.comparingInt(Pair::getDistance));

            for (int j = 0; j < n; j++) {
                if (i == j) continue;
                cola.add(new Pair(distancesMap[i][j], j));
            }

            for (int k = 0; k < TAMVEC; k++) {
                if (!cola.isEmpty()) {
                    Pair aux = cola.poll();
                    candidatosDeNodo.get(i).add(aux.node);
                }
            }
        }
    }

    private List<List<Integer>> computeCandidates(int[][] cityDistances) {
        int n = cityDistances.length;
        List<List<Integer>> nodesCandidates = new ArrayList<>();
        // Inicializar la lista de listas para almacenar los índices de candidatos.
        for (int i = 0; i < n; i++) {
            nodesCandidates.add(new ArrayList<>());
        }
        for (int i = 0; i < n; i++) {
            // Lista para mantener los costes y los índices de cada ciudad relacionada.
            List<CostIndexPair> queue = new ArrayList<>();
            for (int j = 0; j < n; j++) {
                if (i == j) {
                    continue;
                }
                queue.add(new CostIndexPair(cityDistances[i][j], j));
            }
            // Ordenar la lista de CostIndexPair basado en el coste de menor a mayor.
            Collections.sort(queue);
            // Agregar los índices de los candidatos de menor coste a nodesCandidates.
            for (int k = 0; k < Parameters.CANDIDATES_LENGTH && k < queue.size(); k++) {
                nodesCandidates.get(i).add(queue.get(k).index);
            }
        }
        return nodesCandidates;
    }
    private static class CostIndexPair implements Comparable<CostIndexPair> {

        int cost;
        int index;

        public CostIndexPair(int cost, int index) {
            this.cost = cost;
            this.index = index;
        }

        @Override
        public int compareTo(CostIndexPair other) {
            return Integer.compare(this.cost, other.cost);
        }

    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder("Distances Map for ");
        builder.append(name).append(":\n");
        for (int i = 0; i < distancesMap.length; i++) {
            builder.append("\t[");
            for (int j = 0; j < distancesMap[i].length; j++) {
                builder.append(distancesMap[i][j]).append(",");
            }
            builder.append("],\n");
        }
        return builder.toString();
    }
}
