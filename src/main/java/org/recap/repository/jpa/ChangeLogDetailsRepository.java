package org.recap.repository.jpa;

import org.recap.model.jpa.ChangeLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by rajeshbabuk on 18/10/16.
 */
public interface ChangeLogDetailsRepository extends JpaRepository<ChangeLogEntity, Integer> {
}
