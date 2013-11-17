package ru.spb.locon.zulModels.admin

import com.google.common.collect.Lists
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.zkoss.bind.BindUtils
import org.zkoss.bind.annotation.BindingParam
import org.zkoss.bind.annotation.Command
import org.zkoss.bind.annotation.ContextParam
import org.zkoss.bind.annotation.ContextType
import org.zkoss.bind.annotation.Init
import org.zkoss.bind.annotation.NotifyChange
import org.zkoss.zk.ui.Executions
import org.zkoss.zk.ui.event.Event
import org.zkoss.zk.ui.sys.ExecutionsCtrl
import org.zkoss.zkplus.databind.BindingListModelList
import org.zkoss.zul.*
import ru.spb.locon.*
import ru.spb.locon.zulModels.admin.models.AdvancedTreeModel
import ru.spb.locon.zulModels.admin.windows.EditCallback
import ru.spb.locon.wrappers.CategoryTreeNode

class EditorViewModel {

  AdvancedTreeModel categoryTreeModel
  ListModelList<ProductEntity> productsModel = new BindingListModelList<ProductEntity>(Lists.newArrayList(), true)

  ListModelList<ManufacturerEntity> manufsFilterModel = new BindingListModelList<ManufacturerEntity>(Lists.newArrayList(), true)
  ListModelList<FilterEntity> usageFilterModel = new BindingListModelList<FilterEntity>(Lists.newArrayList(), true);

  Treeitem selectedItem
  CategoryTreeNode root
  Long categoryID

  CartService cartService = ApplicationHolder.getApplication().getMainContext().getBean("cartService") as CartService
  InitService initService = ApplicationHolder.getApplication().getMainContext().getBean("initService") as InitService
  ImageService imageService = ApplicationHolder.getApplication().getMainContext().getBean("imageService") as ImageService

  @Init
  public void init() {
    categoryID = Executions.getCurrent().getParameter("categoryID") as Long
    categoryTreeModel = new AdvancedTreeModel(getRootNode())
    manufsFilterModel.setMultiple(true)
    usageFilterModel.setMultiple(true)
  }

  public CategoryTreeNode getRootNode() {
    List<CategoryEntity> categories = CategoryEntity.findAllWhere(parentCategory: null)
    root = new CategoryTreeNode(null, "ROOT")
    createTreeModel(root, categories)
    return root
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
          category.listCategory.size() > 0)
        createTreeModel(node, category.listCategory.asList())
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
   * Отмечает всех производителей, если выбрано хоть одно применение.
   * @param event
   */
  @Command
  public void selectAllManufs(@ContextParam(ContextType.TRIGGER_EVENT) Event event) {
    Listitem listitem = event.getTarget() as Listitem
    if (listitem.isSelected())
      manufsFilterModel.setSelection(manufsFilterModel.getInnerList())
  }

  /**
   * Кнопка поиска.
   * @param event
   */
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
        FilterEntity filter = it.filter
        if (usageSelection.size() > 0) {
          if (usageSelection.contains(filter.name))
            result.add(it)
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
    treeitem.setOpen(!treeitem.isOpen())
    CategoryTreeNode node = treeitem.getValue() as CategoryTreeNode
    CategoryEntity categoryEntity = node.getData()
    categoryID = categoryEntity.id

    CategoryEntity retrivedCategory = CategoryEntity.get(categoryID)
    //обновляем модель фильтра по производителю.
    List<ProductEntity> products = collectAllProducts(retrivedCategory, Lists.newArrayList())

    manufsFilterModel.clear()
    manufsFilterModel.addAll(products.manufacturer.unique() as List<ManufacturerEntity>)

    //обновляем модель фильтра по применению.
    usageFilterModel.clear()
    usageFilterModel.addAll(products.filter.unique() as List<FilterEntity>)

    //обновляем модель товаров.
    productsModel.clear()
    productsModel.addAll(collectAllProducts(retrivedCategory, Lists.newArrayList()))

  }

  /**
   * Иерархический сбор товаров.
   * @param category - категория с которой собираем продукты.
   * @param filters - список хранящий продукты.
   * @return список продуктов.
   */
  List<ProductEntity> collectAllProducts(CategoryEntity category, List<ProductEntity> filters) {
    List<CategoryEntity> categories = category.listCategory as List<CategoryEntity>
    filters.addAll(category.products as List<ProductEntity>)
    if (categories != null && categories.size() > 0)
      categories.each { CategoryEntity it ->
        if (it.listCategory != null && it.listCategory.size() > 0)
          collectAllProducts(it, filters)
        else
          filters.addAll(it.products as List<ProductEntity>)
      }
    return filters
  }

  @Command
  public void saveCategory(@BindingParam("node") CategoryTreeNode node) {

    CategoryEntity.withTransaction { status ->
      String name = node.getName()
      CategoryEntity data = CategoryEntity.get(node.getData().id)
      if (!name.isEmpty() && !name.equals(data.name)) {
        data.setName(name)
        data.save(flush: true)
        initService.refreshShop()
      }

    }

    changeEditableStatus(node);
    refreshRowTemplate(node);
  }

