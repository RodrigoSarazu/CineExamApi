package com.cine.api.services;

import java.util.List;

import com.cine.api.modelo.MetodoPago;

public interface IMetodoPagoService {
	public List<MetodoPago>findAll();
	public MetodoPago findById(Long idmetpago);
	public MetodoPago save(MetodoPago metodopago);
	public void delete(Long idmetpago);
}