package org.example.dto;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
@Getter
public class AntGustavo {
    private List<Integer> rutaSeguida;
    private List<Boolean> visitadas;
    private int idCiudadActual;
    private MyMap mapa;
    private Random engine;
    private int largo;

    public AntGustavo(MyMap mapa, Random engine) {
        this.mapa = mapa;
        this.engine = engine;
        this.largo = mapa.getDistancesMap().length;
        this.visitadas = new ArrayList<>(IntStream.range(0, largo).mapToObj(i -> false).collect(Collectors.toList()));
        this.rutaSeguida = new ArrayList<>();

        this.idCiudadActual = engine.nextInt(largo);
        this.visitadas.set(this.idCiudadActual, true);
        this.rutaSeguida.add(this.idCiudadActual);
    }
    public int[] getRutaSeguidaArray() {
        int[] rutaArray = new int[rutaSeguida.size()];
        for (int i = 0; i < rutaSeguida.size(); i++) {
            rutaArray[i] = rutaSeguida.get(i);
        }
        return rutaArray;
    }
    public int mover() {
        List<Integer> noVisitadas = new ArrayList<>();
        List<Double> probabilidad = new ArrayList<>();

        for (int cand = 0; cand < largo; cand++) {
            if (!visitadas.get(cand)) {
                noVisitadas.add(cand);
                probabilidad.add(mapa.getProbabilidadInicial().get(idCiudadActual).get(cand));
            }
        }

        if (noVisitadas.isEmpty()) return 0;

        double[] probabilities = probabilidad.stream().mapToDouble(d -> d).toArray();
        int nuevaCiudad = noVisitadas.get(sampleIndexBasedOnProbability(probabilities, engine));

        idCiudadActual = nuevaCiudad;
        rutaSeguida.add(nuevaCiudad);
        visitadas.set(nuevaCiudad, true);

        return 1;
    }

    public void mostrarRuta() {
        rutaSeguida.forEach(ciudad -> System.out.print(ciudad + " "));
        System.out.println();
    }

    private int sampleIndexBasedOnProbability(double[] probabilities, Random engine) {
        double total = 0.0;
        for (double prob : probabilities) total += prob;

        double value = engine.nextDouble() * total;
        for (int i = 0; i < probabilities.length; i++) {
            value -= probabilities[i];
            if (value <= 0.0) return i;
        }
        return probabilities.length - 1;  // should not reach here
    }
}