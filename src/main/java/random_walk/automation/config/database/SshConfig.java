package random_walk.automation.config.database;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@NoArgsConstructor
@Configuration
@ConfigurationProperties("datasource.database-settings.ssh")
public class SshConfig {

    private Integer sshPort;

    private String sshHost;

    private String sshUser;

    private String sshPrivateKey;

    private String remoteHost;

    private Integer remotePort;
}
