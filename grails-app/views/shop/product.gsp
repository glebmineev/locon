<g:applyLayout name="main">
  <tpl:useTemplate template="/template">

    <tpl:block name="header">
      <g:include view="common/shopHeader.gsp"/>
    </tpl:block>

    <tpl:block name="menu">
      <g:include view="common/menu.gsp"/>
    </tpl:block>

    <tpl:block name="wrap_content">
      <tpl:zkBody zul="/zul/shop/product.zul"/>
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