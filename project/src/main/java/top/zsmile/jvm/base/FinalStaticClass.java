package top.zsmile.jvm.base;

public class FinalStaticClass {
    public static void main(String[] args) {
        System.out.println(Price.P.Price);//result:-2.7
    }
}


class Price {
    /**
     * method1: give apple add final keyword, result:17.3;
     * method2: Swap 18 to 19, result:17.3;
     */
    static Price P = new Price(2.7);
    final static double apple = 20;
    double Price;

    public Price(double orange) {
        Price = apple - orange;
    }
}
