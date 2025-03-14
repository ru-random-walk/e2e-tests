package random_walk.automation.config.database;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public abstract class DatabaseAuthConfig {

    private String username;

    private String password;
}
