/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package nl._42.jarb.populate;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
public class PopulateTestConfig {
    
    @Bean
    public DriverManagerDataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.hsqldb.jdbcDriver");
        dataSource.setUrl("jdbc:hsqldb:mem:jarb-init");
        dataSource.setUsername("sa");
        return dataSource;
    }

}
