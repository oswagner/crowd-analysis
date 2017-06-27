package com.pucrs.controller;

import com.pucrs.parsing.Coords;
import com.pucrs.parsing.Parser;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Controller {
    private static final Float MOVEMENT_SCALING_FACTOR = 3f;
    private static final Float APPROACH_STOP_RADIUS = 30f;
    private static final Float DANGER_ZONE_RADIUS = 50f;

    private List<Person> personList;

    private List<Coords> currentCoordsList;
    private List<Coords> nextCoordsList;

    private DangerZone dangerZone;

    public Controller() {
        personList = Parser.personList;
        currentCoordsList = new ArrayList<>();
        nextCoordsList = new ArrayList<>();

        // collects first set of coordinates
        nextCoordsList = personList.stream().map(p -> p.getNextCoord()).collect(Collectors.toList());
    }

    public void prepareNextCoords() {
        // keeps current position for reference
        currentCoordsList = new ArrayList(nextCoordsList);
        // receives next set of coords
        nextCoordsList = personList.stream().map(p -> p.getNextCoord()).collect(Collectors.toList());

        // calculate collisions and take measures for the next frame
        applyPersonAvoidance();
    }

    private void applyPersonAvoidance() {
        // for every coordinate of the next frame
        for (int i = 0; i < nextCoordsList.size()-1; i++) {
            for (int j = 1; j < nextCoordsList.size(); j++) {
                // check if people will overlap each other
                if (nextCoordsList.get(i).getX()+APPROACH_STOP_RADIUS > nextCoordsList.get(j).getX()
                    || nextCoordsList.get(i).getX()-APPROACH_STOP_RADIUS < nextCoordsList.get(j).getX()
                    || nextCoordsList.get(i).getY()+APPROACH_STOP_RADIUS > nextCoordsList.get(j).getY()
                    || nextCoordsList.get(i).getY()-APPROACH_STOP_RADIUS < nextCoordsList.get(j).getX()) {
                    // if they do, the person with the lowest index keeps moving and the other stops in place
                    personList.get(j).setTemporaryCoord(personList.get(j).getCurrentCoord());
                }
            }
        }
    }

    private void applyDangerZoneAvoidance() {
        for (int i = 0; i < nextCoordsList.size(); i++) {
                // check if the coordinate is within the range of a DangerZone
                if (dangerZone.getCoords().getX()+DANGER_ZONE_RADIUS > nextCoordsList.get(i).getX()
                        || dangerZone.getCoords().getX()-DANGER_ZONE_RADIUS < nextCoordsList.get(i).getX()
                        || dangerZone.getCoords().getX()+DANGER_ZONE_RADIUS > nextCoordsList.get(i).getY()
                        || dangerZone.getCoords().getX()-DANGER_ZONE_RADIUS < nextCoordsList.get(i).getX()) {
                    // if they do, the person if the lowest index keeps moving and the other stops
                    if (personList.get(i).getTemporaryCoord() == null) {
                        personList.get(i).setTemporaryCoord(calculateOppositeCoords(personList.get(i).getCurrentCoord()));
                    }
                }
            }
        }

    // math wrong
    private Coords calculateOppositeCoords(Coords currentCoord) {
        Float dx, dy;
        dx = dangerZone.getCoords().getX() - currentCoord.getX();
        dy = dangerZone.getCoords().getY() - currentCoord.getY();

        if (dx < 0) {
            currentCoord.setX(currentCoord.getX()+MOVEMENT_SCALING_FACTOR);
        }
        else {
            currentCoord.setX(currentCoord.getX()-MOVEMENT_SCALING_FACTOR);
        }

        if (dy < 0) {
            currentCoord.setY(currentCoord.getY()+MOVEMENT_SCALING_FACTOR);
        }
        else {
            currentCoord.setY(currentCoord.getY()-MOVEMENT_SCALING_FACTOR);
        }
        return currentCoord;
    }
}