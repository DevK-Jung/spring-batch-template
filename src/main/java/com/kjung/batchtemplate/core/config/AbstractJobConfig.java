package com.kjung.batchtemplate.core.config;

import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;

public abstract class AbstractJobConfig {
    @Autowired
    protected JobRepository jobRepository;

    @Autowired
    protected PlatformTransactionManager transactionManager;

}
