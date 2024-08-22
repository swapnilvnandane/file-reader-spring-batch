package com.filereader.app.config.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * DatabaseProperties class is used to read the properties from the application.yml file or from environment variables
 * which are related to the MySQL database.
 */
@Data
@Component
@ConfigurationProperties(prefix = "com.filereader.mysql")
public class DatabaseProperties {
    private String driverClassName;
    private String username;
    private String password;
    private String connectionUrl;
    private long maxWait;
    private int maxActive;
    private String validationQuery;
    private int minIdle;
}
