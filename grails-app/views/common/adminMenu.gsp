%{--<ul id="nav">

  <li class="main">
    <g:link controller="admin" action="importCatalog">Импорт</g:link>
  </li>

  <li class="main">
    <g:link controller="admin" action="orders">Редакторы</g:link>
    <ul>
      <li class="submenu">
        <g:link controller="admin" action="products">Товары</g:link>
      </li>
      <li class="submenu">
        <g:link controller="admin" action="info">Информация</g:link>
      </li>
      <li class="submenu">
        <g:link controller="admin" action="manufacturers">Производители</g:link>
      </li>
      <li class="submenu">
        <g:link controller="admin" action="editor">Редактор</g:link>
      </li>
    </ul>

    <div class="clear"></div>
  </li>

  <li class="main">
    <g:link controller="admin" action="orders">Заказы</g:link>
    <div class="clear"></div>
  </li>
  <li class="main">
    <g:link controller="shop" action="index">В Магазин</g:link>
    <div class="clear"></div>
  </li>

</ul>

<div class="clear"></div>--}%


<div class="green">
  <ul id="mega-menu" class="mega-menu">
    <li>
      <g:link controller="admin" action="importCatalog">Импорт</g:link>
    </li>
    <li>
      <g:link>Редакторы</g:link>
      <ul>
        <li>
          <g:link controller="admin" action="products">Товары</g:link>
        </li>
        <li>
          <g:link controller="admin" action="info">Информация</g:link>
        </li>
        <li>
          <g:link controller="admin" action="manufacturers">Производители</g:link>
        </li>
      <li>
        <g:link controller="admin" action="filters">Фильтры</g:link>
      </li>
        <li>
          <g:link controller="admin" action="editor">Редактор</g:link>
        </li>
      </ul>
    </li>
    <li>
      <g:link controller="admin" action="orders">Заказы</g:link>
    </li>
    <li>
      <g:link controller="shop" action="index">В Магазин</g:link>
    </li>
</div>