package com.unihub.unihub.user.repository;

import com.unihub.unihub.user.entity.User;
import com.unihub.unihub.user.entity.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 用户数据访问层
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * 根据用户名查找用户
     */
    Optional<User> findByUsername(String username);
    
    /**
     * 根据邮箱查找用户
     */
    Optional<User> findByEmail(String email);
    
    /**
     * 根据学号查找用户
     */
    Optional<User> findByStudentId(String studentId);
    
    /**
     * 根据手机号查找用户
     */
    Optional<User> findByPhoneNumber(String phoneNumber);
    
    /**
     * 检查用户名是否存在
     */
    boolean existsByUsername(String username);
    
    /**
     * 检查邮箱是否存在
     */
    boolean existsByEmail(String email);
    
    /**
     * 检查学号是否存在
     */
    boolean existsByStudentId(String studentId);
    
    /**
     * 根据状态查找用户
     */
    List<User> findByStatus(UserStatus status);
    
    /**
     * 根据大学查找用户
     */
    List<User> findByUniversity(String university);
    
    /**
     * 根据专业查找用户
     */
    List<User> findByMajor(String major);
    
    /**
     * 根据年级查找用户
     */
    List<User> findByGrade(String grade);
    
    /**
     * 分页查询用户
     */
    Page<User> findByStatus(UserStatus status, Pageable pageable);
    
    /**
     * 根据关键词搜索用户
     */
    @Query("SELECT u FROM User u WHERE u.status = :status AND " +
           "(u.username LIKE %:keyword% OR u.realName LIKE %:keyword% OR " +
           "u.studentId LIKE %:keyword% OR u.university LIKE %:keyword% OR " +
           "u.major LIKE %:keyword%)")
    Page<User> searchUsers(@Param("status") UserStatus status, 
                          @Param("keyword") String keyword, 
                          Pageable pageable);
    
    /**
     * 统计各状态用户数量
     */
    @Query("SELECT u.status, COUNT(u) FROM User u GROUP BY u.status")
    List<Object[]> countUsersByStatus();
} 