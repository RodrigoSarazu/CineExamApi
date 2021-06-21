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

import com.cine.api.modelo.MetodoPago;
import com.cine.api.services.IMetodoPagoService;
import com.cine.api.services.IUploadFileService;

@RestController
@RequestMapping("/api")
public class MetodoPagoRestController {
	
	@Autowired
	private IMetodoPagoService metodopagoService;
	
	@Autowired
	private IUploadFileService uploadService;

	// listar por método de pago
	@GetMapping("/listarMetodoPago")
	public ResponseEntity<?> listar() {
		
		Map<String, Object> response = new HashMap<>();
		List<MetodoPago>metodopago=metodopagoService.findAll();
		
		response.put("metodopago",metodopago);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}

	// listar por id método de pago
	@GetMapping("/listarMetodoPago/{idmetpago}")
	public ResponseEntity<?> show(@PathVariable Long idmetpago) {
		MetodoPago metodopago = null;
		Map<String, Object> response = new HashMap<>();
		try {
			metodopago = metodopagoService.findById(idmetpago);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar la consulta");
			response.put("error", e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if (metodopago == null) {
			response.put("mensaje", "El cliente ud: ".concat(idmetpago.toString().concat(" no existe en la base de datos!")));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<MetodoPago>(metodopago, HttpStatus.OK);
	}

	// agregar método de pago
	@PostMapping("/agregarMetodoPago")
	public ResponseEntity<?> create(@Valid @RequestBody MetodoPago metodopago, BindingResult result) {
		MetodoPago localNew = null;
		Map<String, Object> response = new HashMap<>();
		if (result.hasErrors()) {
			List<String> errors = result.getFieldErrors().stream()
					.map(err -> "El campo '" + err.getField() + "' " + err.getDefaultMessage())
					.collect(Collectors.toList());
			response.put("errors", errors);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}
		try {
			localNew = metodopagoService.save(metodopago);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar el insert en la base de datos");
			response.put("error", e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "El método de pago ha sido creado con exito");
		response.put("localNew", localNew);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}

	// eliminar método de pago
	@DeleteMapping("/eliminarMetodoPago/{idmetpago}")
	public ResponseEntity<?> delete(@PathVariable Long idmetpago) {
		Map<String, Object> response = new HashMap<>();
		try {
			MetodoPago metodopago = metodopagoService.findById(idmetpago);
			String nombreFotoAnterior = metodopago.getImgtipopago();
			uploadService.eliminar(nombreFotoAnterior);	
			metodopagoService.delete(idmetpago);
		} catch (DataAccessException e) {
		
			response.put("mensaje", "Error al eliminar este método en la base de datos");
			response.put("error", e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "El método de pago ha sido eliminado con éxito");
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}
	
	//enviar imagen método de pago
	@PostMapping("/metodopago/upload")
	public ResponseEntity<?> upload(@RequestParam("archivo") MultipartFile archivo,
			@RequestParam("idmetpago") Long idmetpago) {

		Map<String, Object> response = new HashMap<>();
		MetodoPago metodopago = metodopagoService.findById(idmetpago);
		if (!archivo.isEmpty()) {
			String nombreArchivo = null;
			try {
				nombreArchivo = uploadService.copiar(archivo);
			} catch (IOException e) {
				response.put("mensaje", "Error al subir la imagen");

				response.put("error", e.getMessage().concat(":").concat(e.getCause().getMessage()));
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			String nombreFotoAnterior = metodopago.getImgtipopago();
			uploadService.eliminar(nombreFotoAnterior);
			metodopago.setImgtipopago(nombreArchivo);
			metodopagoService.save(metodopago);
			response.put("metodopago", metodopago);
			response.put("mensaje", "Has subido correctamente la imagen: " + nombreArchivo);
		}
		
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}

	@GetMapping("/uploads/imagenMetPago/{nombreFoto:.+}")
	public ResponseEntity<Resource> verFoto(@PathVariable String nombreFoto) {
		Resource recurso = null;
		try {
			recurso = uploadService.cargar(nombreFoto);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		HttpHeaders cabecera = new HttpHeaders();
		cabecera.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + recurso.getFilename() + "\"");
		return new ResponseEntity<Resource>(recurso, cabecera, HttpStatus.OK);
	}

	@GetMapping("/photometpago/{idmetpago}")
	public ResponseEntity<?> getImage(@PathVariable Long idmetpago) throws IOException {

		MetodoPago metodopago = null;
		String fotometpag = null;
		Map<String, Object> response = new HashMap<>();

		try {
			metodopago = metodopagoService.findById(idmetpago);

			if (metodopago == null) {
				response.put("mensaje",
						"El método de pago con id: " + idmetpago.toString() + " no existe en la base de datos");
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
			} else {

				fotometpag = metodopago.getImgtipopago();

				if (fotometpag == null) {
					response.put("mensaje", "El método de pago que seleccionó no cuenta con foto");
					return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
				} else {
					File img = new File("uploads/" + fotometpag);
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
