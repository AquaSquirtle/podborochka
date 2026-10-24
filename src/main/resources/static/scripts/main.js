(() => {
    window.addEventListener("load", () => {
        const footer = document.querySelector(".footer");
        if (!footer) return;

        const loadTime = performance.now() / 1000;
        const timeElement = document.createElement("div");
        timeElement.className = "footer__load-time";
        timeElement.style.marginTop = "8px";
        timeElement.style.opacity = "0.7";
        timeElement.textContent = `Клиентская загрузка: ${loadTime.toFixed(2)} сек.`;
        footer.appendChild(timeElement);
    });

    document.addEventListener("DOMContentLoaded", () => {
        const highlightActiveLink = () => {
            const currentPath = window.location.pathname;

            document.querySelectorAll(".header__link").forEach((link) => {
                const href = link.getAttribute("href");
                if (!href) return;

                const linkPath = new URL(href, window.location.href).pathname;

                if (linkPath === currentPath) {
                    link.classList.add("header__link--active");
                }
            });
        };

        highlightActiveLink();
    });
})();

function getErrorMessage(input) {
    if (input.validity.valueMissing) {
        return "Поле обязательно для заполнения";
    }

    if (input.validity.typeMismatch && input.type === "email") {
        return "Введите корректный email";
    }

    if (input.validity.tooShort) {
        return `Минимум ${input.minLength} символов`;
    }

    return "";
}

function showFieldError(input, errorBlock) {
    const message = getErrorMessage(input);

    if (!message) {
        errorBlock.textContent = "";
        input.classList.remove("auth__input--invalid");
        return true;
    }

    errorBlock.textContent = message;
    input.classList.add("auth__input--invalid");
    return false;
}

function bindValidation(inputSelector, errorSelector) {
    const input = document.querySelector(inputSelector);
    const errorBlock = document.querySelector(errorSelector);

    if (!input || !errorBlock) return null;

    input.addEventListener("input", () => showFieldError(input, errorBlock));
    input.addEventListener("blur", () => showFieldError(input, errorBlock));

    return () => showFieldError(input, errorBlock);
}

function initLoginValidation() {
    const form = document.querySelector("#login-form");
    if (!form) return;

    const validateEmail = bindValidation("#login-email", "#login-email-error");
    const validatePassword = bindValidation("#login-password", "#login-password-error");

    form.addEventListener("submit", (e) => {
        const emailOk = validateEmail ? validateEmail() : true;
        const passwordOk = validatePassword ? validatePassword() : true;

        if (!(emailOk && passwordOk)) {
            e.preventDefault();
        }
    });
}

function initRegisterValidation() {
    const form = document.querySelector("#register-form");
    if (!form) return;

    const validateName = bindValidation("#register-name", "#register-name-error");
    const validateEmail = bindValidation("#register-email", "#register-email-error");
    const validatePassword = bindValidation("#register-password", "#register-password-error");

    form.addEventListener("submit", (e) => {
        const nameOk = validateName ? validateName() : true;
        const emailOk = validateEmail ? validateEmail() : true;
        const passwordOk = validatePassword ? validatePassword() : true;

        if (!(nameOk && emailOk && passwordOk)) {
            e.preventDefault();
        }
    });
}

document.addEventListener("DOMContentLoaded", () => {
    initLoginValidation();
    initRegisterValidation();
});