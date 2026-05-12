package com.projeto.tcc.letramento.repository;

import com.projeto.tcc.letramento.model.Trail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TrailRepository extends JpaRepository<Trail, Long> {
}
