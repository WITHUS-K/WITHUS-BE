package KUSITMS.WITHUS.domain.organization.organizationRole.entity;

import KUSITMS.WITHUS.domain.organization.organization.entity.Organization;
import KUSITMS.WITHUS.domain.user.userOrganizationRole.entity.UserOrganizationRole;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ORGANIZATION_ROLE")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OrganizationRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ORGANIZATION_ROLE_ID")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String color;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORGANIZATION_ID", nullable = false)
    private Organization organization;

    @Builder.Default
    @OneToMany(mappedBy = "organizationRole", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserOrganizationRole> userOrganizationRoles = new ArrayList<>();

    public void update(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void associateOrganization(Organization organization) {
        this.organization = organization;
    }

    public void addUserOrganizationRole(UserOrganizationRole role) {
        this.userOrganizationRoles.add(role);
        role.associateOrganizationRole(this);
    }
}
