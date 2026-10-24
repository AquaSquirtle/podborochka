const STORAGE_KEY = "podborochka_wishlist";

export const loadItems = () => {
    try {
        const raw = localStorage.getItem(STORAGE_KEY);
        if (!raw) return [];

        const parsed = JSON.parse(raw);
        return Array.isArray(parsed) ? parsed : [];
    } catch (error) {
        console.error("Не удалось прочитать wishlist из localStorage:", error);
        return [];
    }
};

export const saveItems = (items) => {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(items));
};