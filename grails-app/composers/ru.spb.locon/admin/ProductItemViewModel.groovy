package ru.spb.locon.admin

import org.zkoss.bind.annotation.Command
import org.zkoss.bind.annotation.ContextParam
import org.zkoss.bind.annotation.ContextType
import org.zkoss.bind.annotation.Init
import org.zkoss.zk.ui.Page
import org.zkoss.zk.ui.event.Event
import org.zkoss.zul.ListModelList
import org.zkoss.zul.Window
import ru.spb.locon.CategoryEntity

/**
 * Created with IntelliJ IDEA.
 * User: gleb
 * Date: 3/18/13
 * Time: 12:25 AM
 * To change this template use File | Settings | File Templates.
 */
class ProductItemViewModel {

  ListModelList<CategoryEntity> categoryModel;

  @Init
  public void init(){
    categoryModel = new ListModelList<CategoryEntity>(CategoryEntity.list())
  }

  @Command
  public void saveItem() {

  }

  @Command
  public void closeWnd(@ContextParam(ContextType.TRIGGER_EVENT) Event event) {
    Page page = event.getTarget().getPage()
    Window fellow = page.getFirstRoot() as Window
    fellow.detach()
  }

  ListModelList<CategoryEntity> getCategoryModel() {
    return categoryModel
  }

  void setCategoryModel(ListModelList<CategoryEntity> categoryModel) {
    this.categoryModel = categoryModel
  }
/*  @Init
  public void init() {
    //Selectors.wireEventListeners(view, this);
  }*/

/*  @AfterCompose
  public void doAfterCompose(@ContextParam(ContextType.VIEW) Component view){
    Selectors.wireEventListeners(view, this);
  }*/

/*
  @Command
  @NotifyChange(["ckeditorValue"])
  public void saveProductItem(@ContextParam(ContextType.TRIGGER_EVENT) Event event){

    Button button = event.getTarget() as Button
    String cKEditorValue = getCKEditorValue(button)
    int r = 0
  }

  public String getCKEditorValue(Button button){
    Page page = button.getPage()
    Window window = page.getFirstRoot() as Window
    //Важно - копируем значение ckeditor в textbox.

    String javascript = """
                        \$
                        """

    Clients.evalJavaScript("\$('.result').val(CKEDITOR.instances['ckeditor'].getData())\")")
    //Textbox result = window.getFellow("result") as Textbox
    return null//result.getValue()
  }*/
/*  @Listen("onSave=#save")
  public void onSave(Event event){
    ForwardEvent eventx = (ForwardEvent) event;
    println(eventx.getOrigin().getData())
  }*/
}
