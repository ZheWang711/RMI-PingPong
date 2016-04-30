import java.net.*;


class Getip {
    public static void main(String[] args) {
        try{
            System.out.println(Inet4Address.getLocalHost().getHostAddress());
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
