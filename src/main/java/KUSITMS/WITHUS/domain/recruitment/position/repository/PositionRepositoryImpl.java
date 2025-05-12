package KUSITMS.WITHUS.domain.recruitment.position.repository;


import KUSITMS.WITHUS.domain.recruitment.position.entity.Position;
import KUSITMS.WITHUS.domain.recruitment.recruitment.entity.Recruitment;
import KUSITMS.WITHUS.global.exception.CustomException;
import KUSITMS.WITHUS.global.exception.ErrorCode;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static KUSITMS.WITHUS.domain.recruitment.position.entity.QPosition.position;

@Repository
@RequiredArgsConstructor
public class PositionRepositoryImpl implements PositionRepository {

    private final PositionJpaRepository positionJpaRepository;
    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Position> findById(Long id) {
        return positionJpaRepository.findById(id);
    }

    @Override
    public Position getById(Long id) {
        return findById(id).orElseThrow(() -> new CustomException(ErrorCode.POSITION_NOT_EXIST));
    }

    @Override
    public Position save(Position position) {
        return positionJpaRepository.save(position);
    }

    @Override
    public void delete(Long id) {
        positionJpaRepository.deleteById(id);
    }

    @Override
    public Optional<Position> findByRecruitmentAndName(Recruitment recruitment, String name) {
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(position)
                        .where(
                                position.recruitment.eq(recruitment),
                                position.name.eq(name)
                        )
                        .fetchOne()
        );
    }
}
