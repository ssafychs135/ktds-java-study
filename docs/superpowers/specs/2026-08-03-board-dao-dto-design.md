# 게시판 MVC + DAO/DTO 계층 분리 설계

- 작성일: 2026-08-03
- 대상: `Homework/src/board`
- 성격: 리팩터링 (기능 추가 없음, 화면 동작 100% 동일 유지)
- 적용 패턴: MVC (Model 2), DAO, DTO

## 1. 배경과 목적

### 현재 구조의 문제

`BoardServiceImpl` 하나가 네 가지 책임을 동시에 진다.

```
BoardServiceImpl
  ├─ 입력   ScannerUtil.nextLine(...)
  ├─ 검증   ValidationUtil.validate...(...)
  ├─ 저장   private List<Article> articleList
  └─ 출력   System.out.println(...)
```

그 결과 `BoardService`의 메서드 12개가 전부 `void`다. 서비스가 데이터를 반환하지 않고 직접 화면에 출력하므로, `writeArticle()`은 파라미터조차 없다 — 값을 서비스 내부에서 직접 입력받기 때문이다.

부수적으로 `if (article == null) return;` 패턴이 9곳에서 반복된다. 실패를 null 반환으로 알리기 때문이다.

### 목적

계층 분리 연습. 각 계층이 하나의 책임만 지고, 값을 반환하며, 실패는 예외로 알리는 구조로 바꾼다. 역할 구분은 MVC로 명시한다.

### MVC 역할 배치

| MVC 역할 | 담당 | 비고 |
|---|---|---|
| **Model** | `service` + `dao` + `dto` | 데이터와 그 데이터를 다루는 규칙 전체. DTO만이 Model인 것이 아니다 |
| **View** | `view` | 입력과 출력 |
| **Controller** | `controller` | 메뉴 번호를 받아 Model과 View를 조율 |

여기서 적용하는 것은 **Model 2(MVC2)** 다. 원조 MVC(Smalltalk)에서는 Model이 바뀌면 Observer로 View에 직접 통지하지만, Model 2에서는 Controller가 흐름을 주도하고 **Model은 View의 존재를 모른다.** 3절의 "Service는 화면이 존재한다는 사실을 모른다"가 이 원칙의 구체적 표현이다.

`BoardMain`은 MVC 어디에도 속하지 않는다. 객체를 조립하고 실행을 시작하는 진입점일 뿐이다.

### 범위 밖 (명시적 제외)

- DB / JDBC 연동 — 저장은 메모리 `List`를 유지한다
- 고유 ID(PK) 도입 — 번호는 현행대로 **리스트 인덱스**를 쓴다 (`BoardItem` 주석의 기존 결정을 존중)
- 기능 추가·화면 문구 변경 — 출력은 한 글자도 바뀌지 않아야 한다

## 2. 목표 구조

```
board/
  BoardMain.java              진입점 — 객체 조립만        약 25줄
  controller/                                                    [C]
    BoardController.java      메뉴 루프 + 분기            약 90줄
  view/                                                          [V]
    BoardView.java            입력/출력 전담              약 130줄
  service/                                                       ┐
    BoardService.java         서비스 계약                        │
    BoardServiceImpl.java     비즈니스 규칙 + 검증        약 110줄│
  dao/                                                           │
    BoardDao.java             저장소 계약                        ├ [M]
    BoardMemoryDao.java       List<Article> 소유          약 80줄 │
  dto/                                                           │
    BoardItem.java                                               │
    Article.java                                                 │
    Comment.java                                                 │
    ArticleSummary.java       신규 — 화면 번호 + 게시글          ┘
  exceptions/
    BoardException.java       신규 — 공통 부모
    ArticleException.java
    ArticleWriterException.java
    CommentException.java     신규
  utils/
    ScannerUtil.java
    ValidationUtil.java
```

**신규 7개**: `BoardController`, `BoardView`, `BoardDao`, `BoardMemoryDao`, `BoardException`, `CommentException`, `ArticleSummary`
**이동 5개**: DTO 3개 → `dto`, 서비스 2개 → `service`
**수정 4개**: `BoardMain`, `ValidationUtil`, 기존 예외 2개(부모 변경)

### 조립 지점

의존성은 모두 **생성자 주입**으로 연결한다. 각 클래스는 자기가 쓸 부품을 직접 `new` 하지 않고 밖에서 받는다. `BoardServiceImpl`이 `new BoardMemoryDao()`를 직접 만들면 저장소를 갈아끼울 수 없기 때문이다.

