<%@ page contentType="text/html;charset=UTF-8" %>
<html>

<head>
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

    <div id="header">
        <table>
            <tr>
                <td>
                    <g:link>
                        Авторизация
                    </g:link>
                </td>
                <td>
                    <g:link>
                        Регистрация
                    </g:link>
                </td>
            </tr>
            <tr>
                <td colspan="2">
                    <g:img dir="images" file="logo.png"/>
                </td>
                <td>
                    <table>
                        <tr>
                            <td colspan="4">
                                8 (812) 448 77 55
                            </td>
                            <td colspan="2">
                                мы работаем с 9 до 21
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <g:link>Контакты</g:link>
                            </td>
                            <td>
                                <g:link>О нас</g:link>
                            </td>
                            <td>
                                <g:link>О доставке</g:link>
                            </td>
                            <td>
                                <g:link>Как покупать</g:link>
                            </td>
                            <td>
                                <g:textField name="search"/>
                            </td>
                            <td>
                                <g:img dir="images" file="search.png"/>
                            </td>
                        </tr>
                        <tr>
                            <td/>
                            <td/>
                            <td/>
                            <td/>
                            <td>
                                Корзина
                            </td>
                            <td>
                                <g:img dir="images" file="cart.png"/>
                            </td>
                        </tr>
                    </table>
                </td>
            </tr>
        </table>
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