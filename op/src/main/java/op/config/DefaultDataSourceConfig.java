package op.config;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

/**
 * POSTGRESQL_DATABASE_NAME=xxx
 * POSTGRESQL_DATABASE_PASSWORD=xxx
 * POSTGRESQL_DATABASE_USER=xxx
 * POSTGRESQL_PORT=tcp://xxx.xxx.xxx.xxx:5432
 * POSTGRESQL_PORT_5432_TCP=tcp://xxx.xxx.xxx.xxx:5432
 * POSTGRESQL_PORT_5432_TCP_ADDR=xxx.xxx.xxx.xxx
 * POSTGRESQL_PORT_5432_TCP_PORT=5432
 * POSTGRESQL_PORT_5432_TCP_PROTO=tcp
 * POSTGRESQL_SERVICE_HOST=xxx.xxx.xxx.xxx
 * POSTGRESQL_SERVICE_PORT=5432
 * POSTGRESQL_SERVICE_PORT_POSTGRESQL=5432
 * 
 * @author karln
 *
 */
@Configuration
@Profile("default")
class DefaultDataSourceConfig implements DataSourceConfig {
	private static final Logger log = Logger.getLogger(DefaultDataSourceConfig.class.getName());

    @Value("${dataSource.driverClassName}")
    private String driver;
    @Value("${dataSource.url}")
    private String url;
    @Value("${dataSource.username}")
    private String username;
    @Value("${dataSource.password}")
    private String password;

    @Bean
    @Override
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driver);
        
        String finalUrl = "jdbc:postgresql://";

        String dbUrl = System.getenv("POSTGRESQL_SERVICE_HOST");
        if ( dbUrl == null ) finalUrl += "localhost:5432/";
        else finalUrl += dbUrl + ":5432/" ;

        dbUrl = System.getenv("POSTGRESQL_DATABASE_NAME");
        if ( dbUrl == null ) finalUrl += "op";
        else finalUrl += dbUrl;

        log.info("finalUrl: " + finalUrl);
        dataSource.setUrl(finalUrl);
        String dbUsername = System.getenv("POSTGRESQL_DATABASE_USER");
        if ( dbUsername == null ) dbUsername = username;
        log.info("dbUsername: " + dbUsername);
        dataSource.setUsername(dbUsername);
        String dbPassword = System.getenv("POSTGRESQL_DATABASE_PASSWORD");        
        if ( dbPassword == null ) dbPassword = password;
        log.info("dbPassword: " + dbPassword);
        dataSource.setPassword(dbPassword);
        return dataSource;
    }
}
