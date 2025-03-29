package random_walk.automation.databases.auth.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@NoArgsConstructor
@Entity
@Table(name = "auth_user")
public class AuthUser {

    @Id
    private UUID id;

    @Column(name = "full_name")
    private String fullName;

    private String username;

    private String email;

    private String password;

    private Boolean enabled;

    private String avatar;

}
