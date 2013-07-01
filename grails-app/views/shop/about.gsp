<g:applyLayout name="main">
  <tpl:useTemplate template="/template">

    <%
      def initService = grailsApplication.mainContext.getBean("initService");
    %>

    <tpl:block name="header">
      <g:include view="common/header.gsp"/>
    </tpl:block>

    <tpl:block name="menu">
      <g:include view="common/menu.gsp"/>
    </tpl:block>

    <tpl:block name="wrap_content">
      %{--<tpl:zkBody zul="/zul/shop/test.zul"/>--}%
      <div class="info">
        <div id="test"/>
        <g:javascript>
          $('#test').html('${initService.info.details}');
        </g:javascript>
      </div>
    </tpl:block>

    <tpl:block name="footer">
      <g:include view="common/footer.gsp"/>
    </tpl:block>

  </tpl:useTemplate>
</g:applyLayout>