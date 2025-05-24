package KUSITMS.WITHUS.domain.application.distributionRequest.entity;

import KUSITMS.WITHUS.domain.evaluation.evaluationCriteria.enumerate.EvaluationType;
import KUSITMS.WITHUS.domain.organization.organizationRole.entity.OrganizationRole;
import KUSITMS.WITHUS.domain.recruitment.position.entity.Position;
import KUSITMS.WITHUS.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "DISTRIBUTION_ASSIGNMENT")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class DistributionAssignment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DISTRIBUTION_REQUEST_ID", nullable = false)
    private DistributionRequest request;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POSITION_ID")
    private Position position;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORGANIZATION_ROLE_ID")
    private OrganizationRole organizationRole;

    @Enumerated(EnumType.STRING)
    @Column(name = "EVALUATION_TYPE", nullable = false)
    private EvaluationType evaluationType;

    @Column(nullable = false)
    private int count;

    public void updateRequest(DistributionRequest request) {
        this.request = request;
    }
}
