package com.theah64.frenemy.android.utils;

import android.content.Context;
import android.util.Log;

import com.theah64.frenemy.android.commandcenter.CommandFactory;
import com.theah64.frenemy.android.commandcenter.commands.BaseCommand;
import com.theah64.frenemy.android.models.SocketMessage;

import org.apache.commons.cli.ParseException;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by theapache64 on 7/6/17.
 */

public class WebSocketHelper {

    private static final String SOCKET_URL_FORMAT = App.IS_DEBUG_MODE ? "ws://192.168.43.141:8080/frenemy/v1/frenemy_socket/device/%s/%s" : "ws://theapache64.xyz:8080/frenemy/v1/frenemy_socket/device/%s/%s";
    private static final String X = WebSocketHelper.class.getSimpleName();
    private static final Map<String, AdvancedWebSocketClient> socketMap = new HashMap<>();
    private static WebSocketHelper instance;
    private final Context context;

    private WebSocketHelper(Context context) {
        this.context = context;
    }

    public static WebSocketHelper getInstance(final Context context) {
        if (instance == null) {
            instance = new WebSocketHelper(context.getApplicationContext());
        }
        return instance;
    }

    public AdvancedWebSocketClient getHelper(String terminalToken, String apiKey) {

        final String url = String.format(SOCKET_URL_FORMAT, terminalToken, apiKey);

        System.out.println("SOCKET URL : " + url);

        AdvancedWebSocketClient socketClient = null;

        try {
            if (!socketMap.containsKey(url)) {

                //new socket

                socketClient = getNewSocketClient(url);

                socketClient.connect();
                Log.d(X, "Created new instance for " + url);
            } else {
                Log.d(X, "Has old instance");
                socketClient = socketMap.get(url);
                System.out.println("isOpen: " + socketClient.getConnection().isOpen());
                System.out.println("isClosed: " + socketClient.getConnection().isClosed());
                System.out.println("isClosing: " + socketClient.getConnection().isClosing());

                if (socketClient.getConnection().isClosed()) {
                    System.out.println("Reopening closed socket");
                    socketClient = getNewSocketClient(url);
                }
            }

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }


        return socketClient;
    }

    private AdvancedWebSocketClient getNewSocketClient(final String url) throws URISyntaxException {

        return new AdvancedWebSocketClient(new URI(url), new Draft_17()) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                super.onOpen(handshakedata);
                Log.i(X, "Socket opened and added to cache");
                final StringBuilder sb = new StringBuilder();
                socketMap.put(url, this);
                sb.append(SocketMessage.WAKEUP_RESPONSE).append("\n").append("Total terminals connected : ").append(socketMap.size());
                send(new SocketMessage(sb.toString(), true, true));
            }

            @Override
            public void onMessage(String message) {

                System.out.println("Message received : " + message);

                try {
                    final String command = new JSONObject(message).getString("command");
                    CommandFactory.getCommand(command).handle(context, new BaseCommand.Callback() {
                        @Override
                        public void onError(String message) {
                            send(new SocketMessage(message, true, true));
                        }

                        @Override
                        public void onInfo(String message) {
                            send(new SocketMessage(message, false, true));
                        }

                        @Override
                        public void onSuccess(String message) {
                            send(new SocketMessage(message, false, true));
                        }

                        @Override
                        public void onFinish(String message) {
                            send(new SocketMessage(message, true, true));
                        }
                    });
                } catch (JSONException | BaseCommand.CommandException | ParseException e) {
                    e.printStackTrace();
                    send(new SocketMessage(e.getMessage(), true, true));
                } catch (BaseCommand.CommandHelp commandHelp) {
                    commandHelp.printStackTrace();
                    send(new SocketMessage(commandHelp.getMessage(), true, false));
                }

            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                Log.e(X, "WebSocket closed : " + reason);
                socketMap.remove(url);
            }

            @Override
            public void onError(Exception ex) {
                ex.printStackTrace();
            }
        };
    }
}
