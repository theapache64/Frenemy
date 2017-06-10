<%@ page import="com.theah64.frenemy.web.utils.PathInfo" %>
<%@ page import="com.theah64.frenemy.web.model.Frenemy" %>
<%@ page import="com.theah64.frenemy.web.database.tables.Frenemies" %>
<%@ page import="com.theah64.frenemy.web.exceptions.RequestException" %>
<%@ page import="com.theah64.frenemy.web.utils.RandomString" %><%--suppress ALL --%>
<%
    Frenemy frenemy = null;
    String terminalToken = null;
    try {
        final PathInfo pathInfo = new PathInfo(request.getPathInfo(), 1, 1);
        terminalToken = RandomString.getRandomString(30);
        final String deviceHash = pathInfo.getPart(1);
        System.out.println("Device hash : " + deviceHash);

        frenemy = Frenemies.getInstance().get(Frenemies.COLUMN_DEVICE_HASH, deviceHash);
        if (frenemy == null) {
            throw new RequestException("Invalid frenemy");
        }

    } catch (PathInfo.PathInfoException | RequestException e) {
        e.printStackTrace();
        response.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        return;
    }


%>
<html>
<head>
    <title><%=frenemy.getName()%> @ Frenemy Web
    </title>
    <%@include file="../common_headers.jsp" %>
    <style>
        * {
            font-family: 'Ubuntu Mono', monospace;
            color: white;
        }

        body {
            background-color: #300A24;
        }

        span.spanDevice {
            color: #7FE234;
        }

        .status_success {
            color: white;
        }

        .status_danger {
            color: #C8031A;
        }

        span.spanPath {
            color: #6D9FCF;
        }

        input#iCommand {
            border: none;
            background: transparent;
            font-size: 16px;
            width: 86%;
        }

        input#iCommand:focus {
            outline: none;
        }

    </style>

    <script>

        $(document).ready(function () {

            $("body").bind("DOMSubtreeModified", function () {
                //Hide content on each update
                $("html, body").animate({scrollTop: $(document).height()}, 1000);
            });

            const ROW = '<div class="divLineNode"> <span class="spanDevice">frenemy@<%=frenemy.getName()%></span>:<span class="spanPath">~</span> <input id="iCommand" onblur="this.focus()"  autofocus type="text"/> </div>';

            var statusDiv = $("body");


            //Status update functions
            function addNormalStatus(msg) {
                $(statusDiv).append('<pre class="status_normal">' + msg + '</pre>');
            }

            function addSuccessStatus(msg) {
                $(statusDiv).append('<pre class="status_success">' + msg + '</pre>');
            }

            function addDangerStatus(msg) {
                $(statusDiv).append('<pre class="status_danger">' + msg + '</pre>');
            }


            //Establishing connection
            addNormalStatus("Connecting to <%=frenemy.getName()%>...");


            var socketUrl = "<%=Connection.isDebugMode()
            ? "ws://localhost:8080/"
            : "ws://theapache64.xyz:8080/"%>";

            socketUrl += "frenemy/v1/frenemy_socket/terminal/<%=terminalToken%>/<%=frenemy.getApiKey()%>";

            console.log("Socket URL : " + socketUrl);

            //Building socket
            var webSocket = new WebSocket(socketUrl);

            webSocket.onopen = function (evnt) {
                console.log("Socket opened");
            };

            webSocket.onmessage = function (evnt) {

                console.log("Socket got new message");
                console.log(evnt);

                var data = JSON.parse(evnt.data);

                if (data.error) {
                    addDangerStatus(data.message);
                } else {

                    addSuccessStatus(data.message);

                    if (data.is_wakeup) {
                        onConnectionEstablished();
                    }

                }

                if (!data.is_wakeup && data.is_finished) {
                    var prevICommand = $("input#iCommand");
                    $(prevICommand).attr('onblur', null);
                    $("body").append(ROW);

                    var lastICommand = $("body div:last input#iCommand");
                    $(lastICommand).attr('onblur', 'this.focus()');
                    $(lastICommand).focus();
                    $(lastICommand).on('keydown', processCommand);
                }
            };

            webSocket.onclose = function (evnt) {
                console.log("Socket closed!");
                addDangerStatus(evnt.reason);
            };

            webSocket.onerror = function (evnt) {
                console.log("Socket error occurred");
            };

            function onConnectionEstablished() {
                $("body").append(ROW);
                $("input#iCommand").on('keydown', processCommand);
            }


            function getFormat(command) {
                var data = {
                    command: command,
                    message: "execute " + command
                };

                return JSON.stringify(data);
            }


            function processCommand(e) {

                if (e.ctrlKey && e.keyCode == 27) { //76 = l
                    $("body").html('');
                    onConnectionEstablished();
                    $("input#iCommand").focus();
                }

                if (e.keyCode == 13) {
                    var lastICommand = $("body div:last input#iCommand");
                    $(lastICommand).attr('onblur', null);
                    $(lastICommand).attr('onfocus', 'this.blur()');
                    $(lastICommand).blur();
                    var command = $(lastICommand).val();
                    webSocket.send(getFormat(command));
                    //Enter pressed
                    console.log("Command sent");
                }
            }

        });
    </script>

</head>
<body>

</body>
</html>
