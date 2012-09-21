<%@ page contentType="text/html;charset=UTF-8" %>
<html>

<head>

    <meta http-equiv="Content-type" content="text/html; charset=utf-8"/>

    <g:javascript library="application"/>
    <g:javascript library="jquery" src="jquery-1.7.1.min.js"/>
    <g:javascript library="nivo" src="slider/jquery.nivo.slider.js"/>
    <g:javascript library="carousel" src="carousel/jquery.jcarousel.min.js"/>

    <link rel="stylesheet" href="${resource(dir: 'css', file: 'content.css')}" type="text/css">

    <!-- slider stylesheet -->
    <link rel="stylesheet" href="${resource(dir: 'css/slider/themes/default', file: 'default.css')}" type="text/css" media="screen" />
    <link rel="stylesheet" href="${resource(dir: 'css/slider/themes/light', file: 'light.css')}" type="text/css" media="screen" />
    <link rel="stylesheet" href="${resource(dir: 'css/slider/themes/dark', file: 'dark.css')}" type="text/css" media="screen" />
    <link rel="stylesheet" href="${resource(dir: 'css/slider/themes/bar', file: 'bar.css')}" type="text/css" media="screen" />
    <link rel="stylesheet" href="${resource(dir: 'css/slider', file: 'nivo-slider.css')}" />

    <!-- drop menu -->
    <link rel="stylesheet" href="${resource(dir: 'css/dropmenu', file: 'dropmenu.css')}" />

    <!-- Carousel -->
    <link rel="stylesheet" href="${resource(dir: 'css/carousel', file: 'carousel.css')}" />
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