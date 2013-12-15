package ru.spb.locon.zulModels.admin.windows

import org.apache.commons.io.FileUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.zkoss.bind.annotation.Command
import org.zkoss.bind.annotation.ContextParam
import org.zkoss.bind.annotation.ContextType
import org.zkoss.bind.annotation.Init
import org.zkoss.zk.ui.Executions
import org.zkoss.zk.ui.event.Event
import org.zkoss.zul.Window
import ru.spb.locon.CategoryEntity
import ru.spb.locon.common.CategoryPathHandler
import ru.spb.locon.common.PathBuilder
import ru.spb.locon.common.STD_FILE_NAMES
import ru.spb.locon.common.STD_IMAGE_SIZES
import ru.spb.locon.zulModels.admin.common.DownloadImageViewModel

@Init(superclass=true)
class CategoryWndViewModel extends DownloadImageViewModel {

  //Логгер
  static Logger log = LoggerFactory.getLogger(CategoryWndViewModel.class)

  String name
  String parent
  Long parentID

  @Override
  void downloadParams() {
    std_name = STD_FILE_NAMES.PRODUCT_NAME.getName()
    std_image_size = STD_IMAGE_SIZES.MIDDLE.getSize()
    targetImage = "targetImage"
  }

  @Override
  void initialize() {
    HashMap<String, Object> arg = Executions.getCurrent().getArg() as HashMap<String, Object>
    parentID = arg.get("parentID") as Long
    if (parentID != null)
      this.parent = CategoryEntity.get(parentID).name
    else
      this.parent = "Корневая категория"
  }

  @Command
  public void addCategory(@ContextParam(ContextType.TRIGGER_EVENT) Event event) {
    CategoryEntity.withTransaction {
      CategoryEntity categoryEntity = new CategoryEntity()
      categoryEntity.setName(name)
      if (parent != null)
        categoryEntity.setParentCategory(CategoryEntity.get(parentID))

      if (categoryEntity.validate()) {
        CategoryEntity saved = categoryEntity.save(flush: true)

        if (uuid != null) {

          String categoryPath = CategoryPathHandler.generatePathAsString(CategoryPathHandler.getCategoryPath(saved))

          File temp = new File(new PathBuilder()
              .appendPath(serverFoldersService.temp)
              .appendString(uuid)
              .build())

          File store = new File(new PathBuilder()
              .appendPath(serverFoldersService.categoriesPics)
              .appendString(categoryPath)
              .build())
          if (!store.exists())
            store.mkdirs()

          FileUtils.copyDirectory(temp, store)
        }

      }

      Executions.sendRedirect("/admin/editor?categoryID=${categoryEntity.id}")

    }

  }

  @Command
  public void closeWnd(@ContextParam(ContextType.TRIGGER_EVENT) Event event) {
    try {
      Window wnd = event.getTarget().getSpaceOwner() as Window
      Window owner = wnd as Window
      owner.detach()
    } catch (Exception ex) {
      log.debug(ex.getMessage())
    }
  }

}
