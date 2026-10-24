export const showToast = (message, type = 'success') => {
    const container = document.getElementById('notifications');
    const toast = document.createElement('button');
    toast.type = 'button';
    toast.className = `notification notification--${type}`;
    toast.textContent = message;
    toast.title = 'Нажмите, чтобы закрыть';
    container.append(toast);
    toast.addEventListener('click', () => toast.remove());
    setTimeout(() => toast.remove(), 5000);
};

let wishlistEventSource = null;

export const initWishlistToastSse = () => {
    if (wishlistEventSource) return;
    wishlistEventSource = new EventSource('/api/v1/wishlist-notifications');
    wishlistEventSource.addEventListener('wishlist-saved', (event) => {
        const title = (event.data || '').trim();
        showToast(title ? `Подарок «${title}» сохранён в вишлист` : 'Подарок сохранён в вишлист');
    });
    window.addEventListener('pagehide', () => wishlistEventSource?.close(), { once: true });
};
