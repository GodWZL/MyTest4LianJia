package practiceUtils;

//练习各类查找算法
public class Locating {
    public static void main(String[] args) {
        int[] array1 = new int[]{10, 9, 8, 7, 5, 4, 3};

        System.out.println(二分查找法(array1, 10));


    }

    /**
     * 二分查找法
     */
    public static int 二分查找法(int[] arr, int number) {
        //存在bug，当存在数字超过右边界，此时会陷入死循环中
        int index = -1;
        int temp = 0;
        int leftIndex = 0;
        int rightIndex = arr.length - 1;
        while (leftIndex != rightIndex) {
            temp = (leftIndex + rightIndex) / 2;
            if (arr[temp] == number) {
                index = temp;
                break;
            } else if (arr[temp] < number) {
                rightIndex = temp;
            } else {
                leftIndex = temp;
            }
        }
        return index;
    }
}
