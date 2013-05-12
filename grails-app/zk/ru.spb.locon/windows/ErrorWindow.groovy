package ru.spb.locon.windows

import org.zkoss.zul.*
import org.zkoss.zk.ui.event.*

/**
 * Created by IntelliJ IDEA.
 * User: vfoteeva
 * Date: 04.05.12
 * Time: 15:47
 * To change this template use File | Settings | File Templates.
 */
public class ErrorWindow extends Window {
  public String message;

  public ErrorWindow(Window owner) {

  }

  public ErrorWindow(String message) {
    this.message = message;

    Div hdiv1 = new Div();
    hdiv1.setSclass("nameEditWindowPadding");
    hdiv1.setParent(this);

    Vbox vb1 = new Vbox();
    vb1.setParent(hdiv1);

    Hbox hb1 = new Hbox();
    hb1.setParent(vb1);

    Div hdiv2 = new Div();
    hdiv2.setStyle("padding-right: 10px");
    hdiv2.setParent(hb1);

    //TODO: жестко забита версия плагина niczk-0.1
    Image img = new Image("/images/error.png");
    img.setWidth("50px");
    img.setParent(hdiv2);

    Vbox vb2 = new Vbox();
    vb2.setParent(hb1);

    Div hdiv3 = new Div();
    hdiv3.setParent(vb2);

    Vbox vb3 = new Vbox();
    vb3.setStyle("width: 400px");
    vb3.setParent(hdiv3);

    Label l1 = new Label("Ошибка!");
    l1.setSclass("heading");
    l1.setParent(vb3);

    Label l2 = new Label(this.message);
    l2.setMultiline(true);
    l2.setStyle("width: 350px");
    l2.setParent(vb3);

    Div hdiv4 = new Div();
    hdiv4.setStyle("text-align: center");
    hdiv4.setParent(vb1);

    Button btn1 = new Button("ОК");
    btn1.setParent(hdiv4);

    btn1.addEventListener(Events.ON_CLICK, new EventListener(){
      @Override
      void onEvent(Event t) {
        setVisible(false)
      }
    })
  }

  public ErrorWindow() {

  }
}