  @Command
  public void changeEditableStatus(@BindingParam("node") CategoryTreeNode node) {
    node.setEditingStatus(!node.getEditingStatus())
    refreshRowTemplate(node);
  }

  public void refreshRowTemplate(CategoryTreeNode node) {
    BindUtils.postNotifyChange(null, null, node, "editingStatus");
  }

  @Command
  public void updateSelectedItem(@ContextParam(ContextType.TRIGGER_EVENT) Event event) {
    Tree tree = event.getTarget() as Tree
    selectedItem = tree.getSelectedItem()
    categoryID = ((CategoryTreeNode) selectedItem.getValue()).data.id
  }

  @Command
  public void addCategory() {

    EditCallback callback = new EditCallback() {

      @Override
      void refreshModel() {



        /*if (selectedItem != null) {
          CategoryTreeNode value = selectedItem.getValue() as CategoryTreeNode
          int index = value.getChildren().size()
          CategoryTreeNode[] arr = new CategoryTreeNode[1]
          arr[0] = new CategoryTreeNode(category, category.name)
          categoryTreeModel.insert(value, index, index, arr)
          //открывае мсозданные категории.
          //LinkedList<TreeNode<CategoryEntity>> selection = new LinkedList<TreeNode<CategoryEntity>>()
          //selection.add(arr[0])
          //categoryTreeModel.setSelection(selection)
          //categoryTreeModel.setOpen(arr[0], true)
        } else {
          int index = root.getChildren().size()
          CategoryTreeNode[] arr = new CategoryTreeNode[1]
          arr[0] = new CategoryTreeNode(category, category.name)
          categoryTreeModel.insert(root, index, index, arr)

          //LinkedList<TreeNode<CategoryEntity>> selection = new LinkedList<TreeNode<CategoryEntity>>()
          //selection.add(arr[0])
          //categoryTreeModel.setSelection(selection)
          //categoryTreeModel.setOpen(arr[0], true)
        }*/

      }
    }

    Map<Object, Object> params = new HashMap<Object, Object>()
    params.put("parentID", categoryID)

    Window wnd = Executions.createComponents("/zul/admin/windows/categoryWnd.zul", null, params) as Window
    wnd.setPage(ExecutionsCtrl.getCurrentCtrl().getCurrentPage())
    wnd.doModal()
    wnd.setVisible(true)

  }

  @Command
  @NotifyChange(["categoryTreeModel"])
  public void deleteCategory(@ContextParam(ContextType.TRIGGER_EVENT) Event event) {
    CategoryEntity category = CategoryEntity.get(categoryID)

    Messagebox.show("Удалить?", "Удаление категории", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION, new org.zkoss.zk.ui.event.EventListener() {
      public void onEvent(Event evt) throws InterruptedException {
        if (evt.getName().equals("onOK")) {
          CategoryEntity.withTransaction { status ->
            category.delete(flush: true)
          }

          categoryTreeModel.remove(selectedItem.value as CategoryTreeNode)
          clearSelection()
        }
      }
    });

  }

  @Command
  @NotifyChange(["categoryTreeModel"])
  public void addProduct(@BindingParam("product") ProductEntity product) {

    Window wnd = new Window()
    wnd.setWidth("80%")
    wnd.setHeight("500px")
    wnd.setPage(ExecutionsCtrl.getCurrentCtrl().getCurrentPage())
    Div div = new Div()
    div.setStyle("overflow: auto;")
    div.setWidth("100%")
    div.setHeight("500px")
    wnd.appendChild(div)
    Vlayout panel = new Vlayout()
    div.appendChild(panel)

    Map<String, Object> params = new HashMap<String, Object>()
    params.put("category", categoryID)
    if (product != null)
      params.put("product", product.id)
    Executions.createComponents("/zul/admin/windows/productItemWnd.zul", panel, params)
    wnd.doModal()
    wnd.setVisible(true)
  }

  @Command
  public void clearSelection() {
    categoryTreeModel.clearSelection()
    selectedItem = null
    categoryID = null
  }

  @Command
  public void redirectToProductItem(@BindingParam("product") ProductEntity product) {
    Set<CategoryEntity> categories = ProductEntity.get(product.id).getCategory()
    Executions.sendRedirect("/admin/productItem?product=${product.id}&category=${categories.first().id}")
  }

  @Command
  @NotifyChange(["productsModel"])
  public void deleteProduct(@BindingParam("product") ProductEntity item) {
    ProductEntity.withTransaction { status ->

      ProductEntity product = ProductEntity.get(item.id)
      product.delete(flush: true)
      imageService.cleanStore(product)

      productsModel.clear()
      productsModel.addAll(collectAllProducts(CategoryEntity.get(categoryID), Lists.newArrayList()))

    }
  }

}
