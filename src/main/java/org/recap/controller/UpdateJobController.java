package org.recap.controller;

import org.recap.RecapConstants;
import org.recap.model.jpa.JobEntity;
import org.recap.repository.jpa.JobDetailsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by rajeshbabuk on 12/4/17.
 */
@RestController
@RequestMapping("/updateJobService")
public class UpdateJobController {

    private static final Logger logger = LoggerFactory.getLogger(UpdateJobController.class);

    @Autowired
    private JobDetailsRepository jobDetailsRepository;

    /**
     * This method is used to update the job entity.
     *
     * @param jobEntity the job entity
     * @return the string
     */
    @RequestMapping(value="/updateJob", method = RequestMethod.POST)
    public String updateJob(@RequestBody JobEntity jobEntity) {
        jobDetailsRepository.save(jobEntity);
        return RecapConstants.SUCCESS;
    }
}
