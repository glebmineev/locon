package ru.spb.locon.zulModels.shop

import org.zkoss.bind.annotation.Command
import org.zkoss.bind.annotation.Init
import org.zkoss.bind.annotation.NotifyChange
import org.zkoss.zul.ListModelList
import ru.spb.locon.ManufacturerEntity
import ru.spb.locon.wrappers.ManufacturerWrapper

/**
 * Created with IntelliJ IDEA.
 * User: gleb
 * Date: 5/13/13
 * Time: 9:13 PM
 * To change this template use File | Settings | File Templates.
 */
class CarouselViewModel {

  List<ManufacturerWrapper> manufacturers = new ArrayList<ManufacturerWrapper>()
  List<ManufacturerEntity> entities;
  boolean endList = false
  int currentPos = 0
  int visibleElements = 6

  @Init
  public void init(){
    entities = ManufacturerEntity.list(sort: "name")
    moveCarousel()
  }

  @Command
  @NotifyChange(["manufacturers", "endList", "currentPos"])
  public void next(){
    currentPos++
    if ((currentPos + visibleElements) == entities.size())
      endList = true

    moveCarousel()
  }

  @Command
  @NotifyChange(["manufacturers", "currentPos", "endList"])
  public void back(){
    currentPos--
    endList = false
    moveCarousel()
  }

  void moveCarousel(){
    manufacturers.clear()
    entities.subList(currentPos, visibleElements + currentPos).each {it ->
      manufacturers.add(new  ManufacturerWrapper(it))
    }
  }

}
