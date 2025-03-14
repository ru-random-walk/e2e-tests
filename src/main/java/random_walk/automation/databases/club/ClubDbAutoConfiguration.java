package random_walk.automation.databases.club;

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
import random_walk.automation.config.database.ClubDbConfig;

import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityManagerFactory;

@Configuration
@ComponentScan("random_walk.automation.databases.club")
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "random_walk.automation.databases.club.repos",
        entityManagerFactoryRef = "clubEntityManagerFactory",
        transactionManagerRef = "clubTransactionManager")
@RequiredArgsConstructor
public class ClubDbAutoConfiguration {

    private final SshTunnelManager sshTunnelManager;

    private final ClubDbConfig config;

    @Bean
    @ConfigurationProperties("datasource.database-settings")
    public DataSourceProperties clubDataSourceProperties() {
        DataSourceProperties dataSourceProperties = new DataSourceProperties();
        dataSourceProperties.setUsername(config.getUsername());
        dataSourceProperties.setPassword(config.getPassword());
        return dataSourceProperties;
    }

    @Bean
    public JdbcTemplate clubJdbcTemplate() {
        return new JdbcTemplate(clubDataSource());
    }

    @Bean
    public HikariDataSource clubDataSource() {
        return clubDataSourceProperties().initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }

    private Map<String, String> addConfigurations() {
        var configuration = new HashMap<String, String>();
        configuration.put("hibernate.temp,use_jdbc_metadata_defaults", "false");
        configuration.put("hibernate.enable_lazy_load_no_trans", "true");
        return configuration;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean clubEntityManagerFactory(@Qualifier("clubBuilder") EntityManagerFactoryBuilder builder) {
        return builder.dataSource(clubDataSource())
                .packages("random_walk.automation.databases.club.entities")
                .properties(addConfigurations())
                .build();
    }

    @Bean
    public PlatformTransactionManager clubTransactionManager(@Qualifier("clubEntityManagerFactory") EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }

    @Bean(name = "clubBuilder")
    public EntityManagerFactoryBuilder clubManagerFactoryBuilder() {
        return new EntityManagerFactoryBuilder(new HibernateJpaVendorAdapter(), new HashMap<>(), null);
    }
}
