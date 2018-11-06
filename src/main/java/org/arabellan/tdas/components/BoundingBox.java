package org.arabellan.tdas.components;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.arabellan.tdas.model.Component;

@Value
@Builder
@EqualsAndHashCode(callSuper = true)
public class BoundingBox extends Component {

    @Builder.Default
    float width = 2;

    @Builder.Default
    float height = 2;

}
