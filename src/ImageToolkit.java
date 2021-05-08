import java.util.ArrayList;

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

    private double DPCM3px(int[] nums){
        double result = 0;
        for (double d : nums) {
            result += d;
        }
        return result / nums.length;
    }

    public double DPCM(Pixel[][] mtx, int height, int width,
                       String color, int mode){
        double[] DA = new double[((height-1)*(width-1))];
        int counter = 0;
        switch (mode){
            case 1:
                if (color.equals("blue")){
                    for (int i = 1; i < height; i++){
                        for (int j = 1; j < width; j++){
                            DA[counter] = mtx[i][j].blue - mtx[i][j-1].blue;
                            counter++;
                        }
                    }
                }
                if (color.equals("green")){
                    for (int i = 1; i < height; i++){
                        for (int j = 1; j < width; j++){
                            DA[counter] = mtx[i][j].green - mtx[i][j-1].green;
                            counter++;
                        }
                    }
                }
                if (color.equals("red")){
                    for (int i = 1; i < height; i++){
                        for (int j = 1; j < width; j++){
                            DA[counter] = mtx[i][j].red - mtx[i][j-1].red;
                            counter++;
                        }
                    }
                }
                break;
            case 2:
                if (color.equals("blue")){
                    for (int i = 1; i < height; i++){
                        for (int j = 1; j < width; j++){
                            DA[counter] = mtx[i][j].blue - mtx[i-1][j].blue;
                            counter++;
                        }
                    }
                }
                if (color.equals("green")){
                    for (int i = 1; i < height; i++){
                        for (int j = 1; j < width; j++){
                            DA[counter] = mtx[i][j].green - mtx[i-1][j].green;
                            counter++;
                        }
                    }
                }
                if (color.equals("red")){
                    for (int i = 1; i < height; i++){
                        for (int j = 1; j < width; j++){
                            DA[counter] = mtx[i][j].red - mtx[i-1][j].red;
                            counter++;
                        }
                    }
                }
                break;
            case 3:
                if (color.equals("blue")){
                    for (int i = 1; i < height; i++){
                        for (int j = 1; j < width; j++){
                            DA[counter] = mtx[i][j].blue - mtx[i-1][j-1].blue;
                            counter++;
                        }
                    }
                }
                if (color.equals("green")){
                    for (int i = 1; i < height; i++){
                        for (int j = 1; j < width; j++){
                            DA[counter] = mtx[i][j].green - mtx[i-1][j-1].green;
                            counter++;
                        }
                    }
                }
                if (color.equals("red")){
                    for (int i = 1; i < height; i++){
                        for (int j = 1; j < width; j++){
                            DA[counter] = mtx[i][j].red - mtx[i-1][j-1].red;
                            counter++;
                        }
                    }
                }
                break;
            case 4:
                if (color.equals("blue")){
                    for (int i = 1; i < height; i++){
                        for (int j = 1; j < width; j++){
                            DA[counter] = mtx[i][j].blue - DPCM3px(new int[]{mtx[i][j - 1].blue,
                                    mtx[i - 1][j].blue, mtx[i - 1][j - 1].blue});
                            counter++;
                        }
                    }
                }
                if (color.equals("green")){
                    for (int i = 1; i < height; i++){
                        for (int j = 1; j < width; j++){
                            DA[counter] = mtx[i][j].green - DPCM3px(new int[]{mtx[i][j - 1].green,
                                    mtx[i - 1][j].green, mtx[i - 1][j - 1].green});
                            counter++;
                        }
                    }
                }
                if (color.equals("red")){
                    for (int i = 1; i < height; i++){
                        for (int j = 1; j < width; j++){
                            DA[counter] = mtx[i][j].red - DPCM3px(new int[]{mtx[i][j - 1].red,
                                    mtx[i - 1][j].red, mtx[i - 1][j - 1].red});
                            counter++;
                        }
                    }
                }
                break;
        }

        int[] n = new int[256];
        for (int x = 0; x < 256; x++){
            for (int i = 0; i < DA.length; i++){
                if (DA[i] == x){
                    n[x] = n[x] + 1;
                }
            }
        }

        double[] p = new double[256];
        for (int i = 0; i < n.length; i++){
            for (int j = 0; j < n[i]; j++){
                p[i] = (double) n[i]/DA.length;
            }
        }
        double H = 0;
        for (int i = 0; i < 256; i++){
            H += p[i]*this.log2(p[i]);
        }

        return H * -1;
    }

    public Pixel[][] decA(Pixel[][]mtx, int h, int w, int n){
//        Pixel[][] decMtx = new Pixel[h/n][];
        ArrayList<ArrayList<Pixel>> decList = new ArrayList<>(h/n);
        //init
        for (int i = 0; i < h/n; i++){
            ArrayList<Pixel> buf = new ArrayList<>(w/n);
            for (int j = 0; j < w/n; j++) {
                buf.add(new Pixel(0, 0, 0));
            }
            decList.add(buf);
        }

        int c = 0;
        for (int i = 0; i < h; i += n){
            for (int j = 0; j < w; j += n){
                decList.get(c).add(mtx[i][j]);
            }
            c++;
        }


        //rest
        ArrayList<ArrayList<Pixel>> restList = new ArrayList<>(h);
        //init
        for (int i = 0; i < h; i++){
            ArrayList<Pixel> buf = new ArrayList<>(w);
            for (int j = 0; j < w; j++) {
                buf.add(new Pixel(0, 0, 0));
            }
            restList.add(buf);
        }

        for (int i = 0; i < h; i++){
            for (int j = 0; j < w; j++){
                restList.get(i).add(decList.get(i/n).get(j/n));
            }
        }
        Pixel[][] res = new Pixel[restList.size()][];
        for (int i = 0; i < restList.size(); i++){
            ArrayList<Pixel> row = restList.get(i);
            res[i] = row.toArray(new Pixel[row.size()]);
        }
        return res;
    }

}
