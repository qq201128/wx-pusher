package com.wang.repository;

import com.wang.dto.IdentityInfo;
import com.wang.dto.IdentityInfoKey;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *  身份信息存储
 */
public interface IdentityInfoRepository extends JpaRepository<IdentityInfo, IdentityInfoKey> {
}
