package ru.spb.locon.catalog

import org.zkoss.zk.grails.composer.*

import org.zkoss.zk.ui.event.*
import org.zkoss.zk.ui.Executions

import org.zkoss.zul.*

import org.zkoss.zkplus.databind.BindingListModelList
import ru.spb.locon.tree.node.CategoryTreeNode

import org.zkoss.zk.ui.Component

import ru.spb.locon.*

class CatalogComposer extends GrailsComposer {

  Tree categoryTree
  TreeModel treeModel

  Tabbox filterGroups
  String defaultGroup = "Производитель"

  Listbox productFilter
  BindingListModelList<FilterEntity> productFilterModel

  Listbox products
  BindingListModelList<ProductEntity> productsModel

  List<Long> checked = new ArrayList<Long>()
  CategoryEntity currentCategory

  def afterCompose = { Window window ->
    Long categoryId = Long.parseLong(execution.getParameter("category"))
    currentCategory = CategoryEntity.get(categoryId)

    productFilter = new Listbox()
    initializeTree()
    initializeProductListBox()

    initializeFilterTabs()
    initializeFilterListBox()

  }

  /*
   * метод инициализирует дерево категорий.
   */

  private void initializeTree() {
    //берем все категории без парента.
    List<CategoryEntity> categories = CategoryEntity.findAllWhere(parentCategory: null)
    CategoryTreeNode root = new CategoryTreeNode(null, new ArrayList<CategoryTreeNode>())
    createTreeModel(root, categories)

    treeModel = new DefaultTreeModel<CategoryEntity>(root)
    categoryTree.setTreeitemRenderer(new TreeCategoryRenderer())
    categoryTree.setModel(treeModel)
  }

  /**
   * метод инициализирует listbox товаров.
   */
  private void initializeProductListBox() {
    if (productsModel == null) {
      List<ProductEntity> result = collectAllProducts(currentCategory, new ArrayList<ProductEntity>())
      productsModel = new BindingListModelList<ProductEntity>(result, true)
    }
    products.setModel(productsModel)
    products.setItemRenderer(new ProductRenderer())
  }

  /**
   * инициализация фильтра.
   */
  private void initializeFilterListBox() {

    productFilter.setWidth("256px")
    productFilter.appendChild(new Listhead())
    Listheader cbHeader = new Listheader()
    cbHeader.setWidth("50px")
    productFilter.listhead.appendChild(cbHeader)
    productFilter.listhead.appendChild(new Listheader())

    if (productFilterModel == null)
      productFilterModel = new BindingListModelList<FilterEntity>(listProductFilter(defaultGroup), true)

    productFilter.addEventListener(Events.ON_CLICK, productFilterListener)
    productFilter.setModel(productFilterModel)
    productFilter.setItemRenderer(new FilterRenderer())
  }

  void initializeFilterTabs() {
    filterGroups.setWidth("260px")
    filterGroups.appendChild(new Tabs())
    filterGroups.appendChild(new Tabpanels())
    FilterGroupEntity.list().each { FilterGroupEntity item ->

      Tab tab = new Tab(item.name)
      Tabpanel tabpanel = new Tabpanel()
      if ("Производитель".equals(item.name)) {
        tab.setSelected(true)
        tabpanel.appendChild(productFilter)
      }
      filterGroups.tabs.appendChild(tab)
      filterGroups.tabpanels.appendChild(tabpanel)
    }

  }

  public void onClick_filterGroups(Event event) {
    Tab tab = filterGroups.selectedTab
    rebuildFilterModel(tab.label)
    rebuildListboxModel()
    productFilter.detach()
    filterGroups.selectedPanel.appendChild(productFilter)
  }

