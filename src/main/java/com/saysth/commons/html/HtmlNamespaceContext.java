package com.saysth.commons.html;

import java.util.Iterator;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;

public class HtmlNamespaceContext implements NamespaceContext {

	public String getNamespaceURI(String prefix) {
		if (prefix == null)
			throw new NullPointerException("Null prefix");
		else if ("h".equals(prefix))
			return "http://www.w3.org/1999/xhtml";
		else if ("xml".equals(prefix))
			return XMLConstants.XML_NS_URI;
		return XMLConstants.NULL_NS_URI;

	}

	public String getPrefix(String namespaceURI) {
		return null;
	}

	public Iterator<String> getPrefixes(String namespaceURI) {
		return null;
	}

}
