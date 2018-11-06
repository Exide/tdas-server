package org.arabellan.tdas.model;

import lombok.Value;

import java.util.List;

@Value
public class Level {

    String name;
    float width;
    float height;
    List<Wall> walls;
    List<Asteroid> asteroids;

    @Value
    public class Wall {

        float x;
        float y;
        float w;
        float h;

    }

    @Value
    public class Asteroid {

        float x;
        float y;
        float r;
        int size;

    }

}
