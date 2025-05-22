package KUSITMS.WITHUS.domain.user.user.entity;

import KUSITMS.WITHUS.domain.application.comment.entity.Comment;
import KUSITMS.WITHUS.domain.interview.timeslotUser.entity.TimeSlotUser;
import KUSITMS.WITHUS.domain.user.user.enumerate.ProfileColor;
import KUSITMS.WITHUS.domain.user.userOrganization.entity.UserOrganization;
import KUSITMS.WITHUS.domain.user.user.enumerate.Role;
import KUSITMS.WITHUS.domain.user.userOrganizationRole.entity.UserOrganizationRole;
import KUSITMS.WITHUS.global.common.BaseEntity;
import KUSITMS.WITHUS.global.common.enumerate.Gender;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "USERS")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID")
    private Long id;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "EMAIL", nullable = false, unique = true)
    private String email;

    @Column(name = "PASSWORD", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "ROLE", nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(name = "GENDER", nullable = false)
    private Gender gender;

    @Column(name = "BIRTH_DATE")
    private LocalDate birthDate;

    @Column(name = "PHONE_NUMBER", nullable = false)
    private String phoneNumber;

    @Column(name = "PROFILE_IMAGE_URL")
    private String profileImageUrl;

    @Column(name = "PROFILE_COLOR")
    private ProfileColor profileColor;

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserOrganization> userOrganizations = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TimeSlotUser> timeSlotUsers = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserOrganizationRole> userOrganizationRoles = new ArrayList<>();

    public void updatePassword(String newEncodedPassword) {
        this.password = newEncodedPassword;
    }

    public void update(String name, String phoneNumber, String encodedNewPassword, String profileImageUrl) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.password = encodedNewPassword;

        if (profileImageUrl != null && !profileImageUrl.isBlank()) {
            this.profileImageUrl = profileImageUrl;
        }
    }

    public void addUserOrganization(UserOrganization userOrganization) {
        this.userOrganizations.add(userOrganization);
        userOrganization.associateUser(this);
    }

    public void addComment(Comment comment) {
        this.comments.add(comment);
        comment.associateUser(this);
    }

    public void addTimeSlotUser(TimeSlotUser timeSlotUser) {
        this.timeSlotUsers.add(timeSlotUser);
        timeSlotUser.assignUser(this);
    }

    public void addUserOrganizationRole(UserOrganizationRole role) {
        this.userOrganizationRoles.add(role);
        role.associateUser(this);
    }
}
