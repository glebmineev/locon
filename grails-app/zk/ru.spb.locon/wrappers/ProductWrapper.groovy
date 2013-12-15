package ru.spb.locon.wrappers

import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.zkoss.image.AImage
import org.zkoss.zul.ListModelList
import ru.spb.locon.FilterEntity
import ru.spb.locon.ImageService
import ru.spb.locon.ManufacturerEntity
import ru.spb.locon.ProductEntity
import ru.spb.locon.ServerFoldersService
import ru.spb.locon.common.PathBuilder
import ru.spb.locon.common.STD_FILE_NAMES
import ru.spb.locon.common.STD_IMAGE_SIZES

/**
 * Created with IntelliJ IDEA.
 * User: gleb
 * Date: 4/27/13
 * Time: 10:39 PM
 * To change this template use File | Settings | File Templates.
 */
class ProductWrapper extends IdentWrapper implements Wrapper {

  String article
  Long price
  String usage
  String description
  String volume
  Long countToStock
  ManufacturerEntity manufacturer
  FilterEntity filter

  ProductWrapper memento

  ListModelList<ManufacturerEntity> manufacturers

  boolean editingStatus = false

  Long totalPrice;
  Long count;

  AImage image

  ImageService imageService = ApplicationHolder.getApplication().getMainContext().getBean("imageService");

  ServerFoldersService serverFoldersService = ApplicationHolder.getApplication().getMainContext().getBean("serverFoldersService") as ServerFoldersService

  boolean inCart = false

  public ProductWrapper(ProductEntity productEntity) {

    this.id = productEntity.id
    this.name = productEntity.name
    this.article = productEntity.article
    this.price = productEntity.price
    this.volume = productEntity.volume
    this.usage = productEntity.usage
    this.description = productEntity.description
    this.countToStock = productEntity.countToStock
    this.manufacturer = productEntity.manufacturer
    this.filter = productEntity.filter

    String path = new PathBuilder()
        .appendPath(serverFoldersService.productImages)
        .appendString(productEntity.engImagePath)
        .build()
    String std_name = STD_FILE_NAMES.PRODUCT_NAME.getName()
    int std_size = STD_IMAGE_SIZES.SMALL.getSize()

    this.image = imageService.getImageFile(path, std_name, std_size)

    manufacturers = new ListModelList<ManufacturerEntity>(ManufacturerEntity.list(sort: "name"))
    manufacturers.addToSelection(manufacturer)

  }

  public void restore(){
    this.id = memento.id
    this.name = memento.name
    this.article = memento.article
    this.price = memento.price
    this.countToStock = memento.countToStock
    this.manufacturer = memento.manufacturer

    manufacturers = new ListModelList<ManufacturerEntity>()
    manufacturers.addAll(ManufacturerEntity.list(sort: "name"))
    manufacturers.addToSelection(memento.manufacturer)
  }

}
