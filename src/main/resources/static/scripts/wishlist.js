import { createWishlistItem, deleteWishlistItem, fetchWishlist, updateWishlistItem } from "./wishlist-api.js";
import { showToast } from "./wishlist-toast.js";

let form;
let list;
let template;

let titleInput;
let linkInput;
let titleError;
let linkError;
let addButton;

let wishlistItems = [];

const isNonEmptyString = (value) => typeof value === "string" && value.trim().length > 0;

const setInvalidState = (inputEl, errorBlock, message, invalidClassName) => {
    errorBlock.textContent = message;
    inputEl.classList.add(invalidClassName);
};

const clearInvalidState = (inputEl, errorBlock, invalidClassName) => {
    errorBlock.textContent = "";
    inputEl.classList.remove(invalidClassName);
};

const validateTitle = (value, errorBlock, inputEl, invalidClassName = "wishlist__input--invalid") => {
    const touched = inputEl.dataset.touched === "true";

    if (!touched) {
        clearInvalidState(inputEl, errorBlock, invalidClassName);
        return false;
    }

    if (!value.trim()) {
        setInvalidState(inputEl, errorBlock, "Введите название подарка.", invalidClassName);
        return false;
    }

    clearInvalidState(inputEl, errorBlock, invalidClassName);
    return true;
};

const validateLink = (value, errorBlock, inputEl, invalidClassName = "wishlist__input--invalid") => {
    const touched = inputEl.dataset.touched === "true";

    if (!touched) {
        clearInvalidState(inputEl, errorBlock, invalidClassName);
        return false;
    }

    if (!value.trim()) {
        setInvalidState(inputEl, errorBlock, "Введите ссылку.", invalidClassName);
        return false;
    }

    if (!/^https?:\/\//.test(value.trim())) {
        setInvalidState(
            inputEl,
            errorBlock,
            "Ссылка должна начинаться с http:// или https://",
            invalidClassName
        );
        return false;
    }

    clearInvalidState(inputEl, errorBlock, invalidClassName);
    return true;
};

const updateAddButtonState = () => {
    const validTitle = validateTitle(titleInput.value, titleError, titleInput);
    const validLink = validateLink(linkInput.value, linkError, linkInput);

    addButton.classList.toggle("button--disabled", !(validTitle && validLink));
};

const renderEmptyState = () => {
    const empty = document.createElement("p");
    empty.className = "wishlist__empty";
    empty.textContent = "Пока нет сохранённых подарков";
    list.appendChild(empty);
};

function toViewModel(item) {
    return {
        id: item.id,
        title: item.customTitle || `Подарок #${item.id}`,
        link: item.customUrl || "#",
        notes: item.notes || "",
        isPurchased: Boolean(item.isPurchased)
    };
}

