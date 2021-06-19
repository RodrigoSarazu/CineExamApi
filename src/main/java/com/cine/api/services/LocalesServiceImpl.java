package com.cine.api.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cine.api.dao.ILocalesDao;
import com.cine.api.modelo.Locales;

@Service
public class LocalesServiceImpl implements ILocalesService{

	@Autowired
	private ILocalesDao localesDao;
	
	@Override
	@Transactional(readOnly = true)
	public List<Locales> findAll() {
		return  (List<Locales>)localesDao.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public Locales findById(Long id) {
		return localesDao.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public Locales save(Locales local) {
		return localesDao.save(local);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		localesDao.deleteById(id);
		
	}

}
