package com.microservices.user_service.repository;

import com.microservices.user_service.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    boolean existsByEmail(String email);
    boolean existsByTelefono(String telefono);
    Optional<Usuario> findByEmail(String email);


}
