package ru.spb.locon.zulModels.admin

import org.apache.commons.io.FileUtils
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.zkoss.bind.annotation.Command
import org.zkoss.bind.annotation.ContextParam
import org.zkoss.bind.annotation.ContextType
import org.zkoss.bind.annotation.Init
import org.zkoss.bind.annotation.NotifyChange
import org.zkoss.image.AImage
import org.zkoss.zk.ui.Executions
import org.zkoss.zk.ui.Page
import org.zkoss.zk.ui.event.Event
import org.zkoss.zk.ui.sys.ExecutionsCtrl
import org.zkoss.zul.Image
import org.zkoss.zul.Messagebox
import org.zkoss.zul.Tree
import org.zkoss.zul.Treeitem
import org.zkoss.zul.Window
import ru.spb.locon.CategoryEntity
import ru.spb.locon.InitService
import ru.spb.locon.common.CategoryPathHandler
import ru.spb.locon.common.PathBuilder
import ru.spb.locon.common.STD_FILE_NAMES
import ru.spb.locon.common.STD_IMAGE_SIZES
import ru.spb.locon.wrappers.CategoryTreeNode
import ru.spb.locon.zulModels.admin.models.AdvancedTreeModel
import ru.spb.locon.zulModels.admin.common.DownloadImageViewModel

@Init(superclass=true)
class CategoriesViewModel extends DownloadImageViewModel {

  AdvancedTreeModel categoryTreeModel

  Treeitem selectedItem
  CategoryTreeNode root
  Long categoryID

  String name
  String description

  InitService initService = ApplicationHolder.getApplication().getMainContext().getBean("initService") as InitService

  @Override
  void downloadParams() {
    std_name = STD_FILE_NAMES.CATEGORY_NAME.getName()
    categoryID = Executions.getCurrent().getParameter("categoryID") as Long
    categoryTreeModel = new AdvancedTreeModel(getRootNode())
  }

  @Override
  void initialize() {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  @Command
  void refreshData(@ContextParam(ContextType.TRIGGER_EVENT) Event event) {
    CategoryEntity retrived = CategoryEntity.get(categoryID)

    Page page = event.getTarget().getPage();
    Image targetImage = page.getFellow(targetImage) as Image

    String categoryPath = CategoryPathHandler.generatePathAsString(
        CategoryPathHandler.getCategoryPath(CategoryEntity.get(categoryID)))

    String path = new PathBuilder()
        .appendPath(serverFoldersService.categoriesPics)
        .appendString(categoryPath)
        .build()
    String std_name = STD_FILE_NAMES.CATEGORY_NAME.getName()
    int std_size = STD_IMAGE_SIZES.SMALL.getSize()

    AImage img = imageService.getImageFile(path, std_name, std_size)

    targetImage.setContent(img)

    name = retrived.getName()
    description = retrived.getDescription()
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

  @Command
  public void save() {

    CategoryEntity.withTransaction { status ->
      CategoryEntity toSave = CategoryEntity.get(categoryID)
      if (toSave != null) {
        toSave.setName(name)
        toSave.setDescription(description)
        if (toSave.validate()) {
          toSave.save(flush: true)

          if (uuid != null) {
            File temp = new File(new PathBuilder()
                                  .appendPath(serverFoldersService.temp)
                                  .appendString(uuid).build())

            File store = new File(new PathBuilder()
                .appendPath(serverFoldersService.categoriesPics)
                .appendString(CategoryPathHandler.generatePathAsString(CategoryPathHandler.getCategoryPath(toSave)))
                .checkDir()
                .build())

            FileUtils.copyDirectory(temp, store)
            initService.refreshShop()
          }

        }
      }

    }

  }

  @Command
  @NotifyChange(["name", "description"])
  public void updateSelectedItem(@ContextParam(ContextType.TRIGGER_EVENT) Event event) {
    Tree tree = event.getTarget() as Tree
    selectedItem = tree.getSelectedItem()
    categoryID = ((CategoryTreeNode) selectedItem.getValue()).data.id
    refreshData(event)
  }

  @Command
  public void addCategory() {

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
  public void clearSelection() {
    categoryTreeModel.clearSelection()
    selectedItem = null
    categoryID = null
  }

}
