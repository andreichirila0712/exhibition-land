package andrei.chirila.prove_yourself.presentation;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PresentationRepository extends CrudRepository<Presentation, UUID> {

}
