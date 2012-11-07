<g:applyLayout name="main">
  <tpl:useTemplate template="/template">

    <tpl:block name="header">
      <h1>Администрирование магазина</h1>
    </tpl:block>

    <tpl:block name="menu">
      <table width="100%">
        <tr>
          <td>
            <h2>
              <g:link controller="admin" action="importCatalog" style="color: #B8D335;">Импортировать каталог</g:link>
            </h2>
          </td>
          <td>
            <h2>
              <g:link controller="admin" action="orders" style="color: #B8D335;">Заказы</g:link>
            </h2>
          </td>
          <td>
            <h2>
              <g:link controller="shop" action="index" style="color: #B8D335;">В Магазин</g:link>
            </h2>
          </td>
        </tr>
      </table>
    </tpl:block>

    <tpl:block name="wrap_content">
      <div class="info">
        <tpl:zkBody zul="/zul/admin/orderItem.zul"/>
      </div>
    </tpl:block>

    <tpl:block name="footer"/>

  </tpl:useTemplate>
</g:applyLayout>