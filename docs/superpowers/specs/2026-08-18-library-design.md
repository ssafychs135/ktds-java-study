# 도서관 관리 프로그램 설계

- 작성일: 2026-08-18
- 대상: `Homework/src/library`
- 요구 문법: Stream API, Has-A(포함) 관계, 파일 입출력

## 1. 목적

도서관이 도서와 회원을 관리하는 콘솔 프로그램. 대여·반납·검색·폐기·연체 관리 기능을 제공하고, 모든 상태를 CSV 파일에 저장한다.

## 2. Has-A 구조

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

## 3. 파일 구성

```
Homework/src/library/
├── LibraryMain.java        메뉴 흐름 (Controller)
├── LibraryView.java        화면 출력
├── InputUtil.java          터미널 입력
├── Library.java            도서관. 업무 규칙
├── Book.java               도서
├── Member.java             회원
├── LibraryFileStore.java   CSV 읽기/쓰기
├── LibraryException.java   업무 규칙 위반 예외
├── books.csv
├── disposed_books.csv
└── members.csv
```

처음에는 `LibraryMain.java` 하나에 UI·도서관·파일 저장·예외를 모두 넣었다가, 커진 뒤 **바뀌는 이유별로** 잘라냈다. MVC Model 2 배치는 `README.md` 참고.

데이터 파일 경로는 `src/library/` 로 시작한다. 이클립스에서 `Run As > Java Application` 의 작업 디렉토리가 프로젝트 루트(`Homework/`)이기 때문이다.

## 4. Book

명세의 도서 정보 17개를 그대로 필드로 갖는다.

| 필드 | 타입 | 비고 |
|---|---|---|
| `managementNo` | String | 관리 고유번호. `B0001` 형식, 도서관이 부여, 중복 불가 |
| `isbn` | String | 책 고유번호. 같은 책이면 동일 |
| `title` | String | 도서명 |
| `subtitle` | String | 도서 부제 |
| `genre` | String | 장르 |
| `publisher` | String | 출판사명 |
| `author` | String | 저자 |
| `publishDate` | LocalDate | 출판일 |
| `printCount` | int | 인쇄 회차 |
| `stockDate` | LocalDate | 입고일 |
| `price` | int | 가격 |
| `rentalCount` | int | 총 대여횟수 |
| `rented` | boolean | 대여상태 |
| `rentalDate` | LocalDate | 대여일. 미대여 시 null |
| `returned` | boolean | 반납완료상태 |
| `returnDate` | LocalDate | **실제 반납한 날짜**. 미반납 시 null |
| `renterName` | String | 대여한 회원명. 미대여 시 빈 문자열 |

### 반납 예정일은 필드가 아니다

명세의 "반납일"은 두 가지로 읽힌다.

- 필드 목록의 `반납일` → **실제 반납한 날짜** (`반납완료상태` 바로 뒤에 있음)
- "반납일 이틀 전이 되면 반납기간이 도래" 의 반납일 → **반납 예정일**

후자는 `대여일 + 7일`로 계산되는 값이므로 저장하지 않고 `getDueDate()` 로 구한다.

### 주요 메서드

| 메서드 | 규칙 |
|---|---|
| `getDueDate()` | `rentalDate + 7일` |
| `isDisposalTarget(today)` | `today.getYear() - publishDate.getYear() >= 10` (햇수 기준) |
| `isDueSoon(today)` | 대여 중이고 `dueDate - 2일 <= today <= dueDate` |
| `isOverdue(today)` | 대여 중이고 `today > dueDate` |
| `getOverdueDays(today)` | `dueDate` 부터 경과 일수. 연체 아니면 0 |
| `rentTo(name, today)` | 대여 상태로 전환, `rentalCount` 증가 |
| `returnBy(today)` | 반납 상태로 전환, `returnDate` 기록 |

## 5. Member

| 필드 | 타입 |
|---|---|
| `name` | String |
| `phone` | String |
| `fine` | int (벌금, 원) |
| `overdueCount` | int (반납기간 초과 횟수) |
| `rentedBooks` | List\<Book\> — **Has-A** |

`canRent()` 는 `overdueCount < 3` 일 때 true.

## 6. Library 기능

