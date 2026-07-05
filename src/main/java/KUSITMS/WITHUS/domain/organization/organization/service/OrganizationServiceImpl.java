package KUSITMS.WITHUS.domain.organization.organization.service;

import KUSITMS.WITHUS.domain.organization.organization.dto.OrganizationRequestDTO;
import KUSITMS.WITHUS.domain.organization.organization.dto.OrganizationResponseDTO;
import KUSITMS.WITHUS.domain.organization.organization.entity.Organization;
import KUSITMS.WITHUS.domain.organization.organization.repository.OrganizationRepository;
import KUSITMS.WITHUS.domain.user.userOrganization.entity.UserOrganization;
import KUSITMS.WITHUS.domain.user.userOrganization.repository.UserOrganizationRepository;
import KUSITMS.WITHUS.global.exception.CustomException;
import KUSITMS.WITHUS.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrganizationServiceImpl implements OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final UserOrganizationRepository userOrganizationRepository;

    /**
     * 조직 생성
     * @param request 조직 이름을 입력받습니다.
     * @return 생성된 조직 정보(ID, 조직명) 반환
     */
    @Override
    @Transactional
    public OrganizationResponseDTO.Create create(OrganizationRequestDTO.Create request) {
        Organization organization = Organization.create(request.name());

        return OrganizationResponseDTO.Create.from(organizationRepository.save(organization));
    }

    /**
     * ID로 조직 단건 조회
     * @param id 조회할 조직의 ID 입력
     * @return 조직 상세 정보 반환
     */
    @Override
    public OrganizationResponseDTO.Detail getById(Long id) {
        Organization organization = organizationRepository.getById(id);

        return OrganizationResponseDTO.Detail.from(organization);
    }

    /**
     * 조직 정보 수정
     * @param id 수정할 조직의 ID
     * @param request 수정 정보(조직명)
     * @return 수정된 조직 정보(ID, 조직명) 반환
     */
    @Override
    @Transactional
    public OrganizationResponseDTO.Update update(Long id, OrganizationRequestDTO.Update request) {
        Organization organization = organizationRepository.getById(id);

        organization.updateName(request.name());

        return OrganizationResponseDTO.Update.from(organization);
    }

    /**
     * ID로 조직 삭제
     * @param id 삭제할 조직의 ID
     */
    @Override
    @Transactional
    public void delete(Long id) {
        organizationRepository.getById(id);
        organizationRepository.delete(id);
    }

    /**
     * 조직 전체 조회
     * @return 저장된 조직 전체를 반환
     */
    @Override
    public List<OrganizationResponseDTO.Summary> getAll() {
        return organizationRepository.findAll().stream()
                .map(OrganizationResponseDTO.Summary::from)
                .toList();
    }

    /**
     * 주어진 키워드를 포함하는 조직 목록을 조회합니다.
     * @param keyword 검색할 키워드 (조직 이름 일부)
     * @return 키워드를 포함하는 조직 리스트
     */
    @Override
    public List<Organization> search(String keyword) {
        return organizationRepository.findByNameContaining(keyword);
    }

    @Override
    public List<OrganizationResponseDTO.Summary> getMyOrganizations(Long userId) {
        Map<Long, Organization> organizationsById = new LinkedHashMap<>();

        userOrganizationRepository.findByUser_Id(userId).stream()
                .map(UserOrganization::getOrganization)
                .forEach(organization -> organizationsById.put(organization.getId(), organization));

        organizationRepository.findAll().stream()
                .filter(organization -> organization.getOrganizationRoles().stream()
                        .flatMap(role -> role.getUserOrganizationRoles().stream())
                        .anyMatch(link -> link.getUser().getId().equals(userId)))
                .forEach(organization -> organizationsById.putIfAbsent(organization.getId(), organization));

        return organizationsById.values().stream()
                .map(OrganizationResponseDTO.Summary::from)
                .toList();
    }

    /**
     * 조직의 초대 코드를 생성하거나 기존 코드를 반환합니다.
     * @param userId 요청을 보낸 사용자의 ID
     * @param organizationId 초대 코드를 생성하거나 조회할 조직의 ID
     * @return 조직의 초대 코드 반환
     * @throws CustomException 사용자가 조직에 속하지 않은 경우 FORBIDDEN 예외 발생
     */
    @Transactional
    public OrganizationResponseDTO.InviteCode generateOrGetInviteCode(Long userId, Long organizationId) {
        List<Long> userOrganizationIds = userOrganizationRepository.findOrganizationIdsByUserId(userId);

        if (!userOrganizationIds.contains(organizationId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        Organization organization = organizationRepository.getById(organizationId);

        if (organization.getInviteCode() != null && !organization.getInviteCode().isEmpty()) {
            log.info("기존 초대코드 반환: {}", organization.getInviteCode());
            return OrganizationResponseDTO.InviteCode.from(organization);
        }

        String inviteCode = generateRandomCode();
        log.info("새로운 초대코드 생성: {}", inviteCode);
        organization.setInviteCode(inviteCode);

        return OrganizationResponseDTO.InviteCode.from(organizationRepository.save(organization));
    }

    private String generateRandomCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    /**
     * 초대 코드로 조직 단건 조회
     * @param inviteCode 조회할 조직의 초대 코드 입력
     * @return 조직 상세 정보 반환
     */
    @Override
    public OrganizationResponseDTO.Detail getByInviteCode(String inviteCode) {
        Organization organization = organizationRepository.findByInviteCode(inviteCode)
                .orElseThrow(() -> new CustomException(ErrorCode.ORGANIZATION_NOT_EXIST));

        return OrganizationResponseDTO.Detail.from(organization);
    }
}
