package random_walk.automation.config;

import com.jcraft.jsch.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import random_walk.automation.config.database.SshConfig;

import java.util.Base64;
import java.util.Properties;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component
@RequiredArgsConstructor
public class SshTunnelManager {

    private final SshConfig sshConfig;

    private static final Logger logger = LoggerFactory.getLogger(SshTunnelManager.class);

    private Session session;

    @PostConstruct
    public void init() {
        try {
            JSch jsch = new JSch();
            jsch.addIdentity(null, Base64.getDecoder().decode(sshConfig.getSshPrivateKey()), null, null);
            logger.info("Создаем объект JSch");
            session = jsch.getSession(sshConfig.getSshUser(), sshConfig.getSshHost(), sshConfig.getSshPort());

            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);

            logger.info("Перенаправляем ssh соединение на локальный порт");
            session.setPortForwardingL(sshConfig.getRemotePort(), sshConfig.getRemoteHost(), sshConfig.getRemotePort());
            session.connect();

            logger.info(
                    "SSH tunnel established: {}:{} -> {}:{}",
                    sshConfig.getSshHost(),
                    sshConfig.getSshPort(),
                    sshConfig.getRemoteHost(),
                    sshConfig.getRemotePort());

        } catch (JSchException e) {
            logger.error("Failed to establish SSH tunnel: {}", e.getMessage());
            throw new RuntimeException("Failed to establish SSH tunnel", e);
        }
    }

    @PreDestroy
    public void destroy() {
        if (session != null) {
            session.disconnect();
            logger.info("SSH tunnel closed");
        }
    }
}
