package top.zsmile.jvm.base;

//class InnerTest{
//    public static void main(String[] args) {
//        InnerClass innerClass = new InnerClass();
//        new InnerClass.InnerStatic().show();
//    }
//}

public class InnerClass {

    public int iid = 2;
    public static String test2 = "123";

    public static class InnerStatic {
        private int id = 1;
        public String name = "staticClass";

        private static String test = "showTest";

        public void show() {
            System.out.println(test2);
//            System.out.println(iid);// Cannot access outer iid
            System.out.println(id + ":" + name);
        }


    }

    public void printInfo() {
        InnerStatic innerStatic = new InnerStatic();
        innerStatic.show();
//        System.out.println(InnerStatic.id);//Cannot access field by Class
        System.out.println(InnerStatic.test);//
        System.out.println(innerStatic.id + ":" + innerStatic.name);// can access private field id
    }

    public static void main(String[] args) {
        InnerClass innerClass = new InnerClass();
        innerClass.printInfo();
    }
}
