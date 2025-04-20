package random_walk.automation.database.chat;

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
import random_walk.automation.config.database.ChatDbConfig;

import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityManagerFactory;

@Configuration
@ComponentScan("random_walk.automation.database.chat")
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "random_walk.automation.database.chat.repos",
        entityManagerFactoryRef = "chatEntityManagerFactory",
        transactionManagerRef = "chatTransactionManager")
@RequiredArgsConstructor
public class ChatDbAutoConfiguration {

    private final SshTunnelManager sshTunnelManager;

    private final ChatDbConfig config;

    @Bean
    @ConfigurationProperties("datasource.database-settings")
    public DataSourceProperties chatDataSourceProperties() {
        DataSourceProperties dataSourceProperties = new DataSourceProperties();
        dataSourceProperties.setUsername(config.getUsername());
        dataSourceProperties.setPassword(config.getPassword());
        return dataSourceProperties;
    }

    @Bean
    public JdbcTemplate chatJdbcTemplate() {
        return new JdbcTemplate(chatDataSource());
    }

    @Bean
    public HikariDataSource chatDataSource() {
        return chatDataSourceProperties().initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }

    private Map<String, String> addConfigurations() {
        var configuration = new HashMap<String, String>();
        configuration.put("hibernate.temp,use_jdbc_metadata_defaults", "false");
        configuration.put("hibernate.enable_lazy_load_no_trans", "true");
        return configuration;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean chatEntityManagerFactory(@Qualifier("chatBuilder") EntityManagerFactoryBuilder builder) {
        return builder.dataSource(chatDataSource())
                .packages("random_walk.automation.database.chat.entities")
                .properties(addConfigurations())
                .build();
    }

    @Bean
    public PlatformTransactionManager chatTransactionManager(@Qualifier("chatEntityManagerFactory") EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }

    @Bean(name = "chatBuilder")
    public EntityManagerFactoryBuilder chatManagerFactoryBuilder() {
        return new EntityManagerFactoryBuilder(new HibernateJpaVendorAdapter(), new HashMap<>(), null);
    }
}
