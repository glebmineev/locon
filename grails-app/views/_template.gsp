<%@ page contentType="text/html;charset=UTF-8" %>
<html>

<head>

    <meta http-equiv="Content-type" content="text/html; charset=utf-8"/>

    <!-- Common js -->
    <g:javascript library="application"/>
    <link rel="stylesheet" href="${resource(dir: 'css', file: 'content.css')}" type="text/css">

    <!-- slider stylesheet -->
    <link rel="stylesheet" href="${resource(dir: 'css/slider/themes/default', file: 'default.css')}" type="text/css" media="screen" />
    <link rel="stylesheet" href="${resource(dir: 'css/slider/themes/light', file: 'light.css')}" type="text/css" media="screen" />
    <link rel="stylesheet" href="${resource(dir: 'css/slider/themes/dark', file: 'dark.css')}" type="text/css" media="screen" />
    <link rel="stylesheet" href="${resource(dir: 'css/slider/themes/bar', file: 'bar.css')}" type="text/css" media="screen" />
    <link rel="stylesheet" href="${resource(dir: 'css/slider', file: 'nivo-slider.css')}" />

    <!-- drop menu -->
    <link rel="stylesheet" href="${resource(dir: 'css/dropmenu', file: 'dropmenu.css')}" />

    <!-- slider java scripts -->
    <script type="text/javascript" src="${resource(dir: 'js', file: 'jquery-1.7.1.min.js')}"></script>
    <script type="text/javascript" src="${resource(dir: 'js/slider', file: 'jquery.nivo.slider.js')}"></script>

    <!-- Carousel -->
    <link rel="stylesheet" href="${resource(dir: 'css/carousel', file: 'carousel.css')}" />
    <script type="text/javascript" src="${resource(dir: 'js/carousel', file: 'jquery.tinycarousel.min.js')}"></script>
    ${head}
</head>

<body>
<div class="content">

    <div id="header" class="header">
        ${header}
    </div>

    <div id="menu" class="menu">
        ${menu}
    </div>

    <div id="wrap_content" class="wrap_content">
        ${wrap_content}
    </div>

    <div id="footer">
        ${footer}
    </div>

</div>

</body>
</html>