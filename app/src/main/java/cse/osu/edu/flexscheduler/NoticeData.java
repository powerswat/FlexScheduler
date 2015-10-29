package cse.osu.edu.flexscheduler;

/**
 * Created by Young Suk Cho on 10/29/2015.
 */
public class NoticeData {
    private String title;
    private String data;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public NoticeData(String title, String data){
        this.title = title;
        this.data = data;
    }


}
