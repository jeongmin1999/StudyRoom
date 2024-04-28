package Info;

public class RoomInfo {
    private String room_image;
    private String room_num;
    private Long max_people;
    private  Long min_people;
    private  String room_id;

    public RoomInfo() {
    }

    public RoomInfo(String imageUri) {
        this.room_image = imageUri;
    }

    public RoomInfo(String room_image, String room_num, Long max_people , Long min_people , String room_id) {
        this.room_image = room_image;
        this.room_num = room_num;
        this.max_people = max_people;
        this.min_people = min_people;
        this.room_id =room_id;
    }

    public String getRoom_id() {
        return room_id;
    }

    public void setRoom_id(String room_id) {
        this.room_id = room_id;
    }

    public Long getMin_people() {
        return min_people;
    }

    public void setMin_people(Long min_people) {
        this.min_people = min_people;
    }

    public String getRoom_image() {
        return room_image;
    }

    public void setRoom_image(String room_image) {
        this.room_image = room_image;
    }

    public String getRoom_num() {
        return room_num;
    }

    public void setRoom_num(String room_num) {
        this.room_num = room_num;
    }

    public Long getMax_people() {
        return max_people;
    }

    public void setMax_people(Long max_people) {
        this.max_people = max_people;
    }
}
