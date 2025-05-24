package KUSITMS.WITHUS.domain.application.distributionRequest.entity;

import KUSITMS.WITHUS.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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

    public static DistributionRequest create(Long recruitmentId, List<DistributionAssignment> assignments) {
        DistributionRequest req = DistributionRequest.builder()
                .recruitmentId(recruitmentId)
                .assignments(assignments)
                .build();
        req.getAssignments().forEach(a -> a.updateRequest(req));
        return req;
    }
}
