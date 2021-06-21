package com.cine.api.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cine.api.modelo.MetodoPago;

public interface IMetodoPagoDao extends JpaRepository<MetodoPago, Long>{

}
