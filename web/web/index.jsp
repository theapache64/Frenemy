<%--suppress ALL --%>


<html>
<head>
    <title>Frenemy Web</title>
    <link href="https://fonts.googleapis.com/css?family=Ubuntu+Mono" rel="stylesheet">
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
</head>
<body>


<div class="divLineNode">
    <span class="spanDevice">frenemy@theapache64</span>:<span class="spanPath">~</span>
    <input id="iCommand" onblur="this.focus()" autofocus type="text"/>
</div>

</body>
</html>
