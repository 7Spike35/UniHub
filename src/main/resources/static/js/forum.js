const { createApp } = Vue;

createApp({
    data() {
        return {
            posts: [],
            loading: true,
            user: null // 当前登录用户
        };
    },
    methods: {
        async loadPosts() {
            this.loading = true;
            try {
                const resp = await fetch('/api/posts/list');
                if (!resp.ok) throw new Error('加载失败');
                const data = await resp.json();
                this.posts = data;
            } catch (e) {
                this.posts = [];
            }
            this.loading = false;
        },
        isAdmin() {
            if (!this.user) return false;
            return this.user.role === 'ADMIN';
        },
        async deletePost(postId) {
            if (!confirm('确定要删除该帖子吗？')) return;
            try {
                const operatorUserId = this.user.id;
                const resp = await fetch(`/api/posts/${postId}?operatorUserId=${operatorUserId}`, { method: 'DELETE' });
                const data = await resp.json();
                if (data.success) {
                    this.posts = this.posts.filter(p => p.id !== postId);
                    alert('删除成功');
                } else {
                    alert('删除失败：' + data.message);
                }
            } catch (e) {
                alert('删除失败');
            }
        },
        goToPostCreate() {
            window.location.href = '/post-create';
        },
        goToPostDetail(postId) {
            window.location.href = '/post-detail/' + postId;
        },
        formatDate(dt) {
            if (!dt) return '';
            if (typeof dt === 'string') return dt.replace('T', ' ').substring(0, 19);
            return dt;
        }
    },
    mounted() {
        // 从localStorage获取当前用户信息
        const user = localStorage.getItem('user');
        if (user) {
            this.user = JSON.parse(user);
        }
        this.loadPosts();
    }
}).mount('#forum-app'); 