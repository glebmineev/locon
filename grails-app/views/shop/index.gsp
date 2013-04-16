<g:applyLayout name="main">
  <tpl:useTemplate template="/template">

    <tpl:block name="header">
      <g:include view="common/header.gsp"/>
    </tpl:block>

    <tpl:block name="menu">
      <g:include view="common/menu.gsp"/>
    </tpl:block>

    <tpl:block name="navigate"/>

    <tpl:block name="wrap_content">
      <g:include view="common/slider.gsp"/>
      <g:include view="common/recommended.gsp"/>
      <g:include view="common/carousel.gsp"/>
    </tpl:block>

    <tpl:block name="footer">
      <g:include view="common/footer.gsp"/>
    </tpl:block>

  </tpl:useTemplate>
</g:applyLayout>