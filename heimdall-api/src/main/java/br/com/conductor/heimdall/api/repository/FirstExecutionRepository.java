package br.com.conductor.heimdall.api.repository;

import br.com.conductor.heimdall.api.entity.FirstExecution;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FirstExecutionRepository extends JpaRepository<FirstExecution, String> {}
