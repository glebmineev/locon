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
        <g:set var="haveResults" value="${searchResult?.results}"/>

        <g:if test="${haveResults}">
          <g:each in="${searchResult.results}" var="result" status="i">
            <g:set var="name" value="${result.name}"/>
            <g:set var="description" value="${result.description}"/>
            <h3>
              <g:link controller="shop" action="product" params="[product: result.id]">
                ${name}
              </g:link>
              <br/>
              <span>${description}</span>
            </h3>
            <br/>
          </g:each>
        </g:if>

        <g:if test="${haveResults}">
          <g:set var="totalPages" value="${Math.ceil(searchResult.total / searchResult.max)}"/>
          <g:if test="${totalPages == 1}">
            <span class="currentStep">1</span>
          </g:if>
          <g:else>
            <g:paginate controller="search" action="index" params="[query: params.query]"
                        total="${searchResult.total}" prev="&lt;" next="&gt;"/>
          </g:else>
        </g:if>
      </div>
    </tpl:block>

    <tpl:block name="footer">
      <g:include view="common/footer.gsp"/>
    </tpl:block>

  </tpl:useTemplate>
</g:applyLayout>