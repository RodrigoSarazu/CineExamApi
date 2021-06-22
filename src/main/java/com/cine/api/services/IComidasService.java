package com.cine.api.services;

import java.util.List;

import com.cine.api.modelo.Comidas;

public interface IComidasService {
	public List<Comidas>findAll();
	public Comidas findById(int id);
	public Comidas save(Comidas comidas);
	public void delete(int id);
}
