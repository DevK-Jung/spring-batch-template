package com.kjung.batchtemplate.jobs.sample;

import com.kjung.batchtemplate.core.base.AbstractJobConfig;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.stream.IntStream;

@Configuration
public class SampleJob extends AbstractJobConfig {

    public static final String JOB_NAME = "testJob";

    public SampleJob(JobRepository jobRepository,
                     PlatformTransactionManager transactionManager) {

        super(jobRepository, transactionManager);
    }

    @Bean
    public Job testJob() {
        return new JobBuilder(JOB_NAME, jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(step1())
                .build();
    }

    @Bean
    @JobScope
    public Step step1() {
        return new StepBuilder("step1", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    IntStream.range(0, 10)
                            .forEach(System.out::println);

                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }
}
