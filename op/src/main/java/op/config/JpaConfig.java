package op.config;

import java.util.Properties;
import java.util.logging.Logger;

import javax.sql.DataSource;

import op.Application;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackageClasses = Application.class)
class JpaConfig implements TransactionManagementConfigurer {
	private static final Logger log = Logger.getLogger(JpaConfig.class.getName());

    @Value("${dataSource.driverClassName}")
    private String driver;
    @Value("${dataSource.url}")
    private String url;
    @Value("${dataSource.username}")
    private String username;
    @Value("${dataSource.password}")
    private String password;
    @Value("${hibernate.dialect}")
    private String dialect;
    @Value("${hibernate.hbm2ddl.auto}")
    private String hbm2ddlAuto;
/*
    @Bean
    public DataSource configureDataSource() {

        DataSource dataSource;
        String openshift = System.getenv("OPENSHIFT_APP_NAME");
        if ( openshift != null ) {
			try {
		        InitialContext ic = new InitialContext();
		        Context initialContext = (Context) ic.lookup("java:comp/env");
				dataSource = (DataSource) initialContext.lookup("jdbc/PostgreSQLDS");
			} catch (NamingException e) {
				throw new RuntimeException(e);
			}
        } else {		
		    dataSource = new DriverManagerDataSource();
		    ((DriverManagerDataSource)dataSource).setDriverClassName(driver);
		    ((DriverManagerDataSource)dataSource).setUrl(url);
		    ((DriverManagerDataSource)dataSource).setUsername(username);
		    ((DriverManagerDataSource)dataSource).setPassword(password);
        }
	    dataSource = new DriverManagerDataSource();
	    ((DriverManagerDataSource)dataSource).setDriverClassName(driver);
	    ((DriverManagerDataSource)dataSource).setUrl(url);
	    ((DriverManagerDataSource)dataSource).setUsername(username);
	    ((DriverManagerDataSource)dataSource).setPassword(password);

	    return dataSource;
    }
*/

    @Bean
    public DataSource configureDataSource() {
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
	
    @Bean
    public LocalContainerEntityManagerFactoryBean configureEntityManagerFactory() {
        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setDataSource(configureDataSource());
        entityManagerFactoryBean.setPackagesToScan("op", "opinions");
        entityManagerFactoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

        Properties jpaProperties = new Properties();
        jpaProperties.put(org.hibernate.cfg.Environment.DIALECT, dialect);
        jpaProperties.put(org.hibernate.cfg.Environment.HBM2DDL_AUTO, hbm2ddlAuto);
        entityManagerFactoryBean.setJpaProperties(jpaProperties);

        return entityManagerFactoryBean;
    }

    @Bean
    public PlatformTransactionManager annotationDrivenTransactionManager() {
        return new JpaTransactionManager();
    }
}
