<%@ page import="com.theah64.frenemy.web.database.Connection" %>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<%--
TODO: Need to add a favicon
<link rel="shortcut icon" href="/frenemy/favicon.ico" type="image/x-icon">
<link rel="icon" href="/frenemy/favicon.ico" type="image/x-icon">--%>

<%
    if (Connection.isDebugMode()) {
%>
<%--OFFLINE RESOURCES--%>
<script src="/frenemy/web/js/jquery-2.2.4.min.js"></script>
<%
} else {
%>
<%--ONLINE RESOURCES--%>
<script src="//ajax.googleapis.com/ajax/libs/jquery/2.2.4/jquery.min.js"></script>
<%
    }
%>

<link href="https://fonts.googleapis.com/css?family=Ubuntu+Mono" rel="stylesheet">