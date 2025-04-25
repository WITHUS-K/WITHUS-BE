package KUSITMS.WITHUS.domain.user.userOrganizationRole.entity;

import KUSITMS.WITHUS.domain.organization.organizationRole.entity.OrganizationRole;
import KUSITMS.WITHUS.domain.user.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "USER_ORGANIZATION_ROLE")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserOrganizationRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ORGANIZATION_ROLE_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORGANIZATION_ROLE_ID", nullable = false)
    private OrganizationRole organizationRole;

    public static UserOrganizationRole assign(User user, OrganizationRole role) {
        return UserOrganizationRole.builder()
                .user(user)
                .organizationRole(role)
                .build();
    }

    public void associateUser(User user) {
        this.user = user;
    }

    public void associateOrganizationRole(OrganizationRole organizationRole) {
        this.organizationRole = organizationRole;
    }
}
