public class HelloNumbers {
    public static void main(String[] args) {
        int x = 0;
        int num = 0;
        while (x < 10) {
            num+= x;
            System.out.print(num + " ");
            x = x+1;
        }
    }
}