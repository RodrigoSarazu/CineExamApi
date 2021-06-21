package com.cine.api.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cine.api.dao.IMetodoPagoDao;
import com.cine.api.modelo.MetodoPago;

@Service
public class MetodoPagoServiceImpl implements IMetodoPagoService{
	
	@Autowired
	private IMetodoPagoDao metodopagoDao;
	
	@Override
	@Transactional(readOnly = true)
	public List<MetodoPago> findAll() {
		return (List<MetodoPago>)metodopagoDao.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public MetodoPago findById(Long idmetpago) {
		return metodopagoDao.findById(idmetpago).orElse(null);
	}

	@Override
	@Transactional
	public MetodoPago save(MetodoPago metodopago) {
		return metodopagoDao.save(metodopago);
	}

	@Override
	@Transactional
	public void delete(Long idmetpago) {
		metodopagoDao.deleteById(idmetpago);
	}
	
}
