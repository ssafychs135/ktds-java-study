package workflow;

public class Homwork {

	public static void doMath(int num1, int num2) {
		
		int addResult = num1 + num2;
		int minusResult = num1 - num2;
		double divResult = num1/(double)num2;
		int multResult = num1*num2;
		
		System.out.println(addResult);
		System.out.println(minusResult);
		System.out.println(multResult);
		System.out.println(divResult);

	}
	
	public static boolean isEven(int num) {
		
		return num%2 == 0;
	}
	
	public static int doReturnMax(int num1, int num2, int num3) {
		
		return Math.max(num1, Math.max(num2, num3));
	}
	
	public static boolean isPrime(int num) {
	    if (num < 2) return false;
	    for (int i = 2; i * i <= num; i++) {
	        if (num % i == 0) return false;
	    }
	    return true;
	}
	
	public static void main(String[] args) {
		//1. 정수 두 개를 받아서 사칙연산(+,-, X, /) 의 결과를 출력하는 메소드를 만들고 호출해보세요.
		doMath(123123,12343);
		
		// 2. 정수 한 개를 받아서 짝수라면 true를, 홀수라면 false를 반환하는 메소드를 만들고 호출해보세요.
		System.out.println(isEven(-13));
		
		// 3. 정수 세 개를 받아서 가장 큰 수를 출력하는 메소드를 만들고 호출해보세요.
		System.out.println(doReturnMax(222999993, 1111111, 136543145));
		// 4. 정수 한 개를 받아서 소수(prime number)라면 true를 아니라면 false를 반환하는 메소드를 만들고 호출해 보세요.
		System.out.println(isPrime(9999999));

	}

}
