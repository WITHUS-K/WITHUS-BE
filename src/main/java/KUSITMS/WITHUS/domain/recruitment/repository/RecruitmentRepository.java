package KUSITMS.WITHUS.domain.recruitment.repository;

import KUSITMS.WITHUS.domain.recruitment.entity.Recruitment;

import java.util.List;

public interface RecruitmentRepository {
    Recruitment getById(Long id);
    List<Recruitment> findAll();
    Recruitment save(Recruitment recruitment);
    void delete(Long id);
}
