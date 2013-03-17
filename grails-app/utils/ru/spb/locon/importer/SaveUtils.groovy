package ru.spb.locon.importer

import ru.spb.locon.CategoryEntity
import ru.spb.locon.ProductEntity
import ru.spb.locon.ManufacturerEntity

import ru.spb.locon.FilterEntity
import ru.spb.locon.FilterGroupEntity
import ru.spb.locon.excel.ImportException

/**
 * User: Gleb
 * Date: 09.11.12
 * Time: 16:19
 */
class SaveUtils {

  FilterGroupEntity manufacturerGroup
  FilterGroupEntity usageGroup

  SaveUtils() {
    manufacturerGroup = FilterGroupEntity.findByName("Производитель")
    usageGroup = FilterGroupEntity.findByName("Применение")
  }

  CategoryEntity getCategory(String name, CategoryEntity parent, Set<FilterEntity> filters) {
    CategoryEntity category = CategoryEntity.findByName(name)
    if (category == null) {
      category = CategoryEntity.newInstance()
      //получаем корневую категорию из названия листа excel файла.
      category.setName(name)
      category.setParentCategory(parent)
    }

/*    if (category.filters != null) {
      filters.each { FilterEntity filter ->
        if (!category.filters.name.contains(filter.name))
          category.addToFilters(FilterEntity.get(filter.id))
      }
    } else {
      filters.each { FilterEntity filter ->
        category.addToFilters(FilterEntity.get(filter.id))
      }
    }*/

    if (category.validate()) {
      category.save(flush: true)
    } else
      throw new ImportException("Ошибка при сохранении категоии ${category.name}.")


    return category

  }

  FilterEntity getFilter(String name, FilterGroupEntity group) {
    FilterEntity filter = FilterEntity.findByName(name)
    if (filter == null) {
      //получаем корневую категорию из названия листа excel файла.
      filter = new FilterEntity(
          name: name,
          filterGroup: group
      )

      if (filter.validate()) {
        filter.save(flush: true)
      } else
        throw new ImportException("Ошибка при сохранении категоии ${filter.name}.")
    }

    return filter

  }

  CategoryEntity saveCategory(String name) {
    CategoryEntity category = CategoryEntity.findByName(name)
    if (category == null) {
      category = new CategoryEntity(name: name)
      category.save(flush: true)
    }
    return category
  }

  ManufacturerEntity getManufacturer(String name) {
    ManufacturerEntity manufacturer = ManufacturerEntity.findByName(name)
    if (manufacturer == null) {
      manufacturer = new ManufacturerEntity(name: name)
      manufacturer.save(flush: true)
    }
    return manufacturer
  }


}
