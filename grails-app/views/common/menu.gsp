<%@ page import="domain.DomainUtils" %>
<ul id="nav">
  <g:each in="${mainCategoties}" var="parent">
    <li>
      <g:link controller="shop" action="catalog" params="[category: parent.id]">${parent.name}</g:link>
      <ul>
        <g:each in="${DomainUtils.getChildCategories(parent.name)}" var="child">
          <li>
            <g:link controller="shop" action="catalog" params="[category: child.id]">${child.name}</g:link>
          </li>
        </g:each>
      </ul>

      <div class="clear"></div>
    </li>
  </g:each>
</ul>

<div class="clear"></div>