package com.pucrs.controller;

import com.pucrs.parsing.Coords;

public class DangerZone {
    private Coords coords;
    private Float explosionDelay;

    public DangerZone(Coords coords) {
        this.coords = coords;
    }

    public Coords getCoords() {
        return coords;
    }

    public Float getExplosionDelay() {
        return (explosionDelay-10);
    }
}
