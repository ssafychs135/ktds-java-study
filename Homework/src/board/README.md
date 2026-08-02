# 게시판 (board)


## 파일 구조

```
board/
├── BoardMain.java              프로그램 시작점, 메뉴 화면과 분기
├── BoardService.java           게시판이 할 수 있는 일의 목록 (인터페이스)
├── BoardServiceImpl.java       그 일을 실제로 하는 코드
│
├── BoardItem.java              게시글과 댓글의 공통 정보 (abstract)
├── Article.java                게시글
├── Comment.java                댓글
│
├── exceptions/
│   ├── ArticleException.java         게시글 제목 규칙 위반
│   └── ArticleWriterException.java   작성자 이름 미입력
│
└── utils/
    ├── ScannerUtil.java        터미널 입력을 한 곳에서 처리
    └── ValidationUtil.java     입력값 검사 규칙
```

## 클래스별 역할

| 클래스 | 하는 일 |
|---|---|
| `BoardMain` | 메뉴를 보여주고, 번호를 입력받아 알맞은 기능을 호출한다. 예외를 잡아 안내 문구를 출력한다 |
| `BoardService` | 게시판이 제공하는 12가지 기능의 이름과 파라미터만 선언한다 |
| `BoardServiceImpl` | 게시글 목록(`List<Article>`)을 소유하고, 12가지 기능을 실제로 수행한다 |
| `BoardItem` | 게시글과 댓글이 똑같이 가지는 정보(내용, 작성자, 작성일)를 모아둔 부모 클래스 |
| `Article` | 게시글. 제목, 조회 수, 댓글 목록을 추가로 가진다 |
| `Comment` | 댓글. 추천 수를 추가로 가진다 |
| `ScannerUtil` | 프로그램 전체가 공유하는 `Scanner` 하나를 관리한다 |
| `ValidationUtil` | 제목 길이, 작성자 이름 등 입력 규칙을 검사하고 어긋나면 예외를 던진다 |

## 클래스 관계

```
            BoardMain
                │  사용
                ▼
          BoardService  (인터페이스)
                △
                │  구현
          BoardServiceImpl
                │
                │  소유
                ▼
          List<Article>


       BoardItem  (abstract)
        │ contents, writer, date
        │
   ┌────┴────┐
   │ 상속     │ 상속
   ▼         ▼
Article    Comment
 title      likeCount
 hit
 comments ──────────► List<Comment>
                (Article has a List<Comment>)
```

`ScannerUtil`과 `ValidationUtil`은 `static` 메서드만 가진 도구 상자라 위 관계도에 넣지 않았다. 필요한 곳에서 이름으로 직접 호출한다.

## 실행 흐름

```
 시작
  │
  ▼
┌─────────────────────────────┐
│ 게시글 목록 출력             │ ◄──────────────┐
│ 메뉴 출력                    │                │
│ 번호 입력 (>>)               │                │
└──────────┬──────────────────┘                │
           │                                   │
     ┌─────┴─────┐                             │
     │ 0번인가?   │                             │
     └─────┬─────┘                             │
      예   │   아니오                           │
     ┌─────┘     └─────┐                       │
     ▼                 ▼                       │
   종료          해당 기능 수행 ────────────────┤
                       │                       │
                  예외 발생 시                  │
                  메시지 출력 ──────────────────┘
```

반복의 맨 앞에서 항상 게시글 목록을 출력한다. 그래서 어떤 이유로든 반복의 처음으로 돌아오면 사용자는 목록 화면을 다시 보게 된다.

## 기능 목록

| 번호 | 메뉴 | 메서드 |
|---|---|---|
| 1 | 게시글 작성 | `writeArticle()` |
| 2 | 전체 게시글 보기 | `printAllArticles()` |
| 3 | 게시글 상세 보기 | `printArticle(int articleNo)` |
| 4 | 게시글 수정 | `modifyArticle(int articleNo)` |
| 5 | 게시글 삭제 | `removeArticle(int articleNo)` |
| 6 | 게시글 개수 | `printArticleCount()` |
| 7 | 댓글 작성 | `writeComment(int articleNo)` |
| 8 | 댓글 삭제 | `removeComment(int articleNo, int commentNo)` |
| 9 | 댓글 추천 | `recommendComment(int articleNo, int commentNo)` |
| 10 | 게시글 제목 검색 | `searchByTitle(String keyword)` |
| 11 | 게시글 전체 삭제 | `removeAllArticles()` |
| 12 | 댓글 전체 삭제 | `removeAllComments(int articleNo)` |
| 0 | 종료 | — |

