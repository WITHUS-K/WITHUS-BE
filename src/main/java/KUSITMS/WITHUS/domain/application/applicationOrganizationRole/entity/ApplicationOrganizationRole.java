package KUSITMS.WITHUS.domain.application.applicationOrganizationRole.entity;

import KUSITMS.WITHUS.domain.application.application.entity.Application;
import KUSITMS.WITHUS.domain.organization.organizationRole.entity.OrganizationRole;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "APPLICATION_ORGANIZATION_ROLE",
        uniqueConstraints = @UniqueConstraint(
                name = "UK_APPLICATION_ORGANIZATION_ROLE",
                columnNames = {"APPLICATION_ID", "ORGANIZATION_ROLE_ID"}
        )
)
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ApplicationOrganizationRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "APPLICATION_ORGANIZATION_ROLE_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "APPLICATION_ID", nullable = false)
    private Application application;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORGANIZATION_ROLE_ID", nullable = false)
    private OrganizationRole organizationRole;

    public static ApplicationOrganizationRole of(Application application, OrganizationRole organizationRole) {
        ApplicationOrganizationRole link = ApplicationOrganizationRole.builder()
                .organizationRole(organizationRole)
                .build();
        link.assignApplication(application);
        return link;
    }

    public void assignApplication(Application application) {
        this.application = application;
    }
}
