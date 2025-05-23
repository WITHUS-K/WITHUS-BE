package KUSITMS.WITHUS.domain.recruitment.recruitment.repository;

import KUSITMS.WITHUS.domain.recruitment.recruitment.entity.Recruitment;

import java.util.List;
import java.util.Optional;

public interface RecruitmentRepository {
    Recruitment getById(Long id);
    List<Recruitment> findByOrganization_IdIn(List<Long> organizationIds);
    Recruitment save(Recruitment recruitment);
    void delete(Long id);
    List<Recruitment> findAllByKeyword(String keyword);
    boolean existsByUrlSlug(String urlSlug);
    Optional<Recruitment> findByUrlSlug(String slug);
    List<Recruitment> findAllByOrganizationIds(List<Long> organizationIds);
}
