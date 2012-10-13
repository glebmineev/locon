package ru.spb.locon.importer

import jxl.read.biff.BiffException
import jxl.Workbook
import jxl.Sheet
import jxl.Cell
import ru.spb.locon.CategoryEntity
import ru.spb.locon.ManufacturerEntity
import ru.spb.locon.ProductEntity
import ru.spb.locon.ProductFilterEntity
import ru.spb.locon.CategoryProductEntity
import importer.ConverterRU_EN
import importer.DirUtils
import importer.ConvertUtils
import java.math.RoundingMode


class ImporterExcel {

  private static Workbook wrkbook = null
  private ManufacturerEntity manufacturer
  private CategoryEntity menuCategory

  ImporterExcel() { }

  ImporterExcel(Workbook wrkbook, String category, String manufacturer) {
    initEntities(category, manufacturer)
    try {
      this.wrkbook = wrkbook
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
        CategoryEntity submenuCategory = getCategory(sheet.getName())
        submenuCategory.setParentCategory(menuCategory)

        //перебираем строки страницы.
        CategoryEntity temp = null
        (0..sheet.getRows() - 1).each { i ->
          Cell[] row = sheet.getRow(i)
          if (row.length != 0) {
            if (row != null && row.length > 1 && !row[1].getContents().isEmpty()) {
              //Создаем продукт.
              ProductEntity product = createProduct(row)
              //связываем со всеми категорими в которые он входит для дальнейшей фильтрации.
              linkToCategories(submenuCategory, product)
              linkToCategories(temp, product)
              linkToCategories(menuCategory, product)
              product.setManufacturer(manufacturer)
              product.save()
            }
            else if (row[0] != null && !row[0].getContents().isEmpty()) {
              String cName = row[0].getContents()
              temp = getCategory(ConvertUtils.formatString(cName))
              temp.setParentCategory(submenuCategory)
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
    (0..row.length - 1).each { i ->
      String value = row[i].getContents()
      if (!value.isEmpty()) {
        if (i == 0)
          product.setArticle(value)
        if (i == 1)
          product.setName(value)
        if (i == 2)
          product.setProductFilter(getProductFilter(value))
        if (i == 3) {
          if (value != null && !value.isEmpty())
            product.setVolume(value)
        }

        if (i == 4) {
          float price = Float.parseFloat(value.replace(",", "."))
          float result = new BigDecimal(price).setScale(2, RoundingMode.UP).floatValue()
          product.setPrice(result)
        }
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
    CategoryProductEntity categoryProduct = new CategoryProductEntity(product: product, category: category)
    categoryProduct.save()
  }

  public createImageDirs(String applicationPath) {
    ProductEntity.withTransaction {
      ProductEntity.list().each {ProductEntity product ->
        List<CategoryEntity> categoryList = product.listCategoryProduct.category as List<CategoryEntity>
        categoryList.each {CategoryEntity category ->
          if (category != null &&
              (category.listCategory == null || category.listCategory.size() == 0)) {
            String dir = createImageDir(category, "")
            DirUtils.createDir(
                ConverterRU_EN.translit(applicationPath + "images\\catalog\\" + dir + "\\" + product.article + " " + product.name))
            product.setImagePath(ConverterRU_EN.translit(dir))
            product.save()
          }
        }
      }
    }
  }

  private String createImageDir(CategoryEntity category, String childrenPath) {
    String parentPath = "\\" + category.name

    CategoryEntity parent = category.parentCategory
    if (parent != null &&
        !parent.name.equals(menuCategory.name)) {
      createImageDir(parent, parentPath + childrenPath)
    }
    else
      return menuCategory.name + "\\" + parentPath + "\\" + manufacturer.name + childrenPath
  }

}
