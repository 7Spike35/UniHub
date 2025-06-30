const { createApp } = Vue;

createApp({
    data() {
        return {
            posts: [],
            loading: true
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
        goToPostCreate() {
            window.location.href = '/post-create';
        },
        formatDate(dt) {
            if (!dt) return '';
            if (typeof dt === 'string') return dt.replace('T', ' ').substring(0, 19);
            return dt;
        }
    },
    mounted() {
        this.loadPosts();
    }
}).mount('#forum-app'); 