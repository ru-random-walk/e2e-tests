package random_walk.automation.database.auth.entities;

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

    private Boolean enabled;

    private String avatar;

    private String description;

    @Column(name = "avatar_version")
    private Integer avatarVersion;
}
