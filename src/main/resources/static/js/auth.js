// 认证页面 Vue.js 应用
const { createApp } = Vue;

createApp({
    data() {
        return {
            activeTab: 'login',
            loading: false,
            message: {
                show: false,
                type: 'success',
                text: ''
            },
            formErrors: {},
            loginForm: {
                username: '',
                password: '',
                rememberMe: false
            },
            registerForm: {
                username: '',
                email: '',
                realName: '',
                studentId: '',
                phoneNumber: '',
                university: '',
                major: '',
                grade: '',
                password: '',
                confirmPassword: '',
                role: 'STUDENT'
            }
        }
    },
    methods: {
        // 切换标签页
        switchTab(tab) {
            this.activeTab = tab;
            this.clearMessage();
            this.clearErrors();
        },

        // 显示消息
        showMessage(type, text) {
            this.message = {
                show: true,
                type: type,
                text: text
            };
            // 5秒后自动隐藏
            setTimeout(() => {
                this.clearMessage();
            }, 5000);
        },

        // 清除消息
        clearMessage() {
            this.message.show = false;
        },

        // 清除错误
        clearErrors() {
            this.formErrors = {};
        },

        // 验证登录表单
        validateLoginForm() {
            this.clearErrors();
            let isValid = true;

            if (!this.loginForm.username.trim()) {
                this.formErrors.username = '请输入用户名';
                isValid = false;
            }

            if (!this.loginForm.password) {
                this.formErrors.password = '请输入密码';
                isValid = false;
            }

            return isValid;
        },

        // 验证注册表单
        validateRegisterForm() {
            this.clearErrors();
            let isValid = true;

            // 用户名验证
            if (!this.registerForm.username.trim()) {
                this.formErrors.username = '请输入用户名';
                isValid = false;
            } else if (this.registerForm.username.length < 3) {
                this.formErrors.username = '用户名至少3个字符';
                isValid = false;
            }

            // 邮箱验证
            if (!this.registerForm.email.trim()) {
                this.formErrors.email = '请输入邮箱地址';
                isValid = false;
            } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(this.registerForm.email)) {
                this.formErrors.email = '请输入有效的邮箱地址';
                isValid = false;
            }

            // 真实姓名验证
            if (!this.registerForm.realName.trim()) {
                this.formErrors.realName = '请输入真实姓名';
                isValid = false;
            }

            // 学号验证
            if (!this.registerForm.studentId.trim()) {
                this.formErrors.studentId = '请输入学号';
                isValid = false;
            }

            // 手机号验证
            if (!this.registerForm.phoneNumber.trim()) {
                this.formErrors.phoneNumber = '请输入手机号';
                isValid = false;
            } else if (!/^1[3-9]\d{9}$/.test(this.registerForm.phoneNumber)) {
                this.formErrors.phoneNumber = '请输入有效的手机号';
                isValid = false;
            }

            // 大学验证
            if (!this.registerForm.university.trim()) {
                this.formErrors.university = '请输入大学名称';
                isValid = false;
            }

            // 专业验证
            if (!this.registerForm.major.trim()) {
                this.formErrors.major = '请输入专业名称';
                isValid = false;
            }

            // 年级验证
            if (!this.registerForm.grade.trim()) {
                this.formErrors.grade = '请输入年级';
                isValid = false;
            }

            // 密码验证
            if (!this.registerForm.password) {
                this.formErrors.password = '请输入密码';
                isValid = false;
            } else if (this.registerForm.password.length < 6) {
                this.formErrors.password = '密码至少6个字符';
                isValid = false;
            }

            // 确认密码验证
            if (!this.registerForm.confirmPassword) {
                this.formErrors.confirmPassword = '请确认密码';
                isValid = false;
            } else if (this.registerForm.password !== this.registerForm.confirmPassword) {
                this.formErrors.confirmPassword = '两次输入的密码不一致';
                isValid = false;
            }

            return isValid;
        },

        // 登录
        async login() {
            if (!this.validateLoginForm()) {
                return;
            }

            this.loading = true;
            try {
                const response = await fetch('/api/users/login', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({
                        username: this.loginForm.username,
                        password: this.loginForm.password
                    })
                });

                const data = await response.json();

                if (response.ok) {
                    this.showMessage('success', '登录成功！正在跳转...');
                    
                    // 保存用户信息和token到localStorage
                    localStorage.setItem('user', JSON.stringify(data.data.user));
                    localStorage.setItem('token', data.data.token || '');
                    
                    // 延迟跳转到主页
                    setTimeout(() => {
                        window.location.href = '/dashboard';
                    }, 1500);
                } else {
                    this.showMessage('error', data.message || '登录失败，请检查用户名和密码');
                }
            } catch (error) {
                console.error('登录错误:', error);
                this.showMessage('error', '网络错误，请稍后重试');
            } finally {
                this.loading = false;
            }
        },

        // 注册
        async register() {
            if (!this.validateRegisterForm()) {
                return;
            }

            this.loading = true;
            try {
                const response = await fetch('/api/users/register', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({
                        username: this.registerForm.username,
                        email: this.registerForm.email,
                        realName: this.registerForm.realName,
                        studentId: this.registerForm.studentId,
                        phoneNumber: this.registerForm.phoneNumber,
                        university: this.registerForm.university,
                        major: this.registerForm.major,
                        grade: this.registerForm.grade,
                        password: this.registerForm.password,
                        confirmPassword: this.registerForm.confirmPassword,
                        role: this.registerForm.role
                    })
                });

                const data = await response.json();

                if (response.ok) {
                    this.showMessage('success', '注册成功！请登录您的账户');
                    
                    // 清空注册表单
                    this.registerForm = {
                        username: '',
                        email: '',
                        realName: '',
                        studentId: '',
                        phoneNumber: '',
                        university: '',
                        major: '',
                        grade: '',
                        password: '',
                        confirmPassword: '',
                        role: 'STUDENT'
                    };
                    
                    // 切换到登录标签页
                    setTimeout(() => {
                        this.switchTab('login');
                    }, 2000);
                } else {
                    this.showMessage('error', data.message || '注册失败，请稍后重试');
                }
            } catch (error) {
                console.error('注册错误:', error);
                this.showMessage('error', '网络错误，请稍后重试');
            } finally {
                this.loading = false;
            }
        }
    },

    mounted() {
        // 检查是否已经登录
        const user = localStorage.getItem('user');
        if (user) {
            window.location.href = '/dashboard';
        }
    }
}).mount('#auth-app'); 