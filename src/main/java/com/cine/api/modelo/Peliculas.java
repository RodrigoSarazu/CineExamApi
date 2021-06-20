package com.cine.api.modelo;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name="pelicula")
public class Peliculas implements Serializable {
	
	@Id
	@GeneratedValue(strategy =GenerationType.IDENTITY )
	private Long idpeli;
	private String nompeli;
	private String infopeli;
	private String fotopeli;
	
	
	public Peliculas() {
		super();
	}
	
	
	
	public Peliculas(Long idpeli, String nompeli, String infopeli, String fotopeli) {
		super();
		this.idpeli = idpeli;
		this.nompeli = nompeli;
		this.infopeli = infopeli;
		this.fotopeli = fotopeli;
	}
	public Long getIdpeli() {
		return idpeli;
	}
	public void setIdpeli(Long idpeli) {
		this.idpeli = idpeli;
	}
	public String getNompeli() {
		return nompeli;
	}
	public void setNompeli(String nompeli) {
		this.nompeli = nompeli;
	}
	public String getInfopeli() {
		return infopeli;
	}
	public void setInfopeli(String infopeli) {
		this.infopeli = infopeli;
	}
	public String getFotopeli() {
		return fotopeli;
	}
	public void setFotopeli(String fotopeli) {
		this.fotopeli = fotopeli;
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
}
