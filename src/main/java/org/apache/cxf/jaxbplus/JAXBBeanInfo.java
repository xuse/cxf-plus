package org.apache.cxf.jaxbplus;

import java.util.Collection;

import javax.xml.namespace.QName;

public interface JAXBBeanInfo {

    boolean isElement();

    Collection<QName> getTypeNames();

    String getElementNamespaceURI(Object object);

    String getElementLocalName(Object object);

}