```java
public class BoardMain {
    public static void main(String[] args) {
        BoardDao dao = new BoardMemoryDao();
        BoardService service = new BoardServiceImpl(dao);
        BoardView view = new BoardView();

        new BoardController(service, view).run();
    }
}
```

`main`이 하는 일은 이것뿐이다. 어떤 구현체를 쓸지 결정하는 자리가 한 곳으로 모이므로, 나중에 `BoardMemoryDao`를 `BoardJdbcDao`로 바꾸려면 이 한 줄만 고치면 된다.

## 3. 계층별 책임

| 계층 | MVC | 하는 일 | 모르는 것 |
|---|---|---|---|
| `BoardMain` | — | 객체 조립, 실행 시작 | 메뉴가 무엇인지 |
| `BoardController` | C | 메뉴 분기, View↔Model 조율, 예외 처리 | 화면 문구, 저장 방식 |
| `BoardView` | V | Scanner 입력, `System.out` 출력 | 데이터가 어디에 저장되는지 |
| `BoardServiceImpl` | M | 검증 호출, 비즈니스 규칙, 예외 발생 | **화면이 존재한다는 사실** |
| `BoardMemoryDao` | M | 리스트 CRUD | 검증 규칙, 화면 |
| `dto` | M | 값 보관 | 나머지 전부 |

`BoardController`가 **화면 문구를 모른다**는 점이 중요하다. 컨트롤러는 `view.printMessage(e.getMessage())`처럼 "출력해라"라고 지시할 뿐, 문구의 형태·줄 구분·들여쓰기는 전부 View가 정한다.

### 완료 판정 기준

`BoardServiceImpl`에서 다음 두 import가 사라진다.

```java
import board.utils.ScannerUtil;   // 제거됨
// System.out 사용처 0곳
```

이것이 계층 분리 성공 여부를 판별하는 가장 확실한 기준이다.

## 4. DTO 설계

### 방침: 자기 필드 조작은 남기고, 컬렉션 CRUD와 규칙은 내보낸다

| 대상 | 처리 | 이유 |
|---|---|---|
| `Article.increaseHit()` | **DTO 유지** | 자기 필드 +1. Service가 *언제* 부를지 결정하고, DTO는 *어떻게* 올릴지 담당 |
| `Comment.increaseLike()` | **DTO 유지** | 위와 동일. `setLikeCount`를 열면 "1씩 증가만" 규칙이 깨진다 |
| `Article.getCommentCount()` | **DTO 유지** | `comments.size()`를 감싼 단순 조회 |
| `Article.addComment()` | → `BoardDao` | 저장소 쓰기 작업 |
| `Article.removeComment()` | → `BoardDao` | 저장소 쓰기 작업 |
| `Article.clearComments()` | → `BoardDao` | 저장소 쓰기 작업 |
| `Article.isCommentFull()` | → `ValidationUtil` | 정원 10개는 비즈니스 규칙 |
| `Article.MAX_COMMENT_COUNT` | → `ValidationUtil` | 규칙 상수는 규칙이 있는 곳에 |

`Article`은 `List<Comment>`를 계속 소유한다. 번호를 리스트 인덱스로 쓰기로 했으므로, 댓글을 별도 DAO로 분리하면 게시글 삭제 시 참조 인덱스가 어긋난다.

`BoardItem`은 변경 없이 `dto` 패키지로 이동만 한다. View가 입력 시점에 `trim`한 값으로 DTO를 조립하므로(8절 참조) setter를 추가할 필요가 없다.

### 신규 DTO: `ArticleSummary`

검색 결과 전용. **화면 번호를 함께 실어 나른다.**

```java
public class ArticleSummary {
    private final int articleNo;    // 전체 목록에서의 화면 번호 (1부터)
    private final Article article;

    public ArticleSummary(int articleNo, Article article) { ... }
    public int getArticleNo() { ... }
    public Article getArticle() { ... }
}
```

필요한 이유: 기존 `searchByTitle`은 매칭된 게시글을 **전체 목록에서의 위치 번호**로 출력한다.

```java
for (int i = 0; i < articleList.size(); i++) {          // i = 전체 목록 인덱스
    if (article.getTitle().contains(keyword)) {
        printArticleSummary(i, article);                 // 이 번호로 출력
```

