package com.ktdsuniversity.edu.oop.restaurant;

public class RestaurantMain {

	public static void main(String[] args) {

		Restaurant restaurant = new Restaurant("웰빙 무한 레스토랑");

		restaurant.printMenu();

		// 손님: 이름, 나이, 부모님 동반 여부
		Customer 김성인 = new Customer("김성인", 30, false);
		Customer 이청소 = new Customer("이청소", 17, false);
		Customer 박동반 = new Customer("박동반", 17, true);

		System.out.println("\n===== 1. 성인 손님의 식사 =====");
		restaurant.serveFood(김성인, "육고기");
		restaurant.serveFood(김성인, "생선");
		restaurant.serveAlcohol(김성인, "소주");
		restaurant.serveAlcohol(김성인, "맥주");
		김성인.printStatus();

		System.out.println("\n===== 2. 미성년자는 주류를 마실 수 없다 =====");
		restaurant.serveFood(이청소, "육고기");
		restaurant.serveAlcohol(이청소, "소주");
		이청소.printStatus();

		System.out.println("\n===== 3. 부모님과 동반한 미성년자는 주류를 마실 수 있다 =====");
		restaurant.serveAlcohol(박동반, "맥주");
		박동반.printStatus();

		System.out.println("\n===== 4. 취기가 60%를 초과하면 더 이상 마실 수 없다 =====");
		restaurant.serveAlcohol(김성인, "위스키");
		restaurant.serveAlcohol(김성인, "꼬냑");
		restaurant.serveAlcohol(김성인, "소주");
		김성인.printStatus();

		System.out.println("\n===== 5. 채소와 과일은 취기를 떨어트린다 =====");
		restaurant.serveFood(김성인, "채소");
		restaurant.serveFood(김성인, "과일");
		restaurant.serveFood(김성인, "채소");
		김성인.printStatus();

		System.out.println("\n===== 6. 포만감이 100%를 초과하면 더 이상 먹을 수 없다 =====");
		restaurant.serveFood(김성인, "육고기");
		restaurant.serveFood(김성인, "육고기");
		restaurant.serveFood(김성인, "육고기");
		restaurant.serveFood(김성인, "육고기");
		restaurant.serveFood(김성인, "생선");
		김성인.printStatus();

		System.out.println("\n===== 7. 맥주를 제외한 주류는 포만감을 떨어트린다 =====");
		restaurant.serveAlcohol(박동반, "소주");
		restaurant.serveAlcohol(박동반, "맥주");
		박동반.printStatus();

		System.out.println("\n===== 8. 메뉴에 없는 음식 =====");
		restaurant.serveFood(김성인, "떡볶이");
		restaurant.serveAlcohol(김성인, "막걸리");

		System.out.println("\n===== 최종 상태 =====");
		김성인.printStatus();
		이청소.printStatus();
		박동반.printStatus();
	}

}
