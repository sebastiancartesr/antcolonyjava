package org.example.dto;

import lombok.Getter;
import org.example.Parameters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static org.example.Parameters.CANDIDATES_LENGTH;
@Getter
public class Ant {

    private final Node[] routNode;
    private final boolean[] visits;
    private int countVisits;
    private final MyMap map;
    public Ant(final MyMap map) {
        this.routNode = new Node[map.getLength()];
        this.visits =  new boolean[map.getLength()];
        this.countVisits = 0;
        this.map = map;
    }
    public void generateRoute() {
        Node currentNode = new Node(0,null,null,0); // Crear el nodo inicial
        routNode[countVisits] = currentNode;
        visits[0] = true;
        countVisits++;
        while (countVisits != map.getLength()) {
            List<Integer> candidates = computeCandidatesForPosition(currentNode.id);
            if (candidates.isEmpty()) {
                break; // Si no hay más candidatos, salir del bucle
            }
            int nextNodeIndex = selectWeightedRandom(candidates);
            Node aux = new Node(nextNodeIndex,null,currentNode,countVisits);
            routNode[aux.id] = aux;
            routNode[currentNode.id].next = aux;
            currentNode = aux;
            visits[nextNodeIndex] = true;
            countVisits++;
        }

        // Conectar el último nodo con el primero para completar el tour
        routNode[currentNode.id].next = routNode[0];
        routNode[0].previous = routNode[currentNode.id];
        //boolean conexa = isConexa(routNode);
        //System.out.println(conexa);
    }
    public static int selectWeightedRandom(List<Integer> numbers) {
        int n = numbers.size();
        // Crear un arreglo de pesos acumulativos
        double[] cumulativeWeights = new double[n];
        double totalWeight = 0.0;
        for (int i = 0; i < n; i++) {
            totalWeight += (n - i); // Peso decreciente para cada posición
            cumulativeWeights[i] = totalWeight;
        }
        // Generar un número aleatorio entre 0 y el peso total
        Random rand = new Random();
        double randomValue = rand.nextDouble() * totalWeight;
        // Seleccionar el número basado en el valor aleatorio
        for (int i = 0; i < n; i++) {
            if (randomValue < cumulativeWeights[i]) {
                return numbers.get(i);
            }
        }
        // Este punto nunca debería ser alcanzado si el algoritmo es correcto
        return -1;
    }
    private List<Integer> computeCandidatesForPosition( int position) {
        int n = map.getLength();
        List<CostIndexPair> queue = new ArrayList<>();
        // Recorrer la fila especificada y almacenar los costes y sus índices
        for (int j = 0; j < n; j++) {
            if (position == j || visits[j]) {
                continue;
            }
            queue.add(new CostIndexPair(map.getCost(position,j), j));
        }
        // Ordenar la lista de CostIndexPair basado en el coste de menor a mayor
        Collections.sort(queue);
        // Lista para almacenar los índices de los candidatos de menor coste
        List<Integer> candidates = new ArrayList<>();
        // Agregar los índices de los primeros CANDIDATES_LENGTH elementos
        int aux = 0;
        for (int k = 0; k < CANDIDATES_LENGTH && k < queue.size(); k++) {

            candidates.add(queue.get(k).index);
        }
        return candidates;
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
    private static boolean isConexa(Node[] nodos){
        Node inicio = nodos[0];
        Node actual = inicio.next;
        int cont=1;
        int pos = inicio.position;
        pos++;
        if (pos==nodos.length){
            pos=0;
        }
        while (actual!=inicio){
            if(pos!=actual.position){
                return false;
            }
            pos++;
            if (pos==nodos.length){
                pos=0;
            }
            cont++;
            if(actual.next.previous != actual){
                return false;
            }
            actual = actual.next;
            if(cont>nodos.length){
                return false;
            }
        }
        return cont==nodos.length;
    }
}
