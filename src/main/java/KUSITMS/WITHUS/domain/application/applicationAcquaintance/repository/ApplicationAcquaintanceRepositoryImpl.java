package KUSITMS.WITHUS.domain.application.applicationAcquaintance.repository;

import KUSITMS.WITHUS.domain.application.applicationAcquaintance.entity.ApplicationAcquaintance;
import KUSITMS.WITHUS.global.exception.CustomException;
import KUSITMS.WITHUS.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ApplicationAcquaintanceRepositoryImpl implements ApplicationAcquaintanceRepository {

    private final ApplicationAcquaintanceJpaRepository applicationAcquaintanceJpaRepository;

    @Override
    public ApplicationAcquaintance getById(Long id) {
        return applicationAcquaintanceJpaRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.APPLICATION_NOT_EXIST));
    }

    @Override
    public boolean existsByApplication_IdAndUser_Id(Long applicationId, Long userId) {
        return applicationAcquaintanceJpaRepository.existsByApplication_IdAndUser_Id(applicationId, userId);
    }

    @Override
    public void deleteByApplication_IdAndUser_Id(Long applicationId, Long userId) {
        applicationAcquaintanceJpaRepository.deleteByApplication_IdAndUser_Id(applicationId, userId);
    }

    @Override
    public void save(ApplicationAcquaintance applicationAcquaintance) {
        applicationAcquaintanceJpaRepository.save(applicationAcquaintance);
    }
}
