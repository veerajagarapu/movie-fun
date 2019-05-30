package org.superbiz.moviefun;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
public class DatabaseConfiguration {

    @Bean
    public DatabaseServiceCredentials actionDatabaseServiceCredentials(@Value("${VCAP_SERVICES}") String vcapServices) {
        return new DatabaseServiceCredentials(vcapServices);
    }

    @Bean
    public DataSource albumsDataSource(DatabaseServiceCredentials serviceCredentials) {
        MysqlDataSource albumsDataSource = new MysqlDataSource();
        albumsDataSource.setURL(serviceCredentials.jdbcUrl("albums-mysql"));
        HikariConfig albumsHikariConfig = new HikariConfig();
        albumsHikariConfig.setDataSource(albumsDataSource);
        return new HikariDataSource(albumsHikariConfig);
    }

    @Bean
    public DataSource moviesDataSource(DatabaseServiceCredentials serviceCredentials) {
        MysqlDataSource moviesDataSource = new MysqlDataSource();
        moviesDataSource.setURL(serviceCredentials.jdbcUrl("movies-mysql"));
        HikariConfig moviesHikariConfig = new HikariConfig();
        moviesHikariConfig.setDataSource(moviesDataSource);
        return new HikariDataSource(moviesHikariConfig);
    }

    @Bean
    public HibernateJpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
        hibernateJpaVendorAdapter.setDatabase(Database.MYSQL);
        hibernateJpaVendorAdapter.setDatabasePlatform("org.hibernate.dialect.MySQL5Dialect");
        hibernateJpaVendorAdapter.setGenerateDdl(true);
        return hibernateJpaVendorAdapter;
    }

    @Bean
    LocalContainerEntityManagerFactoryBean moviesEntityManagerFactory(DataSource moviesDataSource, HibernateJpaVendorAdapter jpaVendorAdapter) {
        LocalContainerEntityManagerFactoryBean moviesEntityFactoryBean = new LocalContainerEntityManagerFactoryBean();
        moviesEntityFactoryBean.setDataSource(moviesDataSource);
        moviesEntityFactoryBean.setJpaVendorAdapter(jpaVendorAdapter);
        moviesEntityFactoryBean.setPackagesToScan("org.superbiz.moviefun");
        moviesEntityFactoryBean.setPersistenceUnitName("movies");
        return moviesEntityFactoryBean;
    }

    @Bean
    LocalContainerEntityManagerFactoryBean albumsEntityManagerFactory(DataSource albumsDataSource, HibernateJpaVendorAdapter jpaVendorAdapter) {
        LocalContainerEntityManagerFactoryBean albumsEntityFactoryBean = new LocalContainerEntityManagerFactoryBean();
        albumsEntityFactoryBean.setDataSource(albumsDataSource);
        albumsEntityFactoryBean.setJpaVendorAdapter(jpaVendorAdapter);
        albumsEntityFactoryBean.setPackagesToScan("org.superbiz.moviefun");
        albumsEntityFactoryBean.setPersistenceUnitName("albums");
        return albumsEntityFactoryBean;
    }

    @Bean
    PlatformTransactionManager moviesPlatformTransactionManager(EntityManagerFactory moviesEntityManagerFactory) {
        return new JpaTransactionManager(moviesEntityManagerFactory);
    }

    @Bean
    PlatformTransactionManager albumsPlatformTransactionManager(EntityManagerFactory albumsEntityManagerFactory) {
        return new JpaTransactionManager(albumsEntityManagerFactory);
    }
}
