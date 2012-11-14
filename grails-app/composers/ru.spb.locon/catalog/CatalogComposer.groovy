package ru.spb.locon.catalog

import org.zkoss.zk.grails.composer.*
import org.zkoss.zul.Window
import org.zkoss.zul.Tree

import org.zkoss.zul.Treeitem

import org.zkoss.zul.Listbox

import org.zkoss.zk.ui.event.EventListener
import org.zkoss.zk.ui.event.Event

import org.zkoss.zk.ui.event.Events
import org.zkoss.zk.ui.Executions

import org.zkoss.zul.DefaultTreeModel
import org.zkoss.zul.TreeModel

import org.zkoss.zkplus.databind.BindingListModelList
import org.zkoss.zul.Listitem
import ru.spb.locon.tree.node.CategoryTreeNode

import org.zkoss.zk.ui.Component

import org.zkoss.zul.Checkbox
import ru.spb.locon.ProductFilterEntity
import ru.spb.locon.ProductEntity
import ru.spb.locon.CategoryEntity
import ru.spb.locon.ProductFilterGroupEntity
import org.zkoss.zul.Tabbox
import org.zkoss.zul.Tabs
import org.zkoss.zul.Tabpanels
import org.zkoss.zul.Tab
import org.zkoss.zul.Tabpanel
import org.zkoss.zul.Listhead
import org.zkoss.zul.Listheader

class CatalogComposer extends GrailsComposer {

  Tree categoryTree
  TreeModel treeModel

  Tabbox filterGroups
  String defaultGroup = "Производитель"
  
  Listbox productFilter
  BindingListModelList<ProductFilterEntity> productFilterModel

  Listbox products
  BindingListModelList<ProductEntity> productsModel

  List<ProductFilterEntity> checked = new ArrayList<ProductFilterEntity>()
  CategoryEntity currentCategory

  def afterCompose = {Window window ->
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
      productsModel = new BindingListModelList<ProductEntity>(currentCategory.listCategoryProduct.product as List<ProductEntity>, true)
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
      productFilterModel = new BindingListModelList<ProductFilterEntity>(listProductFilter(defaultGroup), true)

    productFilter.addEventListener(Events.ON_CLICK, productFilterListener)
    productFilter.setModel(productFilterModel)
    productFilter.setItemRenderer(new FilterRenderer())
  }

  void initializeFilterTabs(){
    filterGroups.setWidth("260px")
    filterGroups.appendChild(new Tabs())
    filterGroups.appendChild(new Tabpanels())
    ProductFilterGroupEntity.list().each {ProductFilterGroupEntity item ->

      Tab tab = new Tab(item.name)
      Tabpanel tabpanel = new Tabpanel()
      if ("Производитель".equals(item.name)){
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
      ProductFilterEntity value = (ProductFilterEntity) listitem.getValue()
      Checkbox checkbox = (Checkbox) window.getFellow("checkbox_${value.name}")
      checkbox.checked = !checkbox.checked

      if (checkbox.checked)
        checked.add(value)
      else
        checked.remove(value)

      productsModel.clear()

      Collection<ProductEntity> retrieved = currentCategory.listCategoryProduct.product
      if (checked.size() > 0) {
        retrieved.each {ProductEntity product ->
          product.productProductFilterList.productFilter.each {ProductFilterEntity productFilter ->
            if (checked.contains(productFilter))
              productsModel.add(product)
          }

        }
      }
      else
        productsModel.addAll(retrieved)

    }
  }

  /*
   * метод формирует мдель дерева категорий.
   */
  void createTreeModel(CategoryTreeNode parent, List<CategoryEntity> children) {
    children.each {CategoryEntity category ->
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
  void openParent(CategoryTreeNode node){
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
  public void onClick_products(Event event){

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
      currentCategory = (CategoryEntity) selectedTreeItem.getValue()
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
    productsModel.addAll(currentCategory.listCategoryProduct.product as List<ProductEntity>)
  }

  /**
   * метод переформирует модель фильтра товаров.
   * @param group - группа фильтра
   */
  void rebuildFilterModel(String group) {
    List<ProductFilterEntity> productFilter = listProductFilter(group)
    checked.clear()
    productFilterModel.clear()
    productFilterModel.addAll(productFilter)
  }

  private List<ProductFilterEntity> listProductFilter(String group) {
    List<ProductFilterEntity> result = currentCategory.productFilterCategoryList.productFilter.asList().findAll {
      it.productFilterGroup.name.equals(group)
    }
    return result
  }

}