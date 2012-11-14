package ru.spb.locon.search

import org.zkoss.zk.grails.composer.GrailsComposer
import org.zkoss.zul.Window
import org.zkoss.zul.Listbox
import org.zkoss.zul.ListModelList
import ru.spb.locon.ProductEntity
import org.zkoss.zk.ui.event.Event
import org.zkoss.zul.Listitem
import org.zkoss.zk.ui.Executions
import ru.spb.locon.domain.DomainUtils

/**
 * User: Gleb
 * Date: 14.11.12
 * Time: 13:12
 */
class ResultsComposer extends GrailsComposer{

  Listbox searchResult
  
  def afterCompose = {Window window ->

    String keyword = execution.getParameter("keyword")
    List<ProductEntity> list = ProductEntity.createCriteria().list {
      ilike("name", "%${keyword}%")
    }
    searchResult.setModel(new ListModelList<ProductEntity>(list))
    searchResult.setItemRenderer(new SearchResultRenderer(keyword))
  }

  public void onClick_searchResult(Event event){
    int index = searchResult.getSelectedIndex()
    Listitem listitem = searchResult.getItemAtIndex(index)
    if (listitem != null) {
      Executions.sendRedirect("/shop/product?product=${listitem.value.id}")
    }
  }

}
