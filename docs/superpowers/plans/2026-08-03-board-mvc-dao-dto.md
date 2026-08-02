# 게시판 MVC + DAO/DTO 리팩터링 구현 계획

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** `board` 패키지를 MVC(Model 2) + DAO + DTO 구조로 재편하되, 화면 출력은 한 글자도 바뀌지 않게 한다.

**Architecture:** `BoardMain`(조립) → `BoardController`(흐름) → `BoardView`(입출력) / `BoardService`(규칙) → `BoardDao`(저장) → `dto`(값). 의존성은 생성자 주입으로 연결한다.

**Tech Stack:** Java 21, 표준 라이브러리만. 빌드는 `javac`, 실행은 `java`. 테스트 프레임워크 없음.

**Spec:** `docs/superpowers/specs/2026-08-03-board-dao-dto-design.md`

## Global Constraints

- **화면 출력 100% 동일.** 모든 태스크는 기준선 출력과 `diff` 결과 0줄이어야 완료다.
- 번호는 리스트 인덱스 기반(1부터)을 유지한다. 고유 ID를 도입하지 않는다.
- 저장은 메모리 `List`. DB/파일 연동 없음.
- Java 21, 인코딩 UTF-8.
- 기존 주석의 설명 의도를 보존한다. 이 코드는 학습용이며 주석이 자산이다.
- 각 태스크 종료 시점에 **컴파일이 되고 프로그램이 정상 실행**되어야 한다.

## 검증 명령 (모든 태스크 공통)

```bash
cd /Users/sunny/java-study/ktds-java-study
find Homework/src -name "*.java" > /tmp/srcs.txt
javac --release 21 -encoding UTF-8 -d bin @/tmp/srcs.txt          # 컴파일
java -cp bin board.BoardMain < $SC/scenario.txt > $SC/actual.txt 2>&1
diff $SC/baseline.txt $SC/actual.txt && echo "회귀 없음"
```

`$SC` = `/private/tmp/claude-501/-Users-sunny-java-study-ktds-java-study/ffa4976d-ae0a-4dee-ab72-f0d971295c8d/scratchpad/board-regression`

---

### Task 1: 회귀 테스트 기준선 확보

리팩터링 전 현재 동작을 고정한다. 이후 모든 태스크가 이 기준선과 비교된다.

**Files:**
- Create: `$SC/scenario.txt` — 표준 입력 시나리오
- Create: `$SC/baseline.txt` — 현재 코드의 출력

**Produces:** `baseline.txt` (이후 전 태스크의 비교 대상)

- [ ] **Step 1: 시나리오 작성**

명세 11절의 시나리오 표를 입력 줄로 옮긴다. 정상 흐름 → 검증 오류 → 댓글 정원 초과 → 빈 상태 순으로 배치한다. 검증 오류 케이스는 **입력 소비 개수**가 정상 케이스와 다르므로 줄 순서에 주의한다.

- [ ] **Step 2: 현재 코드 컴파일 및 기준선 캡처**

```bash
javac --release 21 -encoding UTF-8 -d bin @/tmp/srcs.txt
java -cp bin board.BoardMain < $SC/scenario.txt > $SC/baseline.txt 2>&1
```

- [ ] **Step 3: 기준선 육안 검토**

기대한 분기를 실제로 지났는지 확인한다. 특히 검증 오류 3종, 댓글 정원 초과, 검색 번호가 전체 목록 기준(2번)인지, 게시글 삭제 후 번호 밀림.

---

### Task 2: 예외 계층 정비

**Files:**
- Create: `Homework/src/board/exceptions/BoardException.java`
- Create: `Homework/src/board/exceptions/CommentException.java`
- Modify: `Homework/src/board/exceptions/ArticleException.java` (부모 변경)
- Modify: `Homework/src/board/exceptions/ArticleWriterException.java` (부모 변경)
- Modify: `Homework/src/board/BoardMain.java` (catch 단순화)

