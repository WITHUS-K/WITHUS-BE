package KUSITMS.WITHUS.domain.application.application.service.evaluator;

import KUSITMS.WITHUS.domain.application.application.entity.Application;
import KUSITMS.WITHUS.domain.application.application.repository.ApplicationRepository;
import KUSITMS.WITHUS.domain.application.applicationEvaluator.dto.ApplicationEvaluatorRequestDTO;
import KUSITMS.WITHUS.domain.application.applicationEvaluator.entity.ApplicationEvaluator;
import KUSITMS.WITHUS.domain.application.applicationEvaluator.repository.ApplicationEvaluatorRepository;
import KUSITMS.WITHUS.domain.application.distributionRequest.entity.DistributionAssignment;
import KUSITMS.WITHUS.domain.application.distributionRequest.entity.DistributionRequest;
import KUSITMS.WITHUS.domain.application.distributionRequest.repository.DistributionRequestRepository;
import KUSITMS.WITHUS.domain.organization.organizationRole.entity.OrganizationRole;
import KUSITMS.WITHUS.domain.organization.organizationRole.repository.OrganizationRoleRepository;
import KUSITMS.WITHUS.domain.user.user.entity.User;
import KUSITMS.WITHUS.domain.user.user.repository.UserRepository;
import KUSITMS.WITHUS.domain.user.userOrganizationRole.entity.UserOrganizationRole;
import KUSITMS.WITHUS.domain.user.userOrganizationRole.repository.UserOrganizationRoleRepository;
import KUSITMS.WITHUS.global.exception.CustomException;
import KUSITMS.WITHUS.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EvaluatorAssignmentService {

    private final ApplicationEvaluatorRepository applicationEvaluatorRepository;
    private final ApplicationRepository applicationRepository;
    private final UserOrganizationRoleRepository userOrganizationRoleRepository;
    private final UserRepository userRepository;
    private final OrganizationRoleRepository organizationRoleRepository;
    private final DistributionRequestRepository distributionRequestRepository;

    /**
     * 주어진 공고에 대해 요청된 파트별 정보에 따라 지원서 별 평가자 배정
     * @param request 공고 ID와 함께, 파트별로 평가 담당자 Role ID 및 지원서당 배정할 인원 수를 담은 요청 DTO
     */
    @Transactional
    public void distributeEvaluators(ApplicationEvaluatorRequestDTO.Distribute request) {
        // 요청 이력 dto -> 엔티티 매핑
        List<DistributionAssignment> assignments = request.assignments().stream()
                .map(dto -> {
                    OrganizationRole role = dto.organizationRoleId() != null
                            ? organizationRoleRepository.getById(dto.organizationRoleId())
                            : null;
                    return DistributionAssignment.builder()
                            .organizationRole(role)
                            .evaluationType(dto.evaluationType())
                            .count(dto.count())
                            .build();
                })
                .collect(Collectors.toList());

        // 요청 이력 저장
        DistributionRequest record = DistributionRequest.create(request.recruitmentId(), assignments);
        distributionRequestRepository.save(record);

        // 기존 배정 초기화
        Long recruitmentId = request.recruitmentId();
        applicationEvaluatorRepository.deleteAllByApplication_Recruitment_IdAndEvaluationType(recruitmentId, request.evaluationType());

        // 역할별 배정
        Random rnd = new Random();
        for (var part : request.assignments()) {
            // 후보 평가자 풀 (평가 담당자 역할을 가진 사용자들)
            List<User> pool = new ArrayList<>(userOrganizationRoleRepository
                    .findAllByOrganizationRole_Id(part.evaluatorRoleId())
                    .stream()
                    .map(UserOrganizationRole::getUser)
                    .toList());

            if (pool.size() < part.count()) {
                throw new CustomException(ErrorCode.INSUFFICIENT_EVALUATORS);
            }

            // 이 역할을 지원한 지원서 리스트 (organizationRoleId가 null이면 공통(역할 미지정) 지원서 조회)
            List<Application> apps = part.organizationRoleId() == null
                    ? applicationRepository.findByRecruitment_IdAndOrganizationRoleIsNull(recruitmentId)
                    : applicationRepository.findByRecruitment_IdAndOrganizationRole_Id(recruitmentId, part.organizationRoleId());

            // 각 지원서마다 랜덤 n명 배정
            for (Application app : apps) {
                Collections.shuffle(pool, rnd);
                List<User> chosen = pool.subList(0, part.count());
                List<ApplicationEvaluator> assigns = chosen.stream()
                        .map(u -> new ApplicationEvaluator(app, u, part.evaluationType()))
                        .collect(Collectors.toList());
                applicationEvaluatorRepository.saveAll(assigns);
            }
        }
    }

    /**
     * 주어진 지원서에 대해 기존에 배정된 평가 담당자 임의 재배정
     * @param request applicationId와 새로 배정할 평가자 User ID 리스트를 포함한 요청 DTO
     */
    @Transactional
    public void updateEvaluators(ApplicationEvaluatorRequestDTO.Update request) {
        Application application = applicationRepository.getById(request.applicationId());
        List<User> users = userRepository.findAllById(request.evaluatorIds());

        if (users.size() != request.evaluatorIds().size()) {
            throw new CustomException(ErrorCode.EVALUATOR_NOT_EXIST);
        }

        applicationEvaluatorRepository.deleteAllByApplication_IdAndEvaluationType(
                application.getId(),
                request.evaluationType()
        );

        List<ApplicationEvaluator> assigns = users.stream()
                .map(u -> new ApplicationEvaluator(application, u, request.evaluationType()))
                .toList();
        applicationEvaluatorRepository.saveAll(assigns);
    }
}
