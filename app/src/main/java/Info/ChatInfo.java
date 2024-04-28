package Info;

public class ChatInfo {
    private String chat_name;
    private String chat_text;
    private String chat_date;
    private String chat_email;

    public ChatInfo() {
    }

    public ChatInfo(String chat_name, String chat_text, String chat_date, String chat_email) {
        this.chat_name = chat_name;
        this.chat_text = chat_text;
        this.chat_date = chat_date;
        this.chat_email = chat_email;
    }

    public String getChat_name() {
        return chat_name;
    }

    public void setChat_name(String chat_name) {
        this.chat_name = chat_name;
    }

    public String getChat_text() {
        return chat_text;
    }

    public void setChat_text(String chat_text) {
        this.chat_text = chat_text;
    }

    public String getChat_date() {
        return chat_date;
    }

    public void setChat_date(String chat_date) {
        this.chat_date = chat_date;
    }

    public String getChat_email() {
        return chat_email;
    }

    public void setChat_email(String chat_email) {
        this.chat_email = chat_email;
    }
}
