import java.util.Arrays;

public class MethodPractice {

    // 1. 정수 2개를 받아 값을 출력
    public static void printValues(int a, int b) {
        System.out.println("1) a = " + a + ", b = " + b);
    }

    // 2. 정수 2개를 받아 합계를 출력
    public static void printSum(int a, int b) {
        System.out.println("2) " + a + " + " + b + " = " + (a + b));
    }

    // 3. 정수 3개를 받아 합계를 반환
    public static int sum(int a, int b, int c) {
        return a + b + c;
    }

    // 4. 정수 2개를 받아 나누기 결과(실수)를 출력
    public static void printDivide(int a, int b) {
        if (b == 0) {
            System.out.println("4) 0으로 나눌 수 없습니다.");
            return;
        }
        System.out.println("4) " + a + " / " + b + " = " + ((double) a / b));
    }

    // 5. 정수 2개를 받아 나누기 결과(실수)를 반환
    public static double divide(int a, int b) {
        if (b == 0) {
            return 0.0;
        }
        return (double) a / b;
    }

    // 6. 실수 1개와 정수 1개를 받아 소수점 이하 자리수를 변경하여 반환
    public static double round(double value, int digits) {
        if (digits < 0) {
            digits = 0;
        }
        double factor = Math.pow(10, digits);
        return Math.round(value * factor) / factor;
    }

    // 7. 실수 2개 중 가장 큰 수 반환
    public static double max(double a, double b) {
        if (a > b) {
            return a;
        }
        return b;
    }

    // 8. 실수 4개 중 가장 작은 수 반환
    public static double min(double a, double b, double c, double d) {
        double m = a;
        if (b < m) {
            m = b;
        }
        if (c < m) {
            m = c;
        }
        if (d < m) {
            m = d;
        }
        return m;
    }

    // 9. 정수 배열에서 2, 5, 8의 배수만 출력
    public static void printMultiples(int[] arr) {
        System.out.print("9) ");
        for (int n : arr) {
            boolean isMultiple = false;
            if (n % 2 == 0) {
                isMultiple = true;
            }
            if (n % 5 == 0) {
                isMultiple = true;
            }
            if (n % 8 == 0) {
                isMultiple = true;
            }
            if (isMultiple) {
                System.out.print(n + " ");
            }
        }
        System.out.println();
    }

    // 10. 문자열을 정수만큼 반복 출력
    public static void repeat(String str, int count) {
        System.out.print("10) ");
        for (int i = 0; i < count; i++) {
            System.out.print(str);
        }
        System.out.println();
    }

    // 11. 정수 1개를 받아 구구단 출력
    public static void printGugudan(int dan) {
        System.out.println("11) " + dan + "단");
        for (int i = 1; i <= 9; i++) {
            System.out.println("   " + dan + " x " + i + " = " + (dan * i));
        }
    }

    // 12. 4부터 n까지의 소수만 출력
    public static void printPrimes(int n) {
        System.out.print("12) ");
        for (int i = 4; i <= n; i++) {
            if (isPrime(i)) {
                System.out.print(i + " ");
            }
        }
        System.out.println();
    }

    private static boolean isPrime(int n) {
        if (n < 2) {
            return false;
        }
        for (int i = 2; i <= Math.sqrt(n); i++) {
            if (n % i == 0) {
                return false;
            }
        }
        return true;
    }

    // 13. 배열과 인덱스를 받아 값 반환 (범위 밖이면 0)
    public static int getValue(int[] arr, int index) {
        if (arr == null) {
            return 0;
        }
        if (index < 0) {
            return 0;
        }
        if (index >= arr.length) {
            return 0;
        }
        return arr[index];
    }

    // 14. 배열에서 가장 처음 나오는 3의 배수 반환 (없으면 -1)
    public static int firstMultipleOfThree(int[] arr) {
        for (int n : arr) {
            if (n % 3 == 0) {
                return n;
            }
        }
        return -1;
    }

