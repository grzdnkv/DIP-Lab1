import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        BufferedImage img =
                ImageIO.read(new File("images\\kodim17.png"));
        ImageIO.write(img, "bmp", new File("images\\kodim17.bmp")); //converting to bmp
        img = ImageIO.read(new File("images\\kodim17.bmp"));
        int height = img.getHeight();
        int width = img.getWidth();
        int red = 0;
        int green = 0;
        int blue = 0;
        int rgb = 0;

        //---3---
        ImageToolkit itk = new ImageToolkit();

        //---3.RED---
        BufferedImage imgOutRed = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int h = 0; h < height; h++){
            for (int w = 0; w < width; w++){
                rgb = img.getRGB(w, h); //from source
                red = itk.getRed(rgb);
                Color color = new Color(red, 0, 0);
                rgb = color.getRGB();
                imgOutRed.setRGB(w, h, rgb);
            }
        }
        ImageIO.write(imgOutRed, "bmp", new File("images\\red.bmp"));

        //---3.GREEN---
        BufferedImage imgOutGreen = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int h = 0; h < height; h++){
            for (int w = 0; w < width; w++){
                rgb = img.getRGB(w, h); //from source
                green = itk.getGreen(rgb);
                Color color = new Color(0, green, 0);
                rgb = color.getRGB();
                imgOutGreen.setRGB(w, h, rgb);
            }
        }
        ImageIO.write(imgOutGreen, "bmp", new File("images\\green.bmp"));

        //---3.BLUE---
        BufferedImage imgOutBlue = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int h = 0; h < height; h++){
            for (int w = 0; w < width; w++){
                rgb = img.getRGB(w, h); //from source
                blue = itk.getBlue(rgb);
                Color color = new Color(0, 0, blue);
                rgb = color.getRGB();
                imgOutBlue.setRGB(w, h, rgb);
            }
        }
        ImageIO.write(imgOutBlue, "bmp", new File("images\\blue.bmp"));

    }
}
