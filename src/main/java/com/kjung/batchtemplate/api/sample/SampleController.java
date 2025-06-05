package com.kjung.batchtemplate.api.sample;

import com.kjung.batchtemplate.api.dto.SampleDto;
import com.kjung.batchtemplate.core.batch.BatchJobRunner;
import com.kjung.batchtemplate.jobs.sample.SampleJob;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/samples")
public class SampleController {

    private final BatchJobRunner runner;

    @PostMapping
    public boolean batchStart(@RequestBody SampleDto param) {
        runner.run(SampleJob.JOB_NAME, param);

        return true;
    }
}
