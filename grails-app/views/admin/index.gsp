<g:applyLayout name="main">
    <tpl:useTemplate template="/template">

        <tpl:block name="header">
            <h1>Администрирование магазина</h1>
        </tpl:block>

        <tpl:block name="menu">
%{--            <ul id="nav">

                <li>
                    Admin1
                    <ul>
                        <li><a href="#">Item 01</a></li>
                        <li><a href="#" class="selected">Item 02</a></li>
                        <li><a href="#">Item 03</a></li>
                    </ul>

                    <div class="clear"></div>
                </li>
            </ul>

            <div class="clear"></div>--}%
            <g:link controller="admin" action="importCatalog">Импортировать каталог</g:link>
        </tpl:block>

        <tpl:block name="wrap_content">

        </tpl:block>

        <tpl:block name="footer">

        </tpl:block>

    </tpl:useTemplate>
</g:applyLayout>