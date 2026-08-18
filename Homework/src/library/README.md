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

**Model 2**에서는 Controller가 흐름을 주도하고, **Model은 View의 존재를 모른다.** 원조 MVC(Smalltalk)에서 Model이 바뀌면 Observer로 View에 직접 알리는 것과 다른 점이다.

이 원칙이 지켜졌는지는 `System.out` 이 어디에 있는지로 확인할 수 있다.

| 클래스 | `System.out` 개수 |
|---|---|
| `LibraryView` | 30 |
| `InputUtil` | 7 (프롬프트·검증 안내) |
| `LibraryMain` | **0** |
| `Library` `Book` `Member` `LibraryFileStore` | **0** |

Model에 출력 코드가 하나도 없다. 즉 `Library` 는 자기 결과가 화면에 찍히는지 파일로 나가는지 알지 못한다.

`LibraryMain` 도 0인 이유는 출력을 전부 `LibraryView` 에 위임했기 때문이다. Controller는 "무엇을 어떤 순서로 하는가"만 담당한다.

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

회원의 대여 목록은 도서관 도서 목록과 **같은 `Book` 객체를 참조**한다. 복사본이 아니므로 대여 상태 변경이 양쪽에 함께 반영된다.

이 성질 덕분에 폐기 처리가 자연스럽게 동작한다. `library.books.remove(book)` 을 해도 회원이 들고 있는 참조는 그대로 살아 있어서, "도서 목록에서는 빠졌지만 회원은 아직 대여 중"인 상태가 만들어진다.