**Interfaces:**
- Produces: `BoardException extends RuntimeException`, `CommentException extends BoardException`

- [ ] **Step 1: `BoardException` 작성**

```java
package board.exceptions;

/**
 * 게시판에서 발생하는 모든 예외의 공통 부모.
 * 부모를 하나 두면 잡는 쪽에서 catch (BoardException e) 한 줄로 끝나고,
 * 예외 종류가 늘어나도 잡는 코드를 고치지 않아도 된다.
 */
public class BoardException extends RuntimeException {
    public BoardException(String message) {
        super(message);
    }
}
```

- [ ] **Step 2: 기존 예외 2개의 부모를 `BoardException`으로 변경**

`extends RuntimeException` → `extends BoardException`. 생성자 본문은 그대로.

- [ ] **Step 3: `CommentException` 작성**

```java
package board.exceptions;

/** 댓글 관련 예외. 없는 댓글 번호, 댓글 정원 초과. */
public class CommentException extends BoardException {
    public CommentException(String message) {
        super(message);
    }
}
```

- [ ] **Step 4: `BoardMain`의 catch 단순화**

`catch (ArticleException | ArticleWriterException e)` → `catch (BoardException e)`. import도 정리한다.

- [ ] **Step 5: 컴파일 + 회귀 확인**

`diff` 0줄이어야 한다. 이 태스크는 동작을 바꾸지 않는다.

---

### Task 3: DTO 패키지 분리

**Files:**
- Create: `Homework/src/board/dto/` (BoardItem, Article, Comment 이동)
- Create: `Homework/src/board/dto/ArticleSummary.java`
- Modify: `Homework/src/board/BoardServiceImpl.java` (import 추가)

**Interfaces:**
- Produces: `board.dto.Article`, `board.dto.Comment`, `board.dto.BoardItem`, `board.dto.ArticleSummary`
- `ArticleSummary(int articleNo, Article article)`, `getArticleNo()`, `getArticle()`

- [ ] **Step 1: 세 DTO를 `board/dto/`로 이동하고 package 선언 변경**

`package board;` → `package board.dto;`. 내용은 그대로 둔다. `Article`의 컬렉션 메서드(`addComment` 등)는 Task 4에서 정리한다.

- [ ] **Step 2: `ArticleSummary` 작성**

```java
package board.dto;

/**
 * 검색 결과 한 건. 게시글과 함께 "전체 목록에서 몇 번인지"를 실어 나른다.
 *
 * 검색 결과만 List로 돌려주면 원래 번호를 잃어버려 1, 2, 3으로 다시 매겨진다.
 * 화면에 보여야 하는 번호는 전체 목록 기준이므로 이 클래스가 둘을 함께 담는다.
 */
public class ArticleSummary {

    private final int articleNo;
    private final Article article;

    public ArticleSummary(int articleNo, Article article) {
        this.articleNo = articleNo;
        this.article = article;
    }

    public int getArticleNo() {
        return articleNo;
    }

    public Article getArticle() {
        return article;
    }
}
```

- [ ] **Step 3: `BoardServiceImpl`에 import 추가**

`import board.dto.Article;`, `import board.dto.Comment;`

- [ ] **Step 4: 컴파일 + 회귀 확인**

---

### Task 4: DAO 도입

저장 책임을 `BoardServiceImpl`에서 떼어낸다. 이 시점에도 Service는 여전히 입출력을 한다.

**Files:**
- Create: `Homework/src/board/dao/BoardDao.java`
- Create: `Homework/src/board/dao/BoardMemoryDao.java`
- Modify: `Homework/src/board/dto/Article.java` (컬렉션 메서드 제거)
- Modify: `Homework/src/board/utils/ValidationUtil.java` (정원 규칙 이관)
- Modify: `Homework/src/board/BoardServiceImpl.java` (dao 사용)
- Modify: `Homework/src/board/BoardMain.java` (dao 주입)

**Interfaces:**
- Consumes: `board.dto.*` (Task 3)
- Produces: 명세 5절의 `BoardDao` 11개 메서드

