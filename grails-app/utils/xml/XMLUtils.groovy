package xml

import org.w3c.dom.Document
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.DocumentBuilder
import org.xml.sax.InputSource
import javax.xml.transform.Source
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.TransformerFactory
import javax.xml.transform.Transformer
import javax.xml.transform.OutputKeys
import javax.xml.transform.stream.StreamResult

/**
 * User: Gleb
 * Date: 09.09.12
 * Time: 1:54
 */
class XMLUtils {

  public static Document getDocument(String xml){
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance()
    DocumentBuilder parser = factory.newDocumentBuilder()
    InputSource is = new InputSource();
    is.setCharacterStream(new StringReader(xml));
    return parser.parse(is)
  }

  public static String getString(Document doc){
    TransformerFactory tranFactory = TransformerFactory.newInstance()
    Transformer trans = tranFactory.newTransformer()
    trans.setOutputProperty(OutputKeys.METHOD, "xml")
    trans.setOutputProperty(OutputKeys.INDENT, "yes")
    trans.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", Integer.toString(2))

    StringWriter sw = new StringWriter()
    StreamResult result = new StreamResult(sw)
    DOMSource source = new DOMSource(doc.getDocumentElement())

    trans.transform(source, result)
    //String xmlString =  org.apache.ws.security.util.XMLUtils.PrettyDocumentToString(doc)
    //String xmlString =  org.apache.ws.security.util.XMLUtils.PrettyDocumentToString(doc)
    return sw.toString();
  }

}
