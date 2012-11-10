package ru.spb.locon.importer

import ru.spb.locon.ProductFilterEntity
import ru.spb.locon.CategoryEntity
import ru.spb.locon.ProductEntity
import ru.spb.locon.CategoryProductEntity
import ru.spb.locon.ManufacturerEntity
import ru.spb.locon.ProductFilterCategoryEntity

/**
 * User: Gleb
 * Date: 09.11.12
 * Time: 16:19
 */
class SaveUtils {

  ProductFilterEntity getProductFilter(String filterName) {
    ProductFilterEntity filterEntity = ProductFilterEntity.findByName(filterName)
    if (filterEntity == null) {
      filterEntity = new ProductFilterEntity()
      filterEntity.setName(filterName)
      filterEntity.save()
    }
    return filterEntity
  }

  void linkToCategories(CategoryEntity category, ProductEntity product) {
    CategoryProductEntity categoryProduct = new CategoryProductEntity(product: product, category: category)
    categoryProduct.save()
  }

  CategoryEntity getCategory(String name) {
    CategoryEntity category = CategoryEntity.findByName(name)
    if (category == null) {
      category = new CategoryEntity()
      category.setName(name)
      category.save(insert: true)
    }
    return category
  }

  ManufacturerEntity getManufacturer(String name) {
    ManufacturerEntity manufacturer = ManufacturerEntity.findByName(name)
    if (manufacturer == null) {
      manufacturer = new ManufacturerEntity()
      manufacturer.setName(name)
      manufacturer.save(insert: true)
    }
    return manufacturer
  }

  void saveFilterCategoryLink(CategoryEntity category, ProductFilterEntity productFilter) {
    ProductFilterCategoryEntity exist =
      ProductFilterCategoryEntity.findWhere(category: category, productFilter: productFilter)
    if (exist == null) {
      ProductFilterCategoryEntity productFilterCategory = new ProductFilterCategoryEntity()
      productFilterCategory.setCategory(category)
      productFilterCategory.setProductFilter(productFilter)
      productFilterCategory.save()
    }

  }

}
