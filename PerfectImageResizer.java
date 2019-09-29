import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.nio.file.*;
import java.util.stream.*;
import java.util.*;

public class PerfectImageResizer
{
 public static void createThumbnail(String sourceFile, String destFile,
       int targetWidth,int targetHeight) throws Exception
 {
  try
  {
   BufferedImage img = ImageIO.read(new File(sourceFile));
   int iw = img.getWidth();
   int ih = img.getHeight();

   Object hint = RenderingHints.VALUE_INTERPOLATION_BILINEAR;
   int type = img.getType() == 0? BufferedImage.TYPE_INT_ARGB : img.getType();

   // First get down to no more than 2x in W & H
   while (iw > targetWidth*2 || ih > targetHeight*2) {
     iw = (iw > targetWidth*2) ? iw/2 : iw;
     ih = (ih > targetHeight*2) ? ih/2 : ih;
     img = scaleImage(img, type, hint, iw, ih);
   }

   // REMIND: Conservative approach:
   // first get W right, then worry about H

   // If still too wide - do a horizontal trilinear blend
   // of img and a half-width img
   if (iw > targetWidth) {
     int iw2 = iw/2;
     BufferedImage img2 = scaleImage(img, type, hint, iw2, ih);
     if (iw2 < targetWidth) {
       img = scaleImage(img, type, hint, targetWidth, ih);
       img2 = scaleImage(img2, type, hint, targetWidth, ih);
       interp(img2, img, iw-targetWidth, targetWidth-iw2);
     }
     img = img2;
     iw = targetWidth;
   }
   // iw should now be targetWidth or smaller

   // If still too tall - do a vertical trilinear blend
   // of img and a half-height img
   if (ih > targetHeight) {
     int ih2 = ih/2;
     BufferedImage img2 = scaleImage(img, type, hint, iw, ih2);
     if (ih2 < targetHeight) {
       img = scaleImage(img, type, hint, iw, targetHeight);
       img2 = scaleImage(img2, type, hint, iw, targetHeight);
       interp(img2, img, ih-targetHeight, targetHeight-ih2);
     }
     img = img2;
     ih = targetHeight;
   }
   // ih should now be targetHeight or smaller

   // If we are too small, then it was probably because one of
   // the dimensions was too small from the start.
   if (iw < targetWidth && ih < targetHeight) {
     img = scaleImage(img, type, hint, targetWidth, targetHeight);
   }
   ImageIO.write(img, destFile.substring(destFile.lastIndexOf('.')+1), new FileOutputStream(destFile));

  } catch (IOException thumbException)
  {
   thumbException.printStackTrace();
   throw new Exception(thumbException);
  }
 }
 private static BufferedImage scaleImage(BufferedImage orig,int type,Object hint,int w, int h)
 {
   BufferedImage tmp = new BufferedImage(w, h, type);
   Graphics2D g2 = tmp.createGraphics();
   g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint);
   g2.drawImage(orig, 0, 0, w, h, null);
   g2.dispose();
   return tmp;
 }

 private static void interp(BufferedImage img1,BufferedImage img2,int weight1,int weight2)
 {
   float alpha = weight1;
   alpha /= (weight1 + weight2);
   Graphics2D g2 = img1.createGraphics();
   g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
   g2.drawImage(img2, 0, 0, null);
   g2.dispose();
 }
 public static void main(String[] args) throws Exception
 {




List<String> filesInFolder = Files.walk(Paths.get("C:\\phani\\toupload"))
                                  .filter(Files::isRegularFile)
                                .map(Path::toString)
                                .collect(Collectors.toList());

 long filesize = 0;
 int targetWidth=0;

  for(String name:filesInFolder){


  String sourceFile=name;

    File file= new File(sourceFile);
	filesize = file.length();

	  System.out.println( name +" : "+file.length());


  BufferedImage img = ImageIO.read(file);


  int iw = img.getWidth();
  int ih = img.getHeight();
 // System.out.println(iw);
  //System.out.println(ih);
  if(filesize <55000){
	  targetWidth = 650;
  }else  if(filesize < 72987){
	targetWidth = 450;
  }else if(filesize < 9000){

	  targetWidth = 375;
  }else{
	   targetWidth = 325;
  }
  double imgHeightPercentage=((double)targetWidth/(double)iw)*100;
  double imgTotalHeight=ih * (imgHeightPercentage/100);
  int targetHeight=(int)Math.round(imgTotalHeight);
  String tname = sourceFile.substring(0,(sourceFile.trim()).length()-4) + "g.jpg";
  String tpath ="C:\\phani\\tnails";
  //String tpath ="C:\\phani\\gimages";
  //   tpath+sourceFile.substring(sourceFile.lastIndexOf("\\"),(sourceFile.trim()).length());

  if(iw > ih){
  PerfectImageResizer.createThumbnail(sourceFile, tpath+sourceFile.substring(sourceFile.lastIndexOf("\\"),(sourceFile.trim()).length()),650/4,500/4);
  }else{
  PerfectImageResizer.createThumbnail(sourceFile, tpath+sourceFile.substring(sourceFile.lastIndexOf("\\"),(sourceFile.trim()).length()),500/4,650/4);
  }
  }
 }
}