/**
 * 
 */
package com.saysth.commons.html;

/**
 * @author
 * 
 */
public class HtmlDocumentException extends Exception {
	private static final long serialVersionUID = -732275197570812934L;

	public HtmlDocumentException(String msg) {
		super(msg);
	}

	public HtmlDocumentException(Throwable throwable) {
		super(throwable);
	}

	public HtmlDocumentException(String s, Throwable e) {
		super(s, e);
	}
}
