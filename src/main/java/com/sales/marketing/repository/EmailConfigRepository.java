/**
 * 
 */
package com.sales.marketing.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sales.marketing.model.EmailConfigVO;

/**
 * @author PA
 *
 */
public interface EmailConfigRepository <P> extends JpaRepository<EmailConfigVO, Integer> {

}
