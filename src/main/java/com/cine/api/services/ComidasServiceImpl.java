package com.cine.api.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cine.api.dao.IComidasDao;
import com.cine.api.modelo.Comidas;

@Service
public class ComidasServiceImpl implements IComidasService{

	@Autowired
	private IComidasDao comidasDao;
	
	@Override
	@Transactional(readOnly = true)
	public List<Comidas> findAll() {
		return  (List<Comidas>)comidasDao.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public Comidas findById(int id) {
		return comidasDao.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public Comidas save(Comidas comidas) {
		return comidasDao.save(comidas);
	}

	@Override
	@Transactional
	public void delete(int id) {
		comidasDao.deleteById(id);
		
	}

}
