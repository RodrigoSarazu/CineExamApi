package com.cine.api.services;

import com.cine.api.modelo.Usuario;

public interface IUsuarioService {
	//solo es necesario un método para el login xd
	public Usuario login(String email, String password);
}
