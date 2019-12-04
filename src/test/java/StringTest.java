public class StringTest {
    public static void main(String[] args) {
        String str1 = "建筑面积105.7㎡";
        String str2 = str1.replaceAll("建筑面积","").replaceAll("㎡","");
        System.out.println(str2);
    }
}