    // 15. 두 배열의 중복값만 출력
    public static void printDuplicates(int[] a, int[] b) {
        System.out.print("15) ");
        for (int x : a) {
            for (int y : b) {
                if (x == y) {
                    System.out.print(x + " ");
                    break;
                }
            }
        }
        System.out.println();
    }

    // 16. 두 배열에서 중복되지 않는 값만 출력
    public static void printUniques(int[] a, int[] b) {
        System.out.print("16) ");
        printOnlyIn(a, b);
        printOnlyIn(b, a);
        System.out.println();
    }

    private static void printOnlyIn(int[] src, int[] other) {
        for (int x : src) {
            boolean found = false;
            for (int y : other) {
                if (x == y) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                System.out.print(x + " ");
            }
        }
    }

    // 17. 배열의 모든 값을 2배로 만듦 (반환 없음)
    public static void doubleAll(int[] arr) {
        for (int i = 0; i < arr.length; i++) {
            arr[i] = arr[i] * 2;
        }
    }

    public static void main(String[] args) {
        // 1. 정수 2개 값 출력
        printValues(10, 20);

        // 2. 정수 2개 합계 출력
        printSum(10, 20);

        // 3. 정수 3개 합계 반환
        System.out.println("3) 1 + 2 + 3 = " + sum(1, 2, 3));

        // 4. 나누기 결과(실수) 출력
        printDivide(10, 4);

        // 5. 나누기 결과(실수) 반환
        System.out.println("5) 10 / 4 = " + divide(10, 4));

        // 6. 소수점 자리수 변경 반환
        System.out.println("6) " + round(10.33333333, 2));
        System.out.println("6) " + round(10.33333333, 3));
        System.out.println("6) " + round(10.33333333, 1));
        System.out.println("6) " + round(10.33333333, 0));

        // 7. 실수 2개 중 최댓값 반환
        System.out.println("7) max = " + max(3.14, 2.71));

        // 8. 실수 4개 중 최솟값 반환
        System.out.println("8) min = " + min(3.14, 2.71, 1.41, 1.73));

        // 9. 2, 5, 8의 배수만 출력
        int[] arr = {1, 2, 3, 4, 5, 8, 10, 15, 16, 21};
        printMultiples(arr);

        // 10. 문자열 반복 출력
        repeat("KTDS ", 3);

        // 11. 구구단 출력
        printGugudan(7);

        // 12. 4부터 n까지 소수 출력
        printPrimes(30);

        // 13. 인덱스 값 반환 (범위 밖이면 0)
        int[] arr2 = {10, 20, 30, 40, 50};
        System.out.println("13) index 2 -> " + getValue(arr2, 2));
        System.out.println("13) index 5 -> " + getValue(arr2, 5));
        System.out.println("13) index -1 -> " + getValue(arr2, -1));
        System.out.println("13) index 7 -> " + getValue(arr2, 7));

        // 14. 가장 처음 나오는 3의 배수 반환 (없으면 -1)
        System.out.println("14) " + firstMultipleOfThree(new int[]{4, 5, 9, 12}));
        System.out.println("14) " + firstMultipleOfThree(new int[]{1, 2, 4, 5}));

        // 15. 두 배열의 중복값 출력
        int[] a = {1, 2, 3, 4, 5};
        int[] b = {9, 7, 454, 1, 2, 3};
        printDuplicates(a, b);
        printDuplicates(new int[]{1, 2, 3, 4, 5}, new int[]{1, 2, 3, 4, 5});

        // 16. 두 배열의 고유값 출력
        printUniques(a, b);
        printUniques(new int[]{1, 2, 3, 4, 5}, new int[]{1, 2, 3, 4, 5});

        // 17. 모든 값을 2배로 만든 뒤 출력
        int[] c = {1, 2, 3, 4, 5};
        doubleAll(c);
        System.out.println("17) " + Arrays.toString(c));
    }
}
