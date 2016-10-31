package op.config;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

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
        String dbUrl = System.getenv("OPENSHIFT_POSTGRESQL_DB_URL");
        if ( dbUrl == null ) dbUrl = url;
        log.info("dbUrl: " + dbUrl);
        dataSource.setUrl(dbUrl);
        String dbUsername = System.getenv("OPENSHIFT_POSTGRESQL_DB_USERNAME");
        if ( dbUsername == null ) dbUsername = username;
        log.info("dbUsername: " + dbUsername);
        dataSource.setUsername(dbUsername);
        String dbPassword = System.getenv("OPENSHIFT_POSTGRESQL_DB_PASSWORD");        
        if ( dbPassword == null ) dbPassword = password;
        log.info("dbPassword: " + dbPassword);
        dataSource.setPassword(dbPassword);
        return dataSource;
    }
}
