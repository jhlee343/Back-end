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

    @NotNull
    private UUID followUUID;

    @ManyToOne
    @JoinColumn(name = "follower_id")
    private User followerId;

    @ManyToOne
    @JoinColumn(name = "following_id")
    private User followingId;


    public Follow(UUID followUUID, User follower, User following) {
        this.followUUID = followUUID;
        this.followerId = follower;
        this.followingId = following;
    }
}
