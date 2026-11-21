package mx.edu.utez.veterinariaintegradora.service.veterinario;

import mx.edu.utez.veterinariaintegradora.dto.veterinario.VeterinarioRequest;
import mx.edu.utez.veterinariaintegradora.dto.veterinario.VeterinarioResponse;
import mx.edu.utez.veterinariaintegradora.exception.RecursoNoEncontradoException;
import mx.edu.utez.veterinariaintegradora.exception.ReglaNegocioException;
import mx.edu.utez.veterinariaintegradora.model.usuario.RolUsuario;
import mx.edu.utez.veterinariaintegradora.model.usuario.Usuario;
import mx.edu.utez.veterinariaintegradora.model.veterinario.Veterinario;
import mx.edu.utez.veterinariaintegradora.repository.usuario.UsuarioRepository;
import mx.edu.utez.veterinariaintegradora.repository.veterinario.VeterinarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

/** HU-ADM-02 · Veterinarios */
@Service
public class VeterinarioService {

    private final VeterinarioRepository veterinarioRepository;
    private final UsuarioRepository usuarioRepository;

    public VeterinarioService(VeterinarioRepository veterinarioRepository, UsuarioRepository usuarioRepository) {
        this.veterinarioRepository = veterinarioRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public List<VeterinarioResponse> listar(Boolean activo, String especialidad) {
        return veterinarioRepository.findAll().stream()
                .filter(v -> activo == null || v.isActivo() == activo)
                .filter(v -> especialidad == null || (v.getEspecialidad() != null
                        && v.getEspecialidad().toLowerCase().contains(especialidad.toLowerCase())))
                .sorted(Comparator.comparing(Veterinario::getApellidoPaterno).thenComparing(Veterinario::getNombres))
                .map(VeterinarioResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public VeterinarioResponse obtener(Long id) {
        return VeterinarioResponse.from(buscar(id));
    }

    @Transactional
    public VeterinarioResponse crear(VeterinarioRequest request) {
        Usuario usuario = validarUsuario(request.getUsuarioId());
        if (veterinarioRepository.existsByUsuario_Id(usuario.getId())) {
            throw new ReglaNegocioException("El usuario " + usuario.getCorreo() + " ya tiene un perfil de veterinario.");
        }
        String cedula = limpiar(request.getCedulaProfesional());
        if (cedula != null && veterinarioRepository.existsByCedulaProfesionalIgnoreCase(cedula)) {
            throw new ReglaNegocioException("Ya existe un veterinario con la cédula " + cedula + ".");
        }
        Veterinario veterinario = new Veterinario();
        veterinario.setUsuario(usuario);
        copiarDatos(request, veterinario);
        veterinario.setActivo(request.getActivo() == null || request.getActivo());
        return VeterinarioResponse.from(veterinarioRepository.save(veterinario));
    }

    @Transactional
    public VeterinarioResponse actualizar(Long id, VeterinarioRequest request) {
        Veterinario veterinario = buscar(id);
        Usuario usuario = validarUsuario(request.getUsuarioId());
        if (veterinarioRepository.existsByUsuario_IdAndIdNot(usuario.getId(), id)) {
            throw new ReglaNegocioException("El usuario " + usuario.getCorreo() + " ya está ligado a otro veterinario.");
        }
        String cedula = limpiar(request.getCedulaProfesional());
        if (cedula != null && veterinarioRepository.existsByCedulaProfesionalIgnoreCaseAndIdNot(cedula, id)) {
            throw new ReglaNegocioException("Ya existe otro veterinario con la cédula " + cedula + ".");
        }
        veterinario.setUsuario(usuario);
        copiarDatos(request, veterinario);
        if (request.getActivo() != null) {
            veterinario.setActivo(request.getActivo());
        }
        return VeterinarioResponse.from(veterinarioRepository.save(veterinario));
    }

    /** Un veterinario inactivo deja de recibir citas nuevas (se valida al agendar). */
    @Transactional
    public VeterinarioResponse cambiarEstado(Long id, boolean activo) {
        Veterinario veterinario = buscar(id);
        veterinario.setActivo(activo);
        return VeterinarioResponse.from(veterinarioRepository.save(veterinario));
    }

    @Transactional(readOnly = true)
    public Veterinario buscar(Long id) {
        return veterinarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Veterinario", id));
    }

    private Usuario validarUsuario(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario", usuarioId));
        if (usuario.getRol() != RolUsuario.VETERINARIO) {
            throw new ReglaNegocioException("El usuario debe tener rol VETERINARIO para registrarse como veterinario.");
        }
        return usuario;
    }

    private void copiarDatos(VeterinarioRequest request, Veterinario veterinario) {
        veterinario.setNombres(request.getNombres().trim());
        veterinario.setApellidoPaterno(request.getApellidoPaterno().trim());
        veterinario.setApellidoMaterno(limpiar(request.getApellidoMaterno()));
        veterinario.setCedulaProfesional(limpiar(request.getCedulaProfesional()));
        veterinario.setEspecialidad(limpiar(request.getEspecialidad()));
    }

    private String limpiar(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }
}
