package cn.edu.szu.szuschedule.object;

public class bookItem {

    private String borrow_Time;
    private String book_Name;
    private String return_DeadLine;

    public bookItem(String book_Name, String borrow_Time, String return_DeadLine) {
        this.borrow_Time = borrow_Time;
        this.book_Name = book_Name;
        this.return_DeadLine = return_DeadLine;
    }

    public String getBook_Name() {
        return book_Name;
    }

    public String getBorrow_Time() {
        return borrow_Time;
    }

    public String getReturn_DeadLine() {
        return return_DeadLine;
    }

}
