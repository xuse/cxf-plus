
package jef.com.sun.xml.bind.v2.schemagen.xmlschema;

import javax.xml.namespace.QName;
import jef.com.sun.xml.txw2.TypedXmlWriter;
import jef.com.sun.xml.txw2.annotation.XmlAttribute;
import jef.com.sun.xml.txw2.annotation.XmlElement;

public interface SimpleRestrictionModel
    extends SimpleTypeHost, TypedXmlWriter
{


    @XmlAttribute
    public SimpleRestrictionModel base(QName value);

    @XmlElement
    public NoFixedFacet enumeration();

}
