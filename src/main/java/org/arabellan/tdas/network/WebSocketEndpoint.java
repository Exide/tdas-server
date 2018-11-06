package org.arabellan.tdas.network;

import lombok.extern.slf4j.Slf4j;
import org.arabellan.tdas.EntityService;
import org.arabellan.tdas.SessionService;
import org.arabellan.tdas.components.BoundingBox;
import org.arabellan.tdas.components.RigidBody;
import org.arabellan.tdas.components.Thruster;
import org.arabellan.tdas.components.Transform;
import org.arabellan.tdas.math.Vector;
import org.arabellan.tdas.model.Entity;
import org.arabellan.tdas.model.Level;
import org.arabellan.tdas.LevelService;
import org.arabellan.tdas.utils.Random;

import javax.inject.Inject;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@Slf4j
@ServerEndpoint(value = "/{sessionID}", configurator = ServerEndpointConfigurator.class)
public class WebSocketEndpoint {

    private final SessionService sessionService;
    private final LevelService levelService;
    private final EntityService entityService;

    @Inject
    WebSocketEndpoint(SessionService sessionService, LevelService levelService, EntityService entityService) {
        this.sessionService = sessionService;
        this.levelService = levelService;
        this.entityService = entityService;
    }

    @OnOpen
    public void onOpen(Session session) {
        log.debug(String.format("session %s opened", session.getId()));
        sessionService.add(session);

        Level level = levelService.getCurrentLevel();

        sessionService.sendMessage(session, WebSocketMessage.builder()
                .key("map")
                .value(String.format("%f,%f", level.getWidth(), level.getHeight()))
                .build());

        sessionService.sendMessage(session, WebSocketMessage.builder()
                .key("initialize")
                .values(entityService.getSerializedList())
                .build());

        double x = Random.getDoubleBetween(-level.getWidth()/4, level.getWidth()/4);
        double y = Random.getDoubleBetween(-level.getHeight()/4, level.getHeight()/4);

        Entity player = Entity.builder()
                .sessionId(session.getId())
                .type("Ship")
                .component(Transform.builder()
                        .position(new Vector(x, y))
                        .build())
                .component(BoundingBox.builder()
                        .width(50)
                        .height(50)
                        .build())
                .component(Thruster.builder()
                        .degreesPerSecond(270)
                        .build())
                .component(RigidBody.builder()
                        .build())
                .build();

        entityService.add(player);

        sessionService.broadcastMessage(WebSocketMessage.builder()
                .key("add")
                .value(player.serialize())
                .build());

        sessionService.sendMessage(session, WebSocketMessage.builder()
                .key("identity")
                .value(player.getId())
                .build());
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        log.debug(String.format("session %s message: %s", session.getId(), message));
        Entity entity = entityService.getBySessionId(session.getId());
        int delimiterIndex = message.indexOf("|");
        String command = message.substring(0, delimiterIndex);
        String parameters = message.substring(delimiterIndex+1);
        switch(command) {
            case "start-thrust":
                entity.getComponent(Thruster.class).startThrusting(parameters);
                break;
            case "stop-thrust":
                entity.getComponent(Thruster.class).stopThrusting(parameters);
                break;
            case "start-rotate":
                entity.getComponent(Thruster.class).startRotating(parameters);
                break;
            case "stop-rotate":
                entity.getComponent(Thruster.class).stopRotating(parameters);
                break;
            default:
                log.debug("message ignored: {}", message);
        }
    }

    @OnClose
    public void onClose(Session session) {
        log.debug(String.format("session %s closed", session.getId()));
        Entity entity = entityService.getBySessionId(session.getId());
        entityService.remove(entity);
        sessionService.remove(session);
        sessionService.broadcastMessage(WebSocketMessage.builder()
                .key("remove")
                .value(entity.getId())
                .build());
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        log.debug(String.format("session %s erred", session.getId()), throwable);
    }

}
