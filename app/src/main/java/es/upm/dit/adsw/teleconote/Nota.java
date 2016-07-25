package es.upm.dit.adsw.teleconote;

import java.io.Serializable;
/**
 * @author cif
 * @version 20130426
 */
public class Nota implements Serializable {
	
	private static final long serialVersionUID = 4349879151820234260L;
	private String titulo;
	private String contenido;
	private String categoria;
	private boolean cifrado;
	public static final String NOTA = "Nota";

	public Nota(String titulo, String contenido, String categoria,
			boolean cifrado) {
		this.titulo = titulo;
		this.contenido = contenido;
		this.categoria = categoria;
		this.cifrado = cifrado;
	}

	public boolean isCifrado() {
		return cifrado;
	}

	public void setCifrado(boolean cifrado) {
		this.cifrado = cifrado;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getContenido() {
		return contenido;
	}

	public void setContenido(String contenido) {
		this.contenido = contenido;
	}

	public String getCategoria() {
		return categoria;
	}

	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (cifrado ? 1231 : 1237);
		result = prime * result
				+ ((contenido == null) ? 0 : contenido.hashCode());
		result = prime * result
				+ ((categoria == null) ? 0 : categoria.hashCode());
		result = prime * result + ((titulo == null) ? 0 : titulo.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Nota other = (Nota) obj;
		if (cifrado != other.cifrado)
			return false;
		if (contenido == null) {
			if (other.contenido != null)
				return false;
		} else if (!contenido.equals(other.contenido))
			return false;
		if (categoria == null) {
			if (other.categoria != null)
				return false;
		} else if (!categoria.equals(other.categoria))
			return false;
		if (titulo == null) {
			if (other.titulo != null)
				return false;
		} else if (!titulo.equals(other.titulo))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Nota [titulo=" + titulo + ", contenido=" + contenido
				+ ", categoria=" + categoria + ", cifrado=" + cifrado + "]";
	}

}
