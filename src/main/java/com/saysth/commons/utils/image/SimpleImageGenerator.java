/**
 * $Id: SimpleImageGenerator.java 1525 2011-08-20 03:19:58Z zhuhuiqun $
 */
package com.saysth.commons.utils.image;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * Awt implementation
 * 
 * @author
 * @date Jul 6, 2011 11:27:42 AM
 */
public class SimpleImageGenerator implements ImageGenerator {

	public Size getSize(String image) throws IOException {
		File img = new File(image);
		Image src;
		src = ImageIO.read(img);
		int width = src.getWidth(null);
		int height = src.getHeight(null);
		return new Size(width, height);
	}

	@Override
	public void watermarkByImage(final String watermarkImage, final String sourceImage, final String targetImage,
			final int x, final int y, final int opacity) throws IOException {
		File img = new File(targetImage);
		Image src = ImageIO.read(img);
		int wideth = src.getWidth(null);
		int height = src.getHeight(null);
		BufferedImage image = new BufferedImage(wideth, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = image.createGraphics();
		g.drawImage(src, 0, 0, wideth, height, null);
		// watermark begin
		Image src_biao = ImageIO.read(new File(watermarkImage));
		int wideth_biao = src_biao.getWidth(null);
		int height_biao = src_biao.getHeight(null);
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, opacity));
		g.drawImage(src_biao, (wideth - wideth_biao) / 2, (height - height_biao) / 2, wideth_biao, height_biao, null);
		// watermark end
		g.dispose();
		ImageIO.write((BufferedImage) image, "jpg", img);
	}

	@Override
	public void watermarkByText(final String watermarkText, final String sourceImage, final String targetImage,
			final Font font, final String color, final int x, final int y) throws IOException {
		File img = new File(targetImage);
		Image src = ImageIO.read(img);
		int width = src.getWidth(null);
		int height = src.getHeight(null);
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = image.createGraphics();
		g.drawImage(src, 0, 0, width, height, null);
		g.setColor(Color.getColor(color));
		g.setFont(font);
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP));
		g.drawString(watermarkText, (width - (getLength(watermarkText) * font.getSize())) / 2 + x,
				(height - font.getSize()) / 2 + y);
		g.dispose();
		ImageIO.write((BufferedImage) image, "jpg", img);
	}

	@Override
	public void resize(final String sourceImage, final String destSource, final int width, final int height,
			final boolean padding) throws IOException {
		double ratio = 0.0; // ���ű���
		File f = new File(sourceImage);
		BufferedImage bi = ImageIO.read(f);
		Image itemp = bi.getScaledInstance(width, height, BufferedImage.SCALE_SMOOTH);
		// �������
		if ((bi.getHeight() > height) || (bi.getWidth() > width)) {
			if (bi.getHeight() > bi.getWidth()) {
				ratio = (new Integer(height)).doubleValue() / bi.getHeight();
			} else {
				ratio = (new Integer(width)).doubleValue() / bi.getWidth();
			}
			AffineTransformOp op = new AffineTransformOp(AffineTransform.getScaleInstance(ratio, ratio), null);
			itemp = op.filter(bi, null);
		}
		if (padding) {
			BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			Graphics2D g = image.createGraphics();
			g.setColor(Color.white);
			g.fillRect(0, 0, width, height);
			if (width == itemp.getWidth(null))
				g.drawImage(itemp, 0, (height - itemp.getHeight(null)) / 2, itemp.getWidth(null),
						itemp.getHeight(null), Color.white, null);
			else
				g.drawImage(itemp, (width - itemp.getWidth(null)) / 2, 0, itemp.getWidth(null), itemp.getHeight(null),
						Color.white, null);
			g.dispose();
			itemp = image;
		}
		ImageIO.write((BufferedImage) itemp, "jpg", new File(destSource));
	}

	public static int getLength(String text) {
		int length = 0;
		for (int i = 0; i < text.length(); i++) {
			if (String.valueOf(text.charAt(i)).getBytes().length > 1) {
				length += 2;
			} else {
				length += 1;
			}
		}
		return length / 2;
	}
}
