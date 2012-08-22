/**
 * $Id: Im4javaImageGenerator.java 1525 2011-08-20 03:19:58Z zhuhuiqun $
 */
package com.saysth.commons.utils.image;

import java.awt.Font;
import java.io.IOException;

import org.im4java.core.CompositeCmd;
import org.im4java.core.ConvertCmd;
import org.im4java.core.IM4JavaException;
import org.im4java.core.IMOperation;
import org.im4java.core.Info;
import org.im4java.core.InfoException;
import org.im4java.process.ProcessStarter;

/**
 * Im4java implementation
 * 
 * @author
 * @date Jul 6, 2011 11:27:42 AM
 */
public class Im4javaImageGenerator implements ImageGenerator {
	private final static String OS_NAME = "os.name";
	private final static String WINDOWS = "WINDOWS";
	private final static String DEFAULT_SEARCHPATH = "C:\\Program Files\\GraphicsMagick-1.3.12-Q16;C:\\Program Files\\ImageMagick-6.3.9-Q16";
	private final static String GEOMETRY = "Geometry";
	private final static String PLUS_SIGN = "+";
	private final static String X_SIGN = "x";
	private final static String GRAVITY_SE = "southeast";
	private final static String TEXT = "text";
	private final static String SPACE = " ";
	private final static String COMMA = ",";

	private boolean useGM = true;

	public Im4javaImageGenerator(boolean useGM) {
		this.useGM = useGM;
		if (System.getProperty(OS_NAME).toUpperCase().indexOf(WINDOWS) > 0) {
			setSearchPath(DEFAULT_SEARCHPATH);
		}
	}

	public Im4javaImageGenerator(String searchPath, boolean useGM) {
		this.useGM = useGM;
		setSearchPath(searchPath);
	}

	public void setSearchPath(String searchPath) {
		ProcessStarter.setGlobalSearchPath(searchPath);
	}

	public void setUseGM(boolean useGM) {
		this.useGM = useGM;
	}

	/**
	 * Get size of specified image
	 */
	@Override
	public Size getSize(String image) throws InfoException {
		Info imageInfo = new Info(image);
		String geometry = imageInfo.getProperty(GEOMETRY);
		int endIndex = geometry.indexOf(PLUS_SIGN);
		geometry = endIndex > 0 ? geometry.substring(0, endIndex) : geometry;
		String[] size = geometry.split(X_SIGN);
		int width = Integer.parseInt(size[0]);
		int height = Integer.parseInt(size[1]);
		return new Size(width, height);
	}

	/**
	 * Add watermark on sourceImage and output to targetImage
	 */
	@Override
	public void watermarkByImage(final String watermarkImage, final String sourceImage, final String targetImage,
			final int x, final int y, final int opacity) throws IOException, InterruptedException, IM4JavaException {
		IMOperation op = new IMOperation();
		op.gravity(GRAVITY_SE).dissolve(opacity);
		op.addImage();
		op.addImage();
		op.addImage();
		CompositeCmd cmd = new CompositeCmd(useGM);
		cmd.run(op, watermarkImage, sourceImage, targetImage);
	}

	/**
	 * Add watermark on sourceImage and output to targetImage
	 */
	@Override
	public void watermarkByText(final String watermarkText, final String sourceImage, final String targetImage,
			final Font font, final String color, final int x, final int y) throws IOException, InterruptedException,
			IM4JavaException {
		IMOperation op = new IMOperation();
		StringBuffer text = new StringBuffer();
		text.append(TEXT).append(SPACE).append(x).append(COMMA).append(y).append(SPACE).append(watermarkText);
		// text watermark format: text 350,260 your_text
		op.font(font.getFontName()).pointsize(font.getSize()).fill(color).draw(text.toString());
		op.addImage();
		op.addImage();
		ConvertCmd convert = new ConvertCmd(useGM);
		convert.run(op, sourceImage, targetImage);
	}

	/**
	 * Resize source image, output to target image
	 */
	@Override
	public void resize(final String sourceImage, final String targetImage, final int width, final int height,
			final boolean padding) throws Exception {
		IMOperation op = new IMOperation();
		op.resize(width, height);
		op.addImage(sourceImage);
		op.addImage(targetImage);
		ConvertCmd convert = new ConvertCmd(useGM);
		convert.run(op);
	}

}