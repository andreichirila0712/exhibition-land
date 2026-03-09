package andrei.chirila.prove_yourself.presentation;

import andrei.chirila.prove_yourself.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "Presentations")
public class Presentation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID presentationId;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "thumbnail", nullable = false)
    private String thumbnail;
    @Column(name = "link")
    private String link;
    @Column(name = "description", nullable = false)
    private String description;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;

    public Presentation() {}

    public Presentation(String name, String thumbnail, String link, String description, User owner) {
        this.name = name;
        this.thumbnail = thumbnail;
        this.link = link;
        this.description = description;
        this.owner = owner;
    }

    public UUID getPresentationId() {
        return presentationId;
    }

    public void setPresentationId(UUID presentationId) {
        this.presentationId = presentationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }
}
