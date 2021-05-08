import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;


public class Main {

    public static void main(String[] args) throws IOException, InterruptedException { //239x235 168495+705=169200
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
                '\n' + "---1'st pixel--- \nRed: " + ((int)data[0x38] & 0xff) + "\nGreen: " + ((int)data[0x37] & 0xff) + "\nBlue: " + ((int)data[0x36] & 0xff));
        int width = (((int)data[0x15] << 32) | ((int)data[0x14] << 16) | ((int)data[0x13] << 8) | ((int)data[0x12] & 0xFF));
        int height = (((int)data[0x19] << 32) | ((int)data[0x18] << 16) | ((int)data[0x17] << 8) | ((int)data[0x16] & 0xFF));
        int offBits = (((int)data[0x0D] << 32) | ((int)data[0x0C] << 16) | ((int)data[0x0B] << 8) | ((int)data[0x0A] & 0xFF));
        int padding = 4 - (3*width) % 4;
        int fileSize = (((int)data[0x05] << 32) | ((int)data[0x04] << 16) | ((int)data[0x03] << 8) | ((int)data[0x02] & 0xFF));
        byte[] header = new byte[offBits]; //copy header
        for (int i = 0; i < offBits; i++){
            header[i] = data[i];
        }

        //---3---
        //---3.RED---
        Pixel[][] mtx = new Pixel[height][width];
        int counter = offBits; // [blue byte, green byte, red byte, ...]
        mainLoop:
        for (int i = 0; i < height; i++){ //read certain byte of color
            for (int j = 0; j < width; j++){
//                if (counter == fileSize){
//                    break mainLoop;
//                }
                mtx[i][j] = new Pixel(0, 0, data[counter + 2] & 0xff);
                counter += 3;
            }
            if (padding != 4){
                counter = counter + padding;
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
            if (padding != 4){
                counter = counter + padding;
            }
        }
        ByteArrayInputStream bis = new ByteArrayInputStream(outImg);
        BufferedImage imgOut = ImageIO.read(bis);
        ImageIO.write(imgOut, "bmp", new File("images\\red.bmp"));

        //test
//        System.out.println(width + " " + height + " " + padding);
//        mtx = new Pixel[height][width];
//        counter = offBits; // [blue byte, green byte, red byte, ...]
//        for (int i = 0; i < height; i++){ //read certain byte of color
//            for (int j = 0; j < width; j++){
//
//                mtx[i][j] = new Pixel(data[counter] & 0xff, data[counter + 1] & 0xff,
//                        data[counter + 2] & 0xff);
//                System.out.println(j + " " + i + " " + (data[counter] & 0xff) + " " + (data[counter + 1] & 0xff) + " " + (data[counter + 2] & 0xff));
//                counter += 3;
//                if (padding != 0 && j == width - 1){
//                    counter = counter + padding;
//                }
//            }
//        }
        //
        //---3.GREEN---
        mtx = new Pixel[height][width];
        counter = offBits + 1; // [blue byte, green byte, red byte, ...]
        for (int i = 0; i < height; i++){ //read certain byte of color
            for (int j = 0; j < width; j++){
                mtx[i][j] = new Pixel(0, data[counter] & 0xff, 0);
                counter += 3;
            }
            if (padding != 4){
                counter = counter + padding;
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
            if (padding != 4){
                counter = counter + padding;
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
                mtx[i][j] = new Pixel(data[counter] & 0xff, 0, 0);
                counter += 3;
            }
            if (padding != 4){
                counter = counter + padding;
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
            if (padding != 4){
                counter = counter + padding;
            }
        }
        bis = new ByteArrayInputStream(outImg);
        imgOut = ImageIO.read(bis);
        ImageIO.write(imgOut, "bmp", new File("images\\blue.bmp"));



        //---4---
        int maxRed = 0;
        int maxGreen = 0;
        int maxBlue = 0;

        int minRed = 255;
        int minGreen = 255;
        int minBlue = 255;

        mtx = new Pixel[height][width];
        counter = offBits; // [blue byte, green byte, red byte, ...]

//        int n = Integer.parseInt(Integer.toBinaryString(data[counter]), 2);
//        System.out.println(Integer.toBinaryString(n));
        //int integer = (int) bytes;
        deBugLoop:
        for (int i = 0; i < height; i++){ //read certain byte of color
            for (int j = 0; j < width; j++){
                if ((data[counter] & 0xff)> maxBlue){
                    maxBlue = data[counter] & 0xff;
                }
                if ((data[counter + 1] & 0xff) > maxGreen){
                    maxGreen = data[counter + 1] & 0xff;
                }
                if ((data[counter + 2] & 0xff) > maxRed){
                    maxRed = data[counter + 2] & 0xff;
                }

                if ((data[counter] & 0xff) < minBlue){
                    minBlue = data[counter] & 0xff;
                }
                if ((data[counter + 1] & 0xff) < minGreen){
                    minGreen = data[counter + 1] & 0xff;
                }
                if ((data[counter + 2] & 0xff) < minRed){
                    minRed = data[counter + 2] & 0xff;
                }

                mtx[i][j] = new Pixel(data[counter] & 0xff, data[counter + 1] & 0xff, data[counter + 2] & 0xff);
                //System.out.println(mtx[i][j].toString());
                counter += 3;
//                if (mtx[i][j].red == 179 && mtx[i][j].green == 190 && mtx[i][j].blue == 171){
//                    System.out.println(i + " " + j);
//                }
//                if (mtx[i][j].red == 255){
//                    System.out.println(i + " " + j);
//                    break deBugLoop;
//                }
            }
            if (padding != 4){
                counter = counter + padding;
            }
        }

        Pixel[][] orgMtx = mtx;

        System.out.println("min and max red: " + minRed + " " + maxRed);
        System.out.println("min and max green: " + minGreen + " " + maxGreen);
        System.out.println("min and max blue: " + minBlue + " " + maxBlue);
//        System.out.println(mtx[250][250].toString());

        //calculation of mathematical expectation
        ImageToolkit tk = new ImageToolkit();
        System.out.println("---calculation of mathematical expectation---");
        System.out.println("red: " + tk.mexpet(mtx, height, width, "red"));
        System.out.println("green: " + tk.mexpet(mtx, height, width, "green"));
        System.out.println("blue: " + tk.mexpet(mtx, height, width, "blue"));


        //standard deviation estimate
        System.out.println("---standard deviation estimate---");
        System.out.println("red: " + tk.sigma(mtx, height, width, "red"));
        System.out.println("green: " + tk.sigma(mtx, height, width, "green"));
        System.out.println("blue: " + tk.sigma(mtx, height, width, "blue"));



        //correlation coefficient estimate
        System.out.println("---correlation coefficient estimate---");
        System.out.println("RG: " + tk.correlation(mtx, height, width, "red", "green"));
        System.out.println("RB: " + tk.correlation(mtx, height, width, "red", "blue"));
        System.out.println("BG: " + tk.correlation(mtx, height, width, "blue", "green"));
//        //---5---

        Pixel[][] YCbCrMtx = new Pixel[height][width];
        //--Y--

        double Y = 0;
        mtx = new Pixel[height][width];
        counter = offBits; // [blue byte, green byte, red byte, ...]
        for (int i = 0; i < height; i++){ //read certain byte of color
            for (int j = 0; j < width; j++){
                Y = 0.114*((double) (data[counter] & 0xff))/*blue*/ +
                        0.587*((double) (data[counter + 1] & 0xff))/*green*/ +
                        0.299*((double) (data[counter + 2] & 0xff))/*red*/;
                mtx[i][j] = new Pixel((int) Y, (int) Y, (int) Y);
                YCbCrMtx[i][j] = new Pixel((int) Y, 0, 0);
                counter += 3;
            }
            if (padding != 4){
                counter = counter + padding;
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
            if (padding != 4){
                counter = counter + padding;
            }
        }
        bis = new ByteArrayInputStream(outImg);
        imgOut = ImageIO.read(bis);
        ImageIO.write(imgOut, "bmp", new File("images\\Y.bmp"));

        //--Cb--

        double Cb = 0;
        mtx = new Pixel[height][width];
        counter = offBits; // [blue byte, green byte, red byte, ...]
        for (int i = 0; i < height; i++){ //read certain byte of color
            for (int j = 0; j < width; j++){
                Cb = 0.5643*((double) (data[counter] & 0xff)/*blue*/ - YCbCrMtx[i][j].blue) + 128;
                mtx[i][j] = new Pixel((int) Cb, (int) Cb, (int) Cb);
                YCbCrMtx[i][j] = new Pixel(YCbCrMtx[i][j].blue, (int) Cb, 0);
                counter += 3;
            }
            if (padding != 4){
                counter = counter + padding;
            }
        }

        Pixel[][] orgCbMtx = mtx;
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
            if (padding != 4){
                counter = counter + padding;
            }
        }
        bis = new ByteArrayInputStream(outImg);
        imgOut = ImageIO.read(bis);
        ImageIO.write(imgOut, "bmp", new File("images\\Cb.bmp"));

        //--Cr--

        double Cr = 0;
        mtx = new Pixel[height][width];
        counter = offBits; // [blue byte, green byte, red byte, ...]
        for (int i = 0; i < height; i++){ //read certain byte of color
            for (int j = 0; j < width; j++){
                Cr = 0.7132*((double) (data[counter + 2] & 0xff)/*red*/ - YCbCrMtx[i][j].blue ) + 128;
                mtx[i][j] = new Pixel((int) Cr, (int) Cr, (int) Cr);
                YCbCrMtx[i][j] = new Pixel(YCbCrMtx[i][j].blue, YCbCrMtx[i][j].green, (int) Cr);
                counter += 3;
            }
            if (padding != 4){
                counter = counter + padding;
            }
        }

        Pixel[][] orgCrMtx = mtx;

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
            if (padding != 4){
                counter = counter + padding;
            }
        }
        bis = new ByteArrayInputStream(outImg);
        imgOut = ImageIO.read(bis);
        ImageIO.write(imgOut, "bmp", new File("images\\Cr.bmp"));

        //---5.1---

        System.out.println("---calculation of mathematical expectation---");
        System.out.println("Cr: " + tk.mexpet(YCbCrMtx, height, width, "red"));
        System.out.println("Cb: " + tk.mexpet(YCbCrMtx, height, width, "green"));
        System.out.println("Y: " + tk.mexpet(YCbCrMtx, height, width, "blue"));


        //standard deviation estimate
        System.out.println("---standard deviation estimate---");
        System.out.println("Cr: " + tk.sigma(YCbCrMtx, height, width, "red"));
        System.out.println("Cb: " + tk.sigma(YCbCrMtx, height, width, "green"));
        System.out.println("Y: " + tk.sigma(YCbCrMtx, height, width, "blue"));



        //correlation coefficient estimate
        System.out.println("---correlation coefficient estimate---");
        System.out.println("CrCb: " + tk.correlation(YCbCrMtx, height, width, "red", "green"));
        System.out.println("CrY: " + tk.correlation(YCbCrMtx, height, width, "red", "blue"));
        System.out.println("YCb: " + tk.correlation(YCbCrMtx, height, width, "blue", "green"));


        //---7---

        double G = 0;
        double R = 0;
        double B = 0;
        mtx = new Pixel[height][width];
        counter = offBits; // [blue byte, green byte, red byte, ...]
        mainLoop:
        for (int i = 0; i < height; i++){ //read certain byte of color
            for (int j = 0; j < width; j++){
                G = (double)YCbCrMtx[i][j].blue - 0.714*(double)(YCbCrMtx[i][j].red - 128) - 0.334*(double)(YCbCrMtx[i][j].green - 128);
                R = (double)YCbCrMtx[i][j].blue + 1.402*(double)(YCbCrMtx[i][j].red - 128);
                B = (double)YCbCrMtx[i][j].blue + 1.772*(double)(YCbCrMtx[i][j].green - 128);
                if (G > maxGreen){
                    G = maxGreen;
                }
                if (G < minGreen){
                    G = minGreen;
                }

                if (R > maxRed){
                    R = maxRed;
                }
                if (R < minRed){
                    R = minRed;
                }

                if (B > maxBlue){
                    B = maxBlue;
                }
                if (B < minBlue){
                    B = minBlue;
                }
                mtx[i][j] = new Pixel((int)B, (int)G, (int)R);
                //RGB BGR
                //GBR BRG
                //GRB RBG
                counter += 3;
//                System.out.println(R + " " + minRed + maxRed);
//                System.out.println(G + " " + minGreen + maxGreen);
//                System.out.println(B + " " + minBlue + maxBlue);
//                break mainLoop;
            }
            if (padding != 4){
                counter = counter + padding;
            }
        }

        Pixel[][] restMtx = mtx;

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
            if (padding != 4){
                counter = counter + padding;
            }
        }
        bis = new ByteArrayInputStream(outImg);
        imgOut = ImageIO.read(bis);
        ImageIO.write(imgOut, "bmp", new File("images\\restored.bmp"));
        System.out.println(YCbCrMtx[208][190].toString());
        // 190 208
        //                                   R = 255 G = 129 B = 254
        // R = 179 G = 190 B = 171
        // Y = 184,545 Cb = 120,3565565 Cr = 124,045306
        // R = 179,000519012 G = 189,9 B = 171,000818118

        System.out.println("PSNR(blue): " + tk.psnr(orgMtx, restMtx, height, width, "blue"));
        System.out.println("PSNR(green): " + tk.psnr(orgMtx, restMtx, height, width, "green"));
        System.out.println("PSNR(red): " + tk.psnr(orgMtx, restMtx, height, width, "red"));
        
        //---additional task 3.b---

        Pixel[][][] bitPlans = new Pixel[8][height][width];
        for (int z = 0; z < 8; z++) {
            mtx = new Pixel[height][width];
            for (int i = 0; i < height; i++) { //read certain byte of color
                for (int j = 0; j < width; j++) {
//                    mtx[i][j] = new Pixel(tk.byteFromHighBit(z, tk.getBit((byte)YCbCrMtx[i][j].blue, z)),
//                            tk.byteFromHighBit(z, tk.getBit((byte)YCbCrMtx[i][j].blue, z)),
//                            tk.byteFromHighBit(z, tk.getBit((byte)YCbCrMtx[i][j].blue, z)));
//                    if (z == 0 && (tk.getBit((byte)YCbCrMtx[i][j].blue, z)) == Byte.parseByte("0", 2)){
//                        //System.out.println("true");
//                        mtx[i][j] = new Pixel(0, 0, 0);
//                    } else {
                    mtx[i][j] = new Pixel((int) (tk.getBit((byte)YCbCrMtx[i][j].blue, z) * Math.pow(2, 7 - z)) - 1,
                            (int) (tk.getBit((byte)YCbCrMtx[i][j].blue, z) * Math.pow(2, 7 - z)) - 1,
                            (int) (tk.getBit((byte)YCbCrMtx[i][j].blue, z) * Math.pow(2, 7 - z)) - 1);
                    //}
                }
//            if (padding != 4){
//                counter = counter + padding;
//            }
            }
            bitPlans[z] = mtx;
            outImg = new byte[data.length]; //write header from copy
            for (int i = 0; i < header.length; i++) {
                outImg[i] = header[i];
            }
            counter = offBits; //counter must write every byte to build pixel
            buf = new byte[3];
            for (int i = 0; i < height; i++) { //write image
                for (int j = 0; j < width; j++) {
                    buf = mtx[i][j].toByteArray();
                    outImg[counter] = buf[0];
                    counter++;
                    outImg[counter] = buf[1];
                    counter++;
                    outImg[counter] = buf[2];
                    counter++;
                }
                if (padding != 4) {
                    counter = counter + padding;
                }
            }
            bis = new ByteArrayInputStream(outImg);
            imgOut = ImageIO.read(bis);
            ImageIO.write(imgOut, "bmp", new File("images\\bitPlane" + z + ".bmp"));
        }
        //correlation pairs
        Pixel[][][] bp0x = new Pixel[7][height][width];
//        Pixel[][] bp01mtx = new Pixel[height][width];
//        Pixel[][] bp02mtx = new Pixel[height][width];
//        Pixel[][] bp03mtx = new Pixel[height][width];
//        Pixel[][] bp04mtx = new Pixel[height][width];
//        Pixel[][] bp05mtx = new Pixel[height][width];
//        Pixel[][] bp06mtx = new Pixel[height][width];
//        Pixel[][] bp07mtx = new Pixel[height][width];

        Pixel[][][] bp1x = new Pixel[6][height][width];
//        Pixel[][] bp12mtx = new Pixel[height][width];
//        Pixel[][] bp13mtx = new Pixel[height][width];
//        Pixel[][] bp14mtx = new Pixel[height][width];
//        Pixel[][] bp15mtx = new Pixel[height][width];
//        Pixel[][] bp16mtx = new Pixel[height][width];
//        Pixel[][] bp17mtx = new Pixel[height][width];

        Pixel[][][] bp2x = new Pixel[5][height][width];
//        Pixel[][] bp23mtx = new Pixel[height][width];
//        Pixel[][] bp24mtx = new Pixel[height][width];
//        Pixel[][] bp25mtx = new Pixel[height][width];
//        Pixel[][] bp26mtx = new Pixel[height][width];
//        Pixel[][] bp27mtx = new Pixel[height][width];

        Pixel[][][] bp3x = new Pixel[4][height][width];
//        Pixel[][] bp34mtx = new Pixel[height][width];
//        Pixel[][] bp35mtx = new Pixel[height][width];
//        Pixel[][] bp36mtx = new Pixel[height][width];
//        Pixel[][] bp37mtx = new Pixel[height][width];

        Pixel[][][] bp4x = new Pixel[3][height][width];
//        Pixel[][] bp45mtx = new Pixel[height][width];
//        Pixel[][] bp46mtx = new Pixel[height][width];
//        Pixel[][] bp47mtx = new Pixel[height][width];

        Pixel[][][] bp5x = new Pixel[2][height][width];
//        Pixel[][] bp56mtx = new Pixel[height][width];
//        Pixel[][] bp57mtx = new Pixel[height][width];

        Pixel[][][] bp6x = new Pixel[1][height][width];
       // Pixel[][] bp67mtx = new Pixel[height][width];

        for (int z = 1; z < 8; z++){
            for (int i = 0; i < height; i++){
                for (int j = 0; j < width; j++){
                    bp0x[z-1][i][j] = new Pixel(bitPlans[0][i][j].blue, bitPlans[z][i][j].blue, 0);
                }
            }
        }
        for (int z = 2; z < 8; z++){
            for (int i = 0; i < height; i++){
                for (int j = 0; j < width; j++){
                    bp1x[z-2][i][j] = new Pixel(bitPlans[1][i][j].blue, bitPlans[z][i][j].blue, 0);
                }
            }
        }
        for (int z = 3; z < 8; z++){
            for (int i = 0; i < height; i++){
                for (int j = 0; j < width; j++){
                    bp2x[z-3][i][j] = new Pixel(bitPlans[2][i][j].blue, bitPlans[z][i][j].blue, 0);
                }
            }
        }for (int z = 4; z < 8; z++){
            for (int i = 0; i < height; i++){
                for (int j = 0; j < width; j++){
                    bp3x[z-4][i][j] = new Pixel(bitPlans[3][i][j].blue, bitPlans[z][i][j].blue, 0);
                }
            }
        }
        for (int z = 5; z < 8; z++){
            for (int i = 0; i < height; i++){
                for (int j = 0; j < width; j++){
                    bp4x[z-5][i][j] = new Pixel(bitPlans[4][i][j].blue, bitPlans[z][i][j].blue, 0);
                }
            }
        }
        for (int z = 6; z < 8; z++){
            for (int i = 0; i < height; i++){
                for (int j = 0; j < width; j++){
                    bp5x[z-6][i][j] = new Pixel(bitPlans[5][i][j].blue, bitPlans[z][i][j].blue, 0);
                }
            }
        }
        for (int z = 7; z < 8; z++){
            for (int i = 0; i < height; i++){
                for (int j = 0; j < width; j++){
                    bp6x[z-7][i][j] = new Pixel(bitPlans[6][i][j].blue, bitPlans[z][i][j].blue, 0);
                }
            }
        }

        //correlation coefficient estimate
        System.out.println("---correlation coefficient estimate---");
        System.out.println("--column 1--");
        for (int i = 0; i < bp0x.length; i++) {
            System.out.println("bp0" + (i+1) + ": " + tk.correlation(bp0x[i], height, width, "blue", "green"));
        }
        System.out.println("--column 2--");
        for (int i = 0; i < bp1x.length; i++) {
            System.out.println("bp1" + (i+2) + ": " + tk.correlation(bp1x[i], height, width, "blue", "green"));
        }
        System.out.println("--column 3--");
        for (int i = 0; i < bp2x.length; i++) {
            System.out.println("bp2" + (i+3) + ": " + tk.correlation(bp2x[i], height, width, "blue", "green"));
        }
        System.out.println("--column 4--");
        for (int i = 0; i < bp3x.length; i++) {
            System.out.println("bp3" + (i+4) + ": " + tk.correlation(bp3x[i], height, width, "blue", "green"));
        }
        System.out.println("--column 5--");
        for (int i = 0; i < bp4x.length; i++) {
            System.out.println("bp4" + (i+5) + ": " + tk.correlation(bp4x[i], height, width, "blue", "green"));
        }
        System.out.println("--column 6--");
        for (int i = 0; i < bp5x.length; i++) {
            System.out.println("bp5" + (i+6) + ": " + tk.correlation(bp5x[i], height, width, "blue", "green"));
        }
        System.out.println("--column 7--");
        for (int i = 0; i < bp6x.length; i++) {
            System.out.println("bp6" + (i+7) + ": " + tk.correlation(bp6x[i], height, width, "blue", "green"));
        }

        //---8.a && 11.a---
        //--Cb--
        Pixel[][] t = tk.decA(YCbCrMtx, height, width, 2);
        Pixel[][] decCbMtx = new Pixel[height][width];
        for (int i = 0; i < height; i++){
            for (int j = 0; j < width; j++){
                decCbMtx[i][j] = new Pixel(YCbCrMtx[i][j].green,
                        YCbCrMtx[i][j].green,
                        YCbCrMtx[i][j].green);
                if ((i%2==0)&&(j%2==0)){
                    decCbMtx[i][j].blue = 0;
                    decCbMtx[i][j].green = 0;
                    decCbMtx[i][j].red = 0;
                }
            }
        }
        Pixel[][] dec4CbMtx = new Pixel[height][width];
        for (int i = 0; i < height; i++){
            for (int j = 0; j < width; j++){
                dec4CbMtx[i][j] = new Pixel(YCbCrMtx[i][j].green,
                        YCbCrMtx[i][j].green,
                        YCbCrMtx[i][j].green);
                if ((i%3==0)&&(j%3==0)&&(i > 1 && j > 1)){
                    dec4CbMtx[i][j].blue = 0;
                    dec4CbMtx[i-1][j].blue = 0;
                    dec4CbMtx[i][j-1].blue = 0;
                    dec4CbMtx[i-1][j-1].blue = 0;

                    dec4CbMtx[i][j].green = 0;
                    dec4CbMtx[i-1][j].green = 0;
                    dec4CbMtx[i][j-1].green = 0;
                    dec4CbMtx[i-1][j-1].green = 0;

                    dec4CbMtx[i][j].red = 0;
                    dec4CbMtx[i-1][j].red = 0;
                    dec4CbMtx[i][j-1].red = 0;
                    dec4CbMtx[i-1][j-1].red = 0;
                }
            }
        }

        //--Cr--
        Pixel[][] decCrMtx = new Pixel[height][width];
        for (int i = 0; i < height; i++){
            for (int j = 0; j < width; j++){
                decCrMtx[i][j] = new Pixel(YCbCrMtx[i][j].red,
                        YCbCrMtx[i][j].red,
                        YCbCrMtx[i][j].red);
                if ((i%2==0)&&(j%2==0)){
                    decCrMtx[i][j].blue = 0;
                    decCrMtx[i][j].green = 0;
                    decCrMtx[i][j].red = 0;
                }
            }
        }
        Pixel[][] dec4CrMtx = new Pixel[height][width];
        for (int i = 0; i < height; i++){
            for (int j = 0; j < width; j++){
                dec4CrMtx[i][j] = new Pixel(YCbCrMtx[i][j].red,
                        YCbCrMtx[i][j].red,
                        YCbCrMtx[i][j].red);
                if ((i%3==0)&&(j%3==0)&&(i > 1 && j > 1)){
                    dec4CrMtx[i][j].blue = 0;
                    dec4CrMtx[i-1][j].blue = 0;
                    dec4CrMtx[i][j-1].blue = 0;
                    dec4CrMtx[i-1][j-1].blue = 0;

                    dec4CrMtx[i][j].green = 0;
                    dec4CrMtx[i-1][j].green = 0;
                    dec4CrMtx[i][j-1].green = 0;
                    dec4CrMtx[i-1][j-1].green = 0;

                    dec4CrMtx[i][j].red = 0;
                    dec4CrMtx[i-1][j].red = 0;
                    dec4CrMtx[i][j-1].red = 0;
                    dec4CrMtx[i-1][j-1].red = 0;
                }
            }
        }

        //---8.b---
        //--Cb--
        Pixel[][] decCbAvgMtx = new Pixel[height][width];
        for (int i = 0; i < height; i++){
            for (int j = 0; j < width; j++){
                decCbAvgMtx[i][j] = new Pixel(YCbCrMtx[i][j].green,
                        YCbCrMtx[i][j].green,
                        YCbCrMtx[i][j].green);
                if ((i%2==0)&&(j%2==0)&&(i + 1 < height || i + 1 == height)&&
                        (j + 1 < width || j + 1 == width) && (i - 1 > 0 || i - 1 == 0) &&
                        (j - 1 > 0 || j - 1 == 0))
                {
                    decCbAvgMtx[i][j].green = (int)tk.avg4px(new int[]{YCbCrMtx[i - 1][j].green,
                            YCbCrMtx[i][j - 1].green,
                            YCbCrMtx[i + 1][j].green,
                            YCbCrMtx[i][j + 1].green});
//                    decCbAvgMtx[i][j].red = (int)tk.avg4px(new int[] {YCbCrMtx[i - 1][j].red,
//                            YCbCrMtx[i][j - 1].red,
//                            YCbCrMtx[i + 1][j].red,
//                            YCbCrMtx[i][j + 1].red});
                }
            }
        }
        Pixel[][] dec4CbAvgMtx = new Pixel[height][width];
        for (int i = 0; i < height; i++){
            for (int j = 0; j < width; j++){
                dec4CbAvgMtx[i][j] = new Pixel(YCbCrMtx[i][j].green,
                        YCbCrMtx[i][j].green,
                        YCbCrMtx[i][j].green);
                if ((i + 1 < height /*|| i + 1 == height*/)&&
                        (j + 1 < width /*|| j + 1 == width*/) && (i - 1 > 0 || i - 1 == 0) &&
                        (j - 1 > 0 || j - 1 == 0)){
                    dec4CbAvgMtx[i][j].green = (int)tk.avg4px(new int[]{YCbCrMtx[i - 1][j].green,
                            YCbCrMtx[i][j - 1].green,
                            YCbCrMtx[i + 1][j].green,
                            YCbCrMtx[i][j + 1].green});
                }
                if ((i%3==0)&&(j%3==0)&&(i > 1 && j > 1)){
                    dec4CbAvgMtx[i][j].blue = 0;
                    dec4CbAvgMtx[i-1][j].blue = 0;
                    dec4CbAvgMtx[i][j-1].blue = 0;
                    dec4CbAvgMtx[i-1][j-1].blue = 0;

                    dec4CbAvgMtx[i][j].green = 0;
                    dec4CbAvgMtx[i-1][j].green = 0;
                    dec4CbAvgMtx[i][j-1].green = 0;
                    dec4CbAvgMtx[i-1][j-1].green = 0;

                    dec4CbAvgMtx[i][j].red = 0;
                    dec4CbAvgMtx[i-1][j].red = 0;
                    dec4CbAvgMtx[i][j-1].red = 0;
                    dec4CbAvgMtx[i-1][j-1].red = 0;
                }
            }
        }

        //--Cr--
        Pixel[][] decCrAvgMtx = new Pixel[height][width];
        for (int i = 0; i < height; i++){
            for (int j = 0; j < width; j++){
                decCrAvgMtx[i][j] = new Pixel(YCbCrMtx[i][j].green,
                        YCbCrMtx[i][j].green,
                        YCbCrMtx[i][j].green);
                if ((i%2==0)&&(j%2==0)&&(i + 1 < height || i + 1 == height)&&
                        (j + 1 < width || j + 1 == width) && (i - 1 > 0 || i - 1 == 0) &&
                        (j - 1 > 0 || j - 1 == 0))
                {
                    decCrAvgMtx[i][j].green = (int)tk.avg4px(new int[]{YCbCrMtx[i - 1][j].green,
                            YCbCrMtx[i][j - 1].green,
                            YCbCrMtx[i + 1][j].green,
                            YCbCrMtx[i][j + 1].green});
//                    decCbAvgMtx[i][j].red = (int)tk.avg4px(new int[] {YCbCrMtx[i - 1][j].red,
//                            YCbCrMtx[i][j - 1].red,
//                            YCbCrMtx[i + 1][j].red,
//                            YCbCrMtx[i][j + 1].red});
                }
            }
        }
        Pixel[][] dec4CrAvgMtx = new Pixel[height][width];
        for (int i = 0; i < height; i++){
            for (int j = 0; j < width; j++){
                dec4CrAvgMtx[i][j] = new Pixel(YCbCrMtx[i][j].green,
                        YCbCrMtx[i][j].green,
                        YCbCrMtx[i][j].green);
                if ((i + 1 < height /*|| i + 1 == height*/)&&
                        (j + 1 < width /*|| j + 1 == width*/) && (i - 1 > 0 || i - 1 == 0) &&
                        (j - 1 > 0 || j - 1 == 0)){
                    dec4CrAvgMtx[i][j].green = (int)tk.avg4px(new int[]{YCbCrMtx[i - 1][j].green,
                            YCbCrMtx[i][j - 1].green,
                            YCbCrMtx[i + 1][j].green,
                            YCbCrMtx[i][j + 1].green});
                }
                if ((i%3==0)&&(j%3==0)&&(i > 1 && j > 1)){
                    dec4CrAvgMtx[i][j].blue = 0;
                    dec4CrAvgMtx[i-1][j].blue = 0;
                    dec4CrAvgMtx[i][j-1].blue = 0;
                    dec4CrAvgMtx[i-1][j-1].blue = 0;

                    dec4CrAvgMtx[i][j].green = 0;
                    dec4CrAvgMtx[i-1][j].green = 0;
                    dec4CrAvgMtx[i][j-1].green = 0;
                    dec4CrAvgMtx[i-1][j-1].green = 0;

                    dec4CrAvgMtx[i][j].red = 0;
                    dec4CrAvgMtx[i-1][j].red = 0;
                    dec4CrAvgMtx[i][j-1].red = 0;
                    dec4CrAvgMtx[i-1][j-1].red = 0;
                }
            }
        }


        //---9---

        Pixel[][][] mtxArr = new Pixel[8][height][width];
        mtxArr[0] = decCbMtx;
        mtxArr[1] = decCrMtx;
        mtxArr[2] = decCbAvgMtx;
        mtxArr[3] = decCrAvgMtx;

        mtxArr[4] = dec4CbMtx;
        mtxArr[5] = dec4CrMtx;
        mtxArr[6] = dec4CbAvgMtx;
        mtxArr[7] = dec4CrAvgMtx;

        for (int z = 0; z < 8; z = z + 2) {
            restMtx = new Pixel[height][width];
            for (int i = 0; i < height; i++){
                for (int j = 0; j < width; j++){
                    restMtx[i][j] = new Pixel(YCbCrMtx[i][j].blue, mtxArr[z][i][j].green, mtxArr[z + 1][i][j].red);
                    if ((i > 0 && j > 0) && (restMtx[i][j].green == 0 || restMtx[i][j].red == 0)) {
                        restMtx[i][j] = new Pixel(YCbCrMtx[i][j].blue, mtxArr[z][i][j - 1].green, mtxArr[z + 1][i][j - 1].red);
                    } else {
                        restMtx[i][j] = new Pixel(YCbCrMtx[i][j].blue, YCbCrMtx[i][j].green, YCbCrMtx[i][j].red);
                    }

//                    if (z <= 3) {
//                        restMtx[i][j] = new Pixel(YCbCrMtx[i][j].blue, mtxArr[z][i][j].green, mtxArr[z + 1][i][j].red);
//                        if ((i%2==0)&&(j%2==0)&&(i + 1 < height || i + 1 == height)&&
//                                (j + 1 < width || j + 1 == width) && (i - 1 > 0 || i - 1 == 0) &&
//                                (j - 1 > 0 || j - 1 == 0)/*mtxArr[z][i][j].green == 0 && mtxArr[z][i][j].red == 0 && j > 0*/) {
//                            restMtx[i][j] = new Pixel(YCbCrMtx[i][j].blue, mtxArr[z][i][j - 1].green, mtxArr[z + 1][i][j - 1].red);
//                        }
//                    } else if ((i%3==0)&&(j%3==0)&&(i > 1 && j > 1)){
//                        restMtx[i][j] = new Pixel(YCbCrMtx[i][j].blue, mtxArr[z][i][j - 1].green, mtxArr[z + 1][i][j - 1].red);
//                        restMtx[i][j+1] = new Pixel(YCbCrMtx[i][j+1].blue, mtxArr[z][i][j].green, mtxArr[z + 1][i][j].red);
//                        restMtx[i+1][j] = new Pixel(YCbCrMtx[i+1][j].blue, mtxArr[z][i+1][j - 1].green, mtxArr[z + 1][i+1][j - 1].red);
//                        restMtx[i+1][j+1] = new Pixel(YCbCrMtx[i+1][j+1].blue, mtxArr[z][i+1][j].green, mtxArr[z + 1][i+1][j].red);
//                    }
                }
            }
            //mtx = decMtx;
            G = 0;
            R = 0;
            B = 0;
            mtx = new Pixel[height][width];
            counter = offBits; // [blue byte, green byte, red byte, ...]
            for (int i = 0; i < height; i++) { //read certain byte of color
                for (int j = 0; j < width; j++) {
                    G = (double) restMtx[i][j].blue - 0.714 * (double) (restMtx[i][j].red - 128) - 0.334 * (double) (restMtx[i][j].green - 128);
                    R = (double) restMtx[i][j].blue + 1.402 * (double) (restMtx[i][j].red - 128);
                    B = (double) restMtx[i][j].blue + 1.772 * (double) (restMtx[i][j].green - 128);
                    if (G > maxGreen) {
                        G = maxGreen;
                    }
                    if (G < minGreen) {
                        G = minGreen;
                    }

                    if (R > maxRed) {
                        R = maxRed;
                    }
                    if (R < minRed) {
                        R = minRed;
                    }

                    if (B > maxBlue) {
                        B = maxBlue;
                    }
                    if (B < minBlue) {
                        B = minBlue;
                    }
                    mtx[i][j] = new Pixel((int) B, (int) G, (int) R);
                    //RGB BGR
                    //GBR BRG
                    //GRB RBG
                    counter += 3;
//                System.out.println(R + " " + minRed + maxRed);
//                System.out.println(G + " " + minGreen + maxGreen);
//                System.out.println(B + " " + minBlue + maxBlue);
//                break mainLoop;
                }
                if (padding != 4) {
                    counter = counter + padding;
                }
            }

            outImg = new byte[data.length]; //write header from copy
            for (int i = 0; i < header.length; i++) {
                outImg[i] = header[i];
            }
            counter = offBits; //counter must write every byte to build pixel
            buf = new byte[3];
            for (int i = 0; i < height; i++) { //write image
                for (int j = 0; j < width; j++) {
                    buf = mtx[i][j].toByteArray();
                    outImg[counter] = buf[0];
                    counter++;
                    outImg[counter] = buf[1];
                    counter++;
                    outImg[counter] = buf[2];
                    counter++;
                }
                if (padding != 4) {
                    counter = counter + padding;
                }
            }
            bis = new ByteArrayInputStream(outImg);
            imgOut = ImageIO.read(bis);
            if (z == 0) {
                ImageIO.write(imgOut, "bmp", new File("images\\dec-8.a-Rest.bmp"));
                System.out.println("---8.a---");
                System.out.println("PSNR(original Cb and decRest Cb): " + tk.psnr(orgCbMtx, restMtx, height, width, "green"));
                System.out.println("PSNR(original Cr and decRest Cr): " + tk.psnr(orgCrMtx, restMtx, height, width, "red"));
                System.out.println("PSNR(original Blue and decRest Blue): " + tk.psnr(orgMtx, mtx, height, width, "blue"));
                System.out.println("PSNR(original Green and decRest Green): " + tk.psnr(orgMtx, mtx, height, width, "green"));
                System.out.println("PSNR(original Red and decRest Red): " + tk.psnr(orgMtx, mtx, height, width, "red"));
            }
            if (z == 2) {
                ImageIO.write(imgOut, "bmp", new File("images\\dec4-8.a-Rest.bmp"));
                System.out.println("---8.a for 4---");
                System.out.println("PSNR(original Cb and decRest Cb): " + tk.psnr(orgCbMtx, restMtx, height, width, "green"));
                System.out.println("PSNR(original Cr and decRest Cr): " + tk.psnr(orgCrMtx, restMtx, height, width, "red"));
                System.out.println("PSNR(original Blue and decRest Blue): " + tk.psnr(orgMtx, mtx, height, width, "blue"));
                System.out.println("PSNR(original Green and decRest Green): " + tk.psnr(orgMtx, mtx, height, width, "green"));
                System.out.println("PSNR(original Red and decRest Red): " + tk.psnr(orgMtx, mtx, height, width, "red"));
            }
            if (z == 4) {
                System.out.println("---8.b---");
                System.out.println("PSNR(original Cb and decRest Cb): " + tk.psnr(orgCbMtx, restMtx, height, width, "green"));
                System.out.println("PSNR(original Cr and decRest Cr): " + tk.psnr(orgCrMtx, restMtx, height, width, "red"));
                System.out.println("PSNR(original Blue and decRest Blue): " + tk.psnr(orgMtx, mtx, height, width, "blue"));
                System.out.println("PSNR(original Green and decRest Green): " + tk.psnr(orgMtx, mtx, height, width, "green"));
                System.out.println("PSNR(original Red and decRest Red): " + tk.psnr(orgMtx, mtx, height, width, "red"));
                ImageIO.write(imgOut, "bmp", new File("images\\dec-8.b-Rest.bmp"));
            }
            if (z == 6) {
                System.out.println("---8.b for 4---");
                System.out.println("PSNR(original Cb and decRest Cb): " + tk.psnr(orgCbMtx, restMtx, height, width, "green"));
                System.out.println("PSNR(original Cr and decRest Cr): " + tk.psnr(orgCrMtx, restMtx, height, width, "red"));
                System.out.println("PSNR(original Blue and decRest Blue): " + tk.psnr(orgMtx, mtx, height, width, "blue"));
                System.out.println("PSNR(original Green and decRest Green): " + tk.psnr(orgMtx, mtx, height, width, "green"));
                System.out.println("PSNR(original Red and decRest Red): " + tk.psnr(orgMtx, mtx, height, width, "red"));
                ImageIO.write(imgOut, "bmp", new File("images\\dec4-8.b-Rest.bmp"));
            }
        }


//        decMtx = new Pixel[height][width];
//        for (int i = 0; i < height; i++){
//            for (int j = 0; j < width; j++){
//                decMtx[i][j] = new Pixel(YCbCrMtx[i][j].blue,
//                        YCbCrMtx[i][j].green,
//                        YCbCrMtx[i][j].red);
////                if ((i%2==0)&&(j%2==0)&&(j > 1 || j == 1)){
////                    decMtx[i][j].green = decMtx[i][j - 1].green;
////                    decMtx[i][j].red = decMtx[i][j - 1].red;
////                }
//                if ((i%3==0)&&(j%3==0)&&(i > 2 && j > 2)){
//                    decMtx[i][j].green = YCbCrMtx[i][j+1].green;
//                    decMtx[i-1][j].green = YCbCrMtx[i-1][j+1].green;
//                    decMtx[i][j-1].green = YCbCrMtx[i][j-2].green;
//                    decMtx[i-1][j-1].green = YCbCrMtx[i-1][j-2].green;
//
//                    decMtx[i][j].red = YCbCrMtx[i][j+1].red;
//                    decMtx[i-1][j].red = YCbCrMtx[i-1][j+1].red;
//                    decMtx[i][j-1].red = YCbCrMtx[i][j-2].red;
//                    decMtx[i-1][j-1].red = YCbCrMtx[i-1][j-2].red;
//                }
//            }
//        }

//        Pixel[][] dec4Mtx = new Pixel[height][width];
//        for (int i = 0; i < height; i++){
//            for (int j = 0; j < width; j++){
//                dec4Mtx[i][j] = new Pixel(decMtx[i][j].blue, decMtx[i][j].green, decMtx[i][j].red);
//            }
//        }


        //---13---

        mtx = new Pixel[height][width];
        counter = offBits;
        for (int i = 0; i < height; i++){
            for (int j = 0; j < width; j++){
                mtx[i][j] = new Pixel((data[counter] & 0xff),
                        (data[counter+1] & 0xff), (data[counter+2] & 0xff));
                counter += 3;
            }
        }
        System.out.println();

        //--RED--
        int[] n = new int[256];
        for (int x = 0; x < 256; x++){
            for (int i = 0; i < height; i++){
                for (int j = 0; j < width; j++){
                    if (mtx[i][j].red == x){
                        n[x] = n[x] + 1;
                    }
                }
            }
        }

        double[] p = new double[256];
        for (int i = 0; i < n.length; i++){
            //System.out.println(i);
            for (int j = 0; j < n[i]; j++){
                p[i] = (double) n[i]/(height*width);
            }
        }
        double H = 0;
        for (int i = 0; i < 256; i++){
            H += p[i]*tk.log2(p[i]);
        }
        H = H * -1;
        System.out.println("Entropy for red: " + H);
        //--GREEN--
        n = new int[256];
        for (int x = 0; x < 256; x++){
            for (int i = 0; i < height; i++){
                for (int j = 0; j < width; j++){
                    if (mtx[i][j].green == x){
                        n[x] = n[x] + 1;
                    }
                }
            }
        }

        p = new double[256];
        for (int i = 0; i < n.length; i++){
            //System.out.println(i);
            for (int j = 0; j < n[i]; j++){
                p[i] = (double) n[i]/(height*width);
            }
        }
        H = 0;
        for (int i = 0; i < 256; i++){
            H += p[i]*tk.log2(p[i]);
        }
        H = H * -1;
        System.out.println("Entropy for green: " + H);

        //--BLUE--
        n = new int[256];
        for (int x = 0; x < 256; x++){
            for (int i = 0; i < height; i++){
                for (int j = 0; j < width; j++){
                    if (mtx[i][j].blue == x){
                        n[x] = n[x] + 1;
                    }
                }
            }
        }

        p = new double[256];
        for (int i = 0; i < n.length; i++){
            //System.out.println(i);
            for (int j = 0; j < n[i]; j++){
                p[i] = (double) n[i]/(height*width);
            }
        }
        H = 0;
        for (int i = 0; i < 256; i++){
            H += p[i]*tk.log2(p[i]);
        }
        H = H * -1;
        System.out.println("Entropy for blue: " + H);

        //--Y--
        n = new int[256];
        for (int x = 0; x < 256; x++){
            for (int i = 0; i < height; i++){
                for (int j = 0; j < width; j++){
                    if (YCbCrMtx[i][j].blue == x){
                        n[x] = n[x] + 1;
                    }
                }
            }
        }

        p = new double[256];
        for (int i = 0; i < n.length; i++){
            //System.out.println(i);
            for (int j = 0; j < n[i]; j++){
                p[i] = (double) n[i]/(height*width);
            }
        }
        H = 0;
        for (int i = 0; i < 256; i++){
            H += p[i]*tk.log2(p[i]);
        }
        H = H * -1;
        System.out.println("Entropy for Y: " + H);

        //--Cb--
        n = new int[256];
        for (int x = 0; x < 256; x++){
            for (int i = 0; i < height; i++){
                for (int j = 0; j < width; j++){
                    if (YCbCrMtx[i][j].green == x){
                        n[x] = n[x] + 1;
                    }
                }
            }
        }

        p = new double[256];
        for (int i = 0; i < n.length; i++){
            //System.out.println(i);
            for (int j = 0; j < n[i]; j++){
                p[i] = (double) n[i]/(height*width);
            }
        }
        H = 0;
        for (int i = 0; i < 256; i++){
            H += p[i]*tk.log2(p[i]);
        }
        H = H * -1;
        System.out.println("Entropy for Cb: " + H);

        //--Cr--
        n = new int[256];
        for (int x = 0; x < 256; x++){
            for (int i = 0; i < height; i++){
                for (int j = 0; j < width; j++){
                    if (YCbCrMtx[i][j].red == x){
                        n[x] = n[x] + 1;
                    }
                }
            }
        }

        p = new double[256];
        for (int i = 0; i < n.length; i++){
            //System.out.println(i);
            for (int j = 0; j < n[i]; j++){
                p[i] = (double) n[i]/(height*width);
            }
        }
        H = 0;
        for (int i = 0; i < 256; i++){
            H += p[i]*tk.log2(p[i]);
        }
        H = H * -1;
        System.out.println("Entropy for Cr: " + H);

        //---14---

        //--RED--
        System.out.println("\nEntropy for red(after DPCM mode 1): " + tk.DPCM(mtx, height, width, "red", 1));
        System.out.println("Entropy for red(after DPCM mode 2): " + tk.DPCM(mtx, height, width, "red", 2));
        System.out.println("Entropy for red(after DPCM mode 3): " + tk.DPCM(mtx, height, width, "red", 3));
        System.out.println("Entropy for red(after DPCM mode 4): " + tk.DPCM(mtx, height, width, "red", 4));

        //--GREEN--
        System.out.println("\nEntropy for green(after DPCM mode 1): " + tk.DPCM(mtx, height, width, "green", 1));
        System.out.println("Entropy for green(after DPCM mode 2): " + tk.DPCM(mtx, height, width, "green", 2));
        System.out.println("Entropy for green(after DPCM mode 3): " + tk.DPCM(mtx, height, width, "green", 3));
        System.out.println("Entropy for green(after DPCM mode 4): " + tk.DPCM(mtx, height, width, "green", 4));

        //--Blue--
        System.out.println("\nEntropy for blue(after DPCM mode 1): " + tk.DPCM(mtx, height, width, "blue", 1));
        System.out.println("Entropy for blue(after DPCM mode 2): " + tk.DPCM(mtx, height, width, "blue", 2));
        System.out.println("Entropy for blue(after DPCM mode 3): " + tk.DPCM(mtx, height, width, "blue", 3));
        System.out.println("Entropy for blue(after DPCM mode 4): " + tk.DPCM(mtx, height, width, "blue", 4));

        //--Y--
        System.out.println("\nEntropy for Y(after DPCM mode 1): " + tk.DPCM(YCbCrMtx, height, width, "blue", 1));
        System.out.println("Entropy for Y(after DPCM mode 2): " + tk.DPCM(YCbCrMtx, height, width, "blue", 2));
        System.out.println("Entropy for Y(after DPCM mode 3): " + tk.DPCM(YCbCrMtx, height, width, "blue", 3));
        System.out.println("Entropy for Y(after DPCM mode 4): " + tk.DPCM(YCbCrMtx, height, width, "blue", 4));

        //--Cr--
        System.out.println("\nEntropy for Cb(after DPCM mode 1): " + tk.DPCM(YCbCrMtx, height, width, "green", 1));
        System.out.println("Entropy for Cb(after DPCM mode 2): " + tk.DPCM(YCbCrMtx, height, width, "green", 2));
        System.out.println("Entropy for Cb(after DPCM mode 3): " + tk.DPCM(YCbCrMtx, height, width, "green", 3));
        System.out.println("Entropy for Cb(after DPCM mode 4): " + tk.DPCM(YCbCrMtx, height, width, "green", 4));

        //--Cr--
        System.out.println("\nEntropy for Cr(after DPCM mode 1): " + tk.DPCM(YCbCrMtx, height, width, "red", 1));
        System.out.println("Entropy for Cr(after DPCM mode 2): " + tk.DPCM(YCbCrMtx, height, width, "red", 2));
        System.out.println("Entropy for Cr(after DPCM mode 3): " + tk.DPCM(YCbCrMtx, height, width, "red", 3));
        System.out.println("Entropy for Cr(after DPCM mode 4): " + tk.DPCM(YCbCrMtx, height, width, "red", 4));

    }
}
