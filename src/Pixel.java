public class Pixel {
    public int blue = 0;
    public int green = 0;
    public int red = 0;

    public Pixel (int blue, int green, int red){
        this.blue = blue;
        this.green = green;
        this.red = red;
    }
    public byte[] toByteArray(){
        byte[] arr = new byte[3];
        arr[0] = (byte) blue;
        arr[1] = (byte) green;
        arr[2] = (byte) red;
        return arr;
    }
    public String toString(){
        return new String("r:" + red + " g:" + green + " b:" + blue);
    }
}
