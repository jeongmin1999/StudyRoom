package Info;

public class My_GroupInfo {
    private String group_name;
    private String group_text;
    private Long max_people;
    private Long now_people;
    private String group_id;
    public My_GroupInfo() {
    }

    public My_GroupInfo(String group_name, Long max_people, Long now_people, String group_id,String group_text) {
        this.group_name = group_name;
        this.max_people = max_people;
        this.now_people = now_people;
        this.group_id = group_id;
        this.group_text = group_text;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public Long getMax_people() {
        return max_people;
    }

    public void setMax_people(Long max_people) {
        this.max_people = max_people;
    }

    public Long getNow_people() {
        return now_people;
    }

    public void setNow_people(Long now_people) {
        this.now_people = now_people;
    }

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public String getGroup_text() {
        return group_text;
    }

    public void setGroup_text(String group_text) {
        this.group_text = group_text;
    }
}
