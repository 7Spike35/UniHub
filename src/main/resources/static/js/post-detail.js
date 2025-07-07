// 时间格式化函数
function formatTime(dateString) {
    if (!dateString) return '';
    const date = new Date(dateString);
    const now = new Date();
    const diffInSeconds = Math.floor((now - date) / 1000);
    
    if (diffInSeconds < 60) {
        return '刚刚';
    } else if (diffInSeconds < 3600) {
        return Math.floor(diffInSeconds / 60) + '分钟前';
    } else if (diffInSeconds < 86400) {
        return Math.floor(diffInSeconds / 3600) + '小时前';
    } else if (diffInSeconds < 2592000) {
        return Math.floor(diffInSeconds / 86400) + '天前';
    } else {
        return date.toLocaleDateString();
    }
}

// 初始化Vue应用
const { createApp, ref, onMounted } = Vue;

createApp({
    setup() {
        const loading = ref(true);
        const detail = ref(null);
        const showBigImage = ref(false);
        const bigImageUrl = ref('');
        const newComment = ref('');
        const comments = ref([]);
        const currentUser = ref(JSON.parse(localStorage.getItem('user')) || null);
        const commentLoading = ref(false);

        // 加载帖子详情
        const loadPostDetail = async () => {
            try {
                const postId = window.location.pathname.split('/').pop();
                const response = await fetch(`/api/posts/${postId}?currentUserId=${currentUser.value?.id || ''}`);
                const result = await response.json();
                
                if (result.success) {
                    detail.value = result.data;
                    await loadComments();
                }
            } catch (error) {
                console.error('加载帖子详情失败:', error);
            } finally {
                loading.value = false;
            }
        };

        // 加载评论
        const loadComments = async () => {
            try {
                commentLoading.value = true;
                const response = await fetch(`/api/comments?postId=${detail.value.postId}`);
                const result = await response.json();
                
                if (result.success) {
                    comments.value = result.data || [];
                }
            } catch (error) {
                console.error('加载评论失败:', error);
            } finally {
                commentLoading.value = false;
            }
        };

        // 点赞/取消点赞
        const toggleLike = async () => {
            console.log('Toggling like. Current user:', JSON.stringify(currentUser.value));
            console.log('User ID being sent:', currentUser.value?.id);
            if (!currentUser.value) {
                alert('请先登录');
                window.location.href = '/login';
                return;
            }
            
            try {
                const response = await fetch(`/api/posts/${detail.value.postId}/like`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ userId: currentUser.value.id })
                });
                
                const result = await response.json();
                if (result.success) {
                    // 使用后端返回的真实状态更新UI
                    detail.value.liked = result.liked;
                    detail.value.likeCount = result.likeCount;
                }
            } catch (error) {
                console.error('操作失败:', error);
                alert('操作失败，请重试');
            }
        };

        // 收藏/取消收藏
        const toggleFavorite = async () => {
            console.log('Toggling favorite. Current user:', JSON.stringify(currentUser.value));
            console.log('User ID being sent:', currentUser.value?.id);
            if (!currentUser.value) {
                alert('请先登录');
                window.location.href = '/login';
                return;
            }
            
            try {
                const response = await fetch(`/api/posts/${detail.value.postId}/favorite`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ userId: currentUser.value.id })
                });
                
                const result = await response.json();
                if (result.success) {
                    // 使用后端返回的真实状态更新UI
                    detail.value.favorited = result.favorited;
                    detail.value.favoriteCount = result.favoriteCount;
                }
            } catch (error) {
                console.error('操作失败:', error);
                alert('操作失败，请重试');
            }
        };

        // 提交评论
        const submitComment = async () => {
            console.log('Submitting comment. Current user:', JSON.stringify(currentUser.value));
            console.log('User ID being sent:', currentUser.value?.id);
            if (!currentUser.value) {
                alert('请先登录');
                window.location.href = '/login';
                return;
            }
            
            const content = newComment.value.trim();
            if (!content) {
                alert('评论内容不能为空');
                return;
            }
            
            try {
                const response = await fetch('/api/comments', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({
                        postId: detail.value.postId,
                        userId: currentUser.value.id,
                        content: content
                    })
                });
                
                const result = await response.json();
                if (result.success) {
                    newComment.value = '';
                    await loadComments();
                    // 更新评论数
                    detail.value.commentCount = (detail.value.commentCount || 0) + 1;
                }
            } catch (error) {
                console.error('提交评论失败:', error);
                alert('提交评论失败，请重试');
            }
        };

        // 滚动到评论区域
        const scrollToComments = () => {
            const commentsSection = document.getElementById('comments-section');
            if (commentsSection) {
                commentsSection.scrollIntoView({ behavior: 'smooth' });
                // 如果评论框被隐藏，显示它
                const commentForm = document.querySelector('.comment-form');
                if (commentForm) {
                    commentForm.style.display = 'block';
                }
            }
        };

        // 图片预览
        const showImage = (url) => {
            bigImageUrl.value = url;
            showBigImage.value = true;
        };

        // 关闭图片预览
        const closeImage = () => {
            showBigImage.value = false;
            bigImageUrl.value = '';
        };

        // 全屏播放视频
        const requestVideoFullscreen = (event) => {
            const video = event.target;
            if (video.requestFullscreen) {
                video.requestFullscreen();
            } else if (video.webkitRequestFullscreen) {
                video.webkitRequestFullscreen();
            } else if (video.mozRequestFullScreen) {
                video.mozRequestFullScreen();
            } else if (video.msRequestFullscreen) {
                video.msRequestFullscreen();
            }
        };

        // 组件挂载时加载数据
        onMounted(() => {
            loadPostDetail();
            
            // 检查URL中是否有评论锚点
            if (window.location.hash === '#comments') {
                setTimeout(scrollToComments, 500);
            }
        });

        return {
            loading,
            detail,
            showBigImage,
            bigImageUrl,
            newComment,
            comments,
            currentUser,
            commentLoading,
            formatTime,
            toggleLike,
            toggleFavorite,
            submitComment,
            scrollToComments,
            showImage,
            closeImage,
            requestVideoFullscreen
        };
    }
}).mount('#post-detail-app');
