package KUSITMS.WITHUS.domain.user.repository;

import KUSITMS.WITHUS.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {

    Boolean existsByEmail(String email);

    //email을 받아 DB 테이블에서 회원을 조회하는 메소드 작성
    User findByEmail(String email);
}