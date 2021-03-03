import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;


public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        BufferedImage img =
                ImageIO.read(new File("images\\kodim17.png"));
        ImageIO.write(img, "bmp", new File("images\\kodim17.bmp")); //converting to bmp

        img = ImageIO.read(new File("images\\kodim17.bmp"));

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(img, "bmp", bos);
        byte[] data = bos.toByteArray();

        //read header
        String hex = String.format("%02X", data[0x00]) + //converting bytes to hex string
                String.format("%02X", data[0x01]);
        System.out.println("Signature: " + hex + " (424D or 4D42 == .bmp)" +'\n' +
                "File size: " + (((int)data[0x05] << 32) | ((int)data[0x04] << 16) | ((int)data[0x03] << 8) | ((int)data[0x02] & 0xFF)) + " bytes" +
                '\n' + "BITMAPINFO size: " + (((int)data[0x11] << 32) | ((int)data[0x10] << 16) | ((int)data[0x0F] << 8) | ((int)data[0x0E] & 0xFF)) +
                '\n' + "Width: " + (((int)data[0x15] << 32) | ((int)data[0x14] << 16) | ((int)data[0x13] << 8) | ((int)data[0x12] & 0xFF)) +
                '\n' + "Height: " + (((int)data[0x19] << 32) | ((int)data[0x18] << 16) | ((int)data[0x17] << 8) | ((int)data[0x16] & 0xFF)) +
                '\n' + "Bits per pixel: " + (((int)data[0x1D] << 8) | ((int)data[0x1C] & 0xFF)) +
                '\n' + "Compression: " + (((int)data[0x21] << 32) | ((int)data[0x20] << 16) | ((int)data[0x1F] << 8) | ((int)data[0x1E] & 0xFF)) +
                '\n' + "Table of colors size: " + (((int)data[0x31] << 32) | ((int)data[0x30] << 16) | ((int)data[0x2F] << 8) | ((int)data[0x2E] & 0xFF)) +
                '\n' + "ClrImportant: " + (((int)data[0x35] << 32) | ((int)data[0x34] << 16) | ((int)data[0x33] << 8) | ((int)data[0x32] & 0xFF)) +
                '\n' + "OffBits: " + String.format( "%02X", (((int)data[0x0D] << 32) | ((int)data[0x0C] << 16) | ((int)data[0x0B] << 8) | ((int)data[0x0A] & 0xFF))) +
                '\n' + "---1'st pixel--- \nRed: " + ((int)data[0x36]) + "\nGreen: " + ((int)data[0x36]) + "\nBlue: " + ((int)data[0x36]));
        int width = (((int)data[0x15] << 32) | ((int)data[0x14] << 16) | ((int)data[0x13] << 8) | ((int)data[0x12] & 0xFF));
        int height = (((int)data[0x19] << 32) | ((int)data[0x18] << 16) | ((int)data[0x17] << 8) | ((int)data[0x16] & 0xFF));
        int offBits = (((int)data[0x0D] << 32) | ((int)data[0x0C] << 16) | ((int)data[0x0B] << 8) | ((int)data[0x0A] & 0xFF));

        byte[] header = new byte[offBits]; //copy header
        for (int i = 0; i < offBits; i++){
            header[i] = data[i];
        }

        //---3---
        //---3.RED---
        Pixel[][] mtx = new Pixel[height][width];
        int counter = offBits + 2; // [blue byte, green byte, red byte, ...]
        for (int i = 0; i < height; i++){ //read certain byte of color
            for (int j = 0; j < width; j++){
                mtx[i][j] = new Pixel(Byte.parseByte("0"), Byte.parseByte("0"), data[counter]);
                counter += 3;
            }
        }

        byte[] outImg = new byte[data.length]; //write header from copy
        for (int i = 0; i < header.length; i++){
            outImg[i] = header[i];
        }
        counter = offBits; //counter must write every byte to build pixel
        byte[] buf = new byte[3];
        for (int i = 0; i < height; i++){ //write image
            for (int j = 0; j < width; j++){
                buf = mtx[i][j].toByteArray();
                outImg[counter] = buf[0];
                counter++;
                outImg[counter] = buf[1];
                counter++;
                outImg[counter] = buf[2];
                counter++;
            }
        }
        ByteArrayInputStream bis = new ByteArrayInputStream(outImg);
        BufferedImage imgOut = ImageIO.read(bis);
        ImageIO.write(imgOut, "bmp", new File("images\\red.bmp"));

        //---3.GREEN---
        mtx = new Pixel[height][width];
        counter = offBits + 1; // [blue byte, green byte, red byte, ...]
        for (int i = 0; i < height; i++){ //read certain byte of color
            for (int j = 0; j < width; j++){
                mtx[i][j] = new Pixel(Byte.parseByte("0"), data[counter], Byte.parseByte("0"));
                counter += 3;
            }
        }

        outImg = new byte[data.length]; //write header from copy
        for (int i = 0; i < header.length; i++){
            outImg[i] = header[i];
        }
        counter = offBits; //counter must write every byte to build pixel
        buf = new byte[3];
        for (int i = 0; i < height; i++){ //write image
            for (int j = 0; j < width; j++){
                buf = mtx[i][j].toByteArray();
                outImg[counter] = buf[0];
                counter++;
                outImg[counter] = buf[1];
                counter++;
                outImg[counter] = buf[2];
                counter++;
            }
        }
        bis = new ByteArrayInputStream(outImg);
        imgOut = ImageIO.read(bis);
        ImageIO.write(imgOut, "bmp", new File("images\\green.bmp"));

        //---3.BLUE---
        mtx = new Pixel[height][width];
        counter = offBits; // [blue byte, green byte, red byte, ...]
        for (int i = 0; i < height; i++){ //read certain byte of color
            for (int j = 0; j < width; j++){
                mtx[i][j] = new Pixel(data[counter], Byte.parseByte("0"), Byte.parseByte("0"));
                counter += 3;
            }
        }

        outImg = new byte[data.length]; //write header from copy
        for (int i = 0; i < header.length; i++){
            outImg[i] = header[i];
        }
        counter = offBits; //counter must write every byte to build pixel
        buf = new byte[3];
        for (int i = 0; i < height; i++){ //write image
            for (int j = 0; j < width; j++){
                buf = mtx[i][j].toByteArray();
                outImg[counter] = buf[0];
                counter++;
                outImg[counter] = buf[1];
                counter++;
                outImg[counter] = buf[2];
                counter++;
            }
        }
        bis = new ByteArrayInputStream(outImg);
        imgOut = ImageIO.read(bis);
        ImageIO.write(imgOut, "bmp", new File("images\\blue.bmp"));