- [ ] **Step 1: `BoardDao` 인터페이스 작성**

명세 5절 그대로. 파라미터의 번호는 화면 번호(1부터), 인덱스 변환은 구현체 내부에서 한다. DAO는 예외를 던지지 않는다.

- [ ] **Step 2: `BoardMemoryDao` 작성**

`private final List<Article> articleList = new ArrayList<>();`를 소유한다. `selectByNo`는 범위를 벗어나면 `null`을 반환한다(예외 아님). `selectByTitle`은 전체를 순회하며 `new ArticleSummary(i + 1, article)`로 감싼다.

- [ ] **Step 3: `Article`에서 컬렉션 메서드 제거**

제거: `addComment`, `removeComment`, `clearComments`, `isCommentFull`, `MAX_COMMENT_COUNT`
유지: `getComments`, `getCommentCount`, `increaseHit`, getter/setter

- [ ] **Step 4: `ValidationUtil`에 정원 규칙 이관**

```java
/** 게시글 하나에 등록할 수 있는 댓글의 최대 개수 */
public static final int MAX_COMMENT_COUNT = 10;

/**
 * 댓글을 더 등록할 수 있는지 검사.
 * 정원(10개)이 찼으면 CommentException을 던진다.
 */
public static void validateCommentWritable(int currentCommentCount) {
    if (currentCommentCount >= MAX_COMMENT_COUNT) {
        throw new CommentException("댓글을 더 이상 등록할 수 없습니다.");
    }
}
```

- [ ] **Step 5: `BoardServiceImpl`이 dao를 사용하도록 변경**

`private List<Article> articleList` 제거 → `private final BoardDao boardDao;` + 생성자 주입.
`articleList.add(...)` → `boardDao.insert(...)` 등으로 치환.

**주의:** 이 태스크에서 정원 초과는 아직 예외가 아니라 기존처럼 메시지 출력 + `return`을 유지한다. 예외 전환은 Task 6에서 한다. `ValidationUtil.MAX_COMMENT_COUNT`만 참조한다.

- [ ] **Step 6: `BoardMain`에서 dao 주입**

```java
BoardDao boardDao = new BoardMemoryDao();
BoardService boardService = new BoardServiceImpl(boardDao);
```

- [ ] **Step 7: 컴파일 + 회귀 확인**

---

### Task 5: View 신설

`BoardMain`의 화면 코드를 `BoardView`로 옮긴다. Service는 아직 그대로 출력한다.

**Files:**
- Create: `Homework/src/board/view/BoardView.java`
- Modify: `Homework/src/board/BoardMain.java`

**Interfaces:**
- Produces: `BoardView` — `printListHeader()`, `printMenu()`, `printMessage(String)`, `inputMenuNo()`, `inputArticleNo()`, `inputCommentNo()`, `inputKeyword()`

- [ ] **Step 1: `BoardView` 작성 — 이 태스크에서 옮길 수 있는 것만**

`BoardMain.printArticleListScreen()`의 헤더/메뉴 출력, `inputArticleNo()`, `inputCommentNo()`, 메뉴 번호 입력, 검색어 입력.

게시글 목록 출력은 아직 Service가 담당하므로 옮기지 않는다(Task 6).

- [ ] **Step 2: `BoardMain`이 `BoardView`를 사용하도록 변경**

- [ ] **Step 3: 컴파일 + 회귀 확인**

---

### Task 6: Service 시그니처 전환

가장 큰 단계. Service에서 입출력을 완전히 제거하고 값을 반환하게 만든다. 시그니처가 한꺼번에 바뀌므로 중간에 컴파일이 되지 않는 구간이 있다.

**Files:**
- Create: `Homework/src/board/service/BoardService.java` (이동 + 전면 개편)
- Create: `Homework/src/board/service/BoardServiceImpl.java` (이동 + 전면 개편)
- Modify: `Homework/src/board/view/BoardView.java` (출력 메서드 추가)
- Modify: `Homework/src/board/BoardMain.java` (조율)

