package com.cine.api.modelo;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="comidas")
public class Comidas {
	
	@Id
	@GeneratedValue(strategy =GenerationType.IDENTITY )
	private int idcom;
	private String nomcom;
	private double precio;
	private String fotocom;
	
	public Comidas() {
		super();
	}
	
	public Comidas(int idcom, String nomcom, double precio, String fotocom) {
		super();
		this.idcom = idcom;
		this.nomcom = nomcom;
		this.precio = precio;
		this.fotocom = fotocom;
	}

	public int getIdcom() {
		return idcom;
	}

	public void setIdcom(int idcom) {
		this.idcom = idcom;
	}

	public String getNomcom() {
		return nomcom;
	}

	public void setNomcom(String nomcom) {
		this.nomcom = nomcom;
	}

	public double getPrecio() {
		return precio;
	}

	public void setPrecio(double precio) {
		this.precio = precio;
	}

	public String getFotocom() {
		return fotocom;
	}

	public void setFotocom(String fotocom) {
		this.fotocom = fotocom;
	}
		
}
