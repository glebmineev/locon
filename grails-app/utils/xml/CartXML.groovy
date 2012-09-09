package xml

import org.w3c.dom.Document
import org.w3c.dom.NodeList
import org.w3c.dom.Node
import org.w3c.dom.Element
import ru.spb.locon.ProductEntity

/**
 * User: Gleb
 * Date: 08.09.12
 * Time: 23:50
 */
public class CartXML {
  
  private String xml
  Document doc

  public CartXML(String xml) {
    this.xml = xml
    if (xml == null || xml.isEmpty()) {
      //TODO: брать ид пользователя из сессии.
      createXML("")
    }
  }

  public createXML(String user_id){
    StringBuilder xml = new StringBuilder()
    xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n")
    xml.append("<cart user_id=\"" + user_id + "\">\r\n")
    xml.append("\t<products>\r\n")
    xml.append("\t</products>\r\n")
    xml.append("</cart>")
    this.xml = xml.toString()
  }

  public addProduct(Long id) {
    doc = XMLUtils.getDocument(xml)
    NodeList productsList = doc.getElementsByTagName("products")
    Node products = productsList.item(0)
    if (isNewProduct(id)) {
      createNewProduct(products, id)
    } else {
      Node productNode = findExistProduct(id)
      setProductCount(productNode)
    }
    xml = XMLUtils.getString(doc)
  }

  public Boolean isNewProduct(Long id) {
    boolean result = true
    if (findExistProduct(id) != null)
      result = false
    return result
  }

  private createNewProduct(Node products, long id) {
    Element product = doc.createElement("product")
    products.appendChild(product)
    Element elementID = doc.createElement("id")
    elementID.setTextContent(Long.toString(id))
    Element elementCount = doc.createElement("count")
    elementCount.setTextContent("1")
    product.appendChild(elementID)
    product.appendChild(elementCount)
  }

  public Node findExistProduct(Long id) {
    NodeList productList = doc.getElementsByTagName("product")
    Node result = null
    productList.each{Node product ->
      if (getProductID(product) == id)
        result = product
    }
    return result
  }

  public void setProductCount(Node product){
    product.childNodes.each{Node node ->
      if ("count".equals(node.getNodeName()))
        node.getFirstChild().setNodeValue(Integer.toString(getProductCount(product) + 1))
    }
  }

  public Long getProductID(Node product) {
    Long id = null
    product.childNodes.each{Node node ->
      if ("id".equals(node.getNodeName()))
        id = Long.parseLong(node.getFirstChild().getNodeValue())
    }
    return id
  }

  public Integer getProductCount(Node product){
    Integer count = null
    product.childNodes.each{Node node ->
      if ("count".equals(node.getNodeName()))
        count = Integer.parseInt(node.getFirstChild().getNodeValue())
    }
    return count
  }

  public String getXml() {
    return xml
  }

  public Integer getAllCountProducts(){
    int count = 0
    NodeList productList = doc.getElementsByTagName("product")
    productList.each{Node product ->
      count = count + getProductCount(product)
    }
    return count
  }

  public Float getAllPricesProducts(){
    Float result = 0.0
    NodeList productList = doc.getElementsByTagName("product")
    productList.each{Node product ->
      long id = getProductID(product)
      ProductEntity entity = ProductEntity.get(id)
      result = result + (entity.price*getProductCount(product))
    }
    return result
  }

}
