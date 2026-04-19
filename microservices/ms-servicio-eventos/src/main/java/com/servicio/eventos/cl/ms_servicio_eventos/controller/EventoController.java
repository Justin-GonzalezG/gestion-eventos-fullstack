package com.servicio.eventos.cl.ms_servicio_eventos.controller;

import com.servicio.eventos.cl.ms_servicio_eventos.model.Evento;
import com.servicio.eventos.cl.ms_servicio_eventos.service.EventoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/eventos")
public class EventoController {

    // Nos conectamos a la logica del servicio
    @Autowired
    private EventoService eventoService;

    // Buscar la lista de todos los eventos
    @GetMapping
    public ResponseEntity<List<Evento>> listar() {
        List<Evento> eventos = eventoService.obtenerTodos();
        
        // Retornamos 204 si la tabla eventos esta vacia
        if (eventos.isEmpty()) {
            return ResponseEntity.noContent().build(); 
        }
        
        // Retornamos 200 y la lista de objetos
        return ResponseEntity.ok(eventos); 
    }

    // Registrar o Guardar un Evento nuevo
    @PostMapping
    public ResponseEntity<Evento> crear(@RequestBody Evento evento) {
        Evento nuevoEvento = eventoService.guardar(evento);
        // Devolvemos HTTP 201 en senal de exito rotundo
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoEvento); 
    }

    // Traer un solo Evento en especifico
    @GetMapping("/{id}")
    public ResponseEntity<Evento> buscar(@PathVariable Long id) {
        try {
            Evento eventoExistente = eventoService.obtenerPorId(id);
            // Verificamos si existe antes de regresarlo
            if (eventoExistente == null) {
                return ResponseEntity.notFound().build(); // 404 No econtrado
            }
            return ResponseEntity.ok(eventoExistente);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Actualizar todos los datos de un Evento existente
    @PutMapping("/{id}")
    public ResponseEntity<Evento> actualizar(@PathVariable Long id, @RequestBody Evento evento) {
        try {
            Evento eventoExistente = eventoService.obtenerPorId(id);
            if (eventoExistente == null) {
                return ResponseEntity.notFound().build();
            }
            
            // Alteramos las variables temporales
            eventoExistente.setNombre(evento.getNombre());
            eventoExistente.setDescripcion(evento.getDescripcion());
            eventoExistente.setFechaHora(evento.getFechaHora());
            eventoExistente.setUbicacion(evento.getUbicacion());
            eventoExistente.setCapacidadMaxima(evento.getCapacidadMaxima());
            eventoExistente.setEstado(evento.getEstado());
            
            // Plasmamos en la base de datos
            Evento eventoActualizado = eventoService.guardar(eventoExistente);
            return ResponseEntity.ok(eventoActualizado);
        } catch (Exception e) {
             return ResponseEntity.notFound().build();
        }
    }

    // Borrar de raiz
    @DeleteMapping("/{id}")
    public ResponseEntity<?> borrar(@PathVariable Long id) {
        try {
            Evento eventoExistente = eventoService.obtenerPorId(id);
            if (eventoExistente == null) {
                return ResponseEntity.notFound().build(); 
            }
            eventoService.eliminar(id);
            // Avisamos que se elimino y por tanto 204 No Hay Contenido
            return ResponseEntity.noContent().build(); 
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
