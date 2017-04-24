package org.recap.repository.jpa;

import org.recap.model.jpa.JobEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by rajeshbabuk on 4/4/17.
 */
public interface JobDetailsRepository extends JpaRepository<JobEntity, Integer> {

    JobEntity findByJobName(String jobName);
}
