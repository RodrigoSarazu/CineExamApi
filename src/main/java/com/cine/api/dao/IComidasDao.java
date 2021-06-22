package com.cine.api.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import com.cine.api.modelo.Comidas;

public interface IComidasDao extends JpaRepository<Comidas, Integer>{
	
}
