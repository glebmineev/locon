package ru.spb.locon

import ru.spb.locon.annotation.FieldInfo
import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType

@XmlAccessorType(XmlAccessType.FIELD)
class ProductEntity implements Comparable {

  //static searchable = [except: '']
  String engImagePath
  String imagePath

  @FieldInfo(isFilter=true, isEditable=true)
  String article
  @FieldInfo(isFilter=true, isEditable=true)
  String name
  @FieldInfo(isEditable=true)
  String description
  @FieldInfo(isEditable=true)
  String usage
  @FieldInfo(isEditable=true)
  String volume
  @FieldInfo(isFilter=true, isEditable=true)
  Long price
  @FieldInfo(isFilter=true, isEditable=true)
  Long countToStock

  FilterEntity filter

  ProductPropertyEntity productProperty
  ManufacturerEntity manufacturer

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
      categories joinTable: [name: 'category_product', key: 'product_id']
      filter column: 'product_filter'
      //filters joinTable: [name: 'product_filter', key: 'product_id']
    }

    version false

    //categories cascade: 'all-delete-orphan'
    //listCategoryProduct lazy: false,  cascade: 'all-delete-orphan'
    listOrderProduct  cascade: 'all-delete-orphan'
    //filters cascade: 'all-delete-orphan'
  }

  //static belongsTo = FilterEntity

  static hasMany = [
      categories: CategoryEntity,
      listOrderProduct: OrderProductEntity//,
      //filters: FilterEntity
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
    manufacturer nullable: true
    countToStock nullable: true
    filter nullable: true
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
