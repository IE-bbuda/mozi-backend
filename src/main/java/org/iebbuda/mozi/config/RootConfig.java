package org.iebbuda.mozi.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;

@Configuration
@PropertySource({"classpath:/application.properties"})
@EnableTransactionManagement
@MapperScan(basePackages = {"org.iebbuda.mozi.user.mapper", "org.iebbuda.mozi.policy.mapper", "org.iebbuda.mozi.product.mapper","org.iebbuda.mozi.profile.mapper"})
@ComponentScan(basePackages = {"org.iebbuda.mozi.user.service", "org.iebbuda.mozi.policy.service", "org.iebbuda.mozi.product.scheduler",
        "org.iebbuda.mozi.product.service","org.iebbuda.mozi.profile.service"})


public class RootConfig {
    @Value("${jdbc.driver}") String driver;
    @Value("${jdbc.url}") String url;
    @Value("${jdbc.username}") String username;
    @Value("${jdbc.password}") String password;

    @Autowired
    ApplicationContext applicationContext;

    @Bean
    public DataSource dataSource(){

        //커넥션 풀의 설정값을 담은 설정 객체
        HikariConfig config = new HikariConfig();

        config.setDriverClassName(driver);
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        //빠르고 가벼운 connection pool 라이브러리
        HikariDataSource dataSource = new HikariDataSource(config);

        return dataSource;
    }
    @Bean
    public SqlSessionFactory sqlSessionFactory() throws Exception{
        SqlSessionFactoryBean sqlSessionFactory = new SqlSessionFactoryBean();
        sqlSessionFactory.setConfigLocation(applicationContext.getResource("classpath:/mybatis-config.xml"));
        sqlSessionFactory.setDataSource(dataSource());

        return (SqlSessionFactory) sqlSessionFactory.getObject();
    }
    @Bean
    public DataSourceTransactionManager transactionManager(){
        DataSourceTransactionManager manager = new DataSourceTransactionManager(dataSource());
        return manager;
    }

    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

}


