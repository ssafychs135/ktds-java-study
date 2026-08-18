# 도서관 관리 프로그램 (library)

## 파일 구조

```
library/
├── LibraryMain.java          프로그램 시작점, 메뉴 흐름            162줄
├── LibraryView.java          화면 출력 전담                       133줄
├── InputUtil.java            터미널 입력 전담                      64줄
│
├── Library.java              도서관. 업무 규칙 9개                 257줄
├── Book.java                 도서                                 284줄
├── Member.java               회원                                 190줄
├── LibraryFileStore.java     CSV 읽기/쓰기                        109줄
├── LibraryException.java     업무 규칙 위반 예외                    17줄
│
├── books.csv                 도서 목록
├── disposed_books.csv        폐기됐지만 아직 대여 중인 도서
└── members.csv               회원 목록
```

## MVC Model 2

| MVC 역할 | 담당 클래스 | 하는 일 |
|---|---|---|
| **Model** | `Library` `Book` `Member` `LibraryFileStore` | 데이터와 그 데이터를 다루는 규칙 전체 |
| **View** | `LibraryView` `InputUtil` | 화면 출력과 터미널 입력 |
| **Controller** | `LibraryMain` | 메뉴 번호를 받아 Model과 View를 조율 |


| 클래스 | `System.out` 개수 |
|---|---|
| `LibraryView` | 30 |
| `InputUtil` | 7 (프롬프트·검증 안내) |
| `LibraryMain` | **0** |
| `Library` `Book` `Member` `LibraryFileStore` | **0** |

```java
private static void rentBook(Library library, LocalDate today) {
    LibraryView.printTitle("도서 대여");                          // View

    String memberName = InputUtil.nextText("회원명 : ");           // View
    String isbn = InputUtil.nextText("대여할 도서의 ISBN : ");

    Book book = library.rentBook(memberName, isbn, today);        // Model

    LibraryFileStore.save(library);                               // Model
    LibraryView.printRentResult(book);                            // View
}
```

## Has-A 구조

```
Library ──has-a──> List<Book>     도서 목록
   │
   ├────has-a──> List<Book>       폐기 도서 (아직 대여 중인 것만)
   │
   └────has-a──> List<Member>     회원 목록
                     │
                     └──has-a──> List<Book>   대여 도서 목록
```

