public class Test{

    public void boom(String greet){
        System.out.printf("Hello World : %s\n", greet);
    }

    public static void main(String[] args){
        // these won't even fire...
        Test t = new Test();
        t.boom("Boom!");
    }
}