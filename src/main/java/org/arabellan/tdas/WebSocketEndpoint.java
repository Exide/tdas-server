package org.arabellan.tdas;

import lombok.extern.slf4j.Slf4j;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@Slf4j
@ServerEndpoint(value = "/")
public class WebSocketEndpoint {

    @OnOpen
    public void onOpen(Session session) {
        log.debug(String.format("socket %s opened", session.getId()));
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        log.debug(String.format("socket %s message: %s", session.getId(), message));
    }

    @OnClose
    public void onClose(Session session) {
        log.debug(String.format("socket %s closed", session.getId()));
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        log.debug(String.format("socket %s errored", session.getId()), throwable);
    }

}
