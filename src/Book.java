public class Book {
    private int id;
    private String isbn;
    private String title;
    private String author;
    private String publisher;
    private Integer year;
    private int quantity;
    private double price;

    public Book() {}

    public Book(int id, String isbn, String title, String author, String publisher, Integer year, int quantity, double price) {
        this.id = id; this.isbn = isbn; this.title = title; this.author = author;
        this.publisher = publisher; this.year = year; this.quantity = quantity; this.price = price;
    }

    // getters & setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
}
