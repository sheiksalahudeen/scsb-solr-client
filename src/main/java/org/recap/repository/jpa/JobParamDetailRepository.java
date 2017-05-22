package org.recap.repository.jpa;

import org.recap.model.jpa.JobParamEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Created by angelind on 2/5/2017.
 */
@RepositoryRestResource(collectionResourceRel = "jobParam", path = "jobParam")
public interface JobParamDetailRepository extends JpaRepository<JobParamEntity, Integer> {

    JobParamEntity findByJobName(String jobName);

}
