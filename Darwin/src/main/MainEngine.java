import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.Random;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class MainEngine {
    static int width, height;
    static int jWidth, jHeight;
    static int startAnimals;
    static double startEnergy;
    static int movementCost, plantEnergy;
    static int mapNumber;
    static int sleepTime;
    static long fixedSeed;
    static int maxWindowWidth, maxWindowHeight;
    static InfoPanel[] infoPanels;
    static Engine[] engines;
    static String statFilename = null;

    public static void main(String[] args) throws IOException, ParseException {
        readParameters();

        infoPanels = new InfoPanel[mapNumber];
        engines = new Engine[mapNumber];

        Random seedGen = new Random();
        long seed = fixedSeed;
        for(int i=0; i<mapNumber; i++) {
            if(fixedSeed == 0) seed = seedGen.nextInt();
            engines[i] = new Engine(width, height, jWidth, jHeight, seed, i);
        }

        JFrame window = new JFrame();
        JPanel contentPane = new JPanel(new GridLayout(1,2));
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        for(int i=0; i<mapNumber; i++) {
            infoPanels[i] = new InfoPanel(i);
            contentPane.add(infoPanels[i].getPanel());
        }

        window.setContentPane(contentPane);
        window.setSize(Math.min(250 * mapNumber, maxWindowWidth), Math.min(600, maxWindowHeight));
        window.setVisible(true);

        for(int i=0; i<mapNumber; i++) engines[i].start();
    }

    private static void readParameters() throws IOException, ParseException {
        Object jsonData = new JSONParser().parse(new FileReader("./parameters.json"));
        JSONObject inputData = (JSONObject) jsonData;
        width = (int) (long) inputData.get("width");
        height = (int) (long) inputData.get("height");
        jWidth = (int) (long) inputData.get("jungleWidth");
        jHeight = (int) (long) inputData.get("jungleHeight");
        startAnimals = (int) (long) inputData.get("startAnimals");
        startEnergy = (int) (long) inputData.get("startEnergy");
        movementCost = (int) (long) inputData.get("movementCost");
        plantEnergy = (int) (long) inputData.get("plantEnergy");
        mapNumber = (int) (long) inputData.get("numberOfMaps");
        sleepTime = (int) (long) inputData.get("sleepTime");
        fixedSeed = (int) (long) inputData.get("fixedSeed");
        maxWindowWidth = (int) (long) inputData.get("maxWindowWidth");
        maxWindowHeight = (int) (long) inputData.get("maxWindowHeight");
    }

    public static void select(Field field) {
        int mapId = identify(field);
        Animal selected = field.getFittest();
        infoPanels[mapId].select(selected);
        engines[mapId].reselectField();
    }

    public static int identify(Field field) {
        int mapId = 0;
        for(int i=0; i<mapNumber; i++) {
            if(engines[i].getField(field.getId()) == field) {
                mapId = i;
                break;
            }
        }
        return mapId;
    }

}
