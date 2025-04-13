package KUSITMS.WITHUS.domain.user.service;

import KUSITMS.WITHUS.domain.user.dto.UserRequestDTO;

public interface UserService {
    void joinProcess(UserRequestDTO.Join request);
}
