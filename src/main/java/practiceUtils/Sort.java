package practiceUtils;


import java.util.Arrays;

//练习各类排序算法
public class Sort {
    public static void main(String[] args) {
        int[] array1 = new int[]{10,9,8,7,5,4,3};
        System.out.println(Arrays.toString(quickSort(array1,0,array1.length-1)));
    }
    //快速排序
    public static int[] quickSort(int arr[],int start,int end){
        int pivot = arr[start];
        int i = start;
        int j = end;
        while (i<j) {
            while ((i<j)&&(arr[j]>pivot)) {
                j--;
            }
            while ((i<j)&&(arr[i]<pivot)) {
                i++;
            }
            if ((arr[i]==arr[j])&&(i<j)) {
                i++;
            } else {
                int temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
            }
        }
        if (i-1>start) arr=quickSort(arr,start,i-1);
        if (j+1<end) arr=quickSort(arr,j+1,end);
        return arr;
    }
}
