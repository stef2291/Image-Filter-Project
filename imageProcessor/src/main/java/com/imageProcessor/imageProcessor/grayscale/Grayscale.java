package com.imageProcessor.imageProcessor.grayscale;

import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
public class Grayscale {
    public static BufferedImage img = null;
    public static File f = null;
    public static void main(String args[])throws IOException{
    }

    public static void createGrayscale(String filepath){

        try{
            //read image
            f = new File(filepath);
            img = ImageIO.read(f);

            //get image width and height
            Integer width = img.getWidth();
            Integer height = img.getHeight();

            //loop through the image pixels!
            for(int y = 0; y < height; y++){
                for(int x = 0; x < width; x++){
                    int p = img.getRGB(x,y); //get ARGB value

                    //isolate each byte by bit shifting, then logical AND with 0xff (255 in decimal)
                    int a = (p>>24)&0xff;
                    int r = (p>>16)&0xff;
                    int g = (p>>8)&0xff;
                    int b = p&0xff;

                    //by averaging rgb values, we will essentially have a "brightness" value,
                    // the colour will be a shade of black->white i.e,
                    // if they were all 255, the avg would be white, if they were all 0, the avg would be black
                    int avg = (r+g+b)/3;

                    //perform the bit shifting in reverse, keep the alpha value, set R, G, B all to the avg value
                    p = (a<<24) | (avg<<16) | (avg<<8) | avg;
                    img.setRGB(x, y, p);
                }
            }

            //finally, save the image, append black_and_white to the filename
            try{
                f = new File(filepath.substring(0, filepath.length()-5) + "black_and_white.jpg");
                ImageIO.write(img, "jpg", f);
            }catch(IOException e){
                System.out.println(e);
            }
        }catch(IOException e){
            System.out.println(e);
        }
    }
}
