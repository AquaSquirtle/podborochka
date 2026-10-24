import Toastify from "https://cdn.jsdelivr.net/npm/toastify-js/src/toastify-es.js";

export const showToast = (message, type = "success") => {
    Toastify({
        text: message,
        duration: 3000,
        close: true,
        gravity: "bottom",
        position: "right",
        stopOnFocus: true,
        className: `toastify-podborochka toastify-podborochka--${type}`,
    }).showToast();
};

let wishlistEventSource = null;

export const initWishlistToastSse = () => {
    if (wishlistEventSource) {
        return;
    }

    wishlistEventSource = new EventSource("/api/v1/wishlist-notifications");

    wishlistEventSource.addEventListener("wishlist-saved", (event) => {
        const title = (event.data || "").trim();
        showToast(
            title ? `Подарок "${title}" сохранён в вишлист` : "Подарок сохранён в вишлист"
        );
    });

    wishlistEventSource.addEventListener("error", () => {
        if (wishlistEventSource?.readyState === EventSource.CLOSED) {
            wishlistEventSource = null;
        }
    });
};