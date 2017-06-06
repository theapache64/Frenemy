<%@ page import="com.theah64.frenemy.web.utils.PathInfo" %>
<%@ page import="com.theah64.frenemy.web.model.Frenemy" %>
<%@ page import="com.theah64.frenemy.web.database.tables.Frenemies" %>
<%@ page import="com.theah64.frenemy.web.exceptions.RequestException" %><%--suppress ALL --%>
<%
    Frenemy frenemy = null;
    try {
        final PathInfo pathInfo = new PathInfo(request.getPathInfo(), 2, 2);
        final String token = pathInfo.getPart(1);
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

        span.spanDevice {
            color: #7FE234;
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

            const ROW = '<div class="divLineNode"> <span class="spanDevice">frenemy@theapache64</span>:<span class="spanPath">~</span> <input id="iCommand" onblur="this.focus()"  autofocus type="text"/> </div>';

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

</body>
</html>
