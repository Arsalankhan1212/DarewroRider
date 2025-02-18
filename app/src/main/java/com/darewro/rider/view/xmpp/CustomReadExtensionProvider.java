package com.darewro.rider.view.xmpp;

import org.jivesoftware.smack.packet.Element;
import org.jivesoftware.smack.provider.ExtensionElementProvider;
import org.xmlpull.v1.XmlPullParser;

public class CustomReadExtensionProvider extends ExtensionElementProvider {
    @Override
    public Element parse(XmlPullParser parser, int initialDepth) throws Exception {
        CustomReadExtension customExtension = new CustomReadExtension();

        int eventType = parser.next();

        while (eventType == XmlPullParser.START_TAG) {

            String name = parser.getName();

            switch (parser.getEventType()) {

                case XmlPullParser.START_TAG:
                    /*if (name.equals("elementName")) {

                        customExtension.setElementName(parser.nextText());
                    } else if (name.equals("elementNameSpace")) {

                        customExtension.setElementNameSpace(parser.nextText());
                    }*/
            }

            eventType = parser.next();
        }

        return customExtension;
    }
}
