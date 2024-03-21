package shootingstar.var.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.w3c.dom.Text;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
public class Warning {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long warningId;

    private String warningUUID;

    @ManyToOne
    @JoinColumn(name = "review_id")
    private User userId;

    @Column(columnDefinition = "TEXT")
    @NotBlank
    private String warningContent;

    @Builder
    public Warning(String warningUUID, User userId, String warningContent){
        this.warningUUID= UUID.randomUUID().toString();
        this.warningContent=warningContent;
        this.userId=userId;
    }
}
