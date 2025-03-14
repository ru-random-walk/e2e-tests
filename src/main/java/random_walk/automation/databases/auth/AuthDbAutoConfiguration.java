package random_walk.automation.databases.auth;

import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import random_walk.automation.config.SshTunnelManager;
import random_walk.automation.config.database.AuthDbConfig;

import java.util.HashMap;
import javax.persistence.EntityManagerFactory;

@Configuration
@ComponentScan("random_walk.automation.databases.auth")
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "random_walk.automation.databases.auth.repos",
        entityManagerFactoryRef = "authEntityManagerFactory",
        transactionManagerRef = "authTransactionManager")
@RequiredArgsConstructor
public class AuthDbAutoConfiguration {

    private final SshTunnelManager sshTunnelManager;

    private final AuthDbConfig config;

    @Bean
    @ConfigurationProperties("datasource.database-settings")
    public DataSourceProperties authDataSourceProperties() {
        DataSourceProperties dataSourceProperties = new DataSourceProperties();
        dataSourceProperties.setUsername(config.getUsername());
        dataSourceProperties.setPassword(config.getPassword());
        return dataSourceProperties;
    }

    @Bean
    public JdbcTemplate authJdbcTemplate() {
        return new JdbcTemplate(authDataSource());
    }

    @Bean
    public HikariDataSource authDataSource() {
        return authDataSourceProperties().initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean authEntityManagerFactory(@Qualifier("authBuilder") EntityManagerFactoryBuilder builder) {
        return builder.dataSource(authDataSource()).packages("random_walk.automation.databases.auth.entities").build();
    }

    @Bean
    public PlatformTransactionManager authTransactionManager(@Qualifier("authEntityManagerFactory") EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }

    @Bean(name = "authBuilder")
    public EntityManagerFactoryBuilder authManagerFactoryBuilder() {
        return new EntityManagerFactoryBuilder(new HibernateJpaVendorAdapter(), new HashMap<>(), null);
    }
}
