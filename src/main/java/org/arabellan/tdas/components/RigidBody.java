package org.arabellan.tdas.components;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.arabellan.tdas.math.Vector;
import org.arabellan.tdas.model.Component;

@Data
@Builder
@EqualsAndHashCode(callSuper = true)
public
class RigidBody extends Component {

    @Builder.Default
    Vector velocity = new Vector();

    public boolean update(double deltaTimeSeconds) {
        if (velocity.getX() == 0 && velocity.getY() == 0) return false;

        Transform transform = getParent().getComponent(Transform.class);
        Vector updatedPosition = transform.getPosition().add(velocity);
        transform.setPosition(updatedPosition);
        return true;
    }

}
