package workflow;

/**
 * 작가(Author) 클래스
 * - "사랑스러운 강아지" 페이지의 작가 정보를 필수 항목만 남겨 표현한다.
 * - 그림(일러스트) 정보는 설계 대상에서 제외한다.
 * - getter/setter 없이 필드를 public 으로 직접 접근한다.
 */
public class Author {

    /** 작가 이름 (예: 몰티즈) */
    public String name;

    public Author() {
    }

    public Author(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Author{name='" + name + "'}";
    }
}