## 규칙

| 항목 | 규칙 | 어겼을 때 |
|---|---|---|
| 게시글 제목 | 필수, 30자 이내 (공백만 입력하면 0자로 처리) | `ArticleException` |
| 게시글 작성자 | 필수, 길이 제한 없음 | `ArticleWriterException` |
| 게시글 조회 수 | 항상 0에서 시작, 상세 보기 시 1 증가 | — |
| 댓글 개수 | 게시글당 최대 10개 | `댓글을 더 이상 등록할 수 없습니다.` |
| 게시글/댓글 번호 | 1부터 시작 | `잘못된 게시글 번호입니다` / `잘못된 댓글 번호입니다` |

수정할 수 있는 것은 **게시글의 제목과 내용뿐**이다. 작성자, 작성일, 조회 수, 댓글은 수정 대상이 아니다.

## 알아두면 좋은 설계 결정

### 1. 번호를 필드로 저장하지 않는다

게시글 번호와 댓글 번호는 **리스트에서의 위치가 곧 번호**다. `BoardItem`에 `no` 같은 필드가 없는 이유다.

번호를 필드에도 저장하면 게시글을 하나 지울 때마다 뒤에 있는 모든 번호를 다시 매겨야 하고, 한 번이라도 어긋나면 버그가 된다. 위치를 번호로 쓰면 `List`가 알아서 당겨주므로 맞출 일이 없다.

대신 화면 번호(1부터)와 리스트 인덱스(0부터)가 1 차이 나므로, 리스트를 다룰 때 `articleNo - 1`을 쓴다.

### 2. 인터페이스와 구현을 나눈다

`BoardMain`에서 `BoardServiceImpl`이라는 이름이 등장하는 곳은 딱 한 줄이다.

```java
BoardService boardService = new BoardServiceImpl();
//    ↑ 인터페이스                  ↑ 구현 클래스
```

이후 `printArticleListScreen(BoardService ...)`, `runMenu(BoardService ...)` 처럼 전부 인터페이스 타입을 쓴다. 덕분에 `BoardMain`은 게시판이 *무엇을 할 수 있는지*만 알고, 게시글이 `ArrayList`에 담기는지 파일에 저장되는지는 모른다.

나중에 저장 방식이 다른 구현을 만들면 위 한 줄만 바꾸면 된다.

### 3. 검사 규칙을 `ValidationUtil` 한 곳에 모았다

제목 길이 제한이 30자에서 50자로 바뀌어도 `MAX_TITLE_LENGTH` 한 줄만 고치면 된다. 검사 코드가 여러 곳에 흩어져 있으면 한 군데를 빠뜨리게 된다.

### 4. 잘못된 입력은 예외로 알린다

`ValidationUtil`이 예외를 던지면 `BoardMain`의 `try-catch`가 받아서 메시지를 출력하고 메뉴 화면으로 돌아간다.

```java
try {
    runMenu(boardService, menuNo);
} catch (ArticleException | ArticleWriterException e) {
    System.out.println(e.getMessage());
}
```

검사에 실패한 자리에서 바로 멈추므로, 제목이 잘못됐는데 작성자·날짜·내용까지 물어보는 일이 없다.

### 5. 숫자가 아닌 입력은 -1로 처리한다

`ScannerUtil.nextInt()`는 `"abc"` 같은 값이 들어오면 프로그램을 멈추는 대신 `-1`을 돌려준다.

```java
try {
    return Integer.parseInt(sc.nextLine().trim());
} catch (NumberFormatException e) {
    return -1;
}
```

`-1`은 어떤 메뉴 번호도 게시글 번호도 아니므로, 부르는 쪽의 `switch` `default`나 번호 검사에서 자연스럽게 걸러진다.

### 6. `Scanner`는 하나만 쓴다

`ScannerUtil`이 `static` `Scanner` 하나를 들고 있고 모든 입력이 이곳을 거친다. `Scanner`를 여러 개 만들면 같은 `System.in`을 나눠 읽게 되어 입력이 뒤엉킨다.
