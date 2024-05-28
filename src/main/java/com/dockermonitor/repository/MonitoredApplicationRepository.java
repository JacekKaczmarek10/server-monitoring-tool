package com.dockermonitor.repository;

import com.dockermonitor.entity.MonitoredApplication;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MonitoredApplicationRepository extends JpaRepository<MonitoredApplication, Long> {}