| # | 기능 | 규칙 |
|---|---|---|
| 1 | 신규 도서 입고 | 관리번호를 `기존 최대값 + 1` 로 자동 부여 |
| 2 | 10년 경과 도서 폐기 | 도서 목록에서 제거. 대여 중이면 `disposedBooks` 로 이동 |
| 3 | 반납 도래·초과 회원 조회 | 폐기 도서만 대여한 회원은 제외. 정상 도서를 하나라도 끼고 있으면 조회됨 |
| 4 | 인기 도서 조회 | 총 대여횟수 내림차순 상위 5권 |
| 5 | 비인기 도서 조회 | 총 대여횟수 오름차순 하위 5권 |
| 6 | 상습 미반납 회원 조회 | 초과 횟수 3회 이상 |
| 7 | 도서 검색 | 출판사명 / 저자 / 장르 중 하나로 검색. 대여 상태 무관, **같은 ISBN은 1권만** |
| 8 | 도서 대여 | 대여 기간 7일. 초과 횟수 3회 이상이면 거부. 같은 ISBN 중 대여 가능한 첫 권 배정 |
| 9 | 도서 반납 | 연체 시 초과 횟수 +1, 벌금 `연체일수 × 500원`. **폐기 도서는 벌금·초과횟수 모두 없음** |

## 7. Stream 사용처

| 기능 | 파이프라인 |
|---|---|
| 도서 검색 | `filter(조건)` → `filter(distinct by ISBN)` |
| 폐기 대상 추출 | `filter(b -> b.isDisposalTarget(today))` |
| 반납 도래·초과 회원 | `filter(m -> m.getRentedBooks().stream().anyMatch(...))` |
| 인기·비인기 도서 | `sorted(comparingInt(Book::getRentalCount))` → `limit(5)` |
| 상습 미반납 회원 | `filter(m -> m.getOverdueCount() >= 3)` |
| CSV 읽기 | `Files.lines(path)` → `filter(비어있지 않음)` → `map(Book::parse)` → `toList()` |
| CSV 쓰기 | `stream().map(Book::toCsv).toList()` → `Files.write(...)` |

## 8. CSV 형식

구분자는 쉼표. **도서명·부제 등에 쉼표가 들어가면 형식이 깨지므로, 입고 시 입력 검증으로 쉼표를 거부한다.**

`LocalDate` 가 null 인 필드는 빈 칸으로 저장하고, 읽을 때 빈 칸이면 null 로 되돌린다.

### books.csv / disposed_books.csv

```
관리번호,ISBN,도서명,부제,장르,출판사,저자,출판일,인쇄회차,입고일,가격,총대여횟수,대여상태,대여일,반납완료상태,반납일,대여회원명
```

### members.csv

```
회원명,연락처,벌금,초과횟수
김철수,010-2222-2222,0,1
```

**대여 관계는 저장하지 않는다.** "누가 무엇을 빌렸는가"는 `books.csv` 의 `대여회원명` 칸 한 곳에만 적는다. 같은 사실을 두 곳에 적으면 서로 어긋날 수 있기 때문이다(정규화).

읽을 때는 대여 중인 도서를 회원명으로 짝지어 참조를 복원한다. 폐기 도서도 회원이 들고 있을 수 있으므로 두 목록을 합쳐서 살펴본다.

이 방식은 회원명이 사실상 식별자가 되므로 동명이인을 지원하지 않는다. 다만 `findMember(name)` 도 이미 이름으로 회원을 찾으므로 새로 생긴 제약은 아니다.

## 9. 폐기 도서의 영속화

폐기된 도서는 `books.csv` 에서 빠진다. 그런데 회원이 그 책을 대여 중이면 다음 실행 때 참조를 복원할 데이터가 없어진다.

그래서 **폐기됐지만 아직 대여 중인 도서만** `disposed_books.csv` 에 보관한다. 반납되면 이 파일에서도 사라진다. 아무도 대여하지 않은 폐기 도서는 저장하지 않는다.

## 10. 저장 시점

데이터가 바뀌는 기능(입고, 폐기, 대여, 반납)이 끝날 때마다 세 파일을 즉시 저장한다. 명세의 "저장/갱신되어야 합니다" 를 따른다.

## 11. 오늘 날짜

`LocalDate.now()` 를 사용한다. 샘플 데이터는 작성 시점(2026-08-18) 기준으로 폐기 대상·연체·반납 임박 상황이 모두 재현되도록 구성한다. 날짜가 흐르면 연체 일수는 늘어나지만 각 상황의 성격은 유지된다.

## 12. 예외 처리

업무 규칙 위반은 `LibraryException` (RuntimeException 상속) 하나로 통일해 던지고, 메뉴 루프에서 잡아 안내 문구를 출력한다.

- 존재하지 않는 회원·도서
- 초과 횟수 3회 이상 회원의 대여 시도
- 대여 가능한 책이 없는 경우
- 대여 중이 아닌 책의 반납 시도
