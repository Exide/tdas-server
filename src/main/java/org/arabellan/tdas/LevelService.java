package org.arabellan.tdas;

import com.google.gson.Gson;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arabellan.tdas.components.BoundingBox;
import org.arabellan.tdas.components.Transform;
import org.arabellan.tdas.math.Vector;
import org.arabellan.tdas.model.Configuration;
import org.arabellan.tdas.model.Entity;
import org.arabellan.tdas.model.Level;
import org.arabellan.tdas.utils.FileIO;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.List;

@Slf4j
@Singleton
public class LevelService {

    private static final String LEVEL_FILE_PATH = "/levels";

    private final FileIO fileIO;
    private final Gson gson;
    private final EntityService entityService;

    @Getter
    private Level currentLevel;

    @Inject
    LevelService(Configuration config, FileIO fileIO, Gson gson, EntityService entityService) {
        this.fileIO = fileIO;
        this.gson = gson;
        this.entityService = entityService;
        this.currentLevel = loadLevel(config.getLevelName());
    }

    private Level loadLevel(String name) {
        String relativePath = String.format("%s/%s.json", LEVEL_FILE_PATH, name);
        Level level = loadLevelFromFile(relativePath);
        loadWalls(level.getWalls());
        loadAsteroids(level.getAsteroids());
        return level;
    }

    private void loadWalls(List<Level.Wall> walls) {
        walls.forEach(wall -> {
            Entity entity = Entity.builder()
                    .type("Wall")
                    .component(Transform.builder()
                            .position(new Vector(wall.getX(), wall.getY()))
                            .build())
                    .component(BoundingBox.builder()
                            .width(wall.getW())
                            .height(wall.getH())
                            .build())
                    .build();
            entityService.add(entity);
        });
    }

    private void loadAsteroids(List<Level.Asteroid> asteroids) {
        asteroids.forEach(asteroid -> {
            Entity entity = Entity.builder()
                    .type("Asteroid")
                    .component(Transform.builder()
                            .position(new Vector(asteroid.getX(), asteroid.getY()))
                            .rotation(asteroid.getR())
                            .build())
                    .component(BoundingBox.builder()
                            .width(asteroid.getSize())
                            .height(asteroid.getSize())
                            .build())
                    .build();
            entityService.add(entity);
        });
    }

    private Level loadLevelFromFile(String path) {
        log.info("loading level: {}", path);
        try {
            String json = fileIO.loadFileAsString(path);
            return gson.fromJson(json, Level.class);
        } catch (IOException e) {
            log.error("unable to load level: " + path);
            throw new RuntimeException(e);
        }
    }

}
