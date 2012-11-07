package ru.spb.locon.renderers

import org.zkoss.zul.ListitemRenderer
import ru.spb.locon.ProductFilterEntity
import org.zkoss.zul.Checkbox
import org.zkoss.zul.Listcell
import org.zkoss.zk.ui.event.EventListener
import org.zkoss.zk.ui.event.Event
import ru.spb.locon.ProductEntity
import org.zkoss.zk.ui.event.Events
import org.zkoss.zk.ui.event.CheckEvent
import org.zkoss.zkplus.databind.BindingListModelList
import ru.spb.locon.CategoryEntity

/**
 * User: Gleb
 * Date: 20.10.12
 * Time: 18:24
 */
class FilterRenderer implements ListitemRenderer<ProductFilterEntity> {

  List<ProductFilterEntity> checked = new ArrayList<ProductFilterEntity>()
  //BindingListModelList<ProductEntity> productsModel
  CategoryEntity category

  @Override
  void render(org.zkoss.zul.Listitem listitem, ProductFilterEntity t, int i) {
    listitem.setValue(t)

    Checkbox checkbox = new Checkbox()
    checkbox.setId("checkbox_${t.name}")
    checkbox.setDisabled(true)
    //checkbox.setAttribute("entity", t)
    //checkbox.addEventListener(Events.ON_CHECK, selectListener)
    Listcell checkboxCell = new Listcell()
    checkboxCell.appendChild(checkbox)
    checkboxCell.setParent(listitem)
    
    Listcell labelCell = new Listcell(t.name)
    labelCell.setParent(listitem)

  }

/*  EventListener selectListener = new EventListener() {
    @Override
    void onEvent(Event t) {
      CheckEvent event = (CheckEvent) t
      Checkbox checkbox = (Checkbox) t.target
      ProductFilterEntity entity = (ProductFilterEntity) checkbox.getAttribute("entity")
      if (event.isChecked()){
        checked.add(entity)
      }
      else
        checked.remove(entity)

      productsModel.clear()

      Collection<ProductEntity> retrieved = category.listCategoryProduct.product
      if (checked.size() > 0 ){
        retrieved.each {ProductEntity product ->
          if (checked.contains(product.productFilter))
            productsModel.add(product)
        }
      }
      else
        productsModel.addAll(retrieved)

    }
  }

  void setCategory(CategoryEntity category) {
    this.category = category
  }

  void setProductsModel(BindingListModelList<ProductEntity> productsModel) {
    this.productsModel = productsModel
  }*/


}
