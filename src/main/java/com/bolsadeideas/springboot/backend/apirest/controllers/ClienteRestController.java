package com.bolsadeideas.springboot.backend.apirest.controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.bolsadeideas.springboot.backend.apirest.models.entity.Cliente;
import com.bolsadeideas.springboot.backend.apirest.models.services.IClienteService;

@CrossOrigin(origins = { "http://localhost:4200" })
@RestController
@RequestMapping("/api")
public class ClienteRestController {

	@Autowired
	private IClienteService clienteService;

	@GetMapping("/clientes")
	public List<Cliente> index() {
		return clienteService.findAll();
	}

	@GetMapping("/clientes/{id}")
	//public Cliente show(@PathVariable Long id) {
	public ResponseEntity<?> show(@PathVariable Long id) {
		Cliente cliente=null;
		Map<String, Object> response = new HashMap<>();
		
		try {
			cliente=this.clienteService.findById(id);
		} catch (Exception e) {
			response.put("mensaje", "Error al realizar la consulta en la BD");
			response.put("error", e.getMessage().concat(" ").concat(e.getMessage() ) );
			return new ResponseEntity< Map<String, Object> >(response,HttpStatus.NOT_FOUND);
		}
		
		if(cliente==null) {
			response.put("mensaje", "El cliente  con  ID ".concat(id.toString().concat(" no existe en la BD") ) );
			return new ResponseEntity< Map<String, Object> >(response,HttpStatus.NOT_FOUND);
		}
		
		
		return new ResponseEntity<Cliente>(cliente,HttpStatus.OK);
	}

	
	//@ResponseStatus(HttpStatus.CREATED)
	//public Cliente create(@RequestBody Cliente cliente) {
	@PostMapping("/clientes")
	public ResponseEntity<?> create(@Valid @RequestBody Cliente cliente,BindingResult result) {
		Cliente clienteNuevo=null;
		Map<String, Object> response = new HashMap<>();
		
		/*if(result.hasErrors()) {
			List<String> errors= new ArrayList<>();
			for(FieldError err: result.getFieldErrors()) {
				errors.add("El campo:"+err.getField() +  err.getDefaultMessage());
			} Antes del JDK 8 nueva forma a continuacion con map y collector
			
			List<String> errors=result.getFieldErrors().stream()
					.map(err->"El campo:"+err.getField() +  err.getDefaultMessage())//cada file-erros(err) se convertira en un string
					.collect(Collectors.toList()); //pasar los sting a una lista
			
			response.put("error", errors );
			return new ResponseEntity< Map<String, Object> >(response,HttpStatus.BAD_REQUEST); //400
		}*/
		
		try {
			cliente.setCreateAt(new Date());
			clienteNuevo=clienteService.save(cliente);
		} catch (Exception e) {
			response.put("mensaje", "Error al insertar en la BD");
			response.put("error", e.getMessage().concat(" ").concat(e.getMessage() ) );
			return new ResponseEntity< Map<String, Object> >(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		response.put("mensaje", "el cliente ha sido creado con exito");
		response.put("cliente", clienteNuevo);
		return new ResponseEntity <Map<String, Object> >(response,HttpStatus.CREATED);
		
	}

	@PutMapping("/clientes/{id}")
	//@ResponseStatus(HttpStatus.CREATED)
	//public Cliente update(@RequestBody Cliente cliente, @PathVariable Long id) {
	public ResponseEntity<?> update(@RequestBody Cliente cliente, @PathVariable Long id) {
		Cliente currentCliente = this.clienteService.findById(id);
		Cliente clienteUpdate= null;
		
		Map<String, Object> response = new HashMap<>();
		
		
		if(currentCliente==null) {
			response.put("mensaje", "Error: no se puede editar, el cliente con  ID ".concat(id.toString().concat(" no existe en la BD") ) );
			return new ResponseEntity< Map<String, Object> >(response,HttpStatus.NOT_FOUND);
		}
		
		try {
			
			currentCliente.setNombre(cliente.getNombre());
			currentCliente.setApellido(cliente.getApellido());
			currentCliente.setEmail(cliente.getEmail());	
			currentCliente.setCreateAt(new Date());
			
			clienteUpdate=clienteService.save(currentCliente);			
			
			
			
			clienteUpdate=clienteService.save(currentCliente);
		} catch (Exception e) {
			response.put("mensaje", "Error al actualizar en la BD");
			response.put("error", e.getMessage().concat(" ").concat(e.getMessage() ) );
			return new ResponseEntity< Map<String, Object> >(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		response.put("mensaje", "el cliente ha sido actualizado con exito");
		response.put("cliente", clienteUpdate);
		return new ResponseEntity <Map<String, Object> >(response,HttpStatus.CREATED);
	}

	@DeleteMapping("/clientes/{id}")
	//@ResponseStatus(HttpStatus.NO_CONTENT)
	//public void delete(@PathVariable Long id) {
	public ResponseEntity<?> delete(@PathVariable Long id) {
		Cliente currentCliente = this.clienteService.findById(id);
		
		Map<String, Object> response = new HashMap<>();
		
		try {
			clienteService.delete(currentCliente); //si no existe dara error en el catch
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al eliminar en la BD");
			response.put("error", e.getMessage().concat(" ").concat(e.getMostSpecificCause().getMessage() ) );
			return new ResponseEntity< Map<String, Object> >(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		response.put("mensaje", "el cliente ha sido eliminado con exito");
		response.put("cliente", currentCliente);
		return new ResponseEntity <Map<String, Object> >(response,HttpStatus.CREATED);
	}
}
