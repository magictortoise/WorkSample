/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.evi.web.resources.backingbeanencapsulation;

import com.sun.image.codec.jpeg.ImageFormatException;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import java.awt.Graphics2D;
import java.awt.MediaTracker;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.apache.log4j.Logger;

/**
 * Creates Scaled Images.
 * <p>Creates a scaled image (often times a thumbnail) for a picture. Puts it in
 * the same directory as the source (for now).</p>
 * @author tao
 */
public class ImageScale {

   private Logger logger = Logger.getLogger(ImageScale.class);
   /** Image Handler*/
   protected BufferedImage img;
   /** MediaTracker Handler*/
   protected MediaTracker tracker;
   /** Resultant Width*/
   protected int thumbWidth;
   /** Resultant Height*/
   protected int thumbHeight;

   /**
    * Creates a image scale ready to use.
    * @param pathToFile Path to source file
    * @param width      Width of the resultant thumbnail
    * @param height     Height of the resultant thumbnail
    */
   public ImageScale(File file, int maxHeight, int maxWidth) throws IOException {
      logger.fatal("File: " + file.getCanonicalPath());
			img = ImageIO.read(file);

      int sourceHeight = img.getHeight(null);
      int sourceWidth = img.getWidth(null);
      double ratio = calcImageRatio(sourceWidth, sourceHeight);

      logger.fatal("Starting! Width: " + sourceWidth + " Height: " + sourceHeight);
      logger.fatal("Change needed in height: " + (sourceHeight / maxHeight));
      logger.fatal("Change needed in width: " + (sourceWidth / maxWidth));
      if (sourceHeight <= maxHeight && sourceWidth <= maxWidth) { //If it already fits, don't change it.
         thumbHeight = sourceHeight;
         thumbWidth = sourceWidth;
      } else if ((sourceHeight / maxHeight) > (sourceWidth / maxWidth)) { //Too tall
         thumbHeight = maxHeight;
         thumbWidth = (int) (maxHeight * ratio);
      } else if ((sourceHeight / maxHeight) < (sourceWidth / maxWidth)) { //Too wide
         thumbWidth = maxWidth;
         thumbHeight = (int) (maxWidth / ratio);
      } else { //Same ratio, just too big
         thumbWidth = maxWidth;
         thumbHeight = maxHeight;
      }
      logger.fatal("Finished! Width: " + thumbWidth + " Height: " + thumbHeight);
   }

   /**
    * Creates a thumbnail of the prepared image.
    * Creates it with the given filename at the given file path.
    * @param filename
    * @param filepath
    */
   public void createThumbnail(File file) {
      logger.fatal("Thumb Width: " + thumbWidth + " Thumb Height: " + thumbHeight);
      BufferedImage thumb = new BufferedImage(thumbWidth, thumbHeight, BufferedImage.TYPE_INT_RGB);
      Graphics2D graphics2D = thumb.createGraphics();
      graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
      graphics2D.drawImage(img, 0, 0, thumbWidth, thumbHeight, null);

      try {
         BufferedOutputStream out = null;
         out = new BufferedOutputStream(new FileOutputStream(file));

         if (out != null) {
            JPEGImageEncoder enc = JPEGCodec.createJPEGEncoder(out);
            JPEGEncodeParam param = enc.getDefaultJPEGEncodeParam(thumb);
            param.setQuality((float) 100.0f / 100.0f, false);
            enc.setJPEGEncodeParam(param);
            try {
               enc.encode(thumb);
               out.close();
            } catch (IOException ioe) {
               ioe.printStackTrace();
            }
         }
      } catch (FileNotFoundException ex) {
         logger.error(null, ex);
      } catch (ImageFormatException ex) {
         logger.error(null, ex);
      }
   }

   //<editor-fold defaultstate="collapsed" desc="Private Helper Functions">
   private double calcImageRatio(int w, int h) {
      return (double) w / (double) h;
   }
//</editor-fold>
}
