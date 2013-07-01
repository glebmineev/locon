package ru.spb.locon.zulModels.shop

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.zkoss.bind.annotation.BindingParam
import org.zkoss.bind.annotation.Command
import org.zkoss.bind.annotation.Init
import org.zkoss.zk.ui.Executions
import org.zkoss.zk.ui.event.Events
import org.zkoss.zul.Image
import ru.spb.locon.CategoryEntity
import org.zkoss.zk.ui.event.*
import ru.spb.locon.common.PathHandler;
import ru.spb.locon.wrappers.CategoryWrapper
import ru.spb.locon.wrappers.HrefWrapper

class CatalogNewViewModel {

  static Logger log = LoggerFactory.getLogger(CatalogNewViewModel.class)

  List<CategoryWrapper> categories = new ArrayList<CategoryWrapper>()
  //List<CategoryEntity> cache = new ArrayList<CategoryEntity>(CategoryEntity.findAllWhere(parentCategory: null))
  //Навигация.
  List<HrefWrapper> links = new LinkedList<HrefWrapper>()

  @Init
  public void init(){

    Long categoryID = Executions.getCurrent().getParameter("category") as Long

    categories.clear()
    if (categoryID == null) {
      CategoryEntity.findAllWhere(parentCategory: null).each { it ->
        fillModel(it)
      }
    } else {
      CategoryEntity category = CategoryEntity.get(categoryID)
      category.listCategory.each {it ->
        fillModel(it)
      }
    }
    rebuildPath(categoryID)

  }

  public void fillModel(CategoryEntity entity){
    CategoryWrapper wrapper = new CategoryWrapper(entity)
    fillChildren(wrapper, entity.listCategory)
    categories.add(wrapper)
  }

  public void fillChildren(CategoryWrapper wrapper, Set<CategoryEntity> list) {
    list.each {it ->
      CategoryWrapper child = new CategoryWrapper(it)
      wrapper.addChild(child)
    }
  }

  void rebuildPath(Long categoryID){
    List<CategoryEntity> categories = PathHandler.getCategoryPath(CategoryEntity.get(categoryID))
    links.clear()
    links.add(new HrefWrapper("Главная", "/shop"))
    categories.each {it ->
      links.add(new HrefWrapper(it.name, "/shop/catalog/category?category=${it.id}"))
    }
  }

  @Command
  public void appendElse(){

  }

}
