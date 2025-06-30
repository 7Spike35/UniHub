-- UniHub 用户管理模块演示数据
-- 注意：密码都是经过BCrypt加密的 "123456"

-- 清空现有数据（可选）
-- DELETE FROM users;

-- 插入演示用户数据
INSERT INTO users (username, email, password, real_name, phone_number, student_id, university, major, grade, role, status, create_time, update_time, last_login_time, post_count) VALUES
('admin', 'admin@unihub.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', '系统管理员', '13800138000', '2024001', '清华大学', '计算机科学与技术', '2024', 'ADMIN', 'ACTIVE', NOW(), NOW(), NOW(), 0),
('zhangsan', 'zhangsan@unihub.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', '张三', '13800138001', '2024002', '北京大学', '软件工程', '2024', 'USER', 'ACTIVE', NOW(), NOW(), NOW(), 0),
('lisi', 'lisi@unihub.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', '李四', '13800138002', '2024003', '复旦大学', '信息管理', '2024', 'USER', 'ACTIVE', NOW(), NOW(), NOW(), 0),
('wangwu', 'wangwu@unihub.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', '王五', '13800138003', '2024004', '上海交通大学', '数据科学', '2024', 'USER', 'INACTIVE', NOW(), NOW(), NOW(), 0),
('zhaoliu', 'zhaoliu@unihub.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', '赵六', '13800138004', '2024005', '浙江大学', '人工智能', '2024', 'USER', 'SUSPENDED', NOW(), NOW(), NOW(), 0),
('qianqi', 'qianqi@unihub.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', '钱七', '13800138005', '2024006', '南京大学', '网络工程', '2024', 'USER', 'ACTIVE', NOW(), NOW(), NOW(), 0),
('sunba', 'sunba@unihub.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', '孙八', '13800138006', '2024007', '武汉大学', '信息安全', '2024', 'USER', 'ACTIVE', NOW(), NOW(), NOW(), 0),
('zhoujiu', 'zhoujiu@unihub.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', '周九', '13800138007', '2024008', '华中科技大学', '计算机技术', '2024', 'USER', 'ACTIVE', NOW(), NOW(), NOW(), 0),
('wushi', 'wushi@unihub.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', '吴十', '13800138008', '2024009', '中山大学', '软件技术', '2024', 'USER', 'INACTIVE', NOW(), NOW(), NOW(), 0),
('zhengshi', 'zhengshi@unihub.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', '郑十一', '13800138009', '2024010', '四川大学', '数字媒体', '2024', 'USER', 'ACTIVE', NOW(), NOW(), NOW(), 0);

-- 注意：如果表已存在数据，请先备份或清空后再执行此脚本
-- 密码都是 "123456" 的BCrypt加密形式 