매칭된 `Article`만 `List`로 반환하면 원래 번호를 잃고 1,2,3으로 다시 매겨져 출력이 달라진다. `ArticleSummary`가 번호를 함께 담아 이를 막는다.

전체 목록(`getAllArticles()`)은 순서가 그대로이므로 `인덱스 + 1`이 곧 번호라서 이 문제가 없다. 따라서 전체 목록은 `List<Article>`을 그대로 쓴다.

## 5. DAO 계약

```java
public interface BoardDao {

    // 게시글
    void insert(Article article);
    List<Article> selectAll();
    Article selectByNo(int articleNo);                   // 없으면 null
    List<ArticleSummary> selectByTitle(String keyword);  // 없으면 빈 리스트
    void update(int articleNo, String title, String contents);
    void delete(int articleNo);
    void deleteAll();
    int count();

    // 댓글 (게시글에 소속)
    void insertComment(int articleNo, Comment comment);
    void deleteComment(int articleNo, int commentNo);
    void deleteAllComments(int articleNo);
}
```

### 계약 세부

- **파라미터의 `articleNo` / `commentNo`는 화면 번호(1부터 시작)** 다. 인덱스 변환(`-1`)은 DAO 내부에서 한다.
- **DAO는 예외를 던지지 않는다.** 없는 번호를 조회하면 `null`, 검색 결과가 없으면 빈 리스트를 반환한다. 실패를 예외로 바꾸는 것은 Service의 책임이다.
- **댓글 메서드는 `articleNo`가 유효하다고 가정한다.** Service가 호출 전에 이미 검증한다.
- `selectByTitle`은 전체 목록을 순회하며 매칭된 항목을 `ArticleSummary(인덱스 + 1, 게시글)`로 감싸 반환한다. DAO가 리스트를 소유하므로 화면 번호를 정확히 알 수 있다.
- `update`는 메모리 구현에서 조회 후 setter를 호출하지만, 인터페이스에는 남긴다. 저장소가 DB로 바뀔 때 명시적 update 호출이 필요하기 때문이다.

## 6. Service 계약

```java
public interface BoardService {

    void writeArticle(Article article);                                  // 1
    List<Article> getAllArticles();                                      // 2
    Article readArticleDetail(int articleNo);                            // 3  조회수 +1
    Article getArticle(int articleNo);                                   // 조회만 (수정 화면용)
    void modifyArticle(int articleNo, String title, String contents);    // 4
    void removeArticle(int articleNo);                                   // 5
    int getArticleCount();                                               // 6
    void checkCommentWritable(int articleNo);                            // 7 입력 전 사전 확인
    void writeComment(int articleNo, Comment comment);                   // 7
    void removeComment(int articleNo, int commentNo);                    // 8
    int recommendComment(int articleNo, int commentNo);                  // 9  추천 후 수 반환
    List<ArticleSummary> searchByTitle(String keyword);                  // 10
    int removeAllArticles();                                             // 11 삭제 개수 반환
    int removeAllComments(int articleNo);                                // 12 삭제 개수 반환
}
```

### 기존 대비 변화

| 기존 | 변경 후 | 변화의 의미 |
|---|---|---|
| `void writeArticle()` | `void writeArticle(Article article)` | 완성된 DTO를 넘겨받는다 — DTO라는 이름의 유래 |
| `void printAllArticles()` | `List<Article> getAllArticles()` | 출력이 아니라 데이터를 반환 |
| `void printArticle(int)` | `Article readArticleDetail(int)` | 조회수 증가는 여기서 |
| `void printArticleCount()` | `int getArticleCount()` | 개수를 반환 |
| `void recommendComment(a,c)` | `int recommendComment(a,c)` | 추천 후 수를 반환 |
| `void searchByTitle(String)` | `List<ArticleSummary> searchByTitle(String)` | 번호를 실은 결과 목록을 반환 |
| `void removeAllArticles()` | `int removeAllArticles()` | 삭제 개수를 반환 |
| `void removeAllComments(int)` | `int removeAllComments(int)` | 삭제 개수를 반환 |

`getArticle`과 `readArticleDetail`이 둘 다 필요한 이유: 메뉴 4(수정)는 현재 값을 화면에 보여줘야 하지만, 그때 조회수가 오르면 안 된다.

## 7. Controller 계약

