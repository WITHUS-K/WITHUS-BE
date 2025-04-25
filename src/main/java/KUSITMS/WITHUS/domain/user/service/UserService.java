package KUSITMS.WITHUS.domain.user.service;

import KUSITMS.WITHUS.domain.user.dto.UserRequestDTO;
import KUSITMS.WITHUS.domain.user.entity.User;

public interface UserService {
    User getUserByEmail(String email);
    void adminJoinProcess(UserRequestDTO.AdminJoin request);
    void userJoinProcess(UserRequestDTO.UserJoin request);
    boolean isEmailDuplicated(String email);
}
