package cn.edu.szu.szuschedule.object;

/**
 * Created by chenlin on 30/05/2017.
 */
public class TodoItem {
    private String itemName;
    private String itemNote;
    private String timeBegin;
    private String timeEnd;
    private boolean showAlert;

    public TodoItem(String itemName, String itemNote, String timeBegin, String timeEnd) {
        this(itemName, itemNote, timeBegin, timeEnd, false);
    }

    public TodoItem(String itemName, String itemNote, String timeBegin, String timeEnd, boolean showAlert) {
        this.itemName = itemName;
        this.itemNote = itemNote;
        this.timeBegin = timeBegin;
        this.timeEnd = timeEnd;
        this.showAlert = showAlert;
    }

    public String getItemName() {
        return itemName;
    }

    public String getItemNote() {
        return itemNote;
    }

    public boolean isShowAlert() {
        return showAlert;
    }

    public String getTimeBegin() {
        return timeBegin;
    }

    public String getTimeEnd() {
        return timeEnd;
    }
}
