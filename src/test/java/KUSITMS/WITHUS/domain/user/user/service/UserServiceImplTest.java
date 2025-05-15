package KUSITMS.WITHUS.domain.user.user.service;

import KUSITMS.WITHUS.domain.organization.organization.entity.Organization;
import KUSITMS.WITHUS.domain.user.user.dto.UserRequestDTO;
import KUSITMS.WITHUS.domain.user.user.entity.User;
import KUSITMS.WITHUS.global.common.enumerate.Gender;
import KUSITMS.WITHUS.mock.container.TestContainer;
import KUSITMS.WITHUS.util.SecurityTestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class UserServiceImplTest {

    private TestContainer testContainer;

    @BeforeEach
    void setUp() {
        testContainer = new TestContainer();

        Organization savedOrg = testContainer.organizationRepository.save(
                Organization.builder()
                        .name("큐시즘")
                        .build()
        );

        testContainer.verificationCacheUtil.markVerified("01012345678", Duration.ofMinutes(3));

        UserRequestDTO.UserJoin userJoinRequest = new UserRequestDTO.UserJoin(
                "김재관",
                LocalDate.of(2001, 4, 23),
                Gender.MALE,
                savedOrg.getId(),
                "test@example.com",
                "Abc123!@",
                "01012345678"
        );

        testContainer.userService.userJoinProcess(userJoinRequest);
        // SecurityTestUtil.setAuthentication(this.testMember);
    }

    @Test
    @DisplayName("회원 정보 조회 성공")
    void getUserSuccessfully() {
        // Given
        String email = "test@example.com";

        // When
        User user = testContainer.userRepository.findByEmail(email);

        // Then
        assertThat(user).isNotNull();
        assertThat(user.getName()).isEqualTo("김재관");
        assertThat(user.getEmail()).isEqualTo("test@example.com");
        assertThat(user.getPhoneNumber()).isEqualTo("01012345678");
    }
}