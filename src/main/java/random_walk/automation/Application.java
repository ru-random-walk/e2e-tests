package random_walk.automation;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication(scanBasePackages = "random_walk.automation",
        exclude = { DataSourceAutoConfiguration.class })
@ConfigurationPropertiesScan
public class Application {}
