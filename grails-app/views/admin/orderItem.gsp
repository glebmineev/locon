<g:applyLayout name="main">
  <tpl:useTemplate template="/template">

    <tpl:block name="header">
      <h1>Администрирование магазина</h1>
    </tpl:block>

    <tpl:block name="menu"/>

    <tpl:block name="wrap_content">
      <tpl:zkBody zul="/zul/admin/orderItem.zul"/>
    </tpl:block>

    <tpl:block name="footer"/>

  </tpl:useTemplate>
</g:applyLayout>