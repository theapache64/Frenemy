package com.theah64.frenemy.android.utils;

import com.theah64.frenemy.android.models.SocketMessage;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

/**
 * Created by theapache64 on 7/6/17.
 */

public abstract class AdvancedWebSocketClient extends WebSocketClient {

    private static final Queue<SocketMessage> pendingMessages = new LinkedList<>();

    public AdvancedWebSocketClient(URI serverURI) {
        super(serverURI);
    }

    public AdvancedWebSocketClient(URI serverUri, Draft draft) {
        super(serverUri, draft);
    }

    public AdvancedWebSocketClient(URI serverUri, Draft draft, Map<String, String> headers, int connecttimeout) {
        super(serverUri, draft, headers, connecttimeout);
    }

    public void send(SocketMessage socketMessage) {
        if (getConnection().isOpen()) {
            System.out.println("Message sent : " + socketMessage);
            send(socketMessage.toString());
        } else {
            System.out.println("Socket not opened, added to pending list");
            pendingMessages.add(socketMessage);
        }
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        while (getConnection().isOpen() && pendingMessages.iterator().hasNext()) {
            final SocketMessage socketMessage = pendingMessages.poll();
            send(socketMessage);
        }
    }
}
