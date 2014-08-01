
package jef.com.sun.xml.bind.v2.schemagen.xmlschema;

import jef.com.sun.xml.txw2.TypedXmlWriter;
import jef.com.sun.xml.txw2.annotation.XmlAttribute;
import jef.com.sun.xml.txw2.annotation.XmlElement;

@XmlElement("import")
public interface Import
    extends Annotated, TypedXmlWriter
{


    @XmlAttribute
    public Import namespace(String value);

    @XmlAttribute
    public Import schemaLocation(String value);

}
