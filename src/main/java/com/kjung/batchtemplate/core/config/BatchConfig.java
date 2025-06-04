//package com.kjung.batchtemplate.core.config;
//
//import org.springframework.batch.core.repository.JobRepository;
//import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
//import org.springframework.batch.item.database.support.DefaultDataFieldMaxValueIncrementerFactory;
//import org.springframework.batch.support.DatabaseType;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.transaction.PlatformTransactionManager;
//import org.springframework.transaction.annotation.Isolation;
//
//import javax.sql.DataSource;
//
//@Configuration
//public class BatchConfig {
//
//    @Bean
//    public JobRepository createJobRepository(DataSource dataSource,
//                                             PlatformTransactionManager txManager) throws Exception {
//        JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
//        factory.setDataSource(dataSource);
//        factory.setTransactionManager(txManager);
//        factory.setDatabaseType(DatabaseType.fromMetaData(dataSource).name());
//        factory.setIncrementerFactory(new DefaultDataFieldMaxValueIncrementerFactory(dataSource));
//        factory.setIsolationLevelForCreateEnum(Isolation.DEFAULT);
//        factory.setTablePrefix("TEST_");
//        factory.setJdbcOperations(new JdbcTemplate(dataSource));
//        factory.afterPropertiesSet();
//
//        return factory.getObject();
//    }
//
//}
