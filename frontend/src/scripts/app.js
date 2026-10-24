import { showToast } from './wishlist-toast.js';

const page = document.body.dataset.page;
const protectedPages = new Set(['chat', 'wishlist', 'profile']);
const authStatus = document.getElementById('auth-status');

function destination() {
    const next = new URLSearchParams(location.search).get('next');
    if (!next) return '/chat';
    const url = new URL(next, location.origin);
    return url.origin === location.origin && ['/chat', '/wishlist', '/profile'].includes(url.pathname)
        ? url.pathname : '/chat';
}

function link(label, href) {
    const element = document.createElement('a');
    element.className = 'header__link link';
    element.textContent = label;
    element.href = href;
    return element;
}

function renderAuth(user) {
    authStatus.replaceChildren();
    if (user) {
        authStatus.append(link(`Вы вошли как ${user.displayName}`, '/profile'));
        const logout = document.createElement('button');
        logout.type = 'button';
        logout.className = 'header__link link header__logout-button';
        logout.textContent = 'Выход';
        logout.addEventListener('click', async () => {
            logout.disabled = true;
            try {
                // Spring Security's existing logout filter invalidates the session.
                const response = await fetch('/logout', { method: 'POST', credentials: 'same-origin' });
                if (!response.ok) throw new Error('Logout failed');
                location.assign('/');
            } catch {
                showToast('Не удалось выйти. Попробуйте ещё раз.', 'error');
                logout.disabled = false;
            }
        });
        authStatus.append(logout);
    } else {
        authStatus.append(link('Регистрация', '/register/form'), link('Вход', '/login/form'));
    }
}

async function postAuth(action, values) {
    const response = await fetch(`/api/v1/auth/${action}`, {
        method: 'POST',
        credentials: 'same-origin',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(values)
    });
    if (!response.ok) {
        if (response.status === 409) throw new Error('Пользователь с таким email уже существует.');
        if (response.status === 400) throw new Error('Проверьте заполненные поля.');
        throw new Error(action === 'login'
            ? 'Не удалось войти. Проверьте email и пароль.'
            : 'Не удалось зарегистрироваться. Попробуйте ещё раз.');
    }
    return response.json();
}

function bindAuthForm() {
    const form = document.getElementById(`${page}-form`);
    const error = document.getElementById('auth-error');
    const button = form.querySelector('button[type="submit"]');
    form.addEventListener('submit', async (event) => {
        event.preventDefault();
        if (button.disabled || !form.reportValidity()) return;
        error.hidden = true;
        button.disabled = true;
        const values = Object.fromEntries(new FormData(form));
        values.email = values.email.trim();
        if (values.displayName) values.displayName = values.displayName.trim();
        let registered = false;
        try {
            if (page === 'register') {
                await postAuth('register', values);
                registered = true;
            }
            await postAuth('login', { email: values.email, password: values.password });
            location.assign(destination());
        } catch (cause) {
            error.textContent = registered
                ? 'Аккаунт создан, но автоматический вход не удался. Перейдите на страницу входа.'
                : cause.message;
            error.hidden = false;
            button.disabled = false;
        }
    });
}

async function init() {
    const response = await fetch('/api/v1/users/me', { credentials: 'same-origin' });
    let user = null;
    if (response.ok) user = await response.json();
    else if (response.status !== 401) throw new Error('Backend unavailable');
    if (!user && protectedPages.has(page)) {
        location.replace(`/login/form?next=${encodeURIComponent(location.pathname)}`);
        return;
    }
    if (user && ['login', 'register'].includes(page)) {
        location.replace(destination());
        return;
    }
    renderAuth(user);
    for (const item of document.querySelectorAll('.header__link[href]')) {
        item.classList.toggle('header__link--active', item.getAttribute('href') === location.pathname);
    }
    document.body.dataset.ready = 'true';
    if (['login', 'register'].includes(page)) bindAuthForm();
    if (page === 'chat') await import('./chat-api.js');
    if (page === 'wishlist') await import('./wishlist.js');
    if (page === 'profile') await import('./profile.js');
}

init().catch(() => {
    // Keep public forms and navigation usable after a temporary service failure.
    renderAuth(null);
    if (['login', 'register'].includes(page)) bindAuthForm();
    showToast('Не удалось подключиться к приложению. Обновите страницу через несколько секунд.', 'error');
});
