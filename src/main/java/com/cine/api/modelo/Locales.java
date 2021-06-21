package com.cine.api.modelo;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="locales")
public class Locales implements Serializable{

	@Id
	@GeneratedValue(strategy =GenerationType.IDENTITY )
	private Long id;
	
	private String nomloc;
	private String foto;
	
	public Locales() {
		super();
	}
	
	public Locales(Long id, String nomloc, String foto) {
		super();
		this.id = id;
		this.nomloc = nomloc;
		this.foto = foto;
	}
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getNomloc() {
		return nomloc;
	}
	
	public void setNomloc(String nomloc) {
		this.nomloc = nomloc;
	}
	
	public String getFoto() {
		return foto;
	}
	
	public void setFoto(String foto) {
		this.foto = foto;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
}
