package org.gateway.repository;

import org.gateway.entity.ApiClient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApiClientRepository extends JpaRepository<ApiClient,Long>{

}
