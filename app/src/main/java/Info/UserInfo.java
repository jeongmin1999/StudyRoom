package Info;

public class UserInfo {
    private String name;
    private String phoneNumber;
    private String level;

    public UserInfo(String name, String phoneNumber, String level) {

        this.name = name;
        this.phoneNumber = phoneNumber;
        this.level = level;
    }


    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public void setPhoneNumber(String phone) {
        this.phoneNumber = phone;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}
