package ru.spb.locon

import org.zkoss.zk.grails.composer.*
import org.zkoss.zul.Window
import org.zkoss.zul.Tree

import org.zkoss.zul.Treeitem

import org.zkoss.zul.Listbox

import org.springframework.context.MessageSource
import org.zkoss.zkplus.spring.SpringUtil
import ru.spb.locon.renderers.ProductRenderer

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
import org.zkoss.zul.Image
import org.zkoss.zk.ui.Component
import org.zkoss.zul.Label
import org.zkoss.zul.Checkbox

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
    currentCategory = CategoryEntity.get(categoryId)
    productFilter.addEventListener(Events.ON_CLICK, productFilterLister)
    productFilter.setModel(productFilterModel)
    productFilter.setItemRenderer(filterRenderer)
  }

  List<ProductFilterEntity> checked = new ArrayList<ProductFilterEntity>()
  CategoryEntity currentCategory

  EventListener productFilterLister = new EventListener() {
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
          if (checked.contains(product.productFilter))
            productsModel.add(product)
        }
      }
      else
        productsModel.addAll(retrieved)


    }
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
      if (selectedTreeItem.open)
        selectedTreeItem.setOpen(false)
      else
        selectedTreeItem.setOpen(true)
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
    currentCategory = category
    //filterRenderer.setCategory(category)
    //filterRenderer.checked.clear()
    checked.clear()
    productFilterModel.clear()
    productFilterModel.addAll(productFilter)
  }

  private List<ProductFilterEntity> listProductFilter(CategoryEntity category) {
    List<ProductFilterEntity> result = category.productFilterCategoryList.productFilter as List<ProductFilterEntity>
    return result
  }


}