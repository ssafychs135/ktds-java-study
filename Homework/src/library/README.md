# 도서관 관리 프로그램 (library)

도서관이 도서와 회원을 관리하는 콘솔 프로그램. 대여·반납·검색·폐기·연체 관리를 제공하고 모든 상태를 CSV 파일에 저장한다.

- 요구 문법: **Stream API**, **Has-A(포함) 관계**, **파일 입출력**
- 실행: `LibraryMain` 우클릭 → `Run As > Java Application`

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

---

## 메서드 설명

### LibraryMain — Controller

| 메서드 | 하는 일 |
|---|---|
| `main(args)` | 파일에서 도서관을 읽고 메뉴 루프를 돈다. `LibraryException` 을 잡아 안내만 하고 계속 진행한다 |
| `runMenu(library, menuNo, today)` | 메뉴 번호에 맞는 기능을 호출한다 |
| `addBook(library, today)` | 도서 정보를 입력받아 입고시키고 저장한다 |
| `disposeOldBooks(library, today)` | 폐기를 실행하고, 폐기된 것이 있을 때만 저장한다 |
| `searchBooks(library)` | 검색 기준과 검색어를 입력받아 결과를 넘긴다 |
| `rentBook(library, today)` | 회원명·ISBN을 받아 대여시키고 저장한다 |
| `returnBook(library, today)` | 대여 목록을 보여준 뒤 관리번호를 받아 반납시킨다 |

### Library — 업무 규칙 (Model)

| 메서드 | 규칙 |
|---|---|
| `addBook(...)` | 신규 입고. 관리번호를 `기존 최대값 + 1` 로 자동 부여 |
| `nextManagementNo()` | 기존 최대 번호 + 1. 폐기 도서 번호까지 살펴 재사용을 막는다 |
| `disposeOldBooks(today)` | 출판 후 햇수로 10년 지난 도서를 폐기. 대여 중이면 `disposedBooks` 로 옮긴다 |
| `findDueOrOverdueMembers(today)` | 반납기간 도래·초과 회원. 폐기 도서는 판단에서 제외하므로 **폐기 도서만 빌린 회원은 조회되지 않는다** |
| `findPopularBooks(limit)` | 총 대여횟수 내림차순 상위 N권 |
| `findUnpopularBooks(limit)` | 총 대여횟수 오름차순 하위 N권 |
| `findHabitualOverdueMembers()` | 초과 횟수 3회 이상 회원 |
| `searchBooks(type, keyword)` | 출판사(1)/저자(2)/장르(3)로 검색. **같은 ISBN은 1권만** 남긴다 |
| `rentBook(memberName, isbn, today)` | 같은 ISBN 중 대여 가능한 첫 권을 배정. 초과 3회 이상이면 거부 |
| `returnBook(memberName, managementNo, today)` | 반납. 연체 시 초과 횟수 +1, 벌금 `연체일수 × 500원`. **폐기 도서는 둘 다 없음**. 부과된 벌금을 반환 |
| `findMember(name)` | 회원 조회. 없으면 `LibraryException` |

### Book — 도서 (Model)

| 메서드 | 하는 일 |
|---|---|
| `getDueDate()` | 반납 예정일. 저장하지 않고 `대여일 + 7일` 로 계산한다 |
| `isDisposalTarget(today)` | 폐기 대상 판단. "햇수로 10년"이므로 날짜가 아니라 **연도 차이**로 센다 |
| `isDueSoon(today)` | 반납기간 도래 판단. `예정일 - 2일 <= today <= 예정일` |
| `isOverdue(today)` | 반납 예정일이 지났는가 |
| `getOverdueDays(today)` | 연체 일수. 연체가 아니면 0 |
| `rentTo(memberName, today)` | 대여 상태로 전환. 총 대여횟수 증가, 이전 반납 기록 초기화 |
| `returnBy(today)` | 반납 상태로 전환. 실제 반납일 기록 |
| `toCsv()` / `parse(line)` | CSV 한 줄과 객체를 서로 변환 |
| `dateToText()` / `textToDate()` | `null` 날짜와 빈 칸을 서로 변환 |
| `toSummary()` | 목록용 한 줄 (관리번호·대여 상태 포함) |
| `toBookInfo()` | 검색 결과용 한 줄 (관리번호·대여 상태 제외) |

### Member — 회원 (Model)

