package KUSITMS.WITHUS.domain.mailTemplate.service;

import KUSITMS.WITHUS.domain.mailTemplate.dto.MailTemplateRequestDTO;
import KUSITMS.WITHUS.domain.mailTemplate.dto.MailTemplateResponseDTO;
import KUSITMS.WITHUS.domain.mailTemplate.entity.MailTemplate;
import KUSITMS.WITHUS.domain.mailTemplate.repository.MailTemplateJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MailTemplateServiceImpl implements MailTemplateService {

    private final MailTemplateJpaRepository mailTemplateJpaRepository;

    /**
     * 메일 템플릿 목록 조회
     * @return 모든 메일 템플릿의 요약 정보 리스트
     */
    @Override
    public List<MailTemplateResponseDTO.Summary> listAll() {
        return mailTemplateJpaRepository.findAll().stream()
                .map(MailTemplateResponseDTO.Summary::from)
                .toList();
    }

    /**
     * 메일 템플릿 생성
     * @param dto 메일 템플릿 생성 요청 DTO
     * @return 생성된 메일 템플릿의 상세 정보
     */
    @Override
    @Transactional
    public MailTemplateResponseDTO.Detail create(MailTemplateRequestDTO.Create dto) {
        MailTemplate ent = new MailTemplate(null, dto.name(), dto.subject(), dto.body());
        MailTemplate saved = mailTemplateJpaRepository.save(ent);
        return MailTemplateResponseDTO.Detail.from(saved);
    }

}
