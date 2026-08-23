package lotto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class LottoMain {
    public static int[] draw() {
        List<Integer> numbers = 
        		IntStream.rangeClosed(1, 45) // IntStream
                		 .boxed() // Steam<Integer>
                		 .collect(Collectors.toCollection(ArrayList::new)); // ArrayList<Integer>
        
        // 로또 번호들을 무작위로 섞어준다.
        Collections.shuffle(numbers);
        
        // 만들어진 ArrayList 에서 6개를 뽑아서 int 배열로 변환하여 반환
        return numbers.stream()
	                  .limit(6) //Steam<Integer>
	                  .mapToInt(Integer::intValue) // IntStream
	                  .sorted() // IntStream
	                  .toArray(); // int[]
    }
	
	public static void main(String[] args) {
		for (int i = 0; i < 10; i++) {
			System.out.println(i+"회차 ==> "+Arrays.toString(draw()));
		}
	}
}
