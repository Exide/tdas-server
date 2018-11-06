package org.arabellan.tdas.model;

import lombok.Data;

@Data
public abstract class Component {

    private Entity parent;

    public boolean update(double deltaTimeSeconds) {
        return false;
    }

}
