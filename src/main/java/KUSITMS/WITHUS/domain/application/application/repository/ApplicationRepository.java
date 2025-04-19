package KUSITMS.WITHUS.domain.application.application.repository;

import KUSITMS.WITHUS.domain.application.application.entity.Application;

import java.util.List;

public interface ApplicationRepository {
    Application getById(Long id);
    Application save(Application application);
    List<Application> findByRecruitmentId(Long recruitmentId);
    void delete(Long id);
}
