package org.arabellan.tdas.model;

import lombok.Value;

@Value
public class Configuration {

    Network network;
    String levelName;
    int updatesPerSecond;
    Metrics metrics;

    @Value
    public class Network {

        String address;
        int port;

    }

    @Value
    public class Metrics {

        String prefix;
        String address;
        int port;

    }

}
