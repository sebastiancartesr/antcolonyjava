package org.example.helper;

import org.example.dto.MyMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class MapHelper {
    public static final String MAP_ATT532 = "att532.dat";
    public static final String MAP_ATT532_JSON = "att532.json";
    public static final String MAP_BERLIN52 = "berlin52.dat";
    public static final String MAP_BERLIN52_JSON = "berlin52.json";
    private MapHelper() {
    }
    public static MyMap newMapGenerate(String fileName) throws IOException {
        try {
            // Lee el archivo JSON
            File file = new File("src/main/resources/"+fileName);
            FileInputStream fis = new FileInputStream(file);
            byte[] data = new byte[(int) file.length()];
            fis.read(data);
            fis.close();

            // Convierte el contenido del archivo a JSONArray
            String jsonContent = new String(data, "UTF-8");
            JSONArray jsonArray = new JSONArray(new JSONTokener(jsonContent));

            // Determina las dimensiones de la matriz (suponiendo que sea una matriz cuadrada)
            int size = jsonArray.length();
            int[][] matrix = new int[size][size];

            // Llena la matriz con los valores del JSONArray
            for (int i = 0; i < size; i++) {
                JSONArray rowArray = jsonArray.getJSONArray(i);
                for (int j = 0; j < size; j++) {
                    matrix[i][j] = rowArray.getInt(j);
                }
            }
            return new MyMap(fileName, matrix);
        } catch (IOException | JSONException e) {
            throw new IllegalArgumentException("El archivo al intentar leer el archivo.");
        }
    }
}
