<g:applyLayout name="main">
  <tpl:useTemplate template="/template">

    <tpl:block name="header">
      <g:include view="common/header.gsp"/>
    </tpl:block>

    <tpl:block name="menu">
      <g:include view="common/menu.gsp"/>
    </tpl:block>

    <tpl:block name="wrap_content">
      <table width="100%">
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
        <tr>
          <td align="center">
            <g:include view="common/carousel.gsp"/>
          </td>
        </tr>
      </table>
      <script type="text/javascript">

        $(window).load(function () {
          $('#slider').nivoSlider();
        });
      </script>

    </tpl:block>

    <tpl:block name="footer">
      <g:include view="common/footer.gsp"/>
    </tpl:block>

  </tpl:useTemplate>
</g:applyLayout>