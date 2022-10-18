package de.moritzpetersen.photocopy.util;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public abstract class SimpleElementHandler extends DefaultHandler {
    private final StringBuilder charBuf = new StringBuilder();

    @Override
    public void characters(final char[] ch, final int start, final int length) throws SAXException {
        charBuf.append(ch, start, length);
    }

    @Override
    public void endElement(final String uri, final String localName, final String qName) throws SAXException {
        element(localName, charBuf.toString());
        charBuf.setLength(0);
    }

    protected abstract void element(String localName, String characters);
}
