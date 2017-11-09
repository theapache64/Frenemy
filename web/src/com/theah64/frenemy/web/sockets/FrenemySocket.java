package com.theah64.frenemy.web.sockets;

import com.theah64.frenemy.web.database.tables.Frenemies;
import com.theah64.frenemy.web.exceptions.FrenemySocketException;
import com.theah64.frenemy.web.servlets.AdvancedBaseServlet;
import com.theah64.frenemy.web.utils.FCMUtils;
import com.theah64.frenemy.web.utils.Response;
import org.json.JSONException;
import org.json.JSONObject;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by theapache64 on 21/1/17.
 */
@ServerEndpoint(AdvancedBaseServlet.VERSION_CODE + "/frenemy_socket/{whois}/{terminal_token}/{frenemy_api_key}")
public class FrenemySocket {

    private static final String WHOIS_TERMINAL = "terminal";
    private static final String WHOIS_DEVICE = "device";

    //token, terminal
    private static final Map<String, Session> terminalSessions = Collections.synchronizedMap(new HashMap<>());
    //token, device
    private static final Map<String, Session> deviceSessions = Collections.synchronizedMap(new HashMap<>());

    @OnOpen
    public void onOpen(@PathParam("whois") String whois, @PathParam("terminal_token") String terminalToken, @PathParam("frenemy_api_key") String apiKey, Session session) throws IOException, JSONException {

        try {
            System.out.println("New socket opened");

            //Checking if the frenemy exists
            final String frenemyFcmId = Frenemies.getInstance().get(Frenemies.COLUMN_API_KEY, apiKey, Frenemies.COLUMN_FCM_ID, true);

            if (frenemyFcmId == null) {
                throw new FrenemySocketException("Frenemy doesn't exist with api_key " + apiKey, true);
            }

            if (whois.equals(WHOIS_TERMINAL)) {

                if (terminalSessions.get(terminalToken) == null) {

                    session.getBasicRemote().sendText(new Response("Terminal joined", null).getResponse());

                    terminalSessions.put(terminalToken, session);

                    //Sending wakeup request to device
                    final JSONObject joFcmResp = FCMUtils.sendWakeUp(frenemyFcmId, terminalToken);

                    final boolean isEverythingOk = joFcmResp != null && joFcmResp.getInt("failure") == 0;

                    if (isEverythingOk) {
                        session.getBasicRemote().sendText(new Response("Wakeup request sent", null).getResponse());
                    } else {
                        final String error;
                        if (joFcmResp != null) {
                            error = joFcmResp.getJSONArray("results").getJSONObject(0).getString("error");
                        } else {
                            error = "Undefined error";
                        }

                        throw new FrenemySocketException("Failed to send wakeup request : " + error, true);
                    }

                } else {
                    throw new FrenemySocketException("Terminal already exist with the token " + terminalToken, true);
                }

            } else {
                System.out.println("Device joined");

                if (terminalSessions.get(terminalToken) != null) {
                    deviceSessions.put(terminalToken, session);

                    //Tell the terminal that the device has been joined
                    final Session terminalSession = terminalSessions.get(terminalToken);
                    terminalSession.getBasicRemote().sendText(new Response("Device joined", null).getResponse());
                } else {
                    session.close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "No terminal found with the token " + terminalToken));
                }

            }

        } catch (FrenemySocketException e) {
            e.printStackTrace();

            if (e.isFatal()) {
                //even number error codes are fatal errors
                System.out.println("FATAL ERROR");
                session.close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, e.getMessage()));
            } else {
                //Building error
                System.out.println("WARNING");
                session.getBasicRemote().sendText(new Response(e.getMessage()).getResponse());
            }
        }
    }


    @OnMessage
    public void onMessage(@PathParam("whois") String whois, @PathParam("terminal_token") String terminalToken, @PathParam("frenemy_api_key") String apiKey, Session session, String data) throws IOException {

        try {
            if (whois.equals(WHOIS_TERMINAL)) {

                //Terminal wants to talk to device
                //Getting device
                final Session deviceSession = deviceSessions.get(terminalToken);
                if (deviceSession != null) {
                    try {
                        final JSONObject joCommand = new JSONObject(data);

                        deviceSession.getBasicRemote().sendText(data);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        throw new FrenemySocketException(e.getMessage(), false);
                    }
                } else {
                    throw new FrenemySocketException("Device not yet connected", true);
                }

            } else {
                //Device wants to talk to terminal
                final Session terminalSession = terminalSessions.get(terminalToken);
                if (terminalSession != null) {
                    terminalSession.getBasicRemote().sendText(data);
                } else {
                    throw new FrenemySocketException("Terminal not yet connected", true);
                }
            }

        } catch (FrenemySocketException e) {
            e.printStackTrace();

            if (e.isFatal()) {
                //even number error codes are fatal errors
                System.out.println("FATAL ERROR");
                session.close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, e.getMessage()));
            } else {
                //Building error
                System.out.println("WARNING");
                session.getBasicRemote().sendText(new Response(e.getMessage()).getResponse());
            }
        }
    }

    @OnError
    public void onError(Throwable e) {
        e.printStackTrace();
        System.out.println("ERROR:" + e.getMessage());
    }

    @OnClose
    public void onClose(@PathParam("whois") String whois, @PathParam("terminal_token") String terminalToken, @PathParam("frenemy_api_key") String apiKey, Session session) throws IOException {

        System.out.println("SocketClosed");
        System.out.println("WhoIs:" + whois);
        System.out.println("TerminalToken:" + terminalToken);
        System.out.println("FrenemyApiKey:" + apiKey);

        if (whois.equals(WHOIS_TERMINAL)) {
            //Terminal need tobe removed
            terminalSessions.remove(terminalToken);
            System.out.println("Terminal removed: " + terminalToken);

            //Removing device that's attached to the terminal
            final Session deviceSession = deviceSessions.get(terminalToken);
            if (deviceSession != null) {
                deviceSession.close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "Terminal closed"));
            }
            deviceSessions.remove(terminalToken);

        } else {
            //Device need to be removed
            deviceSessions.remove(terminalToken);
            System.out.println("Device removed :" + apiKey);

            //Tell the terminal that the device got detached from the socket
            final Session terminalSession = terminalSessions.get(terminalToken);
            if (terminalSession != null) {
                //Closing the terminal session
                terminalSession.close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "Device gone offline"));
            } else {
                System.out.println("No terminal found to tell the left of device");
            }
        }
    }


}
