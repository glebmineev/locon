<g:applyLayout name="main">
  <tpl:useTemplate template="/template">

    <tpl:block name="header">
      <g:include view="common/shopHeader.gsp"/>
    </tpl:block>

    <div id="menu" class="menu">

    </div>

    <tpl:block name="wrap_content">
      <tpl:zkBody zul="/zul/shop/catalog.zul"/>
    </tpl:block>

    <div id="footer">

    </div>

  </tpl:useTemplate>
</g:applyLayout>