package org.example.dto;

import lombok.Getter;
import lombok.Setter;
import org.example.Parameters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Getter
public class Tour {
    private int largo;
    private final MyMap map;
    private Node[] nodes;
    private int routeCost;
    public Tour(final Node[] nodes, final MyMap map) {
        this.map = map;
        this.nodes = nodes;
        this.routeCost = calculateTotalDistance(nodes,map.getDistancesMap());
    }
    public void findOptimalSolution(final Tour bestNodesSolution) {
        this.routeCost = this.calculateDistance();
        Node t0;
        Node t1;
        int cont = 0;
        final int endSearch = Parameters.GENETIC_SEARCH_ITERATIONS;
        while (cont < endSearch) {
            t0 = this.getRandomNode();
            final Node init = t0;
            while (true) {
                t1 = t0.next;
                if (bestNodesSolution == null) {
                    break;
                }
                if (bestNodesSolution.getNodeById(t0.id).next.id != t1.id
                        && bestNodesSolution.getNodeById(t0.id).previous.id != t1.id) {
                    break;
                }
                t0 = t0.next;
                if (t0 == init) {
                    t0 = this.getRandomNode();
                    t1 = t0.next;
                    break;
                }
            }
            final Movement movement = this.getMovement(t0, t1);
            if (movement != null) {
                if (movement.t4 == null) {
                    // movimiento 2-opt
                    this.makeMove(t0, t1, movement.t2, movement.t3);
                } else {
                    // movimiento 3-opt
                    this.makeMove(t0, t1, movement.t2, movement.t3);
                    this.makeMove(t0, movement.t3, movement.t4, movement.t5);
                }
                this.routeCost = this.routeCost - movement.revenue;
                cont = 0;
            } else {
                cont++;
            }
        }
    }
    public int calculateDistance() {
        int totalDistance = 0;
        Node currentNode = this.nodes[0];
        do {
            Node nextNode = currentNode.next;
            totalDistance += this.map.getNodesDistance(currentNode, nextNode);
            currentNode = nextNode;
        } while (currentNode != this.nodes[0]);
        return totalDistance;
    }
    public Node getRandomNode() {
        int nodeIndex = ThreadLocalRandom.current().nextInt(this.nodes.length);
        return this.nodes[nodeIndex];
    }
    public Node getNodeById(int nodeId) {
        return this.nodes[nodeId];
    }
    public static int calculateTotalDistance(Node[] nodes, int[][] cityDistances) {
        int totalDistance = 0;
        Node currentNode = nodes[0]; // El primer nodo (inicio de la ruta)

        do {
            Node nextNode = currentNode.next;
            totalDistance += cityDistances[currentNode.id][nextNode.id];
            currentNode = nextNode;
        } while (currentNode != nodes[0]); // Volver al primer nodo para cerrar el ciclo

        return totalDistance;
    }
    private void makeMove(final Node t0, final Node t1, final Node t2, final Node t3) {
        final int routeSize = this.nodes.length;
        int nodesQtyT3t1 = t3.position - t1.position;
        if (nodesQtyT3t1 < 0) {
            nodesQtyT3t1 += routeSize;
        }
        int nodesQtyT0t2 = t0.position - t2.position;
        if (nodesQtyT0t2 < 0) {
            nodesQtyT0t2 += routeSize;
        }
        if (t0.next == t1) {
            if (nodesQtyT3t1 <= nodesQtyT0t2) {
                // si esto se cumple, se debe mover desde t0 a t3, hasta llegar a t2
                this.move(t0, t1, t2, t3);
            } else {
                // si esto se cumple, se debe mover desde t3 a t1 hasta llegar a t2
                this.move(t3, t2, t1, t0);
            }
        } else {
            if (nodesQtyT3t1 <= nodesQtyT0t2) {
                this.move(t1, t0, t3, t2);
            } else {
                this.move(t2, t3, t0, t1);
            }
        }
    }
    private void move(final Node t0, final Node t1, final Node t2, final Node t3) {
        Node actualNodeMove = t1;
        int position = t3.position;
        final int routeSize = this.nodes.length;
        while (actualNodeMove != t2) {
            actualNodeMove.position = position;
            position--;
            if (position < 0) {
                position = routeSize - 1;
            }
            ;
            Node aux = actualNodeMove.next;
            actualNodeMove.next = actualNodeMove.previous;
            actualNodeMove.previous = aux;
            actualNodeMove = actualNodeMove.previous;
        }
        t0.next = t3;
        t3.previous = t0;
        t1.next = t2;
        t2.previous = t1;
    }
    private Movement getMovement(final Node t0, final Node t1) {
        int t0t1ActualCost = this.map.getNodesDistance(t0, t1);
        for (int t2CandidateID : this.map.getNodeCandidates(t1.id)) {
            final Node t2Candidate = this.nodes[t2CandidateID];
            if (t2Candidate == t1 || t2Candidate == t1.next || t2Candidate == t1.previous) {
                continue;
            }
            int t1t2NewCost = this.map.getNodesDistance(t1, t2Candidate);
            int g0 = t0t1ActualCost - t1t2NewCost;
            if (g0 <= 0) {
                // se descarta ya que no cumple por la ganancia 0
                continue;
            }
            final Node t3Candidate = t2Candidate.previous;
            int t2t3ActualCost = this.map.getNodesDistance(t2Candidate, t3Candidate);
            int t0t3NewCost = this.map.getNodesDistance(t0, t3Candidate);
            int g1 = g0 + t2t3ActualCost - t0t3NewCost;
            /*
            if (params.isMoveWithJustG0()) {
                final Movement movement = new Movement();
                movement.t2 = t2Candidate;
                movement.t3 = t3Candidate;
                movement.revenue = g1;
                return movement;
            }
             */
            if (g1 <= 0) {
                for (int t4CandidateID : this.map.getNodeCandidates(t3Candidate.id)) {
                    final Node t4Candidate = this.nodes[t4CandidateID];
                    if (t4Candidate == t3Candidate.next || t4Candidate == t3Candidate.previous) {
                        continue;
                    }
                    int t3t4NewCost = this.map.getNodesDistance(t3Candidate, t4Candidate);
                    int g1t4 = g0 + (t2t3ActualCost - t3t4NewCost);
                    if (g1t4 <= 0) {
                        continue;
                    }
                    Node t5Candidate = t4Candidate.previous;
                    if (this.between(t1, t3Candidate, t4Candidate)) {
                        t5Candidate = t4Candidate.next;
                    }
                    int t4t5ActualCost = this.map.getNodesDistance(t4Candidate, t5Candidate);
                    int t0t5NewCostCost = this.map.getNodesDistance(t5Candidate, t0);
                    int g2 = g1t4 + (t4t5ActualCost - t0t5NewCostCost);
                    if (g2 > 0) {
                        final Movement movement = new Movement();
                        movement.t2 = t2Candidate;
                        movement.t3 = t3Candidate;
                        movement.t4 = t4Candidate;
                        movement.t5 = t5Candidate;
                        movement.revenue = g2;
                        return movement;
                    }
                }
            } else {
                final Movement movement = new Movement();
                movement.t2 = t2Candidate;
                movement.t3 = t3Candidate;
                movement.revenue = g1;
                return movement;
            }
        }
        return null;
    }
    private boolean between(Node minor, Node mayor, Node between) {
        int betweenPos = between.position;
        int minorPos = minor.position;
        int mayorPos = mayor.position;
        return (minorPos <= betweenPos && betweenPos <= mayorPos)
                || (mayorPos < minorPos
                && (minorPos <= betweenPos || betweenPos <= mayorPos)
        );
    }
    private static class Movement {

        public Node t2;
        public Node t3;
        public Node t4;
        public Node t5;
        public int revenue;

    }

    public void printRoute(){
        List<Integer> ruta = new ArrayList<>();
        Node firstNode = nodes[0];
        Node aux = nodes[0];

        do{
            ruta.add(aux.id);
            aux= aux.next;
        }while(aux.next != firstNode);
        System.out.println(ruta);

    }
}
