package org.example;

import org.example.dto.AntGustavo;
import org.example.dto.MyMap;
import org.example.helper.MapHelper;
import org.example.optimization.AntColonyAlgorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.example.helper.MapHelper.newMapGenerate;
import static org.example.optimization.AlgorithmFactory.generarParametrosBase;
import static org.example.optimization.AlgorithmFactory.generarParametrosBaseGustavo;

public class Main {
    public static void main(String[] args)  throws Exception {
        System.out.println("Hello world!");
        final MyMap mapFromJson = newMapGenerate(MapHelper.MAP_ATT532_JSON);
        mapFromJson.computarCandidatos(Parameters.CANDIDATES_LENGTH);
        AntColonyAlgorithm algoritmoBase = generarParametrosBaseGustavo(mapFromJson);
        algoritmoBase.process();
        algoritmoBase.result();

        //AntColonyAlgorithm algoritmoBase = generarParametrosBase(mapFromJson);
        //algoritmoBase.process();
        //algoritmoBase.result();
        System.out.println("fin");
        //final Ant hormiga = new Ant(mapFromJson);
        //hormiga.generateRoute();

    }
}