package random_walk.automation.database.auth.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import random_walk.automation.domain.enums.AuthType;

import java.util.UUID;
import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity
@Table(name = "auth_user")
@Accessors(chain = true)
public class AuthUser {

    @Id
    private UUID id;

    @Column(name = "full_name")
    private String fullName;

    private String username;

    private String email;

    private Boolean enabled;

    private String avatar;

    @Column(name = "account_type")
    @Enumerated(EnumType.STRING)
    private AuthType authType;

    private String description;

    @Column(name = "avatar_version")
    private Integer avatarVersion;
}
