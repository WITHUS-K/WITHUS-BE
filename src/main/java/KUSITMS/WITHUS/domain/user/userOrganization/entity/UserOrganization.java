package KUSITMS.WITHUS.domain.user.userOrganization.entity;

import KUSITMS.WITHUS.domain.organization.organization.entity.Organization;
import KUSITMS.WITHUS.domain.user.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "USER_ORGANIZATION")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserOrganization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ORGANIZATION_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORGANIZATION_ID", nullable = false)
    private Organization organization;

    public void associateUser(User user) {
        this.user = user;
    }

    public void associateOrganization(Organization organization) {
        this.organization = organization;
    }
}
