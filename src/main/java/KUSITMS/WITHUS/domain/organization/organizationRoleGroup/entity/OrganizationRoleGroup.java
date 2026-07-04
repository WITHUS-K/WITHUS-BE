package KUSITMS.WITHUS.domain.organization.organizationRoleGroup.entity;

import KUSITMS.WITHUS.domain.organization.organization.entity.Organization;
import KUSITMS.WITHUS.domain.organization.organizationRole.entity.OrganizationRole;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ORGANIZATION_ROLE_GROUP")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OrganizationRoleGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ORGANIZATION_ROLE_GROUP_ID")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "SELECTION_MIN_COUNT", nullable = false)
    private int selectionMinCount;

    @Column(name = "SELECTION_MAX_COUNT", nullable = false)
    private int selectionMaxCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORGANIZATION_ID", nullable = false)
    private Organization organization;

    @Builder.Default
    @OneToMany(mappedBy = "organizationRoleGroup")
    private List<OrganizationRole> organizationRoles = new ArrayList<>();

    public static OrganizationRoleGroup create(String name, int selectionMinCount, int selectionMaxCount, Organization organization) {
        return OrganizationRoleGroup.builder()
                .name(name)
                .selectionMinCount(selectionMinCount)
                .selectionMaxCount(selectionMaxCount)
                .organization(organization)
                .build();
    }
}
