package KUSITMS.WITHUS.domain.application.position.repository;


import KUSITMS.WITHUS.domain.application.position.entity.Position;
import KUSITMS.WITHUS.global.exception.CustomException;
import KUSITMS.WITHUS.global.exception.ErrorCode;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static KUSITMS.WITHUS.domain.application.position.entity.QPosition.position;

@Repository
@RequiredArgsConstructor
public class PositionRepositoryImpl implements PositionRepository {

    private final PositionJpaRepository positionJpaRepository;
    private final JPAQueryFactory queryFactory;

    @Override
    public Position getById(Long id) {
        return positionJpaRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.POSITION_NOT_EXIST));
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
    public List<Position> findByOrganizationId(Long organizationId) {
        return queryFactory.selectFrom(position)
                .where(position.organization.id.eq(organizationId))
                .fetch();
    }
}
