/**
 * 리뷰(Review) 클래스
 * - "사랑스러운 강아지" 페이지의 리뷰 정보를 필수 항목만 남겨 표현한다.
 * - getter/setter 없이 필드를 public 으로 직접 접근한다.
 */
public class Review {

    /** 리뷰 작성자 */
    public String writer;

    /** 평점 (10점 만점) */
    public double rating;

    /** 리뷰 내용 */
    public String content;

    public Review() {
    }

    public Review(String writer, double rating, String content) {
        this.writer = writer;
        this.rating = rating;
        this.content = content;
    }

    @Override
    public String toString() {
        return "Review{writer='" + writer + "', rating=" + rating + "}";
    }
}
