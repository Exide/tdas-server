package org.arabellan.tdas.components;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.arabellan.tdas.math.Vector;
import org.arabellan.tdas.model.Component;

@Data
@Builder
@EqualsAndHashCode(callSuper = true)
public class Transform extends Component {

    @Builder.Default
    Vector position = new Vector();

    @Builder.Default
    float rotation = 0;

    @Builder.Default
    Vector scale = new Vector();

    void addToRotation(float amount) {
        rotation += amount;
        if (rotation < 0) rotation += 360;
        if (rotation > 360) rotation -= 360;
    }
}
