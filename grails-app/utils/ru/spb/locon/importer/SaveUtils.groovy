package ru.spb.locon.importer

import ru.spb.locon.CategoryEntity
import ru.spb.locon.ProductEntity
import ru.spb.locon.ManufacturerEntity

import ru.spb.locon.FilterEntity
import ru.spb.locon.FilterGroupEntity

/**
 * User: Gleb
 * Date: 09.11.12
 * Time: 16:19
 */
class SaveUtils {

  FilterEntity getProductFilter(String filterName, FilterGroupEntity group) {
    FilterEntity filterEntity = FilterEntity.findByName(filterName)
    if (filterEntity == null) {
      filterEntity = new FilterEntity(
        name: filterName,
        filterGroup: group
      )
      filterEntity.save()
    }
    return filterEntity
  }

  void linkToCategories(CategoryEntity category, ProductEntity product) {
    category.addToProducts(product)
    category.save(flush: true)
  }

  CategoryEntity saveCategory(String name) {
    CategoryEntity category = CategoryEntity.findByName(name)
    if (category == null) {
      category = new CategoryEntity(name: name)
      category.save()
    }
    return category
  }

  ManufacturerEntity getManufacturer(String name) {
    ManufacturerEntity manufacturer = ManufacturerEntity.findByName(name)
    if (manufacturer == null) {
      manufacturer = new ManufacturerEntity(name: name)
      manufacturer.save()
    }
    return manufacturer
  }

  void saveFilterCategoryLink(CategoryEntity category, FilterEntity productFilter) {
    Set<FilterEntity> filters = category.filters
    //if (filters == null)
    //  filters = new HashSet<FilterEntity>()
    //category.filters.clear()
    category.addToFilters(productFilter)
    //category.filters.add
    //filters.add(productFilter)
    category.save()
  }

  void saveFilterToProductLink(ProductEntity product, FilterEntity productFilter) {
    product.addToFilters(productFilter)
    product.save()
  }

  FilterGroupEntity getProductFilterGroup(String name){
    FilterGroupEntity group = FilterGroupEntity.findByName(name)
    if (group == null){
      group = new FilterGroupEntity(name: name)
      group.save()
    }
    return group
  }

}