| 메서드 | 하는 일 |
|---|---|
| `canRent()` | 대여 가능 여부. 초과 횟수 3회 미만 |
| `isHabitualOverdue()` | 상습 미반납 여부. 초과 횟수 3회 이상 |
| `addRentedBook()` / `removeRentedBook()` | 대여 목록에 추가·제거 |
| `findRentedBook(managementNo)` | 대여 중인 도서를 관리번호로 찾는다. 없으면 `null` |
| `chargeOverdue(overdueDays)` | 벌금을 누적하고 초과 횟수를 1 올린다. 부과액 반환 |
| `toCsv()` / `parse(line)` | CSV 변환. **대여 목록은 저장하지 않는다** — 아래 "정규화" 참고 |
| `toSummary()` | 목록용 한 줄 |
| `toOverdueSummary(today, disposedBooks)` | 대여 중인 각 도서의 상태(연체/도래/폐기됨)를 함께 보여준다 |

### LibraryFileStore — 파일 입출력 (Model)

| 메서드 | 하는 일 |
|---|---|
| `load()` | 세 파일을 읽어 도서관을 구성한다. 대여 관계는 저장돼 있지 않으므로, **도서의 `대여회원명` 을 보고** 회원에게 되돌려 붙인다 |
| `save(library)` | 세 파일에 모두 저장한다. 데이터가 바뀔 때마다 호출 |
| `readLines(path)` | 파일을 읽어 빈 줄을 걸러낸다. 파일이 없으면 빈 스트림 |
| `writeLines(path, lines)` | UTF-8로 저장한다 |

### LibraryView / InputUtil — View

| 메서드 | 하는 일 |
|---|---|
| `LibraryView.printMenu(today)` | 메뉴 화면 |
| `LibraryView.printBooks/printMembers(title, list)` | 목록 출력. 비어 있으면 안내 문구 |
| `LibraryView.printDisposedBooks/printDueOrOverdueMembers/printSearchResult/...` | 기능별 결과 출력 |
| `InputUtil.nextText(prompt)` | 문자열 입력. **빈 값과 쉼표를 거부**하고 다시 묻는다 |
| `InputUtil.nextInt(prompt)` | 정수 입력. 숫자가 아니면 다시 묻는다 |
| `InputUtil.nextDate(prompt)` | 날짜 입력. `DateTimeParseException` 을 잡아 다시 묻는다 |

---

## Stream 사용처

| 기능 | 파이프라인 |
|---|---|
| 도서 검색 | `filter(조건)` → `Collectors.toMap(ISBN, ...)` 으로 중복 제거 |
| 폐기 대상 추출 | `filter(b -> b.isDisposalTarget(today))` |
| 반납 도래·초과 회원 | `filter(m -> m.getRentedBooks().stream().anyMatch(...))` — 중첩 Stream |
| 인기·비인기 도서 | `sorted(Comparator.comparingInt(Book::getRentalCount))` → `limit(n)` |
| 상습 미반납 회원 | `filter(Member::isHabitualOverdue)` |
| 관리번호 최대값 | `Stream.concat(...)` → `mapToInt(Book::getManagementNo)` → `max()` |
| 대여 관계 복원 | `Stream.concat(...)` → `filter(대여중 && 회원명 일치)` |
| CSV 읽기 | `readAllLines().stream()` → `filter` → `map(Book::parse)` → `toList()` |
| CSV 쓰기 | `stream().map(Book::toCsv).toList()` |

### 중복 제거에 `distinct()` 를 쓰지 않은 이유

`distinct()` 는 `equals()` 기준이라 "ISBN만 같으면 같은 책"이라는 규칙을 표현할 수 없다. 그래서 ISBN을 키로 모으는 방식을 썼다.

```java
Collectors.toMap(Book::getIsbn, book -> book, (existing, duplicate) -> existing, LinkedHashMap::new)
```

키가 겹치면 **먼저 온 책을 유지**하고, `LinkedHashMap` 으로 입력 순서를 지킨다.

### `Files.lines()` 대신 `Files.readAllLines().stream()`

`Files.lines()` 는 파일 핸들을 열어둔 채 스트림을 돌려주므로 `try-with-resources` 로 닫아야 한다. `readLines()` 는 스트림을 그대로 반환하는 구조라 닫을 시점이 없어서, 목록으로 다 읽어 파일을 닫은 뒤 스트림으로 바꿨다.

---

## 데이터 파일

