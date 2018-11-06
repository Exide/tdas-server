package org.arabellan.tdas;

import lombok.extern.slf4j.Slf4j;
import org.arabellan.tdas.model.Configuration;
import org.arabellan.tdas.model.Entity;
import org.arabellan.tdas.network.WebSocketEndpoint;
import org.arabellan.tdas.network.WebSocketMessage;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;

import javax.inject.Inject;
import javax.websocket.server.ServerContainer;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
class Application {

    private boolean isRunning = true;

    private final Configuration config;
    private final EntityService entityService;
    private final SessionService sessionService;
    private final MetricsService metricsService;

    private LocalDateTime lastUpdate = LocalDateTime.now();
    private long accumulatorMS;
    private final int desiredTickMS;

    @Inject
    Application(Configuration config, EntityService entityService, SessionService sessionService, MetricsService metricsService) {
        this.config = config;
        this.entityService = entityService;
        this.sessionService = sessionService;
        this.metricsService = metricsService;
        this.desiredTickMS = 1000 / config.getUpdatesPerSecond();
    }

    void run() {
        try {
            String bindAddress = config.getNetwork().getAddress();
            int bindPort = config.getNetwork().getPort();
            log.info("starting Jetty server at {}:{}", bindAddress, bindPort);
            InetSocketAddress inetSocketAddress = new InetSocketAddress(bindAddress, bindPort);
            Server jettyServer = new Server(inetSocketAddress);
            ServletContextHandler context = new ServletContextHandler(jettyServer, "/", ServletContextHandler.SESSIONS);
            ServerContainer wsContainer = WebSocketServerContainerInitializer.configureContext(context);
            wsContainer.addEndpoint(WebSocketEndpoint.class);
            jettyServer.setHandler(context);
            jettyServer.start();
        } catch (Exception e) {
            throw new RuntimeException("unable to start the server", e);
        }

        while (isRunning) {
            try {
                loop();
            } catch (InterruptedException e) {
                log.warn("main loop interrupted", e);
                isRunning = false;
            }
        }
    }

    private void loop() throws InterruptedException {
        LocalDateTime startOfLoop = LocalDateTime.now();
        accumulatorMS += ChronoUnit.MILLIS.between(lastUpdate, startOfLoop);
        lastUpdate = startOfLoop;

        if (accumulatorMS < desiredTickMS) return;
        accumulatorMS -= desiredTickMS;

        LocalDateTime startOfTick = LocalDateTime.now();
        metricsService.increment("ticks");
        metricsService.gauge("entities.total", entityService.getEntities().size());

        LocalDateTime startOfUpdate = LocalDateTime.now();
        List<Entity> updatedEntities = entityService.getEntities().stream()
                .filter(entity -> entity.update(desiredTickMS / 1000.0))
                .collect(Collectors.toList());
        metricsService.timing("ticks.updating_entities", ChronoUnit.MILLIS.between(startOfUpdate, LocalDateTime.now()));
        metricsService.gauge("entities.updated", updatedEntities.size());

        if (updatedEntities.size() == 0) return;

        LocalDateTime startOfBroadcast = LocalDateTime.now();
        sessionService.broadcastMessage(WebSocketMessage.builder()
                .key("update")
                .values(updatedEntities.stream()
                        .map(Entity::serialize)
                        .collect(Collectors.toList()))
                .build());
        metricsService.timing("ticks.broadcasting_updates", ChronoUnit.MILLIS.between(startOfBroadcast, LocalDateTime.now()));
        metricsService.timing("ticks.duration", ChronoUnit.MILLIS.between(startOfTick, LocalDateTime.now()));

        long waitMS = accumulatorMS >= desiredTickMS ? 0 : desiredTickMS - accumulatorMS;
        metricsService.timing("ticks.wait", waitMS);
        Thread.sleep(waitMS);
    }
}
