package KUSITMS.WITHUS.domain.application.application.service.evaluator;

import KUSITMS.WITHUS.domain.application.application.entity.Application;
import KUSITMS.WITHUS.domain.application.application.repository.ApplicationRepository;
import KUSITMS.WITHUS.domain.application.applicationEvaluator.dto.ApplicationEvaluatorRequestDTO;
import KUSITMS.WITHUS.domain.application.applicationEvaluator.entity.ApplicationEvaluator;
import KUSITMS.WITHUS.domain.application.applicationEvaluator.repository.ApplicationEvaluatorRepository;
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

    /**
     * 주어진 공고에 대해 요청된 파트별 정보에 따라 지원서 별 평가자 배정
     * @param request 공고 ID와 함께, 파트별로 평가 담당자 Role ID 및 지원서당 배정할 인원 수를 담은 요청 DTO
     */
    @Transactional
    public void distributeEvaluators(ApplicationEvaluatorRequestDTO.Distribute request) {
        // 기존 배정 초기화
        Long recruitmentId = request.recruitmentId();
        applicationEvaluatorRepository.deleteAllByApplication_Recruitment_Id(recruitmentId);

        // 파트별 배정
        Random rnd = new Random();
        for (var part : request.assignments()) {
            // 후보 평가자 풀
            List<User> pool = new ArrayList<>(userOrganizationRoleRepository
                    .findAllByOrganizationRole_Id(part.organizationRoleId())
                    .stream()
                    .map(UserOrganizationRole::getUser)
                    .toList());

            if (pool.size() < part.count()) {
                throw new CustomException(ErrorCode.INSUFFICIENT_EVALUATORS);
            }

            // 이 파트 지원서 리스트
            List<Application> apps = applicationRepository
                    .findByRecruitment_IdAndPosition_Id(recruitmentId, part.positionId());

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

        applicationEvaluatorRepository.deleteAllByApplication_Id(application.getId());

        List<ApplicationEvaluator> assigns = users.stream()
                .map(u -> new ApplicationEvaluator(application, u, request.evaluationType()))
                .toList();
        applicationEvaluatorRepository.saveAll(assigns);
    }
}
