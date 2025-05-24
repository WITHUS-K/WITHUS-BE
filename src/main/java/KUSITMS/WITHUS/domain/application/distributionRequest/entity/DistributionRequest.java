package KUSITMS.WITHUS.domain.application.distributionRequest.entity;

import KUSITMS.WITHUS.domain.application.applicationEvaluator.dto.ApplicationEvaluatorRequestDTO;
import KUSITMS.WITHUS.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "DISTRIBUTION_REQUEST")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DistributionRequest extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DISTRIBUTION_REQUEST_ID", nullable = false)
    private Long id;

    @Column(name = "RECRUITMENT_ID", nullable = false)
    private Long recruitmentId;

    @Builder.Default
    @OneToMany(mappedBy = "request", cascade = CascadeType.ALL, orphanRemoval = true)
    @Column(nullable = false)
    private List<DistributionAssignment> assignments = new ArrayList<>();

    public static DistributionRequest create(Long recruitmentId, List<ApplicationEvaluatorRequestDTO.Distribute.PartAssignment> dtos) {
        // Assignment 엔티티 생성
        List<DistributionAssignment> assigns = dtos.stream()
                .map(dto -> DistributionAssignment.builder()
                        .positionId(dto.positionId())
                        .organizationRoleId(dto.organizationRoleId())
                        .evaluationType(dto.evaluationType())
                        .count(dto.count())
                        .build())
                .collect(Collectors.toList());

        // DistributionRequest 빌더 호출
        DistributionRequest req = DistributionRequest.builder()
                .recruitmentId(recruitmentId)
                .assignments(assigns)
                .build();

        // 연관관계
        req.getAssignments().forEach(a -> a.updateRequest(req));
        return req;
    }
}
