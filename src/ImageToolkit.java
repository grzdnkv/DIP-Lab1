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

    public double correlation(Pixel[][] mtx, int height, int width, String color1, String color2){
        double res = 0;
        double[][] mX = new double[height][width];
        double m1 = 0;
        double m2 = 0;
        double sig1 = 0;
        double sig2 = 0;
        if (color1.equals("red") && color2.equals("green")) {
            m1 = mexpet(mtx, height, width, color1);
            m2 = mexpet(mtx, height, width, color2);
            sig1 = sigma(mtx, height, width, color1);
            sig2 = sigma(mtx, height, width, color2);
            for (int i = 0; i < height; i++){
                for (int j = 0; j < width; j++){
                    mX[i][j] = (mtx[i][j].red - m1)*(mtx[i][j].green - m2);
                }
            }
            for (int i = 0; i < height; i++){
                for (int j = 0; j < width; j ++){
                    res += mX[i][j];
                }
            }
            res = res/(width*height);
            res = res/(sig1*sig2);
        }
        if (color1.equals("red") && color2.equals("blue")) {
            m1 = mexpet(mtx, height, width, color1);
            m2 = mexpet(mtx, height, width, color2);
            sig1 = sigma(mtx, height, width, color1);
            sig2 = sigma(mtx, height, width, color2);
            for (int i = 0; i < height; i++){
                for (int j = 0; j < width; j++){
                    mX[i][j] = (mtx[i][j].red - m1)*(mtx[i][j].blue - m2);
                }
            }
            for (int i = 0; i < height; i++){
                for (int j = 0; j < width; j ++){
                    res += mX[i][j];
                }
            }
            res = res/(width*height);
            res = res/(sig1*sig2);
        }
        if (color1.equals("blue") && color2.equals("green")) {
            m1 = mexpet(mtx, height, width, color1);
            m2 = mexpet(mtx, height, width, color2);
            sig1 = sigma(mtx, height, width, color1);
            sig2 = sigma(mtx, height, width, color2);
            for (int i = 0; i < height; i++){
                for (int j = 0; j < width; j++){
                    mX[i][j] = (mtx[i][j].blue - m1)*(mtx[i][j].green - m2);
                }
            }
            for (int i = 0; i < height; i++){
                for (int j = 0; j < width; j ++){
                    res += mX[i][j];
                }
            }
            res = res/(width*height);
            res = res/(sig1*sig2);
        }
        return res;
    }

    public double sigma(Pixel[][] mtx, int height, int width, String color){
        double res = 0;
        double m = 0;
        if (color.equals("red")) {
            m = mexpet(mtx, height, width, color);
            for (int i = 0; i < height; i++){
                for (int j = 0; j < width; j++){
                    res += Math.pow(mtx[i][j].red - m, 2);
                }
            }
            res = res/((width*height) - 1);
            res = Math.sqrt(res);
        }
        if (color.equals("green")){
            m = mexpet(mtx, height, width, color);
            for (int i = 0; i < height; i++){
                for (int j = 0; j < width; j++){
                    res += Math.pow(mtx[i][j].green - m, 2);
                }
            }
            res = res/((width*height) - 1);
            res = Math.sqrt(res);
        }
        if (color.equals("blue")){
            m = mexpet(mtx, height, width, color);
            for (int i = 0; i < height; i++){
                for (int j = 0; j < width; j++){
                    res += Math.pow(mtx[i][j].blue - m, 2);
                }
            }
            res = res/((width*height) - 1);
            res = Math.sqrt(res);
        }
        return res;
    }

    public double mexpet(Pixel[][] mtx, int height, int width, String color){
        double res = 0;
        if (color.equals("red")) {
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    res += mtx[i][j].red;
                }
            }
            res = res / (width * height);
        }
        if (color.equals("green")){
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    res += mtx[i][j].green;
                }
            }
            res = res / (width * height);
        }
        if (color.equals("blue")){
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    res += mtx[i][j].blue;
                }
            }
            res = res / (width * height);
        }
        return res;
    }

    public double psnr(Pixel[][] org, Pixel[][] rest, int height, int width, String color){
        double res = 0;
        for (int i = 0; i < height; i++){
            for (int j = 0; j < width; j++){
                if (color.equals("blue")){
                    res += Math.pow((org[i][j].blue - rest[i][j].blue), 2);
                }
                if (color.equals("green")){
                    res += Math.pow((org[i][j].green - rest[i][j].green), 2);
                }
                if (color.equals("red")){
                    res += Math.pow((org[i][j].red - rest[i][j].red), 2);
                }
            }
        }
        res = (width*height*Math.pow(255, 2)) / res;
        res = 10*Math.log10(res);
        return res;
    }

    public double avg4px(int[] nums){
        double result = 0;
        for (double d : nums) {
            result += d;
        }
        return result / nums.length;
    }

    public byte getBit(byte ID, int position)
    {
        return (byte) ((ID >> position) & 1);
    }

    public byte byteFromHighBit(int highBit, byte bit){
        StringBuilder sb = new StringBuilder();
        sb.append(bit);
        for (int i = 0; i < highBit; i++){
            sb.append("0");
        }
        return (byte)Integer.parseInt(sb.toString(), 2);
    }

    public double log2(double N)
    {

        // calculate log2 N indirectly
        // using log() method

        double result = (Math.log(N) / Math.log(2));
        if (N == 0){
            return 0;
        }
        return result;
    }

}
