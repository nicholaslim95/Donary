import java.util.Date;

public class EventPost {
    private String eventId;
    private String eventName;
    private String eventDetail;
    private String eventLocation;
    private int noOfParticipants;
    private Date eventStartDate;
    private Date eventEndDate;

    public EventPost(){

    }

    public EventPost(String eventId, String eventName, String eventDetail, String eventLocation, int noOfParticipants, Date eventStartDate, Date eventEndDate) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.eventDetail = eventDetail;
        this.eventLocation = eventLocation;
        this.noOfParticipants = noOfParticipants;
        this.eventStartDate = eventStartDate;
        this.eventEndDate = eventEndDate;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventDetail() {
        return eventDetail;
    }

    public void setEventDetail(String eventDetail) {
        this.eventDetail = eventDetail;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    public int getNoOfParticipants() {
        return noOfParticipants;
    }

    public void setNoOfParticipants(int noOfParticipants) {
        this.noOfParticipants = noOfParticipants;
    }

    public Date getEventStartDate() {
        return eventStartDate;
    }

    public void setEventStartDate(Date eventStartDate) {
        this.eventStartDate = eventStartDate;
    }

    public Date getEventEndDate() {
        return eventEndDate;
    }

    public void setEventEndDate(Date eventEndDate) {
        this.eventEndDate = eventEndDate;
    }
}
