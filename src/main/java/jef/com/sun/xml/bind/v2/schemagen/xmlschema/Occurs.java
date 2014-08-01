
package jef.com.sun.xml.bind.v2.schemagen.xmlschema;

import jef.com.sun.xml.txw2.TypedXmlWriter;
import jef.com.sun.xml.txw2.annotation.XmlAttribute;

public interface Occurs
    extends TypedXmlWriter
{


    @XmlAttribute
    public Occurs minOccurs(int value);

    @XmlAttribute
    public Occurs maxOccurs(String value);

    @XmlAttribute
    public Occurs maxOccurs(int value);

}
