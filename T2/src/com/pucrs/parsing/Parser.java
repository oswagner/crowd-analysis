package com.pucrs.parsing;

import com.pucrs.controller.Person;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {

    private final String paths_d_1 = "res/Paths_D (1).txt";
    private final String paths_d_2 = "res/Paths_D (2).txt";
    private final String paths_d_3 = "res/Paths_D (3).txt";
    private final String paths_d_4 = "res/Paths_D (4).txt";

    private final String germany = "res/germany_1.txt";
    private final String portugal = "res/portugal.txt";
    private final String spain = "res/spain.txt";
    private final String turkey = "res/turkey.txt";

    public static List<Person> personList;

    private final File file = new File(paths_d_1);

    // probably not working :(
    private final Float COORDINATES_SCALING_FACTOR = 1f;

    public static Integer maxWidth = 0;
    public static Integer maxHeight = 0;

    public Parser() {
        personList = new ArrayList<>();
    }

    public void parseFile() {
        System.out.println("\n|--start parsing -----------------------------------");

        // define matcher patterns
        Pattern px = Pattern.compile("\\(\\d*");        // x coordinates
        Pattern py = Pattern.compile(",\\d*,");         // y coordinates

        // declare matcher
        Matcher m;

        // str receives the input stream
        String str;

        // person counter
        int personNumber = 0;

        // begin read
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {

            // discard first line
            str = br.readLine();

            while ((str = br.readLine()) != null) {
                // loop-long list for coords
                List<Coords> coordsList = new ArrayList<>();

                // create lists for x & y coordinates
                List<Float> x = new ArrayList<>();
                List<Float> y = new ArrayList<>();

                // apply x matcher
                m = px.matcher(str);
                while (m.find()) {
                    // get x coordinates
                    x.add(new Float(Float.parseFloat((m.group().substring(1, m.group().length())))));
                    // discover max width
                    if (maxWidth < x.get(x.size()-1)) {
                        maxWidth = x.get(x.size()-1).intValue();
                    }
                }

                // apply y matcher
                m = py.matcher(str);
                while (m.find()) {
                    // get y coordinates
                    y.add(new Float(Float.parseFloat(m.group().substring(1, m.group().length() - 1))));
                    // discover height
                    if (maxHeight < y.get(y.size()-1)) {
                        maxHeight = y.get(y.size()-1).intValue();
                    }
                }

                // prepare coordsList
                for (int i = 0; i < x.size(); i++) {
                    Coords temp = new Coords(x.get(i), y.get(i));
                    temp.setIdOwner(personNumber);
                    coordsList.add(temp);
                }

                // create Person and add it to personList
                personList.add(new Person(coordsList, personNumber));

                personNumber++;
            }
            br.close();

            // if a scaling factor is present, apply it to the viewport
            maxHeight = maxHeight * COORDINATES_SCALING_FACTOR.intValue();
            maxHeight = maxHeight * COORDINATES_SCALING_FACTOR.intValue();

            System.out.println("|--people discovered: " + personList.size());
            System.out.println("|--maxWidth: " + maxWidth);
            System.out.println("|--maxHeight: " + maxHeight);
            System.out.println("|---------------------------------------------------");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void printPersonList() {

        System.out.println("\n|--start print ------------------------------------");
        for (Person p : personList) {
            System.out.println("| " + p.toString());
        }
        System.out.println("----------------------------------------------------");
    }

    public void applyScaling() {
        for (Person person : personList) {
            for (Coords coords : person.getCoordsList()) {
                coords.setX(coords.getX()+ COORDINATES_SCALING_FACTOR);
                coords.setY(coords.getY()+ COORDINATES_SCALING_FACTOR);
            }
        }
    }

    //
    public void centerCrowdOn(int personIndex) {
        Float xCoord = personList.get(personIndex).getCurrentCoord().getX();
        Float yCoord = personList.get(personIndex).getCurrentCoord().getY();
        for (Person person : personList) {
            if (person.getId() != personIndex) {
                for (Coords coords : person.getCoordsList()) {
                    coords.setX(coords.getX()-xCoord);
                    coords.setY(coords.getY()-yCoord);
                }
            }
        }
    }
}