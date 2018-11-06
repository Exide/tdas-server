package org.arabellan.tdas.network;

import org.arabellan.tdas.Main;

import javax.websocket.server.ServerEndpointConfig.Configurator;

public class ServerEndpointConfigurator extends Configurator {

    public <T> T getEndpointInstance(Class<T> endpointClass) {
        return Main.injector.getInstance(endpointClass);
    }

}
