package org.example.optimization.impl;

import org.example.Parameters;
import org.example.dto.*;
import org.example.helper.NodeHalpers;
import org.example.optimization.AntColonyAlgorithm;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

import static org.example.Parameters.ANT_COLONY_LENGTH;

public class AntColonyGustavo implements AntColonyAlgorithm {
    private final MyMap map;
    private Tour bestTour = null;
    private final List<Tour> colony = new ArrayList<>();
    private final int[][] pheromone;
    public AntColonyGustavo(final MyMap map) {

        this.map = map;
        this.pheromone =  new int[map.getLength()][map.getLength()];
    }

    @Override
    public void process() {
        Random engine = new Random();
        for (int i = 0; i < ANT_COLONY_LENGTH; i++) {
            AntGustavo ant = new AntGustavo(map, engine);
            while (ant.mover());
            int[] ruta = ant.getRutaSeguidaArray();
            Node[] nodeRoute = createTour(map, ruta);
            Tour newTour = new Tour(nodeRoute, map);
            newTour.findOptimalSolution(bestTour);
            if (bestTour == null || newTour.getRouteCost() < bestTour.getRouteCost()) {
                bestTour = newTour;
            }
            colony.add(newTour);
        }
    }


    @Override
    public void processThreads() {

    }

    @Override
    public void calculatePheromone() {

    }

    @Override
    public void result() {
        System.out.println("Colonias");
        System.out.println("Mejor valor "+ bestTour.getRouteCost());
        for (int i = 0; i < colony.size(); i++) {
            int n = i+1;
            System.out.println("Colonia "+n);
            //colony.get(i).printRoute();
            System.out.println("Costo "+ colony.get(i).getRouteCost());
        }
    }
    private Node[] generateNodeRoute(List<Integer> ruta) {
        Node[] nodes = new Node[ruta.size()]; // Crear un nuevo arreglo de nodos

        int largo = ruta.size() - 1;
        int posicion = 0;
        int anteriorId = ruta.get(0);

        for (int i = 1; i < ruta.size(); i++) {
            int currentId = ruta.get(i);
            if (posicion == largo) posicion = 0;

            Node currentNode = new Node(currentId);
            currentNode.setPosition(posicion);
            nodes[currentId] = currentNode;

            Node previousNode = nodes[anteriorId];
            currentNode.setPrevious(previousNode);
            previousNode.setNext(currentNode);

            if (i == ruta.size() - 1) {
                currentNode.setNext(nodes[ruta.get(1)]);
            }

            anteriorId = currentId;
            posicion++;
        }

        return nodes; // Devolver el arreglo de nodos generado
    }
    public static Node[] createTour(final MyMap map, final int[] route) {
        Node[] nodesRoute = new Node[route.length];
        Node nextNode = null;
        for (int i = 0; i < route.length - 1; i++) {
            int pos = route[i];
            Node actualNode = nodesRoute[pos];
            if (actualNode == null) {
                actualNode = new Node(pos, null, null, i);
                nodesRoute[pos] = actualNode;
            }
            pos = route[i + 1];
            nextNode = new Node(pos, null, actualNode, i + 1);
            actualNode.next = nextNode;
            nodesRoute[pos] = nextNode;
        }
        nextNode.next = nodesRoute[0];
        nodesRoute[0].previous = nextNode;
        return nodesRoute;
    }
    public static Map<String, List<Integer>> findMissingAndDuplicateNumbers(int[] inputArray) {
        // Usamos un Map para contar las ocurrencias de cada número
        Map<Integer, Integer> countMap = new HashMap<>();
        for (int num : inputArray) {
            countMap.put(num, countMap.getOrDefault(num, 0) + 1);
        }

        List<Integer> missingNumbers = new ArrayList<>();
        List<Integer> duplicateNumbers = new ArrayList<>();

        // Encontrar los números faltantes y duplicados
        for (int i = 0; i <= 52; i++) {
            if (!countMap.containsKey(i)) {
                missingNumbers.add(i);
            } else if (countMap.get(i) > 1) {
                duplicateNumbers.add(i);
            }
        }

        // Crear un Map para retornar ambas listas
        Map<String, List<Integer>> result = new HashMap<>();
        result.put("missingNumbers", missingNumbers);
        result.put("duplicateNumbers", duplicateNumbers);

        return result;
    }
}
