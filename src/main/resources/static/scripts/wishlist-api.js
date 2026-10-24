export async function fetchWishlist(page = 0, size = 50) {
    const response = await fetch(`/api/v1/wishlist-items?page=${page}&size=${size}`);

    if (!response.ok) {
        throw new Error(`HTTP ${response.status}`);
    }

    return await response.json();
}

export async function createWishlistItem(payload) {
    const response = await fetch("/api/v1/wishlist-items", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(payload)
    });

    if (!response.ok) {
        throw new Error(`HTTP ${response.status}`);
    }

    return await response.json();
}

export async function updateWishlistItem(id, payload) {
    const response = await fetch(`/api/v1/wishlist-items/${id}`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(payload)
    });

    if (!response.ok) {
        throw new Error(`HTTP ${response.status}`);
    }

    return await response.json();
}

export async function deleteWishlistItem(id) {
    const response = await fetch(`/api/v1/wishlist-items/${id}`, {
        method: "DELETE"
    });

    if (!response.ok) {
        throw new Error(`HTTP ${response.status}`);
    }
}