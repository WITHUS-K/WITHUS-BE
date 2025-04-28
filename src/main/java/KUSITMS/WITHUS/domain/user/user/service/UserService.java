package KUSITMS.WITHUS.domain.user.user.service;


import KUSITMS.WITHUS.domain.user.user.dto.UserRequestDTO;
import KUSITMS.WITHUS.domain.user.user.entity.User;

public interface UserService {
    User getById(Long id);
    User getUserByEmail(String email);
    void adminJoinProcess(UserRequestDTO.AdminJoin request);
    void userJoinProcess(UserRequestDTO.UserJoin request);
    void resetPassword(String email, String newPassword);
    boolean isEmailDuplicated(String email);
}