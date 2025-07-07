// 仪表板 Vue.js 应用
const { createApp } = Vue;

createApp({
    data() {
        return {
            user: null,
            activePage: 'home',
            showUserMenu: false,
            showPasswordModal: false,
            editing: false,
            saving: false,
            savingPassword: false,
            stats: {
                totalUsers: 0,
                activeUsers: 0,
                resources: 0,
                todayEvents: 0
            },
            profileForm: {
                username: '',
                email: '',
                realName: '',
                studentId: '',
                phoneNumber: '',
                university: '',
                major: '',
                grade: '',
                role: 'STUDENT',
                status: 'ACTIVE'
            },
            passwordForm: {
                oldPassword: '',
                newPassword: '',
                confirmPassword: ''
            },
            // 用户管理相关数据
            allUsers: [],
            filteredUsers: [],
            searchKeyword: '',
            statusFilter: 'ACTIVE',
            // 新增：我的帖子相关
            myPosts: [],
            loadingPosts: false
        }
    },
    computed: {
        userAvatar() {
            if (this.user && this.user.realName) {
                return `https://ui-avatars.com/api/?name=${encodeURIComponent(this.user.realName)}&background=667eea&color=fff&size=100`;
            }
            return 'https://ui-avatars.com/api/?name=User&background=667eea&color=fff&size=100';
        }
    },
    methods: {
        // 切换页面
        switchPage(page) {
            this.activePage = page;
            this.showUserMenu = false;

            // 如果切换到用户管理页面，加载用户数据
            if (page === 'user-management') {
                this.loadUsers();
            }
            // 新增：切换到个人主页时加载我的帖子
            if (page === 'profile') {
                this.loadMyPosts();
            }
        },

        // 切换用户菜单
        toggleUserMenu() {
            this.showUserMenu = !this.showUserMenu;
        },

        // 获取角色文本
        getRoleText(role) {
            const roleMap = {
                'ADMIN': '管理员',
                'USER': '普通用户',
                'STUDENT': '学生',
                'TEACHER': '教师',
                'MODERATOR': '版主'
            };
            return roleMap[role] || '未知';
        },

        // 获取状态文本
        getStatusText(status) {
            const statusMap = {
                'ACTIVE': '正常',
                'INACTIVE': '禁用',
                'PENDING': '待审核'
            };
            return statusMap[status] || '未知';
        },

        // 加载用户信息
        async loadUserInfo() {
            try {
                const token = localStorage.getItem('token');
                const user = localStorage.getItem('user');

                if (!token || !user) {
                    this.redirectToLogin();
                    return;
                }

                // 从localStorage获取用户信息
                this.user = JSON.parse(user);
                this.profileForm = { ...this.user };
                if (!this.profileForm.role) this.profileForm.role = 'STUDENT';
                if (!this.profileForm.status) this.profileForm.status = 'ACTIVE';
            } catch (error) {
                console.error('加载用户信息失败:', error);
                this.redirectToLogin();
            }
        },

        // 加载统计数据
        async loadStats() {
            try {
                const response = await fetch('/api/dashboard/stats');
                if (response.ok) {
                    const data = await response.json();
                    this.stats = data.data;
                }
            } catch (error) {
                console.error('加载统计数据失败:', error);
            }
        },

        // 开始编辑个人资料
        startEdit() {
            this.editing = true;
        },

        // 取消编辑
        cancelEdit() {
            this.editing = false;
            // 恢复原始数据
            this.profileForm = { ...this.user };
        },

        // 保存个人资料
        async saveProfile() {
            if (!this.validateProfileForm()) {
                return;
            }

            this.saving = true;
            try {
                const token = localStorage.getItem('token');
                if (!token) {
                    throw new Error('未找到认证令牌，请重新登录');
                }

                // 确保token是字符串且不包含非法字符
                const cleanToken = String(token).trim();
                // 对token进行Base64编码，确保只包含安全字符
                const encodedToken = btoa(encodeURIComponent(cleanToken).replace(/%([0-9A-F]{2})/g,
                    (match, p1) => String.fromCharCode(parseInt(p1, 16))));

                // 创建请求头
                const headers = new Headers();
                headers.append('Content-Type', 'application/json');
                // 使用编码后的token
                headers.append('Authorization', `Basic ${encodedToken}`);

                const response = await fetch(`/api/users/${this.user.id}`, {
                    method: 'PUT',
                    headers: headers,
                    body: JSON.stringify({
                        realName: this.profileForm.realName,
                        studentId: this.profileForm.studentId,
                        phoneNumber: this.profileForm.phoneNumber,
                        university: this.profileForm.university,
                        major: this.profileForm.major,
                        grade: this.profileForm.grade,
                        role: this.profileForm.role,
                        status: this.profileForm.status
                    })
                });

                const data = await response.json();

                if (response.ok) {
                    // 更新用户信息
                    this.user = { ...this.user, ...this.profileForm };
                    localStorage.setItem('user', JSON.stringify(this.user));
                    this.editing = false;
                    this.showMessage('success', '个人资料更新成功！');
                } else {
                    this.showMessage('error', data.message || '更新失败，请稍后重试');
                }
            } catch (error) {
                console.error('保存个人资料失败:', error);
                const errorMessage = error.message || '网络错误，请稍后重试';
                if (error.message && error.message.includes('未找到认证令牌')) {
                    this.redirectToLogin();
                }
                this.showMessage('error', errorMessage);
            } finally {
                this.saving = false;
            }
        },

        // 验证个人资料表单
        validateProfileForm() {
            if (!this.profileForm.realName.trim()) {
                this.showMessage('error', '请输入真实姓名');
                return false;
            }
            if (!this.profileForm.studentId.trim()) {
                this.showMessage('error', '请输入学号');
                return false;
            }
            if (!this.profileForm.phoneNumber.trim()) {
                this.showMessage('error', '请输入手机号');
                return false;
            }
            if (!/^1[3-9]\d{9}$/.test(this.profileForm.phoneNumber)) {
                this.showMessage('error', '请输入有效的手机号');
                return false;
            }
            if (!this.profileForm.university.trim()) {
                this.showMessage('error', '请输入大学名称');
                return false;
            }
            if (!this.profileForm.major.trim()) {
                this.showMessage('error', '请输入专业名称');
                return false;
            }
            if (!this.profileForm.grade.trim()) {
                this.showMessage('error', '请输入年级');
                return false;
            }
            return true;
        },

        // 修改密码
        changePassword() {
            this.showPasswordModal = true;
            this.passwordForm = {
                oldPassword: '',
                newPassword: '',
                confirmPassword: ''
            };
            this.showUserMenu = false;
        },

        // 关闭密码模态框
        closePasswordModal() {
            this.showPasswordModal = false;
        },

        // 保存密码
        async savePassword() {
            if (!this.validatePasswordForm()) {
                return;
            }

            this.savingPassword = true;
            try {
                const response = await fetch('/api/users/change-password', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({
                        username: this.user.username,
                        oldPassword: this.passwordForm.oldPassword,
                        newPassword: this.passwordForm.newPassword
                    })
                });

                const data = await response.json();

                if (response.ok) {
                    this.showMessage('success', '密码修改成功！');
                    this.closePasswordModal();
                } else {
                    this.showMessage('error', data.message || '密码修改失败，请稍后重试');
                }
            } catch (error) {
                console.error('修改密码失败:', error);
                this.showMessage('error', '网络错误，请稍后重试');
            } finally {
                this.savingPassword = false;
            }
        },

        // 验证密码表单
        validatePasswordForm() {
            if (!this.passwordForm.oldPassword) {
                this.showMessage('error', '请输入当前密码');
                return false;
            }
            if (!this.passwordForm.newPassword) {
                this.showMessage('error', '请输入新密码');
                return false;
            }
            if (this.passwordForm.newPassword.length < 6) {
                this.showMessage('error', '新密码至少6个字符');
                return false;
            }
            if (this.passwordForm.newPassword !== this.passwordForm.confirmPassword) {
                this.showMessage('error', '两次输入的新密码不一致');
                return false;
            }
            return true;
        },

        // 显示消息
        showMessage(type, text) {
            // 创建消息容器（如果不存在）
            let messageContainer = document.getElementById('message-container');
            if (!messageContainer) {
                messageContainer = document.createElement('div');
                messageContainer.id = 'message-container';
                messageContainer.style.position = 'fixed';
                messageContainer.style.top = '20px';
                messageContainer.style.right = '20px';
                messageContainer.style.zIndex = '9999';
                document.body.appendChild(messageContainer);
            }

            // 创建消息元素
            const message = document.createElement('div');
            message.className = `message ${type}`;
            message.style.padding = '12px 24px';
            message.style.marginBottom = '10px';
            message.style.borderRadius = '4px';
            message.style.color = 'white';
            message.style.fontSize = '14px';
            message.style.boxShadow = '0 4px 12px rgba(0, 0, 0, 0.15)';
            message.style.transition = 'all 0.3s ease';
            message.style.opacity = '0';
            message.style.transform = 'translateX(100%)';
            message.style.maxWidth = '320px';
            message.style.wordBreak = 'break-word';

            // 设置不同消息类型的样式
            if (type === 'success') {
                message.style.backgroundColor = '#10b981'; // 绿色
            } else {
                message.style.backgroundColor = '#ef4444'; // 红色
            }

            // 设置消息内容
            message.textContent = text;

            // 添加到容器
            messageContainer.appendChild(message);

            // 触发动画
            setTimeout(() => {
                message.style.opacity = '1';
                message.style.transform = 'translateX(0)';
            }, 10);

            // 3秒后自动消失
            setTimeout(() => {
                message.style.opacity = '0';
                message.style.transform = 'translateX(100%)';

                // 动画结束后移除元素
                setTimeout(() => {
                    message.remove();
                    // 如果容器为空，也移除容器
                    if (messageContainer.children.length === 0) {
                        messageContainer.remove();
                    }
                }, 300);
            }, 3000);
        },

        // 退出登录
        logout() {
            localStorage.removeItem('user');
            localStorage.removeItem('token');
            this.redirectToLogin();
        },

        // 重定向到登录页面
        redirectToLogin() {
            window.location.href = '/login';
        },

        // 点击外部关闭用户菜单
        handleClickOutside(event) {
            const userMenu = document.querySelector('.nav-user');
            if (userMenu && !userMenu.contains(event.target)) {
                this.showUserMenu = false;
            }
        },

        // 用户管理相关方法
        async loadUsers() {
            try {
                const response = await fetch('/api/users');
                if (response.ok) {
                    const data = await response.json();
                    this.allUsers = data.data || [];
                    this.filterUsers();
                }
            } catch (error) {
                console.error('加载用户列表失败:', error);
            }
        },

        filterUsers() {
            let filtered = this.allUsers;

            // 按状态筛选
            if (this.statusFilter !== 'ALL') {
                filtered = filtered.filter(user => user.status === this.statusFilter);
            }

            // 按关键词搜索
            if (this.searchKeyword.trim()) {
                const keyword = this.searchKeyword.toLowerCase();
                filtered = filtered.filter(user =>
                    user.username.toLowerCase().includes(keyword) ||
                    user.realName.toLowerCase().includes(keyword) ||
                    user.email.toLowerCase().includes(keyword) ||
                    user.studentId.toLowerCase().includes(keyword)
                );
            }

            this.filteredUsers = filtered;
        },

        searchUsers() {
            this.filterUsers();
        },

        addUser() {
            // 跳转到用户管理页面进行添加
            window.open('/user-management', '_blank');
        },

        editUser(user) {
            // 跳转到用户管理页面进行编辑
            window.open(`/user-management?edit=${user.id}`, '_blank');
        },

        async deleteMyPost(postId) {
            if (!confirm('确定要删除该帖子吗？')) return;
            try {
                const operatorUserId = this.user.id;
                const resp = await fetch(`/api/posts/${postId}?operatorUserId=${operatorUserId}`, { method: 'DELETE' });
                const data = await resp.json();
                if (data.success) {
                    this.myPosts = this.myPosts.filter(p => p.id !== postId);
                    this.showMessage('success', '删除成功');
                } else {
                    this.showMessage('error', '删除失败：' + data.message);
                }
            } catch (e) {
                this.showMessage('error', '删除失败');
            }
        },

        async deleteUser(user) {
            if (!confirm(`确定要删除用户 "${user.realName}" 吗？此操作不可恢复！`)) return;
            try {
                const response = await fetch(`/api/users/${user.id}`, {
                    method: 'DELETE'
                });
                if (response.ok) {
                    this.allUsers = this.allUsers.filter(u => u.id !== user.id);
                    this.filterUsers();
                    this.showMessage('success', '用户删除成功');
                } else {
                    this.showMessage('error', '删除失败，请稍后重试');
                }
            } catch (error) {
                console.error('删除用户失败:', error);
                this.showMessage('error', '网络错误，请稍后重试');
            }
        },

        async toggleUserStatus(user) {
            const newStatus = user.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE';
            const statusText = newStatus === 'ACTIVE' ? '启用' : '禁用';
            if (!confirm(`确定要${statusText}用户 "${user.realName}" 吗？`)) return;
            try {
                const response = await fetch(`/api/users/${user.id}/status?status=${newStatus}`, {
                    method: 'PUT'
                });
                if (response.ok) {
                    user.status = newStatus;
                    this.showMessage('success', `用户${statusText}成功`);
                } else {
                    this.showMessage('error', '操作失败，请稍后重试');
                }
            } catch (error) {
                console.error('切换用户状态失败:', error);
                this.showMessage('error', '网络错误，请稍后重试');
            }
        },

        async loadMyPosts() {
            if (!this.user) return;
            this.loadingPosts = true;
            try {
                const resp = await fetch(`/api/users/${this.user.id}/posts`);
                const data = await resp.json();
                if ((data.success && data.data) || (data.code === 200 && data.data)) {
                    const posts = data.data;
                    for (const post of posts) {
                        try {
                            // 获取媒体
                            const mediaResp = await fetch(`/api/posts/${post.id}`);
                            const mediaData = await mediaResp.json();
                            if ((mediaData.success && mediaData.data) || (mediaData.code === 200 && mediaData.data)) {
                                post.mediaList = mediaData.data.mediaList || [];
                            } else {
                                post.mediaList = [];
                            }
                        } catch (mediaErr) {
                            console.error('加载媒体失败:', mediaErr);
                            post.mediaList = [];
                        }
                    }
                    this.myPosts = posts;
                } else {
                    this.myPosts = [];
                }
            } catch (e) {
                console.error('加载我的帖子失败:', e);
                this.myPosts = [];
            } finally {
                this.loadingPosts = false;
            }
        },

        formatDate(dateStr) {
            if (!dateStr) return '';
            const date = new Date(dateStr);
            const y = date.getFullYear();
            const m = String(date.getMonth() + 1).padStart(2, '0');
            const d = String(date.getDate()).padStart(2, '0');
            const h = String(date.getHours()).padStart(2, '0');
            const min = String(date.getMinutes()).padStart(2, '0');
            return `${y}-${m}-${d} ${h}:${min}`;
        }
    },

    async mounted() {
        // 检查是否已登录
        const user = localStorage.getItem('user');
        if (!user) {
            this.redirectToLogin();
            return;
        }

        try {
            this.user = JSON.parse(user);
            this.profileForm = { ...this.user };
        } catch (error) {
            console.error('解析用户信息失败:', error);
            this.redirectToLogin();
            return;
        }

        // 加载用户信息和统计数据
        await this.loadUserInfo();
        await this.loadStats();

        // 添加点击外部关闭菜单的事件监听
        document.addEventListener('click', this.handleClickOutside);

        // 强制加载一次我的帖子
        await this.loadMyPosts();
    },

    beforeUnmount() {
        // 移除事件监听
        document.removeEventListener('click', this.handleClickOutside);
    }
}).mount('#dashboard-app'); 