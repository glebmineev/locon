package ru.spb.locon.catalog

import org.zkoss.zk.grails.composer.*
import org.zkoss.zul.Window
import org.zkoss.zul.Tree

import org.zkoss.zul.Treeitem

import org.zkoss.zul.Listbox

import org.springframework.context.MessageSource
import org.zkoss.zkplus.spring.SpringUtil

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
  FilterRenderer filterRenderer = new FilterRenderer()

  Listbox products
  BindingListModelList<ProductEntity> productsModel

  Long categoryId

  MessageSource messageSource = (MessageSource) SpringUtil.getApplicationContext().getBean("messageSource")

  def afterCompose = {Window window ->
    categoryId = Long.parseLong(execution.getParameter("category"))

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
    categoryTree.addEventListener(Events.ON_CLICK, treeListener)
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
    products.addEventListener(Events.ON_CLICK, productsLister)
    //заполняем listbox.
    CategoryEntity category = CategoryEntity.get(categoryId)
    if (productsModel == null) {
      productsModel = new BindingListModelList<ProductEntity>(listProducts(category), true)
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

    CategoryEntity category = CategoryEntity.get(categoryId)
    if (productFilterModel == null) {
      productFilterModel = new BindingListModelList<ProductFilterEntity>(listProductFilter(category, defaultGroup), true)
    }

    currentCategory = CategoryEntity.get(categoryId)
    productFilter.addEventListener(Events.ON_CLICK, productFilterListener)
    productFilter.setModel(productFilterModel)
    productFilter.setItemRenderer(filterRenderer)
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


    filterGroups.addEventListener(Events.ON_CLICK, new EventListener(){
      @Override
      void onEvent(Event t) {
        CategoryEntity category = categoryTree.selectedItem.value
        Tab tab = filterGroups.selectedTab
        rebuildFilterModel(category, tab.label)
        rebuildListboxModel(category)
        productFilter.detach()
        filterGroups.selectedPanel.appendChild(productFilter)
      }
    })

  }

  List<ProductFilterEntity> checked = new ArrayList<ProductFilterEntity>()
  CategoryEntity currentCategory

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

      if (checkbox.checked) {
        checked.add(value)
      }
      else
        checked.remove(value)

      productsModel.clear()

      Collection<ProductEntity> retrieved = currentCategory.listCategoryProduct.product
      if (checked.size() > 0) {
        retrieved.each {ProductEntity product ->
          //if (checked.contains(product.productFilter))
          //  productsModel.add(product)
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
      if (category.id == categoryId) {
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

  void openParent(CategoryTreeNode node){
    CategoryTreeNode parent = (CategoryTreeNode) node.getParent()
    if (parent != null) {
      parent.setOpen(true)
      openParent(parent)
    }

  }
  
  EventListener productsLister = new EventListener() {
    @Override
    void onEvent(Event t) {
      int index = products.getSelectedIndex()
      final Listitem listitem = products.getItemAtIndex(index)

      final Treeitem selectedTreeItem = categoryTree.getSelectedItem()
      CategoryEntity category = (CategoryEntity) selectedTreeItem.getValue()

      if (listitem != null) {
        ProductEntity product = (ProductEntity) listitem.getValue()
        Executions.sendRedirect("/shop/product?category=${category.id}&product=${product.id}")
      }
    }
  }

  EventListener treeListener = new EventListener() {
    @Override
    void onEvent(Event t) {
      final Treeitem selectedTreeItem = categoryTree.getSelectedItem()
      if (selectedTreeItem.open)
        selectedTreeItem.setOpen(false)
      else
        selectedTreeItem.setOpen(true)
      if (selectedTreeItem != null) {
        CategoryEntity category = (CategoryEntity) selectedTreeItem.getValue()
        rebuildListboxModel(category)
        rebuildFilterModel(category, filterGroups.selectedTab.label)
      }

    }
  }

  /*
  * метод переформирует модель listbox при первоначальной загрузке и выборе в дереве категорий.
  */

  private void rebuildListboxModel(CategoryEntity category) {
    productsModel.clear()
    productsModel.addAll(listProducts(category))
  }

  private List<ProductEntity> listProducts(CategoryEntity category) {
    Collection<ProductEntity> products = category.listCategoryProduct.product
    return products
  }

  public void rebuildFilterModel(CategoryEntity category, String group) {
    List<ProductFilterEntity> productFilter = listProductFilter(category, group)
    currentCategory = category
    checked.clear()
    productFilterModel.clear()
    productFilterModel.addAll(productFilter)
  }

  private List<ProductFilterEntity> listProductFilter(CategoryEntity category, String group) {

    //Comboitem item = filterGroups.selectedItem
    //int i = 0
    List<ProductFilterEntity> result = category.productFilterCategoryList.productFilter.asList().findAll {
      it.productFilterGroup.name.equals(group)
    }

    //List<ProductFilterEntity> result = category.productFilterCategoryList.productFilter as List<ProductFilterEntity>
    return result
  }


}