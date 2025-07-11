const UserProfileApp = {
    data() {
        return {
            loading: true,
            user: null,
            postsLoading: true,
            posts: [],
            userId: null,
            favoritesLoading: true,
            favoritePosts: []
        };
    },
    created() {
        this.userId = window.location.pathname.split('/').pop();
        this.fetchUserData();
        this.fetchUserPosts();
        this.fetchFavorites();
    },
    methods: {
        async fetchUserData() {
            this.loading = true;
            try {
                const response = await fetch(`/api/users/${this.userId}`);
                const responseData = await response.json();
                if (responseData.success) {
                    this.user = responseData.data;
                } else {
                    console.error('Failed to fetch user data:', responseData.message);
                }
            } catch (error) {
                console.error('Error fetching user data:', error);
            } finally {
                this.loading = false;
            }
        },
        async fetchUserPosts() {
            this.postsLoading = true;
            try {
                const response = await fetch(`/api/users/${this.userId}/posts`);
                const responseData = await response.json();
                if (responseData.success) {
                    this.posts = responseData.data;
                } else {
                    console.error('Failed to fetch user posts:', responseData.message);
                }
            } catch (error) {
                console.error('Error fetching user posts:', error);
            } finally {
                this.postsLoading = false;
            }
        },
        async fetchFavorites() {
            this.favoritesLoading = true;
            try {
                const user = JSON.parse(localStorage.getItem('user'));
                if (!user) {
                    this.favoritePosts = [];
                    return;
                }
                const response = await fetch(`/api/posts/favorites/user/${user.id}`);
                const responseData = await response.json();
                if (responseData.success) {
                    this.favoritePosts = responseData.data;
                } else {
                    this.favoritePosts = [];
                }
            } catch (error) {
                this.favoritePosts = [];
            } finally {
                this.favoritesLoading = false;
            }
        },
        async cancelFavorite(postId) {
            const user = JSON.parse(localStorage.getItem('user'));
            if (!user) return;
            const resp = await fetch(`/api/posts/${postId}/favorite`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ userId: user.id })
            });
            const data = await resp.json();
            if (data.success) {
                this.favoritePosts = this.favoritePosts.filter(p => p.id !== postId);
            }
        },
        goToPost(postId) {
            window.location.href = `/post-detail/${postId}`;
        },
        formatTime(timeStr) {
            if (!timeStr) return '';
            const date = new Date(timeStr);
            return date.toLocaleString('zh-CN', { 
                year: 'numeric', month: 'long', day: 'numeric', 
                hour: '2-digit', minute: '2-digit' 
            });
        }
    }
};

Vue.createApp(UserProfileApp).mount('#user-profile-app');
