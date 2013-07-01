%{--<%
  def imageService = grailsApplication.mainContext.getBean("imageService");
%>
<ul id="manufacturers" class="jcarousel-skin-tango">
  <g:each in="${manufacturers}" var="manufacturer">
    <%
      def dir = "${imageService.manufacturers}/${manufacturer.id}"
      def image = "1-80${imageService.getImageExtension(dir)}"
    %>
    <li><img dir="" src="file:///${dir}/${image}"/></li>
  </g:each>
</ul>--}%
<div class="recommended">
  <tpl:zkBody zul="/zul/shop/carousel.zul"/>
</div>


%{--<script type="text/javascript">
  jQuery(document).ready(function () {
    jQuery('#manufacturers').jcarousel({

    });
  });
</script>--}%
