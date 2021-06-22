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

import com.cine.api.modelo.Comidas;
import com.cine.api.services.IComidasService;
import com.cine.api.services.IUploadFileService;

@RestController 
@RequestMapping("/api/comidas")
public class ComidasRestController {
	
	@Autowired
	private IComidasService comidasService;

	@Autowired
	private IUploadFileService uploadService;

	@GetMapping("/snacks")
	public ResponseEntity<?> listar() {
		
		Map<String, Object> response = new HashMap<>();
		List<Comidas>comida=comidasService.findAll();
		
		response.put("comidas",comida);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}

	@GetMapping("/snacks/{id}")
	public ResponseEntity<?> show(@PathVariable int id) {
		Comidas comida = null;
		Map<String, Object> response = new HashMap<>();
		try {
			comida = comidasService.findById(id);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar la consulta");
			response.put("error", e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		return new ResponseEntity<Comidas>(comida, HttpStatus.OK);
	}

	// agregar
	@PostMapping("/snacks")
	public ResponseEntity<?> create(@Valid @RequestBody Comidas comida, BindingResult result) {
		Comidas comidaNew = null;
		Map<String, Object> response = new HashMap<>();
		if (result.hasErrors()) {
			List<String> errors = result.getFieldErrors().stream()
					.map(err -> "El campo '" + err.getField() + "' " + err.getDefaultMessage())
					.collect(Collectors.toList());
			response.put("errors", errors);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}
		try {
			comidaNew = comidasService.save(comida);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar el insert en la base de datos");
			response.put("error", e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "La comida ha sido creado con exito");
		response.put("comidaNew", comidaNew);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}

	// eliminar
	@DeleteMapping("/snacks/{id}")
	public ResponseEntity<?> delete(@PathVariable int id) {
		Map<String, Object> response = new HashMap<>();
		try {
			Comidas comidas = comidasService.findById(id);
			String nombreFotoAnterior = comidas.getFotocom();
			uploadService.eliminar(nombreFotoAnterior);	
			comidasService.delete(id);
		} catch (DataAccessException e) {
		
			response.put("mensaje", "Error al realizar el eliminar en la base de datos");
			response.put("error", e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "La comida ha sido eliminado con éxito");
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}
	//enviar imagen
	@PostMapping("/snacks/upload")
	public ResponseEntity<?>upload(@RequestParam("archivo") MultipartFile archivo,@RequestParam("idcom") int idcom){
		
		Map<String, Object> response = new HashMap<>();
		Comidas comidas=comidasService.findById(idcom);
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
			String nombreFotoAnterior=comidas.getFotocom();
			uploadService.eliminar(nombreFotoAnterior);		
			comidas.setFotocom(nombreArchivo);		
			comidasService.save(comidas);
			response.put("comidas", comidas);
			response.put("mensaje", "Has subido correctamente la imagen: "+nombreArchivo);	
		}
		
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
	@GetMapping("/uploads/img/{nombreFoto:.+}")
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
	@GetMapping("/photocom/{id}")
	public ResponseEntity<?> getImage(@PathVariable int id) throws IOException {

		Comidas comida = null;
		String fotocom = null;
		Map<String, Object> response = new HashMap<>();

		try {
			comida = comidasService.findById(id);
				
				fotocom = comida.getFotocom();
				
				if (fotocom == null) {
					response.put("mensaje", "La comida que seleccionó no cuenta con foto de perfil");
					return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
				} else {
					File img = new File("uploads/" + fotocom);
					return ResponseEntity.ok()
							.contentType(MediaType.valueOf(FileTypeMap.getDefaultFileTypeMap().getContentType(img)))
							.body(Files.readAllBytes(img.toPath()));
				}
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar la consulta del registro.");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
