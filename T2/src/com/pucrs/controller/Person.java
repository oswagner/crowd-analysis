package com.pucrs.controller;

import com.pucrs.parsing.Coords;

import java.util.List;

public class Person {
    private List<Coords> coordsList;

    private Coords temporaryCoord;

    private Coords currentCoord;

    private Integer nextCoordIndex;

    private Integer id;

    Boolean reverseRoute;

    public Person(List<Coords> coordsList, Integer id) {
        nextCoordIndex = 0;
        currentCoord = coordsList.get(0);
        reverseRoute = false;
        this.coordsList = coordsList;
        this.id = id;
    }

    public Coords getNextCoord() {
        if (temporaryCoord != null) {
            Coords returnElement = temporaryCoord;
            temporaryCoord = null;
            return returnElement;
        }
        if (reverseRoute) {
            if (nextCoordIndex == 0) {
                reverseRoute = false;
                currentCoord = coordsList.get(nextCoordIndex+1);
                return coordsList.get(nextCoordIndex++);
            }
            currentCoord = coordsList.get(nextCoordIndex-1);
            return coordsList.get(nextCoordIndex--);
        }
        else {
            if (nextCoordIndex == coordsList.size()-1) {
                reverseRoute = true;
                currentCoord = coordsList.get(nextCoordIndex-1);
                return coordsList.get(nextCoordIndex--);
            }
            currentCoord = coordsList.get(nextCoordIndex+1);
            return coordsList.get(nextCoordIndex++);
        }
    }

    public Coords getCurrentCoord() {
        return currentCoord;
    }

    public void setCurrentCoord(Coords currentCoord) {
        this.currentCoord = currentCoord;
    }

    public List<Coords> getCoordsList() {
        return coordsList;
    }

    public Coords getTemporaryCoord() {
        return temporaryCoord;
    }

    public void setTemporaryCoord(Coords temporaryCoord) {
        this.temporaryCoord = temporaryCoord;
    }

    public int getId() {
        return id;
    }

    public String toString() {
        return "Person " + id + ": " + coordsList;
    }
}
