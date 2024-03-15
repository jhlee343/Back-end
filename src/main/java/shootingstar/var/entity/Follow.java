package shootingstar.var.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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

    @NotBlank
    private UUID followUUID;

    @NotBlank
    private String followerId;

    @NotBlank
    private String followingId;

    @ManyToOne
    @JoinColumn(name = "user_Id")
    private User user;
}
