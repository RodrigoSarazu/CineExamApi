package com.cine.api.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cine.api.dao.IUsuarioDao;
import com.cine.api.modelo.Usuario;

@Service
public class UsuarioService implements IUsuarioService{

	@Autowired
	IUsuarioDao usuarioDao;
	
	@Override
	public Usuario login(String email, String password) {
		return usuarioDao.findByEmailAndPassword(email, password).orElse(null);
	}

}