const renderItem = (item) => {
    if (!item) return;

    const node = template.content.cloneNode(true);

    const wrapper = node.querySelector(".wishlist-item");
    const titleLink = node.querySelector(".wishlist-item__title");

    const inputTitle = node.querySelector(".wishlist-item__input-edit--title");
    const inputLink = node.querySelector(".wishlist-item__input-edit--link");

    const errTitle = node.querySelector(".wishlist-item__error--title");
    const errLink = node.querySelector(".wishlist-item__error--link");

    const editBtn = node.querySelector(".wishlist-item__edit");
    const saveBtn = node.querySelector(".wishlist-item__save");
    const cancelBtn = node.querySelector(".wishlist-item__cancel");
    const deleteBtn = node.querySelector(".wishlist-item__delete");

    titleLink.textContent = item.title;
    titleLink.href = item.link || "#";

    if (!item.link || item.link === "#") {
        titleLink.removeAttribute("target");
    }

    if (item.isPurchased) {
        wrapper.classList.add("wishlist-item--purchased");
    }

    editBtn.addEventListener("click", () => {
        wrapper.classList.add("wishlist-item--editing");

        inputTitle.value = item.title;
        inputLink.value = item.link === "#" ? "" : item.link;

        inputTitle.dataset.touched = "false";
        inputLink.dataset.touched = "false";
        inputTitle.dataset.originalValue = item.title;
        inputLink.dataset.originalValue = item.link === "#" ? "" : item.link;

        errTitle.textContent = "";
        errLink.textContent = "";

        inputTitle.classList.remove("wishlist-item__input-edit--invalid");
        inputLink.classList.remove("wishlist-item__input-edit--invalid");

        saveBtn.classList.add("button--disabled");
    });

    const validateEdit = () => {
        const okTitle = validateTitle(
            inputTitle.value,
            errTitle,
            inputTitle,
            "wishlist-item__input-edit--invalid"
        );
        const okLink = validateLink(
            inputLink.value,
            errLink,
            inputLink,
            "wishlist-item__input-edit--invalid"
        );

        const titleChanged = inputTitle.value !== inputTitle.dataset.originalValue;
        const linkChanged = inputLink.value !== inputLink.dataset.originalValue;
        const hasChanges = titleChanged || linkChanged;

        const shouldEnable = okTitle && okLink && hasChanges;
        saveBtn.classList.toggle("button--disabled", !shouldEnable);

        return shouldEnable;
    };

    inputTitle.addEventListener("input", () => {
        inputTitle.dataset.touched = "true";
        validateEdit();
    });

    inputLink.addEventListener("input", () => {
        inputLink.dataset.touched = "true";
        validateEdit();
    });

    saveBtn.addEventListener("click", async () => {
        inputTitle.dataset.touched = "true";
        inputLink.dataset.touched = "true";

        if (!validateEdit()) return;

        try {
            const updated = await updateWishlistItem(item.id, {
                customTitle: inputTitle.value.trim(),
                customUrl: inputLink.value.trim()
            });

            const index = wishlistItems.findIndex((wishlistItem) => wishlistItem.id === item.id);
            if (index !== -1) {
                wishlistItems[index] = updated;
            }

            rerender();
            showToast("Подарок обновлён");
        } catch (error) {
            showToast(`Не удалось обновить подарок: ${error.message}`, "error");
        }
    });

    cancelBtn.addEventListener("click", () => {
        wrapper.classList.remove("wishlist-item--editing");
    });

    deleteBtn.addEventListener("click", async () => {
        try {
            await deleteWishlistItem(item.id);
            wishlistItems = wishlistItems.filter((wishlistItem) => wishlistItem.id !== item.id);
            rerender();
            showToast("Подарок удалён");
        } catch (error) {
            showToast(`Не удалось удалить подарок: ${error.message}`, "error");
        }
    });

    list.appendChild(node);
};

const rerender = () => {
    list.innerHTML = "";

    if (!Array.isArray(wishlistItems) || wishlistItems.length === 0) {
        renderEmptyState();
        return;
    }

    wishlistItems.map(toViewModel).forEach(renderItem);
};

async function loadWishlist() {
    const pageData = await fetchWishlist();
    wishlistItems = Array.isArray(pageData?.content) ? pageData.content : [];
    rerender();
}

export const initWishlist = () => {
    form = document.querySelector("#wishlist-form");
    list = document.querySelector("#wishlist-list");
    template = document.querySelector("#wishlist-item-template");

    if (!form || !list || !template) return;

    titleInput = document.querySelector("#gift-title");
    linkInput = document.querySelector("#gift-link");
    titleError = document.querySelector("#gift-title-error");
    linkError = document.querySelector("#gift-link-error");
    addButton = form.querySelector('button[type="submit"]');

    titleInput.dataset.touched = "false";
    linkInput.dataset.touched = "false";

    titleInput.addEventListener("input", () => {
        titleInput.dataset.touched = "true";
        updateAddButtonState();
    });

    linkInput.addEventListener("input", () => {
        linkInput.dataset.touched = "true";
        updateAddButtonState();
    });

    form.addEventListener("submit", async (e) => {
        e.preventDefault();

        titleInput.dataset.touched = "true";
        linkInput.dataset.touched = "true";
        updateAddButtonState();

        const validTitle = validateTitle(titleInput.value, titleError, titleInput);
        const validLink = validateLink(linkInput.value, linkError, linkInput);

        if (!(validTitle && validLink)) return;

        try {
            const created = await createWishlistItem({
                customTitle: titleInput.value.trim(),
                customUrl: linkInput.value.trim()
            });

            wishlistItems.unshift(created);

            form.reset();
            titleInput.dataset.touched = "false";
            linkInput.dataset.touched = "false";

            updateAddButtonState();
            rerender();
            showToast("Подарок добавлен в список");
        } catch (error) {
            showToast(`Не удалось добавить подарок: ${error.message}`, "error");
        }
    });

    updateAddButtonState();
    loadWishlist().catch((error) => {
        showToast(`Не удалось загрузить wishlist: ${error.message}`, "error");
    });
};

initWishlist();