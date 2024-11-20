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
@Table(name = "auth_user")
public class AuthUser {

    @Id
    private UUID uuid;

    @JsonProperty("full_name")
    private String fullName;

    private String username;

    private String email;

    private String password;

    private Boolean enabled;

}
