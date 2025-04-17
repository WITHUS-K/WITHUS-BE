package KUSITMS.WITHUS.domain.organization.entity;

import KUSITMS.WITHUS.domain.application.position.entity.Position;
import KUSITMS.WITHUS.domain.user.entity.UserOrganization;
import KUSITMS.WITHUS.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ORGANIZATION")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Organization extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ORGANIZATION_ID")
    private Long id;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Builder.Default
    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Position> positions = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserOrganization> userOrganizations = new ArrayList<>();

    public static Organization create(String name) {
        return Organization.builder()
                .name(name)
                .build();
    }
    public void updateName(String name) {
        this.name = name;
    }

    public void addPosition(Position position) {
        this.positions.add(position);
        position.associateOrganization(this);
    }

    public void addUserOrganization(UserOrganization userOrganization) {
        this.userOrganizations.add(userOrganization);
        userOrganization.associateOrganization(this);
    }
}
