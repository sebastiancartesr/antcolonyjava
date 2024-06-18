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
    public AntColonyGustavo(final MyMap map) {
        this.map = map;
    }

    @Override
    public void process() {
        Random engine = new Random();
        for (int i = 0; i < ANT_COLONY_LENGTH; i++) {
            AntGustavo ant = new AntGustavo(map,engine);
            while (ant.mover()>0);
            int[] ruta = ant.getRutaSeguidaArray();
            List<Integer> faltantes = findMissingNumbers(ruta);
            Node[] nodeRoute = createTour(map,ruta);
            NodeHalpers nodeHelper = new NodeHalpers();
            boolean conexa = nodeHelper.isConexa(nodeRoute);

            if(!conexa){
                System.out.println("error");
                break;
            }
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
    public static Node[]  createTour(final MyMap map, final int[] route) {
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
    private static List<Integer> findMissingNumbers(int[] inputArray) {
        Set<Integer> inputSet = new HashSet<>();
        for (int num : inputArray) {
            inputSet.add(num);
        }

        List<Integer> missingNumbers = new ArrayList<>();

        for (int i = 0; i <= 52; i++) {
            if (!inputSet.contains(i)) {
                missingNumbers.add(i);
            }
        }

        return missingNumbers;
    }
}
