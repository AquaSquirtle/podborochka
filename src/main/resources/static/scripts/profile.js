const avatarImg = document.getElementById("profile-avatar");
const displayNameEl = document.getElementById("profile-display-name");
const emailEl = document.getElementById("profile-email");
const roleEl = document.getElementById("profile-role");
const avatarForm = document.getElementById("avatar-form");
const avatarFileInput = document.getElementById("avatar-file");
const messageEl = document.getElementById("profile-message");
const fileNameEl = document.getElementById("file-name");

async function loadProfile() {
    try {
        const response = await fetch("/api/v1/users/me", {
            credentials: "same-origin"
        });

        if (!response.ok) {
            throw new Error(`Ошибка загрузки профиля: ${response.status}`);
        }

        const user = await response.json();

        displayNameEl.textContent = user.displayName ?? "—";
        emailEl.textContent = user.email ?? "—";
        roleEl.textContent = user.role ?? "—";

        if (user.avatarUrl) {
            avatarImg.src = user.avatarUrl;
        }
    } catch (error) {
        console.error(error);
        messageEl.textContent = "Не удалось загрузить профиль.";
    }
}

async function uploadAvatar(event) {
    event.preventDefault();

    const file = avatarFileInput.files?.[0];
    if (!file) {
        messageEl.textContent = "Выберите файл.";
        return;
    }

    const formData = new FormData();
    formData.append("file", file);

    try {
        messageEl.textContent = "Загрузка...";

        const response = await fetch("/api/v1/users/me/avatar", {
            method: "POST",
            body: formData,
            credentials: "same-origin"
        });

        if (!response.ok) {
            throw new Error(`Ошибка загрузки аватара: ${response.status}`);
        }

        const updatedUser = await response.json();

        if (updatedUser.avatarUrl) {
            avatarImg.src = `${updatedUser.avatarUrl}?t=${Date.now()}`;
        }

        messageEl.textContent = "Аватар обновлён.";
        avatarFileInput.value = "";

        if (fileNameEl) {
            fileNameEl.textContent = "Файл не выбран";
        }
    } catch (error) {
        console.error(error);
        messageEl.textContent = "Не удалось загрузить аватар.";
    }
}

document.addEventListener("DOMContentLoaded", () => {
    loadProfile();

    if (avatarForm) {
        avatarForm.addEventListener("submit", uploadAvatar);
    }

    if (avatarFileInput) {
        avatarFileInput.addEventListener("change", () => {
            const file = avatarFileInput.files?.[0];
            if (fileNameEl) {
                fileNameEl.textContent = file ? file.name : "Файл не выбран";
            }
        });
    }
});