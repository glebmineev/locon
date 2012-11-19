<g:applyLayout name="main">
  <tpl:useTemplate template="/template">

    <tpl:block name="header">
      <h1>Администрирование магазина</h1>
    </tpl:block>

    <tpl:block name="menu">
      <g:include view="common/adminMenu.gsp"/>
    </tpl:block>

    <tpl:block name="wrap_content">
      <div class="info">
        <h2>Информация о магазине</h2>
%{--        <g:form id="contacts" controller="admin" action="info">
          <ckeditor:editor id="editor_contacts" height="400px" width="80%">
            ${contacts}
          </ckeditor:editor>
          <g:submitButton name="сохранить"/>
        </g:form>--}%
        <tpl:zkBody zul="/zul/admin/info.zul"/>
      </div>
    </tpl:block>

    <tpl:block name="footer"/>

  </tpl:useTemplate>
</g:applyLayout>