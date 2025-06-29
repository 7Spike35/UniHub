// 用户管理模块 Vue.js 应用
const { createApp } = Vue;

const UserManagementApp = {
    data() {
        return {
            // 当前激活的标签页
            activeTab: 'list',
            
            // 用户列表
            users: [],
            filteredUsers: [],
            
            // 搜索关键词
            searchKeyword: '',
            
            // 加载状态
            loading: false,
            
            // 表单数据
            formData: {
                id: null,
                username: '',
                email: '',
                password: '',
                confirmPassword: '',
                realName: '',
                studentId: '',
                phoneNumber: '',
                university: '',
                major: '',
                grade: '',
                role: 'USER',
                status: 'ACTIVE'
            },
            
            // 表单验证错误
            formErrors: {},
            
            // 是否显示模态框
            showModal: false,
            
            // 模态框标题
            modalTitle: '',
            
            // 操作模式 (add/edit)
            modalMode: 'add',
            
            // 消息提示
            message: {
                show: false,
                type: 'success',
                text: ''
            },
            
            // 确认删除
            confirmDelete: {
                show: false,
                userId: null,
                username: ''
            }
        }
    },
    
    computed: {
        // 过滤后的用户列表
        displayUsers() {
            if (!this.searchKeyword) {
                return this.users;
            }
            
            const keyword = this.searchKeyword.toLowerCase();
            return this.users.filter(user => 
                user.username.toLowerCase().includes(keyword) ||
                user.email.toLowerCase().includes(keyword) ||
                user.role.toLowerCase().includes(keyword)
            );
        }
    },
    
    mounted() {
        this.loadUsers();
    },
    
    methods: {
        // 加载用户列表
        async loadUsers() {
            this.loading = true;
            try {
                const response = await fetch('/api/users');
                if (response.ok) {
                    const data = await response.json();
                    this.users = data.data || [];
                } else {
                    this.showMessage('加载用户列表失败', 'error');
                }
            } catch (error) {
                console.error('加载用户列表错误:', error);
                this.showMessage('网络错误，请稍后重试', 'error');
            } finally {
                this.loading = false;
            }
        },
        
        // 切换标签页
        switchTab(tab) {
            this.activeTab = tab;
            if (tab === 'list') {
                this.loadUsers();
            }
        },
        
        // 搜索用户
        searchUsers() {
            // 搜索逻辑在 computed 属性中处理
        },
        
        // 显示添加用户模态框
        showAddUserModal() {
            this.resetForm();
            this.modalMode = 'add';
            this.modalTitle = '添加新用户';
            this.showModal = true;
        },
        
        // 显示编辑用户模态框
        showEditUserModal(user) {
            this.formData = {
                id: user.id,
                username: user.username,
                email: user.email,
                password: '',
                confirmPassword: '',
                realName: user.realName,
                studentId: user.studentId || '',
                phoneNumber: user.phoneNumber || '',
                university: user.university || '',
                major: user.major || '',
                grade: user.grade || '',
                role: user.role,
                status: user.status
            };
            this.modalMode = 'edit';
            this.modalTitle = '编辑用户信息';
            this.showModal = true;
        },
        
        // 关闭模态框
        closeModal() {
            this.showModal = false;
            this.resetForm();
        },
        
        // 重置表单
        resetForm() {
            this.formData = {
                id: null,
                username: '',
                email: '',
                password: '',
                confirmPassword: '',
                realName: '',
                studentId: '',
                phoneNumber: '',
                university: '',
                major: '',
                grade: '',
                role: 'USER',
                status: 'ACTIVE'
            };
            this.formErrors = {};
        },
        
        // 验证表单
        validateForm() {
            this.formErrors = {};
            
            if (!this.formData.username.trim()) {
                this.formErrors.username = '用户名不能为空';
            } else if (this.formData.username.length < 3) {
                this.formErrors.username = '用户名至少需要3个字符';
            }
            
            if (!this.formData.email.trim()) {
                this.formErrors.email = '邮箱不能为空';
            } else if (!this.isValidEmail(this.formData.email)) {
                this.formErrors.email = '请输入有效的邮箱地址';
            }
            
            if (this.modalMode === 'add') {
                // 添加模式下的验证
                if (!this.formData.realName.trim()) {
                    this.formErrors.realName = '真实姓名不能为空';
                }
                
                if (!this.formData.studentId.trim()) {
                    this.formErrors.studentId = '学号不能为空';
                }
                
                if (!this.formData.university.trim()) {
                    this.formErrors.university = '大学不能为空';
                }
                
                if (!this.formData.major.trim()) {
                    this.formErrors.major = '专业不能为空';
                }
                
                if (!this.formData.grade.trim()) {
                    this.formErrors.grade = '年级不能为空';
                }
                
                if (!this.formData.password) {
                    this.formErrors.password = '密码不能为空';
                } else if (this.formData.password.length < 6) {
                    this.formErrors.password = '密码至少需要6个字符';
                }
            } else {
                // 编辑模式下的验证
                if (this.formData.password && this.formData.password.length < 6) {
                    this.formErrors.password = '密码至少需要6个字符';
                }
            }
            
            if (this.formData.password && this.formData.password !== this.formData.confirmPassword) {
                this.formErrors.confirmPassword = '两次输入的密码不一致';
            }
            
            // 手机号格式验证（如果填写了的话）
            if (this.formData.phoneNumber && !this.isValidPhone(this.formData.phoneNumber)) {
                this.formErrors.phoneNumber = '手机号格式不正确';
            }
            
            return Object.keys(this.formErrors).length === 0;
        },
        
        // 验证邮箱格式
        isValidEmail(email) {
            const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            return emailRegex.test(email);
        },
        
        // 验证手机号格式
        isValidPhone(phone) {
            const phoneRegex = /^1[3-9]\d{9}$/;
            return phoneRegex.test(phone);
        },
        
        // 提交表单
        async submitForm() {
            if (!this.validateForm()) {
                return;
            }
            
            try {
                let url, method;
                
                if (this.modalMode === 'add') {
                    url = '/api/users/register';
                    method = 'POST';
                } else {
                    // 编辑模式，确保id不为null
                    if (!this.formData.id) {
                        this.showMessage('用户ID不能为空', 'error');
                        return;
                    }
                    url = `/api/users/${this.formData.id}`;
                    method = 'PUT';
                }
                
                const requestData = {
                    username: this.formData.username,
                    email: this.formData.email,
                    password: this.formData.password,
                    confirmPassword: this.formData.confirmPassword,
                    realName: this.formData.realName || this.formData.username, // 如果没有真实姓名，使用用户名
                    studentId: this.formData.studentId || '', // 学号可以为空
                    phoneNumber: this.formData.phoneNumber || '', // 手机号可以为空
                    university: this.formData.university || '未知大学',
                    major: this.formData.major || '未知专业',
                    grade: this.formData.grade || '未知年级',
                    role: this.formData.role,
                    status: this.formData.status
                };
                
                // 如果是编辑模式，不需要密码确认字段
                if (this.modalMode === 'edit') {
                    delete requestData.confirmPassword;
                    // 编辑模式下，如果没有设置密码，则不传递密码字段
                    if (!this.formData.password) {
                        delete requestData.password;
                    }
                }
                
                const response = await fetch(url, {
                    method: method,
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(requestData)
                });
                
                if (response.ok) {
                    const data = await response.json();
                    this.showMessage(
                        this.modalMode === 'add' ? '用户添加成功' : '用户信息更新成功',
                        'success'
                    );
                    this.closeModal();
                    this.loadUsers();
                } else {
                    const errorData = await response.json();
                    this.showMessage(errorData.message || '操作失败', 'error');
                }
            } catch (error) {
                console.error('提交表单错误:', error);
                this.showMessage('网络错误，请稍后重试', 'error');
            }
        },
        
        // 显示确认删除对话框
        showConfirmDelete(user) {
            this.confirmDelete = {
                show: true,
                userId: user.id,
                username: user.username
            };
        },
        
        // 关闭确认删除对话框
        closeConfirmDelete() {
            this.confirmDelete = {
                show: false,
                userId: null,
                username: ''
            };
        },
        
        // 删除用户
        async deleteUser() {
            try {
                const response = await fetch(`/api/users/${this.confirmDelete.userId}`, {
                    method: 'DELETE'
                });
                
                if (response.ok) {
                    this.showMessage('用户删除成功', 'success');
                    this.closeConfirmDelete();
                    this.loadUsers();
                } else {
                    const errorData = await response.json();
                    this.showMessage(errorData.message || '删除失败', 'error');
                }
            } catch (error) {
                console.error('删除用户错误:', error);
                this.showMessage('网络错误，请稍后重试', 'error');
            }
        },
        
        // 显示消息提示
        showMessage(text, type = 'success') {
            this.message = {
                show: true,
                type: type,
                text: text
            };
            
            setTimeout(() => {
                this.message.show = false;
            }, 3000);
        },
        
        // 格式化日期
        formatDate(dateString) {
            if (!dateString) return '-';
            const date = new Date(dateString);
            return date.toLocaleString('zh-CN');
        },
        
        // 获取状态显示文本
        getStatusText(status) {
            const statusMap = {
                'ACTIVE': '活跃',
                'INACTIVE': '非活跃',
                'SUSPENDED': '已暂停'
            };
            return statusMap[status] || status;
        },
        
        // 获取角色显示文本
        getRoleText(role) {
            const roleMap = {
                'ADMIN': '管理员',
                'USER': '普通用户'
            };
            return roleMap[role] || role;
        }
    }
};

// 创建并挂载 Vue 应用
createApp(UserManagementApp).mount('#user-management-app'); 