package org.example.optimization.impl;

import org.example.Parameters;
import org.example.dto.Ant;
import org.example.dto.MyMap;
import org.example.dto.Node;
import org.example.dto.Tour;
import org.example.optimization.AntColonyAlgorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.example.Parameters.ANT_COLONY_LENGTH;

public class AntColony implements AntColonyAlgorithm {
    private final MyMap map;
    //private final List<Ant> colony = new ArrayList<>();
    private Tour bestTour = null;
    private final List<Tour> colony = new ArrayList<>();
    private final ExecutorService executor;
    private final CountDownLatch latch;
    public AntColony(final MyMap map) {
        this.map = map;
        this.executor = Executors.newFixedThreadPool(Parameters.THREADS);
        this.latch = new CountDownLatch(ANT_COLONY_LENGTH);
    }
    @Override
    public void process() {
        for (int i = 0; i < ANT_COLONY_LENGTH; i++) {
            Ant ant = new Ant(map);
            ant.generateRoute();
            Tour newTour = new Tour(ant.getRoutNode(), map);
            if (bestTour == null || newTour.getRouteCost() < bestTour.getRouteCost()) {
                bestTour = newTour;
            }
            colony.add(newTour);
        }
    }

    @Override
    public void processThreads() {
        try {
            while (colony.size() < ANT_COLONY_LENGTH) {
                executor.execute(() -> {
                    Ant ant = new Ant(map);
                    ant.generateRoute();
                    Tour newTour = new Tour(ant.getRoutNode(), map);
                    synchronized (this) {
                        if (bestTour == null || newTour.getRouteCost() < bestTour.getRouteCost()) {
                            bestTour = newTour;
                        }
                        colony.add(newTour);
                    }
                    latch.countDown(); // Marcar que un hilo ha completado su trabajo
                });
            }
            latch.await(); // Esperar a que todos los hilos completen su trabajo
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }
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


}
