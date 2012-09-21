<ul id="manufacturers" class="jcarousel-skin-tango">
  <g:each in="${manufacturers}" var="manufacturer">
    <li><g:img dir="images/carousel" file="Indola-80x80.png"/></li>
  </g:each>
</ul>

<script type="text/javascript">
  jQuery(document).ready(function () {
    jQuery('#manufacturers').jcarousel({

    });
  });
</script>
