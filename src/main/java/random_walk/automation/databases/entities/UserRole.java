package random_walk.automation.databases.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@NoArgsConstructor
@Entity
@Table(name = "user_role")
public class UserRole {

    @Id
    @JsonProperty("user_id")
    private UUID userId;

    @JsonProperty("role_id")
    private Integer roleId;

}
