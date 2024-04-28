package Info;

public class MyReservationInfo {
    private String day;
    private String roomId;
    private String startTime;
    private String reservationId;
    private String room_name;

    public MyReservationInfo(String roomId, String day, String startTime, String reservationId, String room_name) {
        this.day = day;
        this.startTime = startTime;
        this.reservationId = reservationId;
        this.roomId  = roomId;
        this.room_name = room_name;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getReservationId() {
        return reservationId;
    }

    public void setReservationId(String reservationId) {
        this.reservationId = reservationId;
    }

    public String getRoom_name() {
        return room_name;
    }

    public void setRoom_name(String room_name) {
        this.room_name = room_name;
    }
}
