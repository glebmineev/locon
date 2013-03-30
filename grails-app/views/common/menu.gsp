<%@ page import="ru.spb.locon.domain.DomainUtils" %>
%{--<ul id="nav">
  <g:set var="count" value="${0}"/>
  <g:each in="${mainCategoties}" var="parent">
    <li class="main">
    <g:link controller="shop" action="catalog" params="[category: parent.id]">${parent.name}</g:link>
    <ul>
      <g:each in="${DomainUtils.getChildCategories(parent.name)}" var="child">
        <li class="submenu">
          <g:link controller="shop" action="catalog" params="[category: child.id]">${child.name}</g:link>
        </li>
      </g:each>
    </ul>
      <g:if test="${count / 3 != 0}"/>
      <g:set var="count" value="${count + 1}"/>
      <div class="clear"></div>
    </li>
  </g:each>
</ul>--}%

<div class="green">
<ul id="mega-menu" class="mega-menu">

  <g:set var="count" value="${0}"/>

  <g:each in="${mainCategoties}" var="parent">
    <li><g:link controller="shop" action="catalog" params="[category: parent.id]">${parent.name}</g:link>
    <g:if test="${DomainUtils.getChildCategories(parent.name).size() > 0}">
      <ul>
      <g:each in="${DomainUtils.getChildCategories(parent.name)}" var="child">

        <li>
          <ul>
            <li>
              <g:link controller="shop" action="catalog" params="[category: child.id]">${child.name}</g:link>
            </li>
          </ul>
        </li>

        <g:set var="count" value="${count + 1}"/>
      </g:each>
      </li>
      </ul>
    </g:if>
  </g:each>

</div>

<div class="clear"></div>