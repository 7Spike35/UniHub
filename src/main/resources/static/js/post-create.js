const { createApp } = Vue;

createApp({
    data() {
        return {
            content: '',
            imageFiles: [],
            videoFiles: [],
            imagePreviews: [],
            videoPreviews: [],
            message: {
                text: '',
                type: '',
                show: false
            },
            userId: null,
            isSubmitting: false,
            showResetConfirm: false,
            MAX_IMAGES: 10,
            MAX_VIDEOS: 3,
            MAX_IMAGE_SIZE: 10 * 1024 * 1024, // 10MB
            MAX_VIDEO_SIZE: 100 * 1024 * 1024 // 100MB
        };
    },
    
    computed: {
        remainingImages() {
            return this.MAX_IMAGES - this.imageFiles.length;
        },
        remainingVideos() {
            return this.MAX_VIDEOS - this.videoFiles.length;
        },
        totalFileCount() {
            return this.imageFiles.length + this.videoFiles.length;
        }
    },
    methods: {
        // 显示消息提示
        showMessage(text, type = 'info') {
            this.message = {
                text,
                type,
                show: true
            };
            
            // 5秒后自动隐藏消息（错误消息需要手动关闭）
            if (type !== 'error') {
                setTimeout(() => {
                    this.message.show = false;
                }, 5000);
            }
        },
        
        // 格式化文件大小
        formatFileSize(bytes) {
            if (bytes === 0) return '0 Bytes';
            const k = 1024;
            const sizes = ['Bytes', 'KB', 'MB', 'GB'];
            const i = Math.floor(Math.log(bytes) / Math.log(k));
            return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
        },
        
        // 计算文件列表总大小
        calculateTotalSize(files) {
            return files.reduce((total, file) => total + file.size, 0);
        },
        
        // 重置表单：显示确认模态框
        resetForm() {
            this.showResetConfirm = true;
        },

        // 确认重置
        confirmReset() {
            this.content = '';
            this.imageFiles = [];
            this.videoFiles = [];
            this.imagePreviews = [];
            this.videoPreviews = [];
            this.message.show = false;
            this.showResetConfirm = false; // 关闭模态框
        },

        // 取消重置
        cancelReset() {
            this.showResetConfirm = false; // 关闭模态框
        },

        // 移除图片
        removeImage(index) {
            this.imageFiles.splice(index, 1);
            this.imagePreviews.splice(index, 1);
            // 释放URL对象
            URL.revokeObjectURL(this.imagePreviews[index]);
        },
        
        // 移除视频
        removeVideo(index) {
            this.videoFiles.splice(index, 1);
            this.videoPreviews.splice(index, 1);
            // 释放URL对象
            URL.revokeObjectURL(this.videoPreviews[index]);
        },
        
        // 验证文件类型和大小
        validateFile(file, isImage = true) {
            const fileType = isImage ? '图片' : '视频';
            const maxSize = isImage ? this.MAX_IMAGE_SIZE : this.MAX_VIDEO_SIZE;
            const allowedTypes = isImage 
                ? ['image/jpeg', 'image/png', 'image/gif', 'image/webp']
                : ['video/mp4', 'video/webm', 'video/quicktime'];
            
            if (!allowedTypes.includes(file.type)) {
                this.showMessage(`不支持的文件类型: ${file.name}，请上传${fileType}文件`, 'error');
                return false;
            }
            
            if (file.size > maxSize) {
                const maxSizeStr = this.formatFileSize(maxSize);
                this.showMessage(`文件 ${file.name} 超过大小限制 (最大 ${maxSizeStr})`, 'error');
                return false;
            }
            
            return true;
        },
        handleImageChange(e) {
            const files = Array.from(e.target.files || []);
            
            // 检查数量限制
            if (this.imageFiles.length + files.length > this.MAX_IMAGES) {
                this.showMessage(`最多只能上传 ${this.MAX_IMAGES} 张图片`, 'error');
                e.target.value = null;
                return;
            }
            
            // 验证并过滤文件
            const validFiles = files.filter(file => this.validateFile(file, true));
            
            // 添加新文件
            this.imageFiles = [...this.imageFiles, ...validFiles];
            
            // 更新预览
            this.updateImagePreviews();
            
            // 清空文件输入，允许重复选择相同文件
            e.target.value = null;
            
            // 显示成功消息
            if (validFiles.length > 0) {
                this.showMessage(`成功添加 ${validFiles.length} 张图片`, 'success');
            }
        },
        
        // 更新图片预览
        updateImagePreviews() {
            // 释放之前的URL对象
            this.imagePreviews.forEach(url => URL.revokeObjectURL(url));
            // 创建新的预览URL
            this.imagePreviews = this.imageFiles.map(file => URL.createObjectURL(file));
        },
        
        handleVideoChange(e) {
            const files = Array.from(e.target.files || []);
            
            // 检查数量限制
            if (this.videoFiles.length + files.length > this.MAX_VIDEOS) {
                this.showMessage(`最多只能上传 ${this.MAX_VIDEOS} 个视频`, 'error');
                e.target.value = null;
                return;
            }
            
            // 验证并过滤文件
            const validFiles = files.filter(file => this.validateFile(file, false));
            
            // 添加新文件
            this.videoFiles = [...this.videoFiles, ...validFiles];
            
            // 更新预览
            this.updateVideoPreviews();
            
            // 清空文件输入，允许重复选择相同文件
            e.target.value = null;
            
            // 显示成功消息
            if (validFiles.length > 0) {
                this.showMessage(`成功添加 ${validFiles.length} 个视频`, 'success');
            }
        },
        // 更新视频预览
        updateVideoPreviews() {
            // 释放之前的URL对象
            this.videoPreviews.forEach(url => URL.revokeObjectURL(url));
            // 创建新的预览URL
            this.videoPreviews = this.videoFiles.map(file => URL.createObjectURL(file));
        },
        
        // 提交帖子
        async submitPost() {
            // 检查用户登录状态
            if (!this.checkUserLogin()) {
                return;
            }
            
            // 验证表单
            if (!this.content.trim()) {
                this.showMessage('帖子内容不能为空', 'error');
                return;
            }
            
            if (this.totalFileCount === 0) {
                const confirmPost = confirm('确定要发布没有附件的帖子吗？');
                if (!confirmPost) return;
            }
            
            this.isSubmitting = true;
            
            try {
                const formData = new FormData();
                formData.append('userId', this.userId);
                formData.append('content', this.content);
                
                // 添加图片和视频文件
                [...this.imageFiles, ...this.videoFiles].forEach((file, index) => {
                    formData.append('mediaFiles', file, file.name);
                });
                
                this.showMessage('正在发布中，请稍候...', 'info');
                
                const response = await fetch('/api/posts', {
                    method: 'POST',
                    body: formData
                    // 注意：不要手动设置 Content-Type，让浏览器自动设置正确的 boundary
                });
                
                const result = await response.json();
                
                if (!response.ok) {
                    throw new Error(result.message || `发帖失败，状态码: ${response.status}`);
                }
                
                // 显示成功消息
                this.showMessage('发帖成功！即将跳转到帖子列表...', 'success');
                
                // 2秒后跳转到帖子列表
                setTimeout(() => {
                    window.location.href = '/forum';
                }, 2000);
                
            } catch (error) {
                console.error('发帖失败:', error);
                const errorMessage = error.message.includes('Failed to fetch') 
                    ? '网络错误，请检查您的网络连接后重试' 
                    : error.message || '发帖失败，请稍后重试';
                this.showMessage(errorMessage, 'error');
            } finally {
                this.isSubmitting = false;
            }
        },
        
        // 获取当前登录用户ID
        getCurrentUserId() {
            const userStr = localStorage.getItem('user');
            if (userStr) {
                try {
                    const user = JSON.parse(userStr);
                    return user.id;
                } catch (e) {
                    console.error('解析用户信息失败:', e);
                    return null;
                }
            }
            return null;
        },
        
        // 检查用户是否已登录
        checkUserLogin() {
            const userId = this.getCurrentUserId();
            if (!userId) {
                this.showMessage('请先登录后再发帖', 'error');
                setTimeout(() => {
                    window.location.href = '/login';
                }, 2000);
                return false;
            }
            this.userId = userId;
            return true;
        }
    },
    
    mounted() {
        // 检查用户登录状态
        if (!this.checkUserLogin()) {
            return;
        }
    }
}).mount('#post-create-app'); 