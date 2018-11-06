package org.arabellan.tdas;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.arabellan.tdas.model.Entity;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Value
@Singleton
public class EntityService {

    private final List<Entity> entities = new ArrayList<>();

    public void add(Entity entity) {
        log.trace("adding {} entity {}", entity.getType(), entity.getId());
        entities.add(entity);
    }

    public void addAll(List<Entity> listOfEntities) {
        log.trace("adding {} entities", listOfEntities.size());
        entities.addAll(listOfEntities);
    }

    public void remove(Entity entity) {
        log.trace("removing {} entity {}", entity.getType(), entity.getId());
        entities.remove(entity);
    }

    public Entity getBySessionId(String id) {
        return entities.stream()
                .filter(e -> e.getSessionId() != null)
                .filter(e -> e.getSessionId().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("unable to find entity with session id: " + id));
    }

    public Collection<String> getSerializedList() {
        return entities.stream().map(Entity::serialize).collect(Collectors.toList());
    }
}
