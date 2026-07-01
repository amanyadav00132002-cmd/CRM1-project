package in.sp.main.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import in.sp.main.entity.User;

public interface UserRepository
        extends JpaRepository<User, Long> {

    // Find user by email
    User findByEmail(String email);

    // Find users by role
    List<User> findByRole(String role);

    
}