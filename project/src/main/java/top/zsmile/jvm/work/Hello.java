package top.zsmile.jvm.work;

/**
 * work1
 * <p>
 * javac -g Hello.java
 * javap -c -verbose Hello.class > Hello.txt
 */
public class Hello {

    private static int[] iNumber = {-1, 1, 5, 6, 128, 129};

    public static int multiplyNum(int num, int mn) {
        return num * mn;
    }

    private static int divNum(int num, int mn) {
        return num / mn;
    }

    protected static int addNum(int num, int mn) {
        return num + mn;
    }

    protected static int subNum(int num, int mn) {
        return num - mn;
    }

    public static void main(String[] args) {
        int sum = 0;
        System.out.println("iNumber, Index from small to big");
        for (int i : iNumber) {
            sum = addNum(sum, i);
        }

        System.out.println("sum * 3");
        sum = multiplyNum(sum, 3);

        System.out.println("sum / 2");
        sum = divNum(sum, 2);

        System.out.println("sum - 1024");
        sum = subNum(sum, 1024);

        if (sum > 0) {
            System.out.println(sum + " gt zero");
        }


        System.out.println("iNumber, Index from big to small");
        for (int i = iNumber.length; i >= 0; i--) {
            sum = addNum(sum, i);
            if (iNumber[i] > 100) {
                break;
            }
        }

        double dsum = sum;
        long lsum = sum;
        float fsum = sum;
        byte bsum = (byte) sum;
        char csum = (char) sum;


    }


}
