package org.arabellan.tdas;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.arabellan.tdas.network.WebSocketMessage;

import javax.inject.Singleton;
import javax.websocket.Session;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Value
@Singleton
public class SessionService {

    private final List<Session> sessions = new ArrayList<>();

    public void add(Session session) {
        log.trace("adding session {}", session.getId());
        sessions.add(session);
    }

    public void remove(Session session) {
        log.trace("removing session {}", session.getId());
        sessions.remove(session);
    }

    public void broadcastMessage(WebSocketMessage message) {
        sessions.forEach(session -> sendMessage(session, message));
    }

    public void sendMessage(Session session, WebSocketMessage message) {
        String socketMessage = message.toSocketString();
        log.debug("sending message to session {}: {}", session.getId(), socketMessage);
        try {
            session.getBasicRemote().sendText(socketMessage);
        } catch (IOException e) {
            log.warn(String.format("unable to send message to session %s: %s", session.getId(), socketMessage), e);
        }
    }

}
