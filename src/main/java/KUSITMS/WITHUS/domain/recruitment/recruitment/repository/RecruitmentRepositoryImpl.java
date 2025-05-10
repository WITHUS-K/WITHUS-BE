package KUSITMS.WITHUS.domain.recruitment.recruitment.repository;

import KUSITMS.WITHUS.domain.recruitment.recruitment.entity.Recruitment;
import KUSITMS.WITHUS.global.exception.CustomException;
import KUSITMS.WITHUS.global.exception.ErrorCode;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static KUSITMS.WITHUS.domain.recruitment.recruitment.entity.QRecruitment.recruitment;


@Repository
@RequiredArgsConstructor
public class RecruitmentRepositoryImpl implements RecruitmentRepository {

    private final RecruitmentJpaRepository recruitmentJpaRepository;
    private final JPAQueryFactory queryFactory;

    @Override
    public Recruitment getById(Long id) {
        return recruitmentJpaRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.RECRUITMENT_NOT_EXIST));
    }

    @Override
    public List<Recruitment> findAll() {
        return queryFactory.selectFrom(recruitment)
                .where(recruitment.isTemporary.isFalse())
                .orderBy(recruitment.createdAt.desc())
                .fetch();
    }

    @Override
    public Recruitment save(Recruitment recruitment) {
        return recruitmentJpaRepository.save(recruitment);
    }

    @Override
    public void delete(Long id) {
        recruitmentJpaRepository.deleteById(id);
    }

    @Override
    public List<Recruitment> findAllByKeyword(String keyword) {
        return queryFactory.selectFrom(recruitment)
                .where(
                        recruitment.isTemporary.isFalse(),
                        keyword != null && !keyword.isBlank() ?
                                recruitment.title.containsIgnoreCase(keyword) : null
                )
                .orderBy(recruitment.createdAt.desc())
                .fetch();
    }

    @Override
    public boolean existsByUrlSlug(String urlSlug) {
        return queryFactory.selectOne()
                .from(recruitment)
                .where(recruitment.urlSlug.eq(urlSlug))
                .fetchFirst() != null;
    }

    @Override
    public Optional<Recruitment> findByUrlSlug(String slug) {
        Recruitment result = queryFactory.selectFrom(recruitment)
                .where(recruitment.urlSlug.eq(slug))
                .fetchOne();

        return Optional.ofNullable(result);
    }
}
