const { createApp } = Vue;

createApp({
    data() {
        return {
            posts: [],
            loading: true,
            user: null, // 当前登录用户
            isProcessing: false // 防止重复提交
        };
    },
    methods: {
        async loadPosts() {
            this.loading = true;
            try {
                const response = await fetch(`/api/posts/list?currentUserId=${this.user.id}`);
                const result = await response.json();
                if (result.success) {
                    this.posts = result.data;
                } else {
                    console.error('获取帖子列表失败:', result.message);
                }
            } catch (error) {
                console.error('获取帖子列表失败:', error);
            } finally {
                this.loading = false;
            }
        },
        
        // 切换点赞状态
        async toggleLike(post) {
            if (!this.user) {
                alert('请先登录');
                return;
            }
            
            if (this.isProcessing) return;
            this.isProcessing = true;
            
            try {
                const response = await fetch(`/api/posts/${post.id}/like`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ userId: this.user.id })
                });
                const result = await response.json();
                if (result.success) {
                    post.liked = result.liked;
                    post.likeCount = result.likeCount;
                }
            } catch (error) {
                console.error('操作失败:', error);
                alert('操作失败，请稍后重试');
            } finally {
                this.isProcessing = false;
            }
        },
        
        // 切换收藏状态
        async toggleFavorite(post) {
            if (!this.user) {
                alert('请先登录');
                return;
            }
            
            if (this.isProcessing) return;
            this.isProcessing = true;
            
            try {
                const response = await fetch(`/api/posts/${post.id}/favorite`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ userId: this.user.id })
                });
                const result = await response.json();
                if (result.success) {
                    post.favorited = result.favorited;
                    post.favoriteCount = result.favoriteCount;
                }
            } catch (error) {
                console.error('操作失败:', error);
                alert('操作失败，请稍后重试');
            } finally {
                this.isProcessing = false;
            }
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
        goToPostDetail(postId, focusComment = false) {
            const url = focusComment 
                ? `/post-detail/${postId}#comments`
                : `/post-detail/${postId}`;
            window.location.href = url;
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