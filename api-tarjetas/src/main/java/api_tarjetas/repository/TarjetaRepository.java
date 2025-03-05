package api_tarjetas.repository;

import api_tarjetas.entity.Tarjetas;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface TarjetaRepository extends JpaRepository<Tarjetas, Long> {
    List<Tarjetas> findByCuentaId(Long cuentaId);
    boolean existsByNumeroTarjeta(String numeroTarjeta);
    boolean existsByCuentaIdAndTipoTarjeta(Long cuentaId, String tipoTarjeta);
    Optional<Tarjetas> findByIdAndCuentaId(Long id, Long cuentaId);
}