```java
public class BoardController {

    private final BoardService boardService;
    private final BoardView boardView;

    public BoardController(BoardService boardService, BoardView boardView) { ... }

    /** 메뉴 루프. 0을 입력할 때까지 반복한다. */
    public void run();
}
```

`run()`의 구조는 기존 `BoardMain.main()`의 루프를 그대로 옮긴 것이다.

```java
public void run() {
    while (true) {
        boardView.printListScreen(boardService.getAllArticles());
        int menuNo = boardView.inputMenuNo();

        try {
            if (menuNo == 0) {
                boardView.printMessage("게시판을 종료합니다.");
                ScannerUtil.close();
                break;
            }
            runMenu(menuNo);
        } catch (BoardException e) {
            boardView.printMessage(e.getMessage());
        }
    }
}
```

`runMenu(int)`와 `case`별 처리는 `private` 메서드로 둔다. 외부에 공개되는 것은 `run()` 하나뿐이다.

## 8. View 계약

```java
public class BoardView {

    // 출력
    void printListScreen(List<Article> articles);      // 헤더 + 목록 + 메뉴
    void printArticleDetail(int articleNo, Article article);
    void printSearchResult(List<ArticleSummary> results);
    void printMessage(String message);

    // 입력
    int inputMenuNo();
    int inputArticleNo();
    int inputCommentNo();
    String inputKeyword();
    Article inputNewArticle();                         // 제목/작성자/날짜/내용 → DTO 조립
    Comment inputNewComment();                         // 내용/작성자/날짜 → DTO 조립
    String inputModifiedTitle(String currentTitle);    // 현재 값 출력 후 새 값 입력
    String inputModifiedContents(String currentContents);
}
```

`ScannerUtil`은 그대로 둔다. 프롬프트 출력을 겸하지만 입력 동작의 일부이고, 이제 View만 사용한다.

`inputNewArticle()`이 `Article`을 조립해 반환한다는 점이 중요하다. View는 화면에서 받은 값으로 DTO를 만들고, Service는 그 DTO를 검증해 저장한다.

### 검증 시점 — View가 입력 직후 검증한다

기존 코드는 **입력을 받자마자** 검증하고, 실패하면 나머지 입력을 받지 않는다.

```java
String title = ScannerUtil.nextLine("게시글 제목 : ");
ValidationUtil.validateArticleTitle(title);              // 여기서 예외가 나면
String writer = ScannerUtil.nextLine("작성자 이름 : ");  // 아래 3줄은 실행되지 않는다
String date = ScannerUtil.nextLine("작성 날짜   : ");
String contents = ScannerUtil.nextLine("게시글 내용 : ");
```

만약 View가 4개를 모두 받은 뒤 Service가 검증하면, 빈 제목을 입력해도 작성자·날짜·내용을 전부 물어본 뒤에야 오류가 뜬다. 화면 흐름이 달라지므로 리팩터링 원칙에 어긋난다.

따라서 **`BoardView`가 입력 직후 `ValidationUtil`을 호출한다.**

```java
public Article inputNewArticle() {
    String title = ScannerUtil.nextLine("게시글 제목 : ");
    ValidationUtil.validateArticleTitle(title);

    String writer = ScannerUtil.nextLine("작성자 이름 : ");
    ValidationUtil.validateArticleWriter(writer);

    String date = ScannerUtil.nextLine("작성 날짜   : ");
    String contents = ScannerUtil.nextLine("게시글 내용 : ");

    return new Article(title.trim(), contents, writer.trim(), date);
}
```

`BoardServiceImpl.writeArticle()`도 저장 전에 같은 검증을 다시 수행한다. 이중 검증은 낭비가 아니다. View는 사용자 편의를 위해 빠르게 걸러내고, Service는 **어떤 경로로 호출되든** 규칙이 지켜지도록 보장한다. 웹의 클라이언트 검증과 서버 검증이 같은 관계다.

`inputModifiedTitle()`도 같은 이유로 입력 직후 검증한다.

### 댓글 정원 확인 시점

댓글 정원 초과도 같은 문제가 있다. 기존 코드는 **입력을 받기 전에** 정원을 확인하고 초과면 즉시 중단한다.

```java
if (article.isCommentFull()) {
    System.out.println("댓글을 더 이상 등록할 수 없습니다.");
    return;                                  // 댓글 내용을 묻지 않는다
}
String contents = ScannerUtil.nextLine("댓글 내용   : ");
```

