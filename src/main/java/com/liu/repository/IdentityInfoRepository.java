package com.liu.repository;

import com.liu.dto.IdentityInfo;
import com.liu.dto.IdentityInfoKey;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *  身份信息存储
 */
public interface IdentityInfoRepository extends JpaRepository<IdentityInfo, IdentityInfoKey> {
}
