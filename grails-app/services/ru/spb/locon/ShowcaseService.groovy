package ru.spb.locon

import com.google.common.base.Function
import com.google.common.collect.Collections2
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.springframework.beans.BeansException
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.zkoss.zk.ui.Desktop
import org.zkoss.zk.ui.Executions
import ru.spb.locon.wrappers.ProductWrapper

/**
 * Сервис для отложенной обработки товаров, для их корректного отображения.
 */
class ShowcaseService implements ApplicationContextAware{

  static scope = "prototype"

  ApplicationContext applicationContext
  IShowcaseComposer composer
  Desktop desktop

  CartService cartService

  void processEntities(List<ProductEntity> data){

    Collection<ProductWrapper> transformed = Collections2.transform(data, new Function<ProductEntity, ProductWrapper>() {
      @Override
      ProductWrapper apply(ProductEntity f) {
        ProductWrapper wrapper = new ProductWrapper(f)
        cartService.initAsCartItem(wrapper)
        return wrapper;
      }
    }) as List<ProductWrapper>
    Executions.activate(desktop)
    composer.complete(transformed)
    Executions.deactivate(desktop)
  }

}
