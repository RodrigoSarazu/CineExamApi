package com.cine.api.services;

import java.util.List;

import com.cine.api.modelo.Locales;

public interface ILocalesService {
	public List<Locales>findAll();
	public Locales findById(Long id);
	public Locales save(Locales local);
	public void delete(Long id);
}