**Interfaces:**
- Consumes: `BoardDao`(Task 4), `BoardView`(Task 5), `board.dto.*`(Task 3), 예외(Task 2)
- Produces: 명세 6절의 `BoardService` 14개 메서드, 명세 8절의 `BoardView` 전체

- [ ] **Step 1: `BoardService` 인터페이스를 명세 6절대로 다시 작성**

`board.service` 패키지로 이동. `checkCommentWritable(int)` 포함 14개.

- [ ] **Step 2: `BoardServiceImpl` 재작성**

`ScannerUtil` import 제거, `System.out` 전면 제거. 실패는 예외로 전환:
- 없는 게시글 번호 → `ArticleException("잘못된 게시글 번호입니다")`
- 없는 댓글 번호 → `CommentException("잘못된 댓글 번호입니다")`
- 댓글 정원 초과 → `ValidationUtil.validateCommentWritable()`

빈 상태(게시글 0개 등)는 예외가 아니라 값(빈 리스트, 0)으로 반환한다.

- [ ] **Step 3: `BoardView`에 출력 메서드 추가**

`printArticleList(List<Article>)`, `printArticleDetail(int, Article)`, `printSearchResult(List<ArticleSummary>)`, `inputNewArticle()`, `inputNewComment()`, `inputModifiedTitle(String)`, `inputModifiedContents(String)`.

**출력 문구는 기존 `BoardServiceImpl`에서 그대로 복사한다.** 한 글자라도 바뀌면 회귀가 난다. 특히 `LINE` 상수(하이픈 50개), 댓글 목록의 들여쓰기 두 칸, `" - "`, `" / "` 구분자.

`inputNewArticle()`과 `inputModifiedTitle()`은 명세 8절대로 입력 직후 `ValidationUtil`을 호출한다.

- [ ] **Step 4: `BoardMain`이 View와 Service를 조율하도록 변경**

메뉴 7은 `service.checkCommentWritable(no)`를 입력 **전에** 호출한다.

- [ ] **Step 5: 컴파일 + 회귀 확인**

이 태스크에서 diff가 날 가능성이 가장 높다. 차이가 나면 문구·공백·입력 순서를 우선 의심한다.

- [ ] **Step 6: 계층 분리 완료 판정**

```bash
grep -n "System.out\|ScannerUtil" Homework/src/board/service/BoardServiceImpl.java
```

결과가 비어 있어야 한다.

---

### Task 7: Controller 분리

**Files:**
- Create: `Homework/src/board/controller/BoardController.java`
- Modify: `Homework/src/board/BoardMain.java` (조립만 남긴다)

**Interfaces:**
- Consumes: `BoardService`(Task 6), `BoardView`(Task 6)
- Produces: `BoardController(BoardService, BoardView)`, `run()`

- [ ] **Step 1: `BoardController` 작성**

`BoardMain`의 `while` 루프와 `runMenu` switch를 그대로 옮긴다. 공개 메서드는 `run()` 하나, 나머지는 `private`.

- [ ] **Step 2: `BoardMain`을 조립 전용으로 축소**

```java
public class BoardMain {
    public static void main(String[] args) {
        BoardDao boardDao = new BoardMemoryDao();
        BoardService boardService = new BoardServiceImpl(boardDao);
        BoardView boardView = new BoardView();

        new BoardController(boardService, boardView).run();
    }
}
```

- [ ] **Step 3: 컴파일 + 회귀 확인 (최종)**

- [ ] **Step 4: 최종 구조 확인**

```bash
find Homework/src/board -name "*.java" | sort
```

명세 2절의 목표 구조와 일치해야 한다.

---

## 완료 기준

1. `diff baseline.txt actual.txt` 결과 0줄
2. `BoardServiceImpl`에 `System.out`과 `ScannerUtil`이 없음
3. 파일 구조가 명세 2절과 일치
4. `BoardMain`이 조립만 수행 (약 25줄)
