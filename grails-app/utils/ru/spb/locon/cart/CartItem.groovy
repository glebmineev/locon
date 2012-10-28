package ru.spb.locon.cart

import ru.spb.locon.ProductEntity

/**
 * User: Gleb
 * Date: 06.10.12
 * Time: 16:53
 */
class CartItem {
  
  private ProductEntity product
  private Long count = 0

  CartItem(ProductEntity product, Long count) {
    this.product = product
    this.count = count
  }

  public ProductEntity getProduct() {
    return product
  }

  public Long getCount() {
    return count
  }

  void setProduct(ProductEntity product) {
    this.product = product
  }

  void setCount(Long count) {
    this.count = count
  }


}
