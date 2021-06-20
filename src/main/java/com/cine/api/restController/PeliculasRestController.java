package com.cine.api.restController;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.activation.FileTypeMap;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


import com.cine.api.modelo.Peliculas;
import com.cine.api.services.IPeliculasService;
import com.cine.api.services.IUploadFileService;


@RestController
@RequestMapping("/api")

public class PeliculasRestController {
	

	@Autowired
	private IPeliculasService peliculasService;
	
	
	@Autowired
	private IUploadFileService uploadService;

	
	// listar peliculas
	@GetMapping("/peliculas")
	public ResponseEntity<?> listar() {
		
		Map<String, Object> response = new HashMap<>();
		List<Peliculas>peliculas=peliculasService.findAll();
		
		response.put("peliculas", peliculas);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}
	
	
	// listar por idpeli
	
	@GetMapping("/peliculas/{idpeli}")
	public ResponseEntity<?> show(@PathVariable Long idpeli) {
		Peliculas peliculas = null;
		Map<String, Object> response = new HashMap<>();
		try {
			peliculas = peliculasService.findById(idpeli);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar la consulta");
			response.put("error", e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if (peliculas == null) {
			response.put("mensaje", "La pelicula idpeli: ".concat(idpeli.toString().concat(" no existe en la base de datos!")));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Peliculas>(peliculas, HttpStatus.OK);
	}
	

	
// agregar peliculas
	
	@PostMapping("/addpeliculas")
	public ResponseEntity<?> create(@Valid @RequestBody Peliculas peliculas, BindingResult result) {
		Peliculas peliculasNew = null;
		Map<String, Object> response = new HashMap<>();
		if (result.hasErrors()) {
			List<String> errors = result.getFieldErrors().stream()
					.map(err -> "El campo '" + err.getField() + "' " + err.getDefaultMessage())
					.collect(Collectors.toList());
			response.put("errors", errors);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}
		try {
			peliculasNew = peliculasService.save(peliculas);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar el insert en la base de datos");
			response.put("error", e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "la nueva pelicula ha sido creado con exito");
		response.put("peliculasNew", peliculasNew);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
	
	// eliminar peliculas
	
		@DeleteMapping("/eliminarpeli/{idpeli}")
		public ResponseEntity<?> delete(@PathVariable Long idpeli) {
			Map<String, Object> response = new HashMap<>();
			try {
				Peliculas peliculas = peliculasService.findById(idpeli);
				String nombreFotoAnterior = peliculas.getFotopeli();
				uploadService.eliminar(nombreFotoAnterior);	
				peliculasService.delete(idpeli);
			} catch (DataAccessException e) {
			
				response.put("mensaje", "Error al realizar el eliminar en la base de datos");
				response.put("error", e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			response.put("mensaje", "la pelicula ha sido eliminado con éxito");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
		}
		
		
		
		
		//enviar imagen
		@PostMapping("/peliculas/upload")
		public ResponseEntity<?>upload(@RequestParam("archivo") MultipartFile archivo,@RequestParam("idpeli")Long idpeli){
			
			Map<String, Object> response = new HashMap<>();
			Peliculas peliculas=peliculasService.findById(idpeli);
			if(!archivo.isEmpty()) {	
				String nombreArchivo=null;
				try {	
					nombreArchivo = uploadService.copiar(archivo);
				} 
				catch (IOException e) {
					response.put("mensaje", "Error al subir la imagen");
					
					response.put("error", e.getMessage().concat(":").concat(e.getCause().getMessage()));
					return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
				}	
				String nombreFotoAnterior=peliculas.getFotopeli();
				uploadService.eliminar(nombreFotoAnterior);		
				peliculas.setFotopeli(nombreArchivo);		
				peliculasService.save(peliculas);
				response.put("peliculas", peliculas);
				response.put("mensaje", "Has subido correctamente la imagen: "+nombreArchivo);	
			}
			
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
		}
		
		@GetMapping("/uploads/imagen/{nombreFoto:.+}")
		public ResponseEntity<Resource> verFoto(@PathVariable String nombreFoto){	
			Resource recurso=null;
			try {
				recurso=uploadService.cargar(nombreFoto);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			HttpHeaders cabecera= new HttpHeaders();
			cabecera.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""+recurso.getFilename()+"\"");
			return new ResponseEntity<Resource>(recurso,cabecera,HttpStatus.OK);
		}
		@GetMapping("/photopeli/{idpeli}")
		public ResponseEntity<?> getImage(@PathVariable Long idpeli) throws IOException {

			Peliculas peliculas = null;
			String fotopeli = null;
			Map<String, Object> response = new HashMap<>();

			try {
				peliculas = peliculasService.findById(idpeli);

				if (peliculas == null) {
					response.put("mensaje", "La pelicula con idpeli: " + idpeli.toString() + " no existe en la base de datos");
					return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
				} else {
					
					fotopeli = peliculas.getFotopeli();
					
					if (fotopeli == null) {
						response.put("mensaje", "la pelicula que seleccionó no cuenta con foto ");
						return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
					} else {
						File img = new File("uploads/" + fotopeli);
						return ResponseEntity.ok()
								.contentType(MediaType.valueOf(FileTypeMap.getDefaultFileTypeMap().getContentType(img)))
								.body(Files.readAllBytes(img.toPath()));
					}
				}
			} catch (DataAccessException e) {
				response.put("mensaje", "Error al realizar la consulta de la foto.");
				response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			
			
		}
		
		
}


