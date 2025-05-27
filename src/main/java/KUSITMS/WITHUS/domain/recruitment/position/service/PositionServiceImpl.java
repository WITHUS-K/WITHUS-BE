package KUSITMS.WITHUS.domain.recruitment.position.service;

import KUSITMS.WITHUS.domain.recruitment.position.dto.PositionRequestDTO;
import KUSITMS.WITHUS.domain.recruitment.position.dto.PositionResponseDTO;
import KUSITMS.WITHUS.domain.recruitment.position.entity.Position;
import KUSITMS.WITHUS.domain.recruitment.position.enumerate.PositionColor;
import KUSITMS.WITHUS.domain.recruitment.position.repository.PositionRepository;
import KUSITMS.WITHUS.domain.recruitment.recruitment.entity.Recruitment;
import KUSITMS.WITHUS.domain.recruitment.recruitment.repository.RecruitmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PositionServiceImpl implements PositionService {

    private final PositionRepository positionRepository;
    private final RecruitmentRepository recruitmentRepository;

    /**
     * 파트 생성
     * @param request 생성 요청 DTO
     * @return 생성된 파트 상세 정보
     */
    @Override
    @Transactional
    public PositionResponseDTO.Detail create(PositionRequestDTO.Create request) {
        Recruitment recruitment = recruitmentRepository.getById(request.recruitmentId());

        Position position = Position.builder()
                .name(request.name())
                .recruitment(recruitment)
                .color(PositionColor.getRandomColor())
                .build();

        return PositionResponseDTO.Detail.from(positionRepository.save(position));
    }

    /**
     * 파트 삭제
     * @param id 삭제할 파트 ID
     */
    @Override
    @Transactional
    public void delete(Long id) {
        positionRepository.getById(id);
        positionRepository.delete(id);
    }

    @Override
    public List<PositionResponseDTO.Detail> findAllByRecruitmentId(Long recruitmentId) {
        return positionRepository.findAllByRecruitmentId(recruitmentId).stream()
                .map(PositionResponseDTO.Detail::from)
                .toList();
    }
}
