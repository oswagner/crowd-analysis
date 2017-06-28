package com.pucrs.controller;

import com.pucrs.parsing.Coords;
import com.pucrs.parsing.Parser;
import com.sun.xml.internal.fastinfoset.algorithm.BooleanEncodingAlgorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Controller {
    private static final Float MOVEMENT_FACTOR = 1f;
    private static final Float APPROACH_STOP_RADIUS = 50f;
    private static final Float DANGER_ZONE_RADIUS = 100f;
    private static final Float DANGER_ZONE_MOVEMENT_FACTOR = 2f;

    private List<Person> personList;

    private List<Coords> currentCoordsList;
    private List<Coords> nextCoordsList;

    private DangerZone dangerZone;

    private Boolean hasDanger = false;

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

        // apply avoidance?
        // calculate collisions and take measures for the next frame
        applyPersonAvoidance();

        // apply danger zone?
        applyDangerZoneAvoidance();

    }

    private void applyPersonAvoidance() {

        // for every coordinate of the next frame
        for (int i = 0; i < nextCoordsList.size()-1; i++) {
            for (int j = 1; j < nextCoordsList.size(); j++) {
                // check if people will overlap each other
                if (nextCoordsList.get(i).getX()+APPROACH_STOP_RADIUS < nextCoordsList.get(j).getX()
                    && nextCoordsList.get(i).getX()-APPROACH_STOP_RADIUS > nextCoordsList.get(j).getX()
                    && nextCoordsList.get(i).getY()+APPROACH_STOP_RADIUS < nextCoordsList.get(j).getY()
                    && nextCoordsList.get(i).getY()-APPROACH_STOP_RADIUS > nextCoordsList.get(j).getX()) {
                    // if they do, the person with the lowest index keeps moving and the other stops in place
                    personList.get(j).setTemporaryCoord(calculateOppositeCoords(nextCoordsList.get(i), nextCoordsList.get(j)));
                }
            }
        }
    }

    private void applyDangerZoneAvoidance() {
        if (dangerZone != null) {
            for (int i = 0; i < nextCoordsList.size(); i++) {
                // check if the coordinate is within the range of a DangerZone
                if (dangerZone.getCoords().getX()+DANGER_ZONE_RADIUS < nextCoordsList.get(i).getX()
                        && dangerZone.getCoords().getX()-DANGER_ZONE_RADIUS > nextCoordsList.get(i).getX()
                        && dangerZone.getCoords().getX()+DANGER_ZONE_RADIUS < nextCoordsList.get(i).getY()
                        && dangerZone.getCoords().getX()-DANGER_ZONE_RADIUS > nextCoordsList.get(i).getX()) {
                    // if they do, the person if the lowest index keeps moving and the other stops
                    personList.get(i).setTemporaryCoord(calculateOppositeDangerCoords(currentCoordsList.get(i)));
                }
            }
        }
    }

    private Coords calculateOppositeCoords(Coords coordsP1, Coords coordsP2) {
        Float dx, dy;
        Coords returnCoords = new Coords(null, null);
        dx = coordsP1.getX() - coordsP2.getX();
        dy = coordsP1.getY() - coordsP2.getY();

        // dx negative, P2x is higher
        if (dx < 0) {
            returnCoords.setX(coordsP1.getX()- MOVEMENT_FACTOR);
        }
        // dx is positive, P2x is lower
        else {
            returnCoords.setX(coordsP1.getX()+ MOVEMENT_FACTOR);
        }

        // dy negative, P2y is higher
        if (dy < 0) {
            returnCoords.setY(coordsP1.getY()- MOVEMENT_FACTOR);
        }
        // dy is positive, P2y is lower
        else {
            returnCoords.setY(coordsP1.getY()+ MOVEMENT_FACTOR);
        }
        return returnCoords;
    }

    private Coords calculateOppositeDangerCoords(Coords currentCoord) {
        Float dx, dy;
        Coords returnCoords = new Coords(null, null);
        dx = currentCoord.getX() - dangerZone.getCoords().getX();
        dy = currentCoord.getY() - dangerZone.getCoords().getY();

        // dx negative, P2x is higher
        if (dx < 0) {
            returnCoords.setX(currentCoord.getX()- DANGER_ZONE_MOVEMENT_FACTOR);
        }
        // dx is positive, P2x is lower
        else {
            returnCoords.setX(currentCoord.getX()+ DANGER_ZONE_MOVEMENT_FACTOR);
        }

        // dy negative, P2y is higher
        if (dy < 0) {
            returnCoords.setY(currentCoord.getY()- DANGER_ZONE_MOVEMENT_FACTOR);
        }
        // dy is positive, P2y is lower
        else {
            returnCoords.setY(currentCoord.getY()+ DANGER_ZONE_MOVEMENT_FACTOR);
        }
        return returnCoords;
    }

    public Boolean hasDanger() {
        return hasDanger;
    }

    public void updateDangerZone() {
        dangerZone.setExplosionDelay(dangerZone.getExplosionDelay()-1f);
        if (dangerZone.getExplosionDelay() <= 0) {
            dangerZone = null;
            hasDanger = false;
        }
    }

    public void dropDanger(DangerZone dangerZone) {
        if (hasDanger == false) {
            this.dangerZone = dangerZone;
            hasDanger = true;
        }
    }
}