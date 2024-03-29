package shootingstar.var.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
public class Follow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long followId;

    private String followUUID;

    @ManyToOne
    @JoinColumn(name = "follower_id")
    private User follower;

    @ManyToOne
    @JoinColumn(name = "following_id")
    private User following;


    public Follow(User follower, User following) {
        this.followUUID = UUID.randomUUID().toString();
        this.follower = follower;
        this.following = following;
    }
}
