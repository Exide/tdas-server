package org.arabellan.tdas.model;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import org.arabellan.tdas.components.BoundingBox;
import org.arabellan.tdas.components.Transform;

import java.util.List;
import java.util.UUID;

@Value
public class Entity {

    String id = UUID.randomUUID().toString();
    String type;
    String sessionId;
    List<Component> components;

    @Builder
    Entity(String type, String sessionId, @Singular List<Component> components) {
        this.type = type;
        this.sessionId = sessionId;
        this.components = components;
        components.forEach(c -> c.setParent(this));
    }

    public boolean update(double deltaTimeSeconds) {
        boolean entityChanged = false;
        for (Component component : components) {
            boolean componentChanged = component.update(deltaTimeSeconds);
            if (!entityChanged && componentChanged) {
                entityChanged = true;
            }
        }
        return entityChanged;
    }

    public String serialize() {
        Transform transform = getComponent(Transform.class);
        double x = transform.getPosition().getX();
        double y = transform.getPosition().getY();
        float r = transform.getRotation();

        BoundingBox bounds = getComponent(BoundingBox.class);
        float w = bounds.getWidth();
        float h = bounds.getHeight();

        return String.format("%s,%s,%f,%f,%f,%f,%f", id, type, x, y, r, w, h);
    }

    public <T extends Component> T getComponent(Class<T> type) {
        return (T) components.stream()
                .filter(c -> c.getClass() == type)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("unable to find component matching: " + type.getName()));
    }

}
