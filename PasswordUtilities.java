/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.evi.web.resources;

import com.evi.web.resources.exceptions.PasswordException;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.security.MessageDigest;
import java.util.Random;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import org.apache.log4j.Logger;

/**
 * A collection of password utilites.
 * <p>Includes creating image codes, passwords and validation of those passwords.</p>
 * 
 * @author tao
 */
public class PasswordUtilities extends EVerifileBean {

   private static Logger logger = Logger.getLogger(PasswordUtilities.class);
   private static Pattern numberPattern = Pattern.compile("\\d");
   private static Pattern capitalPattern = Pattern.compile("[A-Z]");
   private static Pattern lowercasePattern = Pattern.compile("[a-z]");
   private static Pattern punctPattern = Pattern.compile("[^A-Za-z0-9]");
   private static Pattern noTwoPunctPattern = Pattern.compile(".*\\p{Punct}{2,}.*");
   private static Pattern noSingleQuotePattern = Pattern.compile("'");
   private static Pattern noBackslashPattern = Pattern.compile("\\\\");
   private static Pattern noWhitespacePattern = Pattern.compile("\\s");

   /** Creates a new instance of PasswordUtilities. */
   public PasswordUtilities() {
   }

   /**
    * Creates an Image Code for use in Anti-Spam Logins.
    * @param imageCode  The actual code string to create an image from
    * @return           The location of the image
    * @throws java.lang.Exception
    */
   public String createImageCode(String imageCode) throws Exception {

      int width = 180;
      int height = 30;
      int pts = 21;
      String rootDir = getMainProp("rootDir");

      BufferedImage img = new BufferedImage(
            width,
            height,
            BufferedImage.TYPE_INT_RGB);
      Graphics2D gr = img.createGraphics();
      RenderingHints RHAnti = new RenderingHints(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);

      gr.setRenderingHints(RHAnti);
      gr.setColor(new Color(255, 255, 255));
      gr.fillRect(0, 0, width, height);
      gr.setColor(new Color(255, 0, 0));

      Random random;

      for (int i = 0; i < 5; i++) {
         random = new Random();
         int randx1 = random.nextInt(180);
         int randy1 = random.nextInt(30);
         int randx2 = random.nextInt(180);
         int randy2 = random.nextInt(30);

         gr.drawLine(randx1, randy1, randx2, randy2);

      }

      Font f = new Font("Courier Italic", Font.BOLD, pts);

      gr.setColor(Color.red);
      gr.setFont(f);

      gr.drawString(imageCode, 5, 20);

      random = new Random();

      File rootFile = new File(rootDir + "/imagecodes");
      File tempFile = File.createTempFile("imagecode", ".jpg", rootFile);
      tempFile.deleteOnExit();
      ImageIO.write(img, "jpg", tempFile);
      return "imagecodes/" + tempFile.getName();
   }

   /**
    * Generates a random password of a given length.
    * @param length  Length of the new password
    * @return        New Password
    */
   public String generateRandomPassword(int length) {
      String pass;
      do {

         char[] characterArray = new char[length];
         for (int i = 0; i < length; i++) {
            characterArray[i] = (char) ((Math.random() * 86) + 40);
         }

         pass = new String(characterArray);
      } while (!checkPass(pass));

      return pass;

   }

   public String generateImageCodeStr() {
      return md5sum(generateRandomPassword(10)).substring(0, 10);
   }

   public String generateEmployeePassword() {
      String generatedPassword = generateRandomPassword(10);
      String md5sum = md5sum(generatedPassword).substring(0, 7);
      return md5sum;
   }

   /**
    * Validates that a given password meets minimum security requiremetns
    * @param pass Password.
    * @return True if meets requirements.
    */
   public void validatePass(String pass) throws PasswordException {
      if (!numberPattern.matcher(pass).find()) {
         throw new PasswordException("You must include atleast one number. (eg 1)");
      }
      if (!capitalPattern.matcher(pass).find()) {
         throw new PasswordException("You must include atleast one capital letter. (eg E)");
      }
      if (!lowercasePattern.matcher(pass).find()) {
         throw new PasswordException("You must include atleast one lowercase letter. (eg e)");
      }
      if (!punctPattern.matcher(pass).find()) {
         throw new PasswordException("You must include atleast one punctuation mark. (eg !)");
      }
      if (noTwoPunctPattern.matcher(pass).find()) {
         throw new PasswordException("You cannot include two adjacent punctuation marks. (eg ;;)");
      }
      if (noSingleQuotePattern.matcher(pass).find()) {
         throw new PasswordException("You cannot include single quotaton mark. (eg ')");
      }
      if (noBackslashPattern.matcher(pass).find()) {
         throw new PasswordException("You cannot include backslashes. (eg \\\\)");
      }
      if (noWhitespacePattern.matcher(pass).find()) {
         throw new PasswordException("You cannot include any white space.");
      }
   }

   public boolean checkPass(String password) {
      try {
         validatePass(password);
         return true;
      } catch (PasswordException passwordException) {
         return false;
      }
   }

   /**
    * Creates a md5 Sum, which is a hexadecimal encryption method.
    * <p>Primarily used, now, to create random but easy to input image codes.</p>
    * @param input Input
    * @return md5 Sum of input
    */
   public String md5sum(Object input) {
      try {
         MessageDigest md5 = MessageDigest.getInstance("MD5");
         String inaddStr = input.toString();

         byte[] sum = md5.digest(inaddStr.getBytes());

         StringBuffer result = new StringBuffer();

         for (int i = 0; i < sum.length; i++) {
            result.append(String.format("%02x", sum[i]));
         }
         return result.toString();
      } catch (Exception ex) {
         logger.error(null, ex);
         return null;
      }
   }
}
