package org.gateway.repository;

import java.util.List;

import org.gateway.entity.ApiClient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApiClientRepository extends JpaRepository<ApiClient,Long>{
	public List<ApiClient> findAllByActiveTrue();
}
