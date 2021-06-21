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

import com.cine.api.modelo.Locales;
import com.cine.api.services.ILocalesService;
import com.cine.api.services.IUploadFileService;

@RestController
@RequestMapping("/api")
public class LocalesRestController {

	@Autowired
	private ILocalesService localesService;

	@Autowired
	private IUploadFileService uploadService;

	@GetMapping("/locales")
	public ResponseEntity<?> listar() {
		
		Map<String, Object> response = new HashMap<>();
		List<Locales>local=localesService.findAll();
		
		response.put("locales",local);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}

	@GetMapping("/locales/{id}")
	public ResponseEntity<?> show(@PathVariable Long id) {
		Locales local = null;
		Map<String, Object> response = new HashMap<>();
		try {
			local = localesService.findById(id);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar la consulta");
			response.put("error", e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if (local == null) {
			response.put("mensaje", "El cliente ud: ".concat(id.toString().concat(" no existe en la base de datos!")));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Locales>(local, HttpStatus.OK);
	}

	// agregar
	@PostMapping("/locales")
	public ResponseEntity<?> create(@Valid @RequestBody Locales local, BindingResult result) {
		Locales localNew = null;
		Map<String, Object> response = new HashMap<>();
		if (result.hasErrors()) {
			List<String> errors = result.getFieldErrors().stream()
					.map(err -> "El campo '" + err.getField() + "' " + err.getDefaultMessage())
					.collect(Collectors.toList());
			response.put("errors", errors);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}
		try {
			localNew = localesService.save(local);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar el insert en la base de datos");
			response.put("error", e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "El local ha sido creado con exito");
		response.put("localNew", localNew);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}

	// eliminar
	@DeleteMapping("/locales/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		Map<String, Object> response = new HashMap<>();
		try {
			Locales locales = localesService.findById(id);
			String nombreFotoAnterior = locales.getFoto();
			uploadService.eliminar(nombreFotoAnterior);	
			localesService.delete(id);
		} catch (DataAccessException e) {
		
			response.put("mensaje", "Error al realizar el eliminar en la base de datos");
			response.put("error", e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "El local ha sido eliminado con éxito");
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}
	//enviar imagen
	@PostMapping("/locales/upload")
	public ResponseEntity<?>upload(@RequestParam("archivo") MultipartFile archivo,@RequestParam("id") Long id){
		
		Map<String, Object> response = new HashMap<>();
		Locales local=localesService.findById(id);
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
			String nombreFotoAnterior=local.getFoto();
			uploadService.eliminar(nombreFotoAnterior);		
			local.setFoto(nombreArchivo);		
			localesService.save(local);
			response.put("local", local);
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
	
	@GetMapping("/photo/{id}")
	public ResponseEntity<?> getImage(@PathVariable Long id) throws IOException {

		Locales local = null;
		String foto = null;
		Map<String, Object> response = new HashMap<>();

		try {
			local = localesService.findById(id);

			if (local == null) {
				response.put("mensaje", "El local con id: " + id.toString() + " no existe en la base de datos");
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
			} else {
				
				foto = local.getFoto();
				
				if (foto == null) {
					response.put("mensaje", "El local que seleccionó no cuenta con foto de perfil");
					return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
				} else {
					File img = new File("uploads/" + foto);
					return ResponseEntity.ok()
							.contentType(MediaType.valueOf(FileTypeMap.getDefaultFileTypeMap().getContentType(img)))
							.body(Files.readAllBytes(img.toPath()));
				}
			}
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar la consulta del registro.");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
}
