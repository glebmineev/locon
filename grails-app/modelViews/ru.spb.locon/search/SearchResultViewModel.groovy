package ru.spb.locon.search

import org.zkoss.bind.annotation.BindingParam
import org.zkoss.bind.annotation.Command
import org.zkoss.bind.annotation.Init
import org.zkoss.zk.ui.Executions
import org.zkoss.zul.ListModelList
import ru.spb.locon.ProductEntity

/**
 * Created with IntelliJ IDEA.
 * User: gleb
 * Date: 4/28/13
 * Time: 12:41 AM
 * To change this template use File | Settings | File Templates.
 */
class SearchResultViewModel {

  ListModelList<ProductEntity> model

  @Init
  public void init(){
    String keyword = Executions.getCurrent().getParameter("keyword")

    List<ProductEntity> list = ProductEntity.createCriteria().list {
      ilike("name", "%${keyword}%")
      ilike("description", "%${keyword}%")
      //sqlRestriction("product_name like '%${keyword}%' or product_description like '%${keyword}%' or product_usage like '%${keyword}%'")
    }

    model = new ListModelList<ProductEntity>(list);

  }

  @Command
  public void redirectToProductItem(@BindingParam("product") ProductEntity product){
    Executions.sendRedirect("/shop/product?product=${product.id}")
  }

}
