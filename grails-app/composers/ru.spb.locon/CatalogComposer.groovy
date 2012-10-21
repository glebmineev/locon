package ru.spb.locon

import org.zkoss.zk.grails.composer.*
import org.zkoss.zul.Window
import org.zkoss.zul.Tree

import org.zkoss.zul.Treeitem

import org.zkoss.zul.Listbox
import org.zkoss.zul.Listheader

import org.springframework.context.MessageSource
import org.zkoss.zkplus.spring.SpringUtil
import ru.spb.locon.renderers.ProductRenderer
import domain.DomainUtils
import org.zkoss.zk.ui.event.EventListener
import org.zkoss.zk.ui.event.Event

import org.zkoss.zk.ui.event.Events
import org.zkoss.zk.ui.Executions

import org.zkoss.zul.DefaultTreeModel
import org.zkoss.zul.TreeModel
import ru.spb.locon.renderers.TreeCategoryRenderer
import org.zkoss.zkplus.databind.BindingListModelList
import org.zkoss.zul.Listitem
import ru.spb.locon.tree.node.CategoryTreeNode
import ru.spb.locon.renderers.FilterRenderer

class CatalogComposer extends GrailsComposer {

  Tree categoryTree
  TreeModel treeModel

  Listbox productFilter
  BindingListModelList<ProductFilterEntity> productFilterModel
  FilterRenderer filterRenderer = new FilterRenderer()

  Listbox products
  BindingListModelList<ProductEntity> productsModel

  Long categoryId

  MessageSource messageSource = (MessageSource) SpringUtil.getApplicationContext().getBean("messageSource")

  def afterCompose = {Window window ->
    categoryId = Long.parseLong(execution.getParameter("category"))
    initializeProductListBox()
    initializeFilterListBox()
    initializeTree()

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

  /*
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

  private void initializeFilterListBox() {
    CategoryEntity category = CategoryEntity.get(categoryId)
    if (productFilterModel == null) {
      productFilterModel = new BindingListModelList<ProductFilterEntity>(listProductFilter(category), true)
    }
    productFilter.setMultiple(true)
    productFilter.setModel(productFilterModel)
    filterRenderer.setCategory(CategoryEntity.get(categoryId))
    filterRenderer.setProductsModel(productsModel)
    productFilter.setItemRenderer(filterRenderer)
  }

  /*
  * метод формирует мдель дерева категорий.
  */

  private void createTreeModel(CategoryTreeNode parent, List<CategoryEntity> children) {
    List<CategoryTreeNode> nodes = new ArrayList<CategoryTreeNode>()
    children.each {CategoryEntity category ->
      CategoryTreeNode node = new CategoryTreeNode(category, new ArrayList<CategoryTreeNode>())
      if (category.id == categoryId) {
        parent.setOpen(true)
        node.setOpen(true)
        node.setSelected(true)
      }
      if (category.listCategory != null &&
          category.listCategory.size() > 0) {
        createTreeModel(node, category.listCategory.asList())
      }
      nodes.add(node)
    }
    parent.children.addAll(nodes)
  }

  EventListener productsLister = new EventListener() {
    @Override
    void onEvent(Event t) {
      int index = products.getSelectedIndex()
      final Listitem listitem = products.getItemAtIndex(index)
      if (listitem != null) {
        ProductEntity product = (ProductEntity) listitem.getValue()
        Executions.sendRedirect("/shop/product?category=${categoryId}&product=${product.id}")
      }
    }
  }

  EventListener treeListener = new EventListener() {
    @Override
    void onEvent(Event t) {
      final Treeitem selectedTreeItem = categoryTree.getSelectedItem()
      if (selectedTreeItem != null) {
        CategoryEntity category = (CategoryEntity) selectedTreeItem.getValue()
        rebuildListboxModel(category)
        rebuildFilterModel(category)
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

  public void rebuildFilterModel(CategoryEntity category) {
    List<ProductFilterEntity> productFilter = listProductFilter(category)
    filterRenderer.setCategory(category)
    filterRenderer.checked.clear()
    productFilterModel.clear()
    productFilterModel.addAll(productFilter)
  }

  private List<ProductFilterEntity> listProductFilter(CategoryEntity category) {
    List<ProductFilterEntity> result = category.productFilterCategoryList.productFilter as List<ProductFilterEntity>
    return result
  }


}