package Entities;
import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;

@Entity
public class Label {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    int id;

    String text;
    String html_color;

    @ManyToMany(mappedBy = "labels", fetch = FetchType.EAGER)
    private List<Topic> topics = new LinkedList<>();

    public Label() {
    }

    public Label(String text, String html_color) {
        this.text = text;
        this.html_color = html_color;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getHtml_color() {
        return html_color;
    }

    public void setHtml_color(String html_color) {
        this.html_color = html_color;
    }

    public List<Topic> getTopics() {
        return topics;
    }

    public void setTopics(List<Topic> topics) {
        this.topics = topics;
    }
}
