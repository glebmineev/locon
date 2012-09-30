<g:applyLayout name="main">
    <tpl:useTemplate template="/template">

        <tpl:block name="header">
            <h1>Администрирование магазина</h1>
        </tpl:block>

        <tpl:block name="menu">
          <g:link controller="admin" action="importCatalog">Импортировать каталог</g:link>
          <g:link controller="admin" action="orders">Заказы</g:link>
        </tpl:block>

        <tpl:block name="wrap_content">

        </tpl:block>

        <tpl:block name="footer">

        </tpl:block>

    </tpl:useTemplate>
</g:applyLayout>