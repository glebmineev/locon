package ru.spb.locon

import org.zkoss.zk.grails.composer.*

import org.zkoss.zk.ui.select.annotation.Wire
import org.zkoss.zk.ui.select.annotation.Listen
import org.zkoss.zul.Window
import org.zkoss.zul.Label
import locon.ProductEntity
import org.zkoss.zk.ui.sys.ExecutionsCtrl
import org.zkoss.zk.ui.Executions
import org.zkoss.zul.Image

class ProductComposer extends GrailsComposer {

  Label name
  Image productImg

  def afterCompose = {Window window ->

    Long productId = Long.parseLong(execution.getParameter("product"))
    ProductEntity product = ProductEntity.get(productId)
    name.setValue(product.name)

  }
}
