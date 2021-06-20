package com.cine.api.services;

import java.util.List;

import com.cine.api.modelo.Peliculas;

public interface IPeliculasService {
	public List<Peliculas>findAll();
	public Peliculas findById(Long idpeli);
	public Peliculas save(Peliculas pelicula);
	public void delete(Long idpeli);
}


	
		


