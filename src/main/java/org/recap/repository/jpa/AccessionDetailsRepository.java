package org.recap.repository.jpa;

import org.recap.model.jpa.AccessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by rajeshbabuk on 8/5/17.
 */
public interface AccessionDetailsRepository extends JpaRepository<AccessionEntity, Integer> {
}
