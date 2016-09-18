package pojo;

/**
 * Created by Administrator on 2015/8/30.
 */
public class SearchResult {
    private String title;
    private String author;
    private String publisher;
    private Integer recTime;
    private Integer pubTime;
    private Integer[] yearResult;
    private Integer[] countResult;
    private String address;
    private String spareTitle;
    private String spareAuthor;

    public SearchResult(){
        super();
    }

    public SearchResult(String title, String author, String publisher, Integer pubTime, String address){
        super();
        setPubTime(pubTime);
        setAuthor(author);
        setTitle(title);
        setPublisher(publisher);
        setAddress(address);
    }

    public SearchResult(String title, String author, String publisher, Integer pubTime, String address, String spareTitle, String spareAuthor){
        super();
        setPubTime(pubTime);
        setAuthor(author);
        setTitle(title);
        setPublisher(publisher);
        setAddress(address);
        setSpareTitle(spareTitle);
        setSpareAuthor(spareAuthor);
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public Integer getRecTime() {
        return recTime;
    }

    public void setRecTime(Integer recTime) {
        this.recTime = recTime;
    }

    public Integer getPubTime() {
        return pubTime;
    }

    public void setPubTime(Integer pubTime) {
        this.pubTime = pubTime;
    }

    public Integer[] getYearResult() {
        return yearResult;
    }

    public void setYearResult(Integer[] yearResult) {
        this.yearResult = yearResult;
    }

    public Integer[] getCountResult() {
        return countResult;
    }

    public void setCountResult(Integer[] countResult) {
        this.countResult = countResult;
    }

    public String getSpareTitle() {
        return spareTitle;
    }

    public void setSpareTitle(String spareTitle) {
        this.spareTitle = spareTitle;
    }

    public String getSpareAuthor() {
        return spareAuthor;
    }

    public void setSpareAuthor(String spareAuthor) {
        this.spareAuthor = spareAuthor;
    }
}
