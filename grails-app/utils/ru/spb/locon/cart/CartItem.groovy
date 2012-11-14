package ru.spb.locon.cart

import ru.spb.locon.ProductEntity

/**
 * User: Gleb
 * Date: 06.10.12
 * Time: 16:53
 */
class CartItem implements Comparable{
  
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


  @Override
  int compareTo(Object o) {
    CartItem item = (CartItem) o
    if (item.product.name > this.product.name)
      return 1
    if (item.product.name < this.product.name)
      return -1
    if (item.product.name.equals(this.product.name))
      return 0

  }
}
