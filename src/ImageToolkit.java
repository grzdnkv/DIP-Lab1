public class ImageToolkit {
    public int getRed(int rgb){
        return rgb >> 16 & 0xff;
    }

    public int getGreen(int rgb){
        return rgb >> 8 & 0xff;
    }

    public int getBlue(int rgb){
        return rgb & 0xff;
    }
}
