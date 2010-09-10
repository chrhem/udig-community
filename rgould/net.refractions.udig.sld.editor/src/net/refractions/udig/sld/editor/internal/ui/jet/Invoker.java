package net.refractions.udig.sld.editor.internal.ui.jet;

import java.net.URL;
import java.util.Iterator;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Text;
import org.jdom.input.SAXBuilder;

public class Invoker {

    public static void main( String[] args ) throws Exception {
        URL url = new URL("http://schemas.opengis.net/sld/1.0.0/StyledLayerDescriptor.xsd");
        
        SAXBuilder builder = new SAXBuilder(false);
        Document document = null;
        
        document = builder.build(url);
        
        Iterator iter = document.getDescendants();
        while (iter.hasNext()) {
            Object obj = iter.next();
            if (obj instanceof Text) {
                Text text = (Text) obj;
//                System.out.println(text.getText());
            } else
            if (obj instanceof Element) {
                Element elem = (Element) obj;
                System.out.println(elem.getName());
            } else {
//                System.out.println("Unknown: " + obj);
            }
        }
        
    }
}