  EventListener productFilterListener = new EventListener() {
    @Override
    void onEvent(Event t) {
      Component image = t.target
      Window window = (Window) image.getPage().getFirstRoot()

      int index = productFilter.getSelectedIndex()
      Listitem listitem = productFilter.getItemAtIndex(index)
      FilterEntity value = (FilterEntity) listitem.getValue()
      Checkbox checkbox = (Checkbox) window.getFellow("checkbox_${value.name}")
      checkbox.checked = !checkbox.checked

      if (checkbox.checked)
        checked.add(value.id)
      else
        checked.remove(value.id)

      productsModel.clear()

      CategoryEntity categoryEntity = CategoryEntity.get(currentCategory.id)

      List<ProductEntity> retrieved = collectAllProducts(categoryEntity, new ArrayList<ProductEntity>())

      if (checked.size() > 0) {
        retrieved.each { ProductEntity product ->
          ProductEntity get = ProductEntity.get(product.id)

          get.filters.each { FilterEntity productFilter ->
            if (checked.contains(productFilter.id))
              productsModel.add(get)
          }

        }
      } else
        productsModel.addAll(retrieved)

    }

    int r = 0
  }

  List<ProductEntity> collectAllProducts(CategoryEntity category, List<ProductEntity> products) {
    List<CategoryEntity> categories = category.listCategory as List<CategoryEntity>
    if (categories != null && categories.size() > 0)
    categories.each { CategoryEntity it ->
      if (it.listCategory != null && it.listCategory.size() > 0)
        collectAllProducts(it, products)
      else
        products.addAll(it.products as List<ProductEntity>)
    }
    else
    {
      products.addAll(category.products as List<ProductEntity>)
    }
    return products
  }

  /*
   * метод формирует мдель дерева категорий.
   */

  void createTreeModel(CategoryTreeNode parent, List<CategoryEntity> children) {
    children.each { CategoryEntity category ->
      CategoryTreeNode node = new CategoryTreeNode(category, new ArrayList<CategoryTreeNode>())
      parent.children.add(node)
      if (category.id == currentCategory.id) {
        openParent(node)
        node.setOpen(true)
        node.setSelected(true)
      }
      if (category.listCategory != null &&
          category.listCategory.size() > 0) {
        createTreeModel(node, category.listCategory.asList())
      }
    }
  }

  /**
   * открывает рекурсивно все ноды родители.
   * @param node - предыдущая нода.
   */
  void openParent(CategoryTreeNode node) {
    CategoryTreeNode parent = (CategoryTreeNode) node.getParent()
    if (parent != null) {
      parent.setOpen(true)
      openParent(parent)
    }

  }

  /**
   * Слушатель списка товаров.
   * @param event - событие.
   */
  public void onClick_products(Event event) {

    int index = products.getSelectedIndex()
    Listitem listitem = products.getItemAtIndex(index)

    if (listitem != null) {
      ProductEntity product = (ProductEntity) listitem.getValue()
      Executions.sendRedirect("/shop/product?category=${currentCategory.id}&product=${product.id}")
    }

  }

  public void onClick_categoryTree(Event event) {
    Treeitem selectedTreeItem = categoryTree.getSelectedItem()
    if (selectedTreeItem.open)
      selectedTreeItem.setOpen(false)
    else
      selectedTreeItem.setOpen(true)
    if (selectedTreeItem != null) {
      CategoryEntity categoryEntity = (CategoryEntity) selectedTreeItem.getValue()
      currentCategory = CategoryEntity.get(categoryEntity.id)
      rebuildListboxModel()
      rebuildFilterModel(filterGroups.selectedTab.label)
    }
  }

  /**
   * метод переформирует модель товаров.
   * @param category - категория
   */
  void rebuildListboxModel() {
    productsModel.clear()
    List<ProductEntity> result = collectAllProducts(currentCategory, new ArrayList<ProductEntity>())
    productsModel.addAll(result)
  }

  /**
   * метод переформирует модель фильтра товаров.
   * @param group - группа фильтра
   */
  void rebuildFilterModel(String group) {
    List<FilterEntity> productFilter = listProductFilter(group)
    checked.clear()
    productFilterModel.clear()
    productFilterModel.addAll(productFilter)
  }

  private List<FilterEntity> listProductFilter(String group) {
    List<FilterEntity> result = currentCategory.filters.asList().findAll {
      it.filterGroup.name.equals(group)
    }
    return result
  }

}