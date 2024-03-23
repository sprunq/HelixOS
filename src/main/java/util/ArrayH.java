package util;

public class ArrayH {
    public static void reverse(char[] a) {
        int i = a.length - 1;
        int j = 0;
        while (i > j) {
            char temp = a[i];
            a[i] = a[j];
            a[j] = temp;
            i--;
            j++;
        }
    }
}
