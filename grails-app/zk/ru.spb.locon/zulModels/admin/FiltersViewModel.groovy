package ru.spb.locon.zulModels.admin

import com.google.common.base.Function
import com.google.common.collect.Lists
import org.slf4j.Logger
import org.slf4j.LoggerFactory
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
import org.zkoss.zul.DefaultTreeModel
import org.zkoss.zul.ListModelList
import org.zkoss.zul.Tree
import org.zkoss.zul.Treeitem
import org.zkoss.zul.Window
import ru.spb.locon.CategoryEntity
import ru.spb.locon.FilterEntity
import ru.spb.locon.wrappers.CategoryTreeNode
import ru.spb.locon.wrappers.FilterWrapper

class FiltersViewModel {

  //Логгер
  static Logger log = LoggerFactory.getLogger(FiltersViewModel.class)

  Long categoryID

  DefaultTreeModel categoryTreeModel
  ListModelList<FilterWrapper> filtersModel
  Treeitem selectedItem

  boolean visibleCreateBtn = false

  @Init
  public void init(){

    categoryID = Executions.getCurrent().getParameter("categoryID") as Long

    List<CategoryEntity> categories = CategoryEntity.findAllWhere(parentCategory: null)
    CategoryTreeNode root = new CategoryTreeNode(null, "ROOT")
    createTreeModel(root, categories)

    categoryTreeModel = new DefaultTreeModel(root)

    filtersModel = new ListModelList<FilterWrapper>()

    if (categoryID != null) {
      List<FilterEntity> filters = new ArrayList<FilterEntity>()
      collectAllFilters(CategoryEntity.get(categoryID), filters)
      List<FilterWrapper> result = Lists.transform(filters, new Function() {
        @Override
        FilterWrapper apply(Object f) {
          FilterWrapper model = new FilterWrapper(f as FilterEntity)
          model.setMemento(model.clone() as FilterWrapper)
          return model
        }
      })
      filtersModel.addAll(result)

      visibleCreateBtn = true

    }

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
  public void changeEditableStatus(@BindingParam("wrapper") FilterWrapper wrapper) {
    wrapper.setEditingStatus(!wrapper.getEditingStatus())
    refreshRowTemplate(wrapper);
  }

  @Command
  public void refreshRowTemplate(FilterWrapper wrapper) {
    BindUtils.postNotifyChange(null, null, wrapper, "editingStatus");
  }

  @Command
  @NotifyChange(["visibleCreateBtn"])
  public void updateSelectedItem(@ContextParam(ContextType.TRIGGER_EVENT) Event event){
    Tree tree = event.getTarget() as Tree
    selectedItem = tree.getSelectedItem()
    categoryID = selectedItem.getValue().getData().id

    visibleCreateBtn = true

  }

  @Command
  @NotifyChange(["filtersModel"])
  public void refreshModels() {
    try {

      List<FilterEntity> filters = new ArrayList<FilterEntity>()
      collectAllFilters(CategoryEntity.get(categoryID), filters)
      List<FilterWrapper> result = Lists.transform(filters, new Function() {
        @Override
        FilterWrapper apply(Object f) {
          FilterWrapper model = new FilterWrapper(f as FilterEntity)
          model.setMemento(model.clone() as FilterWrapper)
          return model
        }
      })

      filtersModel.clear()
      filtersModel.addAll(result)

    } catch (Exception ex) {
      log.debug("Ошибка обновления модели фильтров: ${ex.getMessage()}")
    }

  }

  void collectAllFilters(CategoryEntity category, List<FilterEntity> filters) {
    List<CategoryEntity> categories = category.listCategory as List<CategoryEntity>
    if (categories != null && categories.size() > 0)
      categories.each { CategoryEntity it ->
        if (it.listCategory != null && it.listCategory.size() > 0)
          collectAllFilters(CategoryEntity.get(it.id), filters)
        else
          filters.addAll(it.filters as List<FilterEntity>)
      }
    else
      filters.addAll(category.filters as List<FilterEntity>)
  }

  @Command
  public void createNew(){
    Map<Object, Object> params = new HashMap<Object, Object>()
    params.put("categoryID", categoryID)
    Window wnd = Executions.createComponents("/zul/admin/windows/createFilterWnd.zul", null, params) as Window
    wnd.setPage(ExecutionsCtrl.getCurrentCtrl().getCurrentPage())
    wnd.doModal()
    wnd.setVisible(true)
  }

  @Command
  public void updateFilter(@BindingParam("wrapper") FilterWrapper wrapper) {
    FilterEntity.withTransaction {
      FilterEntity toUpdate = FilterEntity.get(wrapper.id)
      toUpdate.setName(wrapper.name)
      if (toUpdate.validate())
        toUpdate.save(flush: true)
    }
    changeEditableStatus(wrapper)
  }

  @Command
  @NotifyChange(["filtersModel"])
  public void deleteFilter(@BindingParam("wrapper") FilterWrapper wrapper) {

    FilterEntity.withTransaction {
      FilterEntity toDelete = FilterEntity.get(wrapper.id)
      toDelete.delete(flush: true)
    }

    try {
      CategoryTreeNode node = selectedItem.getValue() as CategoryTreeNode
      CategoryEntity category = node.getData()

      List<FilterEntity> filters = new ArrayList<FilterEntity>()
      collectAllFilters(category, filters)
      List<FilterWrapper> result = Lists.transform(filters, new Function() {
        @Override
        FilterWrapper apply(Object f) {
          FilterWrapper model = new FilterWrapper(f as FilterEntity)
          model.setMemento(model.clone() as FilterWrapper)
          return model
        }
      })

      filtersModel.clear()
      filtersModel.addAll(result)

    } catch (Exception ex) {
      log.debug("Ошибка обновления модели фильтров: ${ex.getMessage()}")
    }

    Executions.sendRedirect("/admin/filters?categoryID=${categoryID}")

  }

  @Command
  public void cancelEditing(@BindingParam("wrapper") FilterWrapper wrapper) {
    wrapper.restore()
    changeEditableStatus(wrapper)
  }

}
