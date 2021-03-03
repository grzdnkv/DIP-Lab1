public class Pixel {
    public byte blue = 0;
    public byte green = 0;
    public byte red = 0;

    public Pixel (byte blue, byte green, byte red){
        this.blue = blue;
        this.green = green;
        this.red = red;
    }
    public byte[] toByteArray(){
        byte[] arr = new byte[3];
        arr[0] = blue;
        arr[1] = green;
        arr[2] = red;
        return arr;
    }
}
