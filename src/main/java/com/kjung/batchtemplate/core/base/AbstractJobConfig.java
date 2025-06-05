package com.kjung.batchtemplate.core.base;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.transaction.PlatformTransactionManager;

@RequiredArgsConstructor
public abstract class AbstractJobConfig {
    protected final JobRepository jobRepository;

    protected final PlatformTransactionManager transactionManager;

}
