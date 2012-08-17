<g:applyLayout name="main">
    <tpl:useTemplate template="/template">

        <tpl:block name="header">

        </tpl:block>

        <tpl:block name="menu">
            <ul id="nav">
                <g:each in="${mainCategoties}" var="mainCategory">
                    <li>
                        ${mainCategory}
                        <ul>
                            <li><a href="#">Item 01</a></li>
                            <li><a href="#" class="selected">Item 02</a></li>
                            <li><a href="#">Item 03</a></li>
                        </ul>

                        <div class="clear"></div>
                    </li>
                </g:each>
            </ul>

            <div class="clear"></div>

            <g:javascript>
                $(document).ready(function () {

                    $('#nav li').hover(
                            function () {
                                //show its submenu
                                $('ul', this).slideDown(100);

                            },
                            function () {
                                //hide its submenu
                                $('ul', this).slideUp(100);
                            }
                    );

                });
            </g:javascript>

        </tpl:block>

        <tpl:block name="wrap_content">
            <table>
                <tr>
                    <td>
                        <div class="slider-wrapper theme-default">
                            <div id="slider" class="nivoSlider">
                                <g:img dir="images/slider" file="toystory.jpg"/>
                                <g:img dir="images/slider" file="up.jpg"/>
                                <g:img dir="images/slider" file="walle.jpg"/>
                                <g:img dir="images/slider" file="nemo.jpg"/>
                            </div>

                            <div id="htmlcaption" class="nivo-html-caption">
                                <strong>This</strong> is an example of a <em>HTML</em> caption with <a
                                    href="#">a link</a>.
                            </div>
                        </div>
                    </td>
                </tr>
            </table>
            <script type="text/javascript">
                $(window).load(function () {
                    $('#slider').nivoSlider();
                });
            </script>

            <div id="carousel">

            </div>

            <div id="manufacturers">
                <a class="buttons prev" href="#">left</a>
                <div class="viewport">
                    <ul class="overview">
                        <g:each in="${manufacturers}" var="manufacturer">
                            <li><g:img dir="images/slider" file="toystory.jpg"/> ${manufacturer}</li>
                        </g:each>
                    </ul>
                </div>
                <a class="buttons next" href="#">right</a>
            </div>
            <script type="text/javascript">
                $(document).ready(function(){
                    $('#manufacturers').tinycarousel({ display: 2 });
                });
            </script>
        </tpl:block>

        <tpl:block name="footer">
            <table>
                <tr>
                    <td>
                        <h1>Информация</h1>
                    </td>
                </tr>
            </table>
        </tpl:block>

    </tpl:useTemplate>
</g:applyLayout>