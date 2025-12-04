package KUSITMS.WITHUS.domain.recruitment.recruitmentOrganizationRole.entity;

import KUSITMS.WITHUS.domain.organization.organizationRole.entity.OrganizationRole;
import KUSITMS.WITHUS.domain.recruitment.recruitment.entity.Recruitment;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "RECRUITMENT_ORGANIZATION_ROLE")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class RecruitmentOrganizationRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RECRUITMENT_ORGANIZATION_ROLE_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RECRUITMENT_ID", nullable = false)
    private Recruitment recruitment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORGANIZATION_ROLE_ID", nullable = false)
    private OrganizationRole organizationRole;

    public static RecruitmentOrganizationRole of(Recruitment recruitment, OrganizationRole role) {
        return RecruitmentOrganizationRole.builder()
                .recruitment(recruitment)
                .organizationRole(role)
                .build();
    }
}

