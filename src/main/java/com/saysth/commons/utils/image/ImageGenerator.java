/**
 * $Id: ImageGenerator.java 1525 2011-08-20 03:19:58Z zhuhuiqun $
 */
package com.saysth.commons.utils.image;

import java.awt.Font;

/**
 * Image processing
 * 
 * @author
 * @date Jul 6, 2011 11:27:42 AM
 */
public interface ImageGenerator {

	/**
	 * Get image size.
	 * 
	 * @param image
	 * @return <code>Size</code>
	 * @throws Exception
	 * @see com.saysth.commons.utils.image.Size
	 */
	public Size getSize(String image) throws Exception;

	/**
	 * Add watermark image on source image, generate to target image. x,y
	 * specified the position of watermark. opacity define the transparency of
	 * the watermark. 0-100, higher value for a lower transparency.
	 * 
	 * @param watermarkImage
	 * @param sourceImage
	 * @param targetImage
	 * @param x
	 * @param y
	 * @param opacity
	 */
	public void watermarkByImage(String watermarkImage, String sourceImage, String targetImage, int x, int y,
			int opacity) throws Exception;

	/**
	 * Add watermark text on source image, generate to target image. x,y
	 * specified the position of watermark.
	 * 
	 * @param watermarkText
	 * @param sourceImage
	 * @param targetImage
	 * @param font
	 * @param color
	 * @param x
	 * @param y
	 */
	public void watermarkByText(String watermarkText, String sourceImage, String targetImage, Font font, String color,
			int x, int y) throws Exception;

	/**
	 * Resize the image. width,height specified the size of new image. padding
	 * specified the resize sclae is not fit the original scale
	 * 
	 * @param sourceImage
	 * @param destImage
	 * @param height
	 * @param width
	 * @param padding
	 */
	public void resize(String sourceImage, String targetImage, int width, int height, boolean padding) throws Exception;

}