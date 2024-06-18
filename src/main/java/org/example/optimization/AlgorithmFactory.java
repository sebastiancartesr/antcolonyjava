package org.example.optimization;

import org.example.dto.MyMap;
import org.example.optimization.impl.AntColony;
import org.example.optimization.impl.AntColonyGustavo;

public final class AlgorithmFactory {
    public static AntColonyAlgorithm generarParametrosBase( final MyMap map) {
        return new AntColony(map);
    }
    public static AntColonyAlgorithm generarParametrosBaseGustavo( final MyMap map) {
        return new AntColonyGustavo(map);
    }

}
