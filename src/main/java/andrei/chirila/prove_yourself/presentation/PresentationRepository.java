package andrei.chirila.prove_yourself.presentation;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PresentationRepository extends CrudRepository<Presentation, UUID> {
    @NativeQuery(value = "SELECT p.* FROM Presentations p JOIN Users u ON p.user_id = u.id WHERE u.provider_id = ?1 AND p.name = ?2")
    Optional<Presentation> findPresentationByUserIdAndName(String userId, String name);
    @NativeQuery(value = "SELECT p.* FROM Presentations p JOIN Users u ON p.user_id = u.id WHERE u.provider_id = ?1")
    List<Presentation> findAllByUserId(String userId);
    @Modifying
    @NativeQuery(value = "DELETE FROM Presentations p USING Users u WHERE p.user_id = u.id AND u.provider_id = ?1 AND p.name = ?2")
    void deletePresentationByUserIdAndName(String userId, String name);
    @NativeQuery(value = "SELECT p.thumbnail FROM Presentations p JOIN Users u ON p.user_id = u.id WHERE u.provider_id = ?1 AND p.name = ?2")
    Optional<String> findThumbnailByUserIdAndName(String userId, String name);
}
