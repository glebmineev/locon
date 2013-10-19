package ru.spb.locon

import ru.spb.locon.annotation.FieldInfo
import ru.spb.locon.zulModels.admin.filters.data.FilterTypes

import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType

@XmlAccessorType(XmlAccessType.FIELD)
class ProductEntity implements Comparable {

  //static searchable = [except: '']
  String engImagePath
  String imagePath

  @FieldInfo(isFilter = true, type = FilterTypes.TEXT_FIELD)
  String article
  @FieldInfo(isFilter = true, type = FilterTypes.TEXT_FIELD)
  String name
  String description
  String usage
  String volume
  @FieldInfo(isFilter = true, type = FilterTypes.MEASURE_FIELD)
  Long price
  @FieldInfo(isFilter = true, type = FilterTypes.NUMBER_FIELD)
  Long countToStock

  FilterEntity filter

  ProductPropertyEntity productProperty
  @FieldInfo(isFilter = true, type = FilterTypes.COMBO_FIELD)
  ManufacturerEntity manufacturer

  CategoryEntity category

  /*
   * Рейтинг товара (сколько продано).
   */
  Long rate

  static mapping = {

    table: 'product'
    columns {
      id column: 'product_id'
      imagePath column: 'product_imagepath', type: 'text'
      engImagePath column: 'product_engimagepath', type: 'text'
      article column: 'product_article'
      name column: 'product_name'
      description column: 'product_description'
      usage column: 'product_usage'
      volume column: 'product_volume'
      productProperty column: 'product_productproperty_id'
      price column: 'product_price'
      manufacturer fetch: "join", column: 'product_manufacturer_id'
      countToStock column: 'product_counttostock'
      //categories joinTable: [name: 'category_product', key: 'product_id']
      category column: 'product_categor_id'
      filter fetch: "join", column: 'product_filter'
      rate column: 'product_rate'
    }

    version false

    //categories cascade: 'all-delete-orphan'
    //listCategoryProduct lazy: false,  cascade: 'all-delete-orphan'
    listOrderProduct cascade: 'all-delete-orphan'
    reviews cascade: 'all-delete-orphan'
    //filters cascade: 'all-delete-orphan'
  }

  static hasMany = [
      listOrderProduct: OrderProductEntity,
      reviews: ReviewsEntity
  ]

  static constraints = {
    imagePath maxSize: 65535, nullable: true
    engImagePath maxSize: 65535, nullable: true
    article nullable: true
    name maxSize: 65535, nullable: true
    description maxSize: 65535, nullable: true
    usage maxSize: 65535, nullable: true
    volume nullable: true
    price nullable: true
    productProperty nullable: true
    manufacturer nullable: false
    countToStock nullable: true
    category nullable: true
    filter nullable: true
    rate nullable: true
  }

  public String toString() {
    return name
  }

  @Override
  int compareTo(Object o) {
    ProductEntity item = (ProductEntity) o
    if (item.name > this.name)
      return 1
    if (item.name < this.name)
      return -1
    if (item.name.equals(this.name))
      return 0

  }

}
