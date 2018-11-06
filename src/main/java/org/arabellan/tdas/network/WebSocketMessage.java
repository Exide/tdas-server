package org.arabellan.tdas.network;

import lombok.Builder;
import lombok.Singular;

import java.util.List;

@Builder
public
class WebSocketMessage {

    String key;

    @Singular
    List<String> values;

    public String toSocketString() {
        return String.format("%s|%s", key, String.join("|", values));
    }

}
