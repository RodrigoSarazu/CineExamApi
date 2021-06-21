package com.cine.api.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cine.api.dao.IPeliculasDao;
import com.cine.api.modelo.Peliculas;

@Service
public class PeliculasServiceImpl implements IPeliculasService {

	@Autowired
	private IPeliculasDao peliculasDao;
	
	@Override
	@Transactional(readOnly = true)
	public List<Peliculas> findAll() {
		return (List<Peliculas>)peliculasDao.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public Peliculas findById(Long idpeli) {
		return peliculasDao.findById(idpeli).orElse(null);
		
	}

	@Override
	@Transactional
	public Peliculas save(Peliculas peliculas) {
		return peliculasDao.save(peliculas);
	}

	@Override
	public void delete(Long idpeli) {
		peliculasDao.deleteById(idpeli);
	}

}
