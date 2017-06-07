package org.recap.repository.jpa;

import org.recap.model.jpa.JobEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by rajeshbabuk on 4/4/17.
 */
public interface JobDetailsRepository extends JpaRepository<JobEntity, Integer> {

    /**
     * Finds job entity by using job name.
     *
     * @param jobName the job name
     * @return the job entity
     */
    JobEntity findByJobName(String jobName);
}
