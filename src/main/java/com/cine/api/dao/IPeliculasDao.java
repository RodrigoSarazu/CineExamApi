package com.cine.api.dao;

import org.springframework.data.jpa.repository.JpaRepository;


import com.cine.api.modelo.Peliculas;

public interface IPeliculasDao extends JpaRepository<Peliculas, Long>{

	

}
