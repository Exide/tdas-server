package org.arabellan.tdas;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;

import javax.websocket.server.ServerContainer;

@Slf4j
class Application {

    void run() {
        try {
            Server jettyServer = new Server(8080);
            ServletContextHandler context = new ServletContextHandler(jettyServer, "/", ServletContextHandler.SESSIONS);
            ServerContainer wsContainer = WebSocketServerContainerInitializer.configureContext(context);
            wsContainer.addEndpoint(WebSocketEndpoint.class);
            jettyServer.setHandler(context);
            jettyServer.start();
        } catch (Exception e) {
            throw new RuntimeException("unable to start the server", e);
        }
    }

}
