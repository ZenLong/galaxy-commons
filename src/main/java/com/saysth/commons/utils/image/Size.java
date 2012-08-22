/**
 * $Id: Size.java 1432 2011-07-18 10:27:51Z zhuhuiqun $
 */
package com.saysth.commons.utils.image;

import java.io.Serializable;

/**
 * Size
 * 
 * @author
 * @date Jul 6, 2011 11:27:42 AM
 */
public class Size implements Serializable {
	private static final long serialVersionUID = -4229478119306199415L;
	private int width;
	private int height;

	public Size() {
	}

	public Size(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public String toString() {
		return width + "x" + height;
	}

}