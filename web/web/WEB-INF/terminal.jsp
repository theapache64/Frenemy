<%@ page import="com.theah64.frenemy.web.utils.PathInfo" %>
<%@ page import="com.theah64.frenemy.web.model.Frenemy" %>
<%@ page import="com.theah64.frenemy.web.database.tables.Frenemies" %>
<%@ page import="com.theah64.frenemy.web.exceptions.RequestException" %><%--suppress ALL --%>
<%
    Frenemy frenemy = null;
    String token = null;
    try {
        final PathInfo pathInfo = new PathInfo(request.getPathInfo(), 2, 2);
        token = pathInfo.getPart(1);
        final String deviceHash = pathInfo.getPart(2);
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

        span.spanDevice, p.status_success {
            color: #7FE234;
        }

        p.status_danger {
            color: #C8031A;
        }

        span.spanPath {
            color: #6D9FCF;
        }

        input#iCommand {
            border: none;
            background: transparent;
            font-size: 16px;
        }

        input#iCommand:focus {
            outline: none;
        }

    </style>

    <script>
        $(document).ready(function () {

            const ROW = '<div class="divLineNode"> <span class="spanDevice">frenemy@<%=frenemy.getName()%></span>:<span class="spanPath">~</span> <input id="iCommand" onblur="this.focus()"  autofocus type="text"/> </div>';

            var statusDiv = $("div#status");

            //Status update functions
            function addNormalStatus(msg) {
                $(statusDiv).append('<p class="status_normal">' + msg + '</p>');
            }

            function addSuccessStatus(msg) {
                $(statusDiv).append('<p class="status_success">' + msg + '</p>');
            }

            function addDangerStatus(msg) {
                $(statusDiv).append('<p class="status_danger">' + msg + '</p>');
            }

            //Establishing connection
            addNormalStatus("Connecting to <%=frenemy.getName()%>...");




            var socketUrl = "<%=(Connection.isDebugMode()
            ? "ws://localhost:8080/frenemy/web/v1/frenemy_socket/terminal/"+token+"/{frenemy_id}"
            : "ws://theapache64.xyz:8080/frenemy/web/v1/pigeon_socket/listener/") + frenemy.getId()%>";



            function onConnectionEstablished() {
                $("body").append(ROW);
            }

            onConnectionEstablished();

            function processCommand(e) {

                if (e.keyCode == 13) {
                    //Enter pressed
                    var prevICommand = $("input#iCommand");
                    $command = $(prevICommand).val();
                    $(prevICommand).attr('onblur', null);
                    $("body").append(ROW);

                    var lastICommand = $("body div:last input#iCommand");
                    $(lastICommand).attr('onblur', 'this.focus()');
                    $(lastICommand).focus();
                    $(lastICommand).on('keydown', processCommand);
                }

            }

            $("input#iCommand").on('keydown', processCommand);

        });
    </script>

</head>
<body>
<div id="status" style="margin-top:10px;line-height: 2px">
</div>
</body>
</html>
