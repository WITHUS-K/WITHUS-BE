package KUSITMS.WITHUS.domain.user.user.service;


import KUSITMS.WITHUS.domain.user.user.dto.UserRequestDTO;
import KUSITMS.WITHUS.domain.user.user.entity.User;

public interface UserService {
    User getUserByEmail(String email);
    void adminJoinProcess(UserRequestDTO.AdminJoin request);
    void userJoinProcess(UserRequestDTO.UserJoin request);
    boolean isEmailDuplicated(String email);
    void requestEmailVerification(String name, String email);
    void requestPhoneVerification(String phoneNumber);
    void confirmVerification(String identifier, String inputCode);
    String reissueAccessToken(String refreshToken);
    void logout();
}