package ru.spb.locon.catalog

import com.google.common.collect.Lists
import org.zkoss.bind.annotation.Command
import org.zkoss.bind.annotation.ContextParam
import org.zkoss.bind.annotation.ContextType
import org.zkoss.bind.annotation.Init
import org.zkoss.zk.ui.Executions
import org.zkoss.zk.ui.Page
import org.zkoss.zk.ui.event.Event
import org.zkoss.zkplus.databind.BindingListModelList
import org.zkoss.zkplus.spring.SpringUtil
import org.zkoss.zul.*
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

  ListModelList<ProductEntity> productsModel
  ProductRenderer productsRenderer

  ListModelList<FilterEntity> manufsFilterModel
  ListModelList<FilterEntity> usageFilterModel
  ListModelList<FilterGroupEntity> groupFilterModel

  Long categoryID

  ImageService imageSyncService = SpringUtil.getApplicationContext().getBean("imageService") as ImageService
  CartService cartService = SpringUtil.getApplicationContext().getBean("cartService") as CartService

  List<Long> checked = new ArrayList<Long>()

  @Init
  public void init() {

    categoryID = Long.parseLong(Executions.getCurrent().getParameter("category"))

    List<CategoryEntity> categories = CategoryEntity.findAllWhere(parentCategory: null)
    CategoryTreeNode root = new CategoryTreeNode(null, new ArrayList<CategoryTreeNode>())
    createTreeModel(root, categories)

    categoryTreeModel = new DefaultTreeModel(root)

    productsModel = new BindingListModelList<ProductEntity>(ProductEntity.list(), true)
    productsRenderer = new ProductRenderer()

    CategoryEntity category = CategoryEntity.get(categoryID)
    FilterGroupEntity manufsGroup = FilterGroupEntity.findByName("Производитель")
    List<FilterEntity> filters = category.filters as List<FilterEntity>
    manufsFilterModel = new BindingListModelList<FilterEntity>(
        filters.findAll { it ->
          it.filterGroup.name.equals(manufsGroup.name)
        },
        true
    )
    manufsFilterModel.setMultiple(true)

    FilterGroupEntity usageGroup = FilterGroupEntity.findByName("Применение")
    usageFilterModel = new BindingListModelList<FilterEntity>(
        filters.findAll { it ->
          it.filterGroup.name.equals(usageGroup.name)
        },
        true
    )

    usageFilterModel.setMultiple(true)

    groupFilterModel = new BindingListModelList<FilterGroupEntity>(FilterGroupEntity.list(sort: "name"), true)

    productsModel = new BindingListModelList<ProductEntity>(collectAllProducts(category, Lists.newArrayList()), true)
    productsRenderer = new ProductRenderer()

  }

  /*
   * метод формирует модель дерева категорий.
   */

  void createTreeModel(CategoryTreeNode parent, List<CategoryEntity> children) {
    children.each { CategoryEntity category ->
      CategoryTreeNode node = new CategoryTreeNode(category, new ArrayList<CategoryTreeNode>())
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
  public void refreshProducts(@ContextParam(ContextType.TRIGGER_EVENT) Event event) {
    Listitem target = event.target as Listitem

    FilterEntity filter = target.getValue() as FilterEntity

    if (target.selected)
      checked.add(filter.id)
    else
      checked.remove(filter.id)


    CategoryEntity categoryEntity = CategoryEntity.get(categoryID)
    List<ProductEntity> retrieved = collectAllProducts(categoryEntity, new ArrayList<ProductEntity>())
    productsModel.clear()

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

  @Command
  public void addAllProducts(@ContextParam(ContextType.TRIGGER_EVENT) Event event) {
    Listheader target = event.target as Listheader

    FilterEntity filter = target.getValue() as FilterEntity

  }

  @Command
  public void selectAllManufs(@ContextParam(ContextType.TRIGGER_EVENT) Event event) {
    Listitem listitem = event.getTarget() as Listitem
    if (listitem.isSelected())
      manufsFilterModel.setSelection(manufsFilterModel.getInnerList())

  }

  @Command
  public void search(@ContextParam(ContextType.TRIGGER_EVENT) Event event) {

    List<String> manufsSelection = manufsFilterModel.getSelection().collect { it.name } as List<String>
    List<String> usageSelection = usageFilterModel.getSelection().collect { it.name } as List<String>

    List<ManufacturerEntity> manufs = ManufacturerEntity.findAllByNameInList(manufsSelection)
    CategoryEntity categoryEntity = CategoryEntity.get(categoryID)
    List<ProductEntity> retrieved = collectAllProducts(categoryEntity, new ArrayList<ProductEntity>())

    List<ProductEntity> result = new ArrayList<ProductEntity>()

    retrieved.each { it ->
      ManufacturerEntity manufacturer = it.manufacturer
      if (manufs.contains(manufacturer)) {
        List<FilterEntity> filters = it.getFilters() as List<FilterEntity>
        if (usageSelection.size() > 0) {
          filters.each { filter ->
            if (usageSelection.contains(filter.name))
              result.add(it)
          }
        } else
          result.add(it)

      }
    }
    productsModel.clear()
    productsModel.addAll(result)
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
    //обновляем модель фильтра по производителю.
    FilterGroupEntity manufsGroup = FilterGroupEntity.findByName("Производитель")
    manufsFilterModel.clear()
    manufsFilterModel.addAll(retrivedCategory.filters.findAll {
      it.filterGroup.name.equals(manufsGroup.name)
    } as List<FilterEntity>)

    //обновляем модель фильтра по применению.
    FilterGroupEntity usageGroup = FilterGroupEntity.findByName("Применение")
    usageFilterModel.clear()
    usageFilterModel.addAll(retrivedCategory.filters.findAll {
      it.filterGroup.name.equals(usageGroup.name)
    } as List<FilterEntity>)

    //обновляем модель товаров.
    productsModel.clear()
    productsModel.addAll(collectAllProducts(retrivedCategory, Lists.newArrayList()))

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

  ProductRenderer getProductsRenderer() {
    return productsRenderer
  }

  void setProductsRenderer(ProductRenderer productsRenderer) {
    this.productsRenderer = productsRenderer
  }

}
