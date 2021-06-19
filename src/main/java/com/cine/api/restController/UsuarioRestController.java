package com.cine.api.restController;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cine.api.modelo.Login;
import com.cine.api.modelo.Usuario;
import com.cine.api.services.IUsuarioService;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioRestController {

	@Autowired
	IUsuarioService usuarioService;

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody Login log) {
		Usuario usuario = null;
		Map<String, Object> response = new HashMap<>();
		try {
			usuario = usuarioService.login(log.getEmail(), log.getPassword());
			if (usuario == null) {
				response.put("mensaje", "El correo o la contraseña con incorrectos.");
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
			} else {
				return new ResponseEntity<>(usuario, HttpStatus.OK);
			}
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar la consulta a la base de datos.");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
