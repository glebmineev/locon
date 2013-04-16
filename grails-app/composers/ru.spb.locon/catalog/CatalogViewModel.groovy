package ru.spb.locon.catalog

import com.google.common.collect.Lists
import javassist.tools.web.Viewer
import org.zkoss.bind.annotation.BindingParam
import org.zkoss.bind.annotation.Command
import org.zkoss.bind.annotation.ContextParam
import org.zkoss.bind.annotation.ContextType
import org.zkoss.bind.annotation.GlobalCommand
import org.zkoss.bind.annotation.Init
import org.zkoss.bind.annotation.NotifyChange
import org.zkoss.zk.ui.Executions
import org.zkoss.zk.ui.Page
import org.zkoss.zk.ui.event.Event
import org.zkoss.zkplus.databind.BindingListModelList
import org.zkoss.zkplus.spring.SpringUtil
import org.zkoss.zul.*
import org.zkoss.zul.event.PagingEvent
import ru.spb.locon.*
import ru.spb.locon.tree.node.CategoryTreeNode

/**
 * Created with IntelliJ IDEA.
 * User: Gleb-PC
 * Date: 16.02.13
 * Time: 12:40
 * To change this template use File | Settings | File Templates.
 */
class CatalogViewModel {

  DefaultTreeModel categoryTreeModel
  ListModelList<List<ProductEntity>> productsModel
  ListModelList<ManufacturerEntity> manufsFilterModel
  ListModelList<FilterEntity> usageFilterModel
  ListModelList<FilterGroupEntity> groupFilterModel

  Long categoryID
  List<ProductEntity> products
  CartService cartService = SpringUtil.getApplicationContext().getBean("cartService") as CartService

  @Init
  public void init() {

    categoryID = Long.parseLong(Executions.getCurrent().getParameter("category"))

    List<CategoryEntity> categories = CategoryEntity.findAllWhere(parentCategory: null)
    CategoryTreeNode root = new CategoryTreeNode(null, "ROOT")
    createTreeModel(root, categories)

    categoryTreeModel = new DefaultTreeModel(root)

    CategoryEntity category = CategoryEntity.get(categoryID)
    products = collectAllProducts(category, Lists.newArrayList())
    List<FilterEntity> filters = products.filter.unique() as List<FilterEntity>
    manufsFilterModel = new BindingListModelList<ManufacturerEntity>(
        products.manufacturer.unique() as List<ManufacturerEntity>,
        true
    )
    manufsFilterModel.setMultiple(true)
    usageFilterModel = new BindingListModelList<FilterEntity>(filters, true)
    usageFilterModel.setMultiple(true)
    productsModel = new BindingListModelList<List<ProductEntity>>(split(products, 3), true)

  }

  /*
   * метод формирует модель дерева категорий.
   */
  void createTreeModel(CategoryTreeNode parent, List<CategoryEntity> children) {
    children.each { CategoryEntity category ->
      CategoryTreeNode node = new CategoryTreeNode(category, category.name)
      parent.children.add(node)
      if (category.id == categoryID) {
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

  @Command
  @NotifyChange(["productsModel"])
  public void filtred() {

    List<String> manufsSelection = manufsFilterModel.getSelection().collect { it.name } as List<String>
    List<String> usageSelection = usageFilterModel.getSelection().collect { it.name } as List<String>

    List<ProductEntity> result = products
    if (manufsSelection.size() > 0)
      result = products.findAll { it -> manufsSelection.contains(it.manufacturer.name) }

    if (usageSelection.size() > 0)
      result = result.findAll { it -> usageSelection.contains(it.filter.name) }

    productsModel.clear()
    productsModel.addAll(split(result, 3))
  }

  /**
   * обновляем модели фильтров и товаров.
   * @param event
   */
  @Command
  public void refreshModels(@ContextParam(ContextType.TRIGGER_EVENT) Event event) {
    Treeitem treeitem = event.getTarget() as Treeitem
    CategoryTreeNode node = treeitem.getValue() as CategoryTreeNode
    CategoryEntity categoryEntity = node.getData()
    categoryID = categoryEntity.id

    CategoryEntity retrivedCategory = CategoryEntity.get(categoryID)
    products = collectAllProducts(retrivedCategory, Lists.newArrayList())

    //обновляем модель фильтра по производителю.
    manufsFilterModel.clear()
    manufsFilterModel.addAll(products.manufacturer.unique() as List<ManufacturerEntity>)

    //обновляем модель фильтра по применению.
    usageFilterModel.clear()
    usageFilterModel.addAll(products.filter.unique() as List<FilterEntity>)

    //обновляем модель товаров.
    productsModel.clear()
    productsModel.addAll(split(products, 3))

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
    else {
      products.addAll(category.products as List<ProductEntity>)
    }

    return products
  }

  public List<List<ProductEntity>> split(List list, int i ){
    List<List<ProductEntity>> out = new ArrayList<List<ProductEntity>>();

    int size = list.size();

    int number = size/i;
    int remain = size % i;
    if(remain != 0){
      number++;
    }

    for(int j = 0; j < number; j++){
      int start  = j * i;
      int end =  start+ i;
      if(end > list.size()){
        end = list.size();
      }
      out.add(list.subList(start, end));
    }
    return out;
  }

  @Command
  public void redirectToProductItem(@BindingParam("product") ProductEntity product){
    Executions.sendRedirect("/shop/product?product=${product.id}")
  }

  @Command
  public void toCart(@BindingParam("product") ProductEntity product){
    cartService.addToCart(ProductEntity.get(product.id))
  }

  ListModelList<FilterGroupEntity> getGroupFilterModel() {
    return groupFilterModel
  }

  void setGroupFilterModel(ListModelList<FilterGroupEntity> groupFilterModel) {
    this.groupFilterModel = groupFilterModel
  }

  DefaultTreeModel getCategoryTreeModel() {
    return categoryTreeModel
  }

  ListModelList<FilterEntity> getUsageFilterModel() {
    return usageFilterModel
  }

  ListModelList<FilterEntity> getManufsFilterModel() {
    return manufsFilterModel
  }

  void setCategoryTreeModel(DefaultTreeModel categoryTreeModel) {
    this.categoryTreeModel = categoryTreeModel
  }

  void setManufsFilterModel(ListModelList<FilterEntity> manufsFilterModel) {
    this.manufsFilterModel = manufsFilterModel
  }

  void setUsageFilterModel(ListModelList<FilterEntity> usageFilterModel) {
    this.usageFilterModel = usageFilterModel
  }

  ListModelList<ProductEntity> getProductsModel() {
    return productsModel
  }

  void setProductsModel(ListModelList<ProductEntity> productsModel) {
    this.productsModel = productsModel
  }

}
