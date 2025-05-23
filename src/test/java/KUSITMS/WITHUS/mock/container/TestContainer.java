package KUSITMS.WITHUS.mock.container;

import KUSITMS.WITHUS.domain.organization.organization.repository.OrganizationRepository;
import KUSITMS.WITHUS.domain.user.user.repository.UserRepository;
import KUSITMS.WITHUS.domain.user.user.service.UserServiceImpl;
import KUSITMS.WITHUS.global.infra.upload.service.FileUploadService;
import KUSITMS.WITHUS.global.util.redis.VerificationCache;
import KUSITMS.WITHUS.mock.repository.FakeOrganizationRepository;
import KUSITMS.WITHUS.mock.repository.FakeUserRepository;
import KUSITMS.WITHUS.util.FakeVerificationCacheUtil;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class TestContainer {

    public final UserRepository userRepository;
    public final UserServiceImpl userService;
    public final OrganizationRepository organizationRepository;
    public final VerificationCache verificationCache;
    public final FileUploadService uploadService;


    public TestContainer() {
        this.verificationCache = new FakeVerificationCacheUtil();
        this.uploadService = Mockito.mock(FileUploadService.class);

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        this.userRepository = new FakeUserRepository();
        this.organizationRepository = new FakeOrganizationRepository();
        this.userService = UserServiceImpl.builder()
                .userRepository(this.userRepository)
                .organizationRepository(this.organizationRepository)
                .verificationCache(this.verificationCache)
                .bCryptPasswordEncoder(passwordEncoder)
                .uploadService(uploadService)
                .build();
    }
}
