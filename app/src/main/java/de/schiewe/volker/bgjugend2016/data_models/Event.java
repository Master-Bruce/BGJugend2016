package de.schiewe.volker.bgjugend2016.data_models;

/**
 * Event-Class for firebase data structure
 */
public class Event {
    //firebase fields
    private String Title;
    private String Date;
    private String Place;
    private String Header;
    private String Text;
    private String Age;
    private String PeopleNumber;
    private String Cost;
    private String Deadline;
    private String Team;
    private Contact Contact;
    private String Image;
    private String Url;

    private int Id;
    private java.util.Date dDeadline;

    public Event() {
        //required for firebase
    }

    public Event(String title, String date, String place, java.util.Date deadline) {
        Title = title;
        Date = date;
        Place = place;
        dDeadline = deadline;
    }

    public String getTitle() {
        return Title;
    }

    public String getDate() {
        return Date;
    }

    public String getPlace() {
        return Place;
    }

    public String getHeader() {
        return Header;
    }

    public String getText() {
        return Text;
    }

    public String getAge() {
        return Age;
    }

    public String getPeopleNumber() {
        return PeopleNumber;
    }

    public String getCost() {
        return Cost;
    }

    public String getDeadline() {
        return Deadline;
    }

    public String getTeam() {
        return Team;
    }

    public Contact getContact() {
        return Contact;
    }

    public String getImage() {
        return Image;
    }

    public java.util.Date getdDeadline() {
        return dDeadline;
    }

    public void setdDeadline(java.util.Date dDeadline) {
        this.dDeadline = dDeadline;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }
}
