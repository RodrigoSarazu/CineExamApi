package com.cine.api.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cine.api.modelo.Usuario;

public interface IUsuarioDao extends JpaRepository<Usuario, Integer>{
	public Optional<Usuario> findByEmailAndPassword(String email, String password);
}
