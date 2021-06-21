package com.cine.api.modelo;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="metodopago")
public class MetodoPago implements Serializable{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idmetpago;
	
	private String tipopago;
	private String imgtipopago;

	public MetodoPago() {
		super();
	}
	
	public MetodoPago(Long idmetpago, String tipopago, String imgtipopago) {
		super();
		this.idmetpago = idmetpago;
		this.tipopago = tipopago;
		this.imgtipopago = imgtipopago;
	}

	public Long getIdmetpago() {
		return idmetpago;
	}

	public void setIdmetpago(Long idmetpago) {
		this.idmetpago = idmetpago;
	}

	public String getTipopago() {
		return tipopago;
	}

	public void setTipopago(String tipopago) {
		this.tipopago = tipopago;
	}
	
	public String getImgtipopago() {
		return imgtipopago;
	}

	public void setImgtipopago(String imgtipopago) {
		this.imgtipopago = imgtipopago;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
}