//        int height = img.getHeight();
//        int width = img.getWidth();
//        int red = 0;
//        int green = 0;
//        int blue = 0;
//        int rgb = 0;
//
//        //---3---
//        ImageToolkit itk = new ImageToolkit();
//
//        //---3.RED---
//        BufferedImage imgOutRed = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
//
//        for (int h = 0; h < height; h++){
//            for (int w = 0; w < width; w++){
//                rgb = img.getRGB(w, h); //from source
//                red = itk.getRed(rgb);
//                Color color = new Color(red, 0, 0);
//                rgb = color.getRGB();
//                imgOutRed.setRGB(w, h, rgb);
//            }
//        }
//        ImageIO.write(imgOutRed, "bmp", new File("images\\red.bmp"));
//
//        //---3.GREEN---
//        BufferedImage imgOutGreen = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
//
//        for (int h = 0; h < height; h++){
//            for (int w = 0; w < width; w++){
//                rgb = img.getRGB(w, h); //from source
//                green = itk.getGreen(rgb);
//                Color color = new Color(0, green, 0);
//                rgb = color.getRGB();
//                imgOutGreen.setRGB(w, h, rgb);
//            }
//        }
//        ImageIO.write(imgOutGreen, "bmp", new File("images\\green.bmp"));
//
//        //---3.BLUE---
//        BufferedImage imgOutBlue = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
//
//        for (int h = 0; h < height; h++){
//            for (int w = 0; w < width; w++){
//                rgb = img.getRGB(w, h); //from source
//                blue = itk.getBlue(rgb);
//                Color color = new Color(0, 0, blue);
//                rgb = color.getRGB();
//                imgOutBlue.setRGB(w, h, rgb);
//            }
//        }
//        ImageIO.write(imgOutBlue, "bmp", new File("images\\blue.bmp"));

    }
}
