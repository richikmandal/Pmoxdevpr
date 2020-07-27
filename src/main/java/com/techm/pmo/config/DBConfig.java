package com.techm.pmo.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class DBConfig {
	
  
    @Bean(name="mySql")
    //@ConfigurationProperties(prefix="spring.datasource")
    public DataSource dataSourceOra() {
    	
    	DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("oracle.jdbc.driver.OracleDriver");
        dataSource.setUrl("jdbc:oracle:thin:@localhost:1522:XE");
        dataSource.setUsername("pmox");
        dataSource.setPassword("pmox");
        
        //return DataSourceBuilder.create().build();
        return dataSource;
    }
    
    @Bean(name = "jdbcMysql") 
    public JdbcTemplate jdbcTemplateOra(@Qualifier("mySql") DataSource mySql) { 
        return new JdbcTemplate(mySql); 
    }
    
}
