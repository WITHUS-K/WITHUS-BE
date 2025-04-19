package KUSITMS.WITHUS.domain.application.application.repository;

import KUSITMS.WITHUS.domain.application.application.entity.Application;
import KUSITMS.WITHUS.global.exception.CustomException;
import KUSITMS.WITHUS.global.exception.ErrorCode;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static KUSITMS.WITHUS.domain.application.application.entity.QApplication.application;


@Repository
@RequiredArgsConstructor
public class ApplicationRepositoryImpl implements ApplicationRepository {

    private final ApplicationJpaRepository applicationJpaRepository;
    private final JPAQueryFactory queryFactory;

    @Override
    public Application getById(Long id) {
        return applicationJpaRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.APPLICATION_NOT_EXIST));
    }

    @Override
    public Application save(Application application) {
        return applicationJpaRepository.save(application);
    }

    @Override
    public List<Application> findByRecruitmentId(Long recruitmentId) {
        return queryFactory
                .selectFrom(application)
                .where(application.template.recruitment.id.eq(recruitmentId))
                .fetch();
    }

    @Override
    public void delete(Long id) {
        applicationJpaRepository.deleteById(id);
    }
}
