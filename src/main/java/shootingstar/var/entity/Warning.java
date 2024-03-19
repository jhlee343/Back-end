package shootingstar.var.entity;

import jakarta.persistence.*;
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

    @NotNull
    private UUID warningUUID;

    @ManyToOne
    @JoinColumn(name = "review_id")
    private User userUUID;

    @Column(columnDefinition = "TEXT")
    private String warningContent;

    @Builder
    public Warning(UUID warningUUID, User userUUID, String warningContent){
        this.warningUUID=warningUUID;
        this.warningContent=warningContent;
        this.userUUID=userUUID;
    }
}
