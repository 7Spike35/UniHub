const { createApp } = Vue;

createApp({
    data() {
        return {
            content: '',
            imageFiles: [],
            videoFiles: [],
            imagePreviews: [],
            videoPreviews: [],
            message: '',
            messageType: '',
            userId: 1 // TODO: 实际项目应从登录信息获取
        };
    },
    methods: {
        handleImageChange(e) {
            this.imageFiles = Array.from(e.target.files);
            this.imagePreviews = this.imageFiles.map(file => URL.createObjectURL(file));
        },
        handleVideoChange(e) {
            this.videoFiles = Array.from(e.target.files);
            this.videoPreviews = this.videoFiles.map(file => URL.createObjectURL(file));
        },
        async submitPost() {
            if (!this.content.trim()) {
                this.message = '帖子内容不能为空';
                this.messageType = 'alert-danger';
                return;
            }
            const formData = new FormData();
            formData.append('userId', this.userId);
            formData.append('content', this.content);
            this.imageFiles.forEach(f => formData.append('mediaFiles', f));
            this.videoFiles.forEach(f => formData.append('mediaFiles', f));
            try {
                const resp = await fetch('/api/posts', {
                    method: 'POST',
                    body: formData
                });
                if (!resp.ok) {
                    const text = await resp.text();
                    throw new Error(text || '发帖失败');
                }
                this.message = '发帖成功！';
                this.messageType = 'alert-success';
                this.content = '';
                this.imageFiles = [];
                this.videoFiles = [];
                this.imagePreviews = [];
                this.videoPreviews = [];
            } catch (e) {
                this.message = e.message || '发帖失败';
                this.messageType = 'alert-danger';
            }
        }
    }
}).mount('#post-create-app'); 