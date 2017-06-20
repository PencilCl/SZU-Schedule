package cn.edu.szu.szuschedule.object;

public class BookItem {
    private User user;
    private String borrow_Time;
    private String book_Name;
    private String return_DeadLine;

    public BookItem(User user, String book_Name, String borrow_Time, String return_DeadLine) {
        this.user = user;
        this.borrow_Time = borrow_Time;
        this.book_Name = book_Name;
        this.return_DeadLine = return_DeadLine;
    }

    public User getUser()   {   return user;    }

    public String getUserName() {   return user.getName();  }

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
