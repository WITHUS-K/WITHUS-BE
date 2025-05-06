package KUSITMS.WITHUS.domain.application.position.repository;


import KUSITMS.WITHUS.domain.application.position.entity.Position;
import KUSITMS.WITHUS.global.exception.CustomException;
import KUSITMS.WITHUS.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PositionRepositoryImpl implements PositionRepository {

    private final PositionJpaRepository positionJpaRepository;

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

}
