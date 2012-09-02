package ru.spb.locon.importer

import jxl.read.biff.BiffException
import jxl.Workbook
import jxl.Sheet
import jxl.Cell
import locon.CategoryEntity
import locon.ManufacturerEntity
import locon.ProductEntity
import locon.ProductFilterEntity
import locon.CategoryProductEntity


class ImporterExcel {

  private static Workbook wrkbook = null
  private ManufacturerEntity manufacturer
  private CategoryEntity menuCategory

  ImporterExcel(InputStream is, String category, String manufacturer) {
    initEntities(category, manufacturer)
    try {
      wrkbook = Workbook.getWorkbook(is)
    } catch (IOException e) {
      e.printStackTrace()
    } catch (BiffException e) {
      e.printStackTrace()
    }
  }

  private void initEntities(String categoryName, String manufacturerName) {
    //категория из шапки меню Для волос, Депиляция и т.д.
    menuCategory = getCategory(categoryName)
    manufacturer = getManufacturer(manufacturerName)
  }

  public void doImport() {
    CategoryEntity.withTransaction {

      Sheet[] sheets = wrkbook.getSheets()
      sheets.each {Sheet sheet ->

        //получаем корневую категорию.
        CategoryEntity rootCategory = getCategory(sheet.getName())
        rootCategory.setParentCategory(menuCategory)

        int rows = sheet.getRows()
        //перебираем строки страницы.
        CategoryEntity temp = null
        for (int i = 0; i < rows; i++) {
          Cell[] row = sheet.getRow(i)
          if (row.length != 0) {
            if (row != null && row.length > 1 && !row[1].getContents().isEmpty()) {
              //Создаем продукт.
              ProductEntity product = createProduct(row)
              //связываем со всеми категорими в которые он входит для дальнейшей фильтрации.
              linkToCategories(rootCategory, product)
              linkToCategories(temp, product)
              linkToCategories(menuCategory, product)

              product.setManufacturer(manufacturer)
              product.save()
            }
            else if (row[0] != null) {
              String cName = row[0].getContents()
              temp = getCategory(cName)
              temp.setParentCategory(rootCategory)
            }

          }
        }
      }
    }

  }

  private CategoryEntity getCategory(String name) {
    CategoryEntity category = CategoryEntity.findByName(name)
    if (category == null) {
      category = new CategoryEntity()
      category.setName(name)
      category.save(insert: true)
    }
    return category
  }

  private ManufacturerEntity getManufacturer(String name) {
    ManufacturerEntity manufacturer = ManufacturerEntity.findByName(name)
    if (manufacturer == null) {
      manufacturer = new ManufacturerEntity()
      manufacturer.setName(name)
      manufacturer.save(insert: true)
    }
    return manufacturer
  }

  private ProductEntity createProduct(Cell[] row) {

    ProductEntity product = new ProductEntity()
    for (int i = 0; i < row.length; i++) {
      String value = row[i].getContents()
      if (!value.isEmpty()) {
        if (i == 0)
          product.setArticle(value)
        if (i == 1)
          product.setName(value)
        if (i == 2)
          product.setProductFilter(getProductFilter(value))
        if (i == 3)
          product.setVolume(value)
        if (i == 4)
          product.setPrice(Float.parseFloat(value.replace(",", ".")))
      }
    }

    return product
  }

  private ProductFilterEntity getProductFilter(String filterName) {
    ProductFilterEntity filterEntity = ProductFilterEntity.findByName(filterName)
    if (filterEntity == null) {
      filterEntity = new ProductFilterEntity()
      filterEntity.setName(filterName)
      filterEntity.save()
    }
    return filterEntity
  }

  private void linkToCategories(CategoryEntity category, ProductEntity product) {
    CategoryProductEntity categoryProduct_1 = new CategoryProductEntity(product: product, category: category)
    categoryProduct_1.save()
  }
}
