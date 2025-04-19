package KUSITMS.WITHUS.domain.recruitment.repository;

import KUSITMS.WITHUS.domain.recruitment.entity.Recruitment;
import KUSITMS.WITHUS.global.exception.CustomException;
import KUSITMS.WITHUS.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class RecruitmentRepositoryImpl implements RecruitmentRepository {

    private final RecruitmentJpaRepository recruitmentJpaRepository;

    @Override
    public Recruitment getById(Long id) {
        return recruitmentJpaRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.RECRUITMENT_NOT_EXIST));
    }

    @Override
    public List<Recruitment> findAll() {
        return recruitmentJpaRepository.findAll();
    }

    @Override
    public Recruitment save(Recruitment recruitment) {
        return recruitmentJpaRepository.save(recruitment);
    }

    @Override
    public void delete(Long id) {
        recruitmentJpaRepository.deleteById(id);
    }
}
