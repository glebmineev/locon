<g:applyLayout name="main">
  <tpl:useTemplate template="/template">

    <tpl:block name="header">
      <g:include view="common/header.gsp"/>
    </tpl:block>

    <tpl:block name="menu">
      <g:include view="common/menu.gsp"/>
    </tpl:block>

    <tpl:block name="wrap_content">
      <g:include view="common/slider.gsp"/>
      <g:include view="common/recommended.gsp"/>
      <g:include view="common/carousel.gsp"/>
      %{--<table width="100%">
        <tr>
          <td>
            <g:include view="common/slider.gsp"/>
          </td>
        </tr>
        <tr>
          <td>
            <g:include view="common/recommended.gsp"/>
          </td>
        </tr>
        <tr>
          <td align="center">
            <g:include view="common/carousel.gsp"/>
          </td>
        </tr>
      </table>--}%
    </tpl:block>

    <tpl:block name="footer">
      <g:include view="common/footer.gsp"/>
    </tpl:block>

  </tpl:useTemplate>
</g:applyLayout>