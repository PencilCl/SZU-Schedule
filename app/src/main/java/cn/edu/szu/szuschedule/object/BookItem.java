package cn.edu.szu.szuschedule.object;

public class BookItem {
    private int id;
    private String startDate;
    private String bookName;
    private String endDate;

    public BookItem(int id, String bookName, String startDate, String endDate) {
        this.id = id;
        this.bookName = bookName;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public int getId() {
        return id;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getBookName() {
        return bookName;
    }

    public String getEndDate() {
        return endDate;
    }
}
