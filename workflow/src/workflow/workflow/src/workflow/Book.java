package workflow;

/**
 * 도서(Book) 클래스
 * - "사랑스러운 강아지" 페이지의 도서 정보를 필수 항목만 남겨 표현한다.
 * - 그림(일러스트) 정보는 설계 대상에서 제외한다.
 * - getter/setter 없이 필드를 public 으로 직접 접근한다.
 */
public class Book {

    /** 도서명 */
    public String title;

    /** 저자 */
    public Author author;

    /** 출판사 */
    public String publisher;

    /** 가격 */
    public int price;

    /** 리뷰 */
    public Review[] reviews;

    


    public Book(String title, Author author, String publisher, int price, Review[] reviews) {
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.price = price;
    }

    @Override
    public String toString() {
        return "Book{title='" + title + "', author='" + author
                + "', publisher='" + publisher + "', price=" + price + "}";
    }
}
