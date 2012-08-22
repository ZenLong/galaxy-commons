package com.saysth.commons.html;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class HtmlDocument {
	private Document document;

	public HtmlDocument(Document document) {
		this.document = document;
	}

	public static HtmlDocument parseHtmlDocument(String htmlContent, String charset) throws HtmlDocumentException {
		assert (null != htmlContent) : "html content can't be null.";
		assert (null != charset) : "charset can't be null or has not text.";
		Document doc = HtmlDocumentHelper.getDocument(htmlContent, charset);
		if (doc == null) {
			throw new HtmlDocumentException("The captured html content invalid.");
		}
		HtmlDocument document = new HtmlDocument(doc);
		return document;
	}

	public String getTextBoxValueById(String id) {
		String xPath = "id('" + id + "')";
		Node node = HtmlDocumentHelper.getNode(document, xPath);
		if (node != null) {
			return HtmlDocumentHelper.getValue(node);
		}
		return null;
	}

	public String getTextAreaValueById(String id) {
		String xPath = "id('" + id + "')";
		Node node = HtmlDocumentHelper.getNode(document, xPath);
		if (node != null) {
			return HtmlDocumentHelper.getInnerHtml(node);
		}
		return null;
	}

	public String getListValueById(String id) {
		String xPath = "id('" + id + "')";
		Node node = HtmlDocumentHelper.getNode(document, xPath);
		if (node != null) {
			NodeList optionNodeList = HtmlDocumentHelper.getNodeList(node, ".//OPTION");
			if (optionNodeList != null) {
				for (int i = 0; i < optionNodeList.getLength(); i++) {
					Node optionNode = optionNodeList.item(i);
					if (HtmlDocumentHelper.hasAttribute(optionNode, HtmlTagConstants.SELECTED)) {
						return HtmlDocumentHelper.getValue(optionNode);
					}
				}
			}
		}
		return null;
	}

	public String getListTextById(String id) {
		String xPath = "id('" + id + "')";
		Node node = HtmlDocumentHelper.getNode(document, xPath);
		if (node != null) {
			NodeList optionNodeList = HtmlDocumentHelper.getNodeList(node, ".//OPTION");
			if (optionNodeList != null) {
				for (int i = 0; i < optionNodeList.getLength(); i++) {
					Node optionNode = optionNodeList.item(i);
					if (HtmlDocumentHelper.hasAttribute(optionNode, HtmlTagConstants.SELECTED)) {
						return HtmlDocumentHelper.getText(optionNode);
					}
				}
			}
		}
		return null;
	}

	public String getListTextByValue(String id, String value) {
		String xPath = "id('" + id + "')";
		Node node = HtmlDocumentHelper.getNode(document, xPath);
		if (node != null) {
			NodeList optionNodeList = HtmlDocumentHelper.getNodeList(node, ".//OPTION");
			if (optionNodeList != null) {
				for (int i = 0; i < optionNodeList.getLength(); i++) {
					Node optionNode = optionNodeList.item(i);
					if (value.equals(HtmlDocumentHelper.getValue(optionNode))) {
						return HtmlDocumentHelper.getText(optionNode);
					}
				}
			}
		}
		return null;
	}

	public String[] getListAllValueById(String id) {
		String xPath = "id('" + id + "')";
		List<String> values = new ArrayList<String>();
		Node node = HtmlDocumentHelper.getNode(document, xPath);
		if (node != null) {
			NodeList optionNodeList = HtmlDocumentHelper.getNodeList(node, ".//OPTION");
			for (int i = 0; i < optionNodeList.getLength(); i++) {
				Node optionNode = optionNodeList.item(i);
				values.add(HtmlDocumentHelper.getValue(optionNode));
			}
		}
		if (values.size() > 0) {
			return values.toArray(new String[values.size()]);
		}
		return null;
	}

	public String getRadiokBoxValueByName(String name) {
		String xPath = "//INPUT[@type='radio' and @name='" + name + "']";
		NodeList checkBoxNodeList = HtmlDocumentHelper.getNodeList(document, xPath);
		if (checkBoxNodeList != null) {
			for (int i = 0; i < checkBoxNodeList.getLength(); i++) {
				Node checkBoxNode = checkBoxNodeList.item(i);
				if (HtmlDocumentHelper.hasAttribute(checkBoxNode, HtmlTagConstants.CHECKED)) {
					return HtmlDocumentHelper.getValue(checkBoxNode);
				}
			}
		}
		return null;
	}

	public String getCheckBoxValueById(String id) {
		String xPath = "id('" + id + "')";
		Node node = HtmlDocumentHelper.getNode(document, xPath);
		if (node != null) {
			return HtmlDocumentHelper.getValue(node);

		}
		return null;
	}

	public boolean isChecked(String id) {
		String xPath = "id('" + id + "')";
		Node node = HtmlDocumentHelper.getNode(document, xPath);
		if (node != null) {
			if (HtmlDocumentHelper.hasAttribute(node, HtmlTagConstants.CHECKED)) {
				return true;
			}
		}
		return false;
	}

	public String getTextBoxValueByName(String name) {
		String xPath = "//INPUT[@type='text' and @name='" + name + "']";
		Node node = HtmlDocumentHelper.getNode(document, xPath);
		if (node == null) {
			xPath = "//INPUT[@name='" + name + "']";
			node = HtmlDocumentHelper.getNode(document, xPath);
		}
		if (node != null) {
			return HtmlDocumentHelper.getValue(node);
		}
		return null;
	}

	public boolean isCheckedByName(String name) {
		String xPath = "//INPUT[@type='checkbox' and @name='" + name + "']";
		Node node = HtmlDocumentHelper.getNode(document, xPath);
		if (node == null) {
			xPath = "//INPUT[@name='" + name + "']";
			node = HtmlDocumentHelper.getNode(document, xPath);
		}
		if (node != null) {
			if (HtmlDocumentHelper.hasAttribute(node, HtmlTagConstants.CHECKED)) {
				return true;
			}
		}
		return false;
	}

	public String getInnerTextById(String id) {
		String xPath = "id('" + id + "')";
		Node node = HtmlDocumentHelper.getNode(document, xPath);
		if (node != null) {
			return HtmlDocumentHelper.getText(node);
		}
		return null;

	}

	public String getInnerHtmlById(String id) {
		String xPath = "id('" + id + "')";
		Node node = HtmlDocumentHelper.getNode(document, xPath);
		if (node != null) {
			return HtmlDocumentHelper.getInnerHtml(node);
		}
		return null;
	}

	public Document getDocument() {
		return document;
	}

}