경로는 `src/library/` 로 시작한다. 이클립스에서 실행할 때 **작업 디렉토리가 프로젝트 루트(`Homework/`)** 이기 때문이다.

구분자는 쉼표다. 도서명·부제에 쉼표가 들어가면 형식이 깨지므로 `InputUtil.nextText()` 가 입력 시점에 거부한다.

### books.csv / disposed_books.csv

머리글 줄 없이 데이터만 담는다. 칸 순서는 아래와 같다.

```
관리번호,ISBN,도서명,부제,장르,출판사,저자,출판일,인쇄회차,입고일,가격,총대여횟수,대여상태,대여일,반납완료상태,반납일,대여회원명
0004,9788956746425,자바의 정석,기초편,IT,도우출판,남궁성,2020-06-01,3,2020-07-01,30000,12,false,,true,2026-07-20,
```

첫 줄은 설명용이며 실제 파일에는 없다.

### members.csv

```
회원명,연락처,벌금,초과횟수
김철수,010-2222-2222,0,1
```

`books.csv` 와 마찬가지로 머리글 줄은 없다.

### 대여 관계를 저장하지 않는 이유 (정규화)

"김철수가 B0005를 빌렸다"는 사실은 `books.csv` 의 **`대여회원명` 칸 한 곳에만** 적는다. 회원 쪽에는 저장하지 않는다.

만약 `members.csv` 에도 `B0003;B0005` 같은 목록을 두면 같은 사실이 두 곳에 적히고, 한쪽만 바뀌면 즉시 모순이 생긴다. 관계형 DB의 **정규화**와 같은 원칙이다.

대신 파일을 읽을 때 대여 관계를 조립한다.

```java
List<Book> allBooks = Stream.concat(books.stream(), disposedBooks.stream()).toList();
for (Member member : members) {
    allBooks.stream()
            .filter(Book::isRented)
            .filter(book -> book.getRenterName().equals(member.getName()))
            .forEach(member::addRentedBook);
}
```

폐기 도서도 회원이 들고 있을 수 있으므로 두 목록을 합쳐서 살펴본다.

이 방식은 **회원명이 사실상 식별자**가 되므로 동명이인을 지원하지 않는다. 다만 `findMember(name)` 도 이미 이름으로 회원을 찾으므로 새로 생긴 제약은 아니다.

### disposed_books.csv 가 따로 있는 이유

폐기된 도서는 `books.csv` 에서 빠진다. 그런데 회원이 그 책을 대여 중이면 다음 실행 때 참조를 복원할 데이터가 없어진다.

그래서 **폐기됐지만 아직 대여 중인 도서만** 이 파일에 보관한다. 반납되면 이 파일에서도 사라지고, 아무도 대여하지 않은 폐기 도서는 저장하지 않는다.

---

## 명세 해석

명세에서 확정되지 않아 판단이 필요했던 항목들.

| 항목 | 결정 | 근거 |
|---|---|---|
| "햇수로 10년" | `올해 - 출판연도 >= 10` | 날짜가 아닌 연도 기준. 2016년 출판 도서는 2026년에 폐기 |
| "반납일"의 두 가지 의미 | 필드는 **실제 반납일**, 반납 예정일은 계산 | 필드 목록에서 `반납완료상태` 바로 뒤에 있어 실제 반납일로 읽힌다. "반납일 이틀 전"의 반납일은 `대여일 + 7일` |
| 인기/비인기 기준 | 상위·하위 **5권** | 명세에 개수가 없어 임의 지정 |
| 상습 미반납 기준 | 초과 **3회 이상** | 대여 금지 기준과 같은 값 사용 |
| 관리 고유번호 | `int` 순번, 화면에는 `%04d` | 순번이므로 값은 정수로 두고, 자릿수 채움은 출력할 때만 입힌다 |
| 대여할 책 선택 | 같은 ISBN 중 대여 가능한 첫 권 | 이용자가 관리번호를 알 필요 없게 |
| 저장 시점 | 데이터가 바뀔 때마다 즉시 | "저장/갱신되어야 합니다" |

## 예외 처리

업무 규칙 위반은 `LibraryException`(RuntimeException 상속) 하나로 통일해 던지고, `LibraryMain` 의 메뉴 루프에서 잡아 안내만 출력한다. 프로그램은 종료되지 않는다.

- 존재하지 않는 회원
- 초과 횟수 3회 이상 회원의 대여 시도
- 대여 가능한 책이 없는 경우
- 대여 중이 아닌 책의 반납 시도
