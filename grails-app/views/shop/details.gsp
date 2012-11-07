<g:applyLayout name="main">
  <tpl:useTemplate template="/template">

    <tpl:block name="header">
      <g:include view="common/header.gsp"/>
    </tpl:block>

    <tpl:block name="menu">
      <g:include view="common/menu.gsp"/>
    </tpl:block>

    <tpl:block name="wrap_content">
      <div class="info">
      ООО «Косметик групп»  тел. 448-77-55
      <br>
      ИНН/КПП  7813516700  /   781301001
      <br>
      ОГРН 1117847449411
      <br>
      ОКПО 30648891
      <br>
      Юридический адрес:
      <br>
      197110, СПБ, Улица Пионерская, д. 22, пом. 45-Н
      </div>
    </tpl:block>

    <tpl:block name="footer">
      <g:include view="common/footer.gsp"/>
    </tpl:block>

  </tpl:useTemplate>
</g:applyLayout>