정원은 저장소 상태에 달린 값이라 View가 알 수 없다. 따라서 Controller가 입력 전에 `boardService.checkCommentWritable(articleNo)`를 호출한다. 이 메서드는 게시글 존재 여부와 정원을 함께 확인하고, 문제가 있으면 예외를 던진다.

## 9. 예외 설계

```
RuntimeException
  └── BoardException                신규 — 공통 부모
       ├── ArticleException         없는 게시글 번호, 제목 규칙 위반
       ├── ArticleWriterException   작성자 이름 미입력
       └── CommentException         신규 — 없는 댓글 번호, 정원 초과
```

`BoardController.run()`이 `catch (BoardException e)` 한 줄로 모든 오류를 처리한다. 예외를 더 추가해도 컨트롤러는 수정하지 않는다.

### 예외로 바뀌는 기존 메시지

| 상황 | 기존 | 변경 후 |
|---|---|---|
| 없는 게시글 번호 | `findArticle()`이 출력 + null 반환 | `ArticleException("잘못된 게시글 번호입니다")` |
| 없는 댓글 번호 | `findComment()`가 출력 + null 반환 | `CommentException("잘못된 댓글 번호입니다")` |
| 댓글 정원 초과 | Service가 출력 후 `return` | `CommentException("댓글을 더 이상 등록할 수 없습니다.")` |

### 예외로 바꾸지 않는 것

다음은 오류가 아니라 **정상적인 빈 상태**이므로 예외로 만들지 않는다. Service가 값(빈 리스트, 0)을 반환하고 View가 문구를 출력한다.

- `아직 등록된 게시글이 없습니다.`
- `등록된 게시글이 없습니다` (메뉴 6)
- `제거할 게시글이 없습니다` (메뉴 11)
- `등록된 댓글이 없습니다` (메뉴 12)
- `검색된 게시글이 없습니다.`
- `등록된 댓글이 없습니다.` (상세 보기)

메뉴 11과 12는 삭제 개수 `0`을 반환하고, View가 `0`일 때 위 문구를 출력한다.

## 10. 데이터 흐름 예시

### 메뉴 3 — 게시글 상세 보기

```
BoardController
  │ int no = view.inputArticleNo();
  │ Article article = service.readArticleDetail(no);
  │        │ Article a = dao.selectByNo(no);
  │        │ if (a == null) throw new ArticleException("잘못된 게시글 번호입니다");
  │        │ a.increaseHit();
  │        └ return a;
  │ view.printArticleDetail(no, article);
  └ 예외 시 → catch (BoardException e) → view.printMessage(e.getMessage());
```

### 메뉴 1 — 게시글 작성

```
BoardController
  │ Article article = view.inputNewArticle();
  │        │ 제목 입력 → ValidationUtil.validateArticleTitle (실패 시 즉시 예외)
  │        │ 작성자 입력 → ValidationUtil.validateArticleWriter (실패 시 즉시 예외)
  │        │ 날짜 입력, 내용 입력
  │        └ return new Article(title.trim(), contents, writer.trim(), date);
  │ service.writeArticle(article);
  │        │ ValidationUtil 재검증 (경로와 무관하게 규칙 보장)
  │        └ dao.insert(article);
  │ view.printMessage("게시글 작성이 완료되었습니다.");
  └ 검증 실패 시 → catch (BoardException e) → view.printMessage(e.getMessage());
```

### 메뉴 7 — 댓글 작성

```
BoardController
  │ int no = view.inputArticleNo();
  │ service.checkCommentWritable(no);              // 입력 전에 확인
  │        │ dao.selectByNo(no) → null이면 ArticleException
  │        └ 댓글 수 >= 10이면 CommentException
  │ Comment comment = view.inputNewComment();      // 통과했을 때만 입력을 받는다
  │ service.writeComment(no, comment);
  │        └ dao.insertComment(no, comment);
  └ view.printMessage("댓글 작성이 완료되었습니다.");
```

### 메뉴 4 — 게시글 수정

```
BoardController
  │ int no = view.inputArticleNo();
  │ Article current = service.getArticle(no);       // 조회수 안 오름
  │ String title = view.inputModifiedTitle(current.getTitle());
  │ String contents = view.inputModifiedContents(current.getContents());
  │ service.modifyArticle(no, title, contents);
  │        │ dao.selectByNo(no) → null이면 예외
  │        │ ValidationUtil.validateArticleTitle(title);
  │        └ dao.update(no, title.trim(), contents);
  └ view.printMessage("게시글 수정이 완료되었습니다.");
```

## 11. 검증 방법

기능 추가가 없는 리팩터링이므로 성공 기준은 하나다. **화면 출력이 이전과 완전히 동일할 것.**

JUnit이 없으므로 표준 입력 시나리오를 파이프로 넣고 출력을 비교한다.

```
1. 리팩터링 전 코드로 시나리오 실행 → 출력을 기준선 파일로 저장
2. 리팩터링 수행
3. 같은 시나리오 실행 → 출력 저장 → diff
   차이가 0줄이면 성공
```

### 시나리오 (기준선용)

정상 흐름과 오류 흐름을 모두 지난다.

| 입력 | 확인 대상 |
|---|---|
| `1` + 제목/작성자/날짜/내용 | 게시글 작성 |
| `1` + 두 번째 글 | 목록 다건 |
| `2` | 전체 목록 |
| `3` → `1` (2회) | 상세 보기, 조회수 증가 확인 |
| `4` → `1` + 수정값 | 수정, 조회수 미증가 확인 |
| `7` → `1` + 댓글 3개 값 | 댓글 작성 |
| `9` → `1` → `1` | 댓글 추천 |
| `3` → `1` | 댓글·추천 반영 확인 |
| `10` → 검색어 | 제목 검색 |
| `10` → 없는 검색어 | `검색된 게시글이 없습니다.` |
| `6` | 게시글 개수 |
| `8` → `1` → `1` | 댓글 삭제 |
| `12` → `1` | 댓글 전체 삭제 (0개일 때 문구 포함) |
| `5` → `2` | 게시글 삭제 후 번호 밀림 확인 |
| `3` → `99` | `잘못된 게시글 번호입니다` |
| `8` → `1` → `99` | `잘못된 댓글 번호입니다` |
| `1` + 빈 제목 | `게시글 제목은 반드시 입력해야 합니다.` |
| `1` + 31자 제목 | `게시글 제목은 30글자 이내로 작성해야 합니다.` |
| `1` + 제목 + 빈 작성자 | `작성자 이름은 반드시 입력해야 합니다.` |
| `abc` (숫자 아님) | `잘못된 입력입니다. 게시글 목록으로 돌아갑니다.` |
| `11` | 전체 삭제 |
| `11` (비어있을 때) | `제거할 게시글이 없습니다` |
| `0` | 종료 |

### 컴파일 확인

```
javac --release 21 -encoding UTF-8 -sourcepath Homework/src -d bin <소스 전체>
```

## 12. 주의점

- **댓글 정원 초과가 예외로 바뀌면 흐름이 달라진다.** 기존에는 정원 초과 시 메시지만 출력하고 댓글 입력 프롬프트를 띄우지 않았다. 예외로 바꿔도 같은 위치에서 던지므로 동작은 같지만, 던지는 시점이 입력 **전**이어야 한다.
- **`trim` 처리 위치.** 기존 `new Article(title.trim(), contents, writer.trim(), date)`를 View의 `inputNewArticle()`이 그대로 수행한다. `contents`와 `date`는 기존에도 trim하지 않았으므로 그대로 둔다.
- **입력 소비 시점이 곧 화면 흐름이다.** 검증 실패 시 뒤따르는 입력을 받지 않아야 출력이 동일해진다. 8절의 검증 시점 규칙이 이를 보장한다. 이 항목은 실제 실행 출력 비교(11절)로 검증된다.
- **`searchByTitle`의 번호는 전체 목록 기준이다.** 4절의 `ArticleSummary`로 해결한다. 검색 결과 안에서 1,2,3으로 다시 매기면 출력이 달라져 리팩터링 원칙에 어긋난다.
- **`modifyArticle`은 조회를 두 번 한다.** `getArticle`(현재 값 표시용)과 `modifyArticle` 내부의 존재 확인이다. 메모리 저장이라 비용이 없고, 두 호출 사이에 값이 바뀔 일도 없는 단일 스레드 프로그램이므로 그대로 둔다.
- **패키지 이동으로 import가 늘어난다.** 기존에는 `Article`, `Comment`가 같은 `board` 패키지라 import가 없었다. 이동 후 `board.dto.Article` 등을 import해야 한다. 컴파일 오류로 즉시 드러나므로 누락 위험은 낮다.
