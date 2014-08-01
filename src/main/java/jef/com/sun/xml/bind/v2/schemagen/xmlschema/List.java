
package jef.com.sun.xml.bind.v2.schemagen.xmlschema;

import javax.xml.namespace.QName;
import jef.com.sun.xml.txw2.TypedXmlWriter;
import jef.com.sun.xml.txw2.annotation.XmlAttribute;
import jef.com.sun.xml.txw2.annotation.XmlElement;

@XmlElement("list")
public interface List
    extends Annotated, SimpleTypeHost, TypedXmlWriter
{


    @XmlAttribute
    public List itemType(QName value);

}
