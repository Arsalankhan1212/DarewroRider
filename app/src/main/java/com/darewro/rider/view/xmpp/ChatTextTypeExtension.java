package com.darewro.rider.view.xmpp;

import org.jivesoftware.smack.packet.ExtensionElement;

public class ChatTextTypeExtension implements ExtensionElement {

    public static final String ELEMENT = "TEXT";
    public static final String NAMESPACE = "urn:xmpp:ChatType";

    @Override
    public String getElementName() {
        return ELEMENT;
    }

    @Override
    public String getNamespace() {
        return NAMESPACE;
    }

    @Override
    public String toXML() {

        StringBuilder builder = new StringBuilder();
        builder.append("<" + getElementName() + " xmlns=\"" + getNamespace()
                + "\">");
/*
        if (elementName != null) {
            builder.append("<elementName>").append(elementName).append("</elementName>");
        }
        if (elementNameSpace != null) {
            builder.append("<elementNameSpace>").append(elementNameSpace).append("</elementNameSpace>");
        }
*/
        builder.append("</" + getElementName() + ">");
        return builder.toString();
    }
}
