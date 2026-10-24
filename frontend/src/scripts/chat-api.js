import {initWishlistToastSse, showToast} from "./wishlist-toast.js";


const chatForm = document.getElementById("chat-form");
const chatInput = document.getElementById("chat-input");
const chatSubmit = document.getElementById("chat-submit");
const chatWindow = document.getElementById("chat-window");
const chatPlaceholder = document.getElementById("chat-placeholder");

const conversationList = document.getElementById("conversation-list");
const newConversationBtn = document.getElementById("new-conversation-btn");

let currentConversationId = null;
let conversations = [];

function removePlaceholder() {
    if (chatPlaceholder) {
        chatPlaceholder.remove();
    }
}

function scrollChatToBottom(smooth = true) {
    if (!chatWindow) return;

    chatWindow.scrollTo({
        top: chatWindow.scrollHeight,
        behavior: smooth ? "smooth" : "auto"
    });
}

function clearChatWindow() {
    if (!chatWindow) return;
    chatWindow.innerHTML = "";
}

function createMessage(role, text = "") {
    const wrapper = document.createElement("div");
    wrapper.className = `chat__message chat__message--${role}`;

    const bubble = document.createElement("div");
    bubble.className = "chat__bubble";

    const paragraph = document.createElement("p");
    paragraph.className = "chat__text";
    paragraph.textContent = text;

    bubble.appendChild(paragraph);
    wrapper.appendChild(bubble);
    chatWindow.appendChild(wrapper);

    return { wrapper, bubble, paragraph };
}

function createAssistantRichMessage() {
    const wrapper = document.createElement("div");
    wrapper.className = "chat__message chat__message--assistant";

    const bubble = document.createElement("div");
    bubble.className = "chat__bubble";

    wrapper.appendChild(bubble);
    chatWindow.appendChild(wrapper);

    return { wrapper, bubble };
}

function appendUserMessage(text) {
    removePlaceholder();
    createMessage("user", text);
    scrollChatToBottom();
}

function appendAssistantMessage(text) {
    removePlaceholder();
    const message = createMessage("assistant", text || "Пустой ответ от backend.");
    scrollChatToBottom();
    return message;
}

function appendLoadingMessage() {
    removePlaceholder();

    const wrapper = document.createElement("div");
    wrapper.className = "chat__message chat__message--assistant";
    wrapper.id = "chat-loading-message";

    const bubble = document.createElement("div");
    bubble.className = "chat__bubble chat__bubble--loading";

    bubble.innerHTML = `
        <span class="chat__dot"></span>
        <span class="chat__dot"></span>
        <span class="chat__dot"></span>
    `;

    wrapper.appendChild(bubble);
    chatWindow.appendChild(wrapper);

    scrollChatToBottom();

    return wrapper;
}

function removeLoadingMessage() {
    const node = document.getElementById("chat-loading-message");
    if (node) node.remove();
}

function escapeHtml(text) {
    const div = document.createElement("div");
    div.innerText = text ?? "";
    return div.innerHTML;
}

function createRecommendationCard(item) {
    const wrapper = document.createElement("div");
    wrapper.className = "gift-recommendation";

    const price = item.approximatePrice != null ? `${item.approximatePrice} ₽` : "—";

    const header = document.createElement("div");
    header.className = "gift-recommendation__header";

    const rank = document.createElement("div");
    rank.className = "gift-recommendation__rank";
    rank.textContent = `${item.rankPosition}.`;

    const title = document.createElement("div");
    title.className = "gift-recommendation__title";
    title.textContent = item.title || "Без названия";

    header.append(rank, title);

    const priceEl = document.createElement("div");
    priceEl.className = "gift-recommendation__price";
    priceEl.textContent = `Примерная цена: ${price}`;

    const description = document.createElement("div");
    description.className = "gift-recommendation__description";
    description.textContent = item.description || "";

    const reasoning = document.createElement("div");
    reasoning.className = "gift-recommendation__reasoning";
    reasoning.innerHTML = `<b>Почему подходит:</b> ${escapeHtml(item.reasoning || "")}`;

    const links = document.createElement("div");
    links.className = "gift-recommendation__links";

    if (item.ozonSearchUrl) {
        const a = document.createElement("a");
        a.href = item.ozonSearchUrl;
        a.target = "_blank";
        a.rel = "noopener noreferrer";
        a.textContent = "Ozon";
        links.appendChild(a);
    }

    if (item.wildberriesSearchUrl) {
        const a = document.createElement("a");
        a.href = item.wildberriesSearchUrl;
        a.target = "_blank";
        a.rel = "noopener noreferrer";
        a.textContent = "Wildberries";
        links.appendChild(a);
    }

    if (item.yandexMarketSearchUrl) {
        const a = document.createElement("a");
        a.href = item.yandexMarketSearchUrl;
        a.target = "_blank";
        a.rel = "noopener noreferrer";
        a.textContent = "Яндекс Маркет";
        links.appendChild(a);
    }

    const actions = document.createElement("div");
    actions.className = "gift-recommendation__actions";

    const button = document.createElement("button");
    button.type = "button";
    button.className = "button gift-recommendation__save-button";
    button.setAttribute("data-save-gift", "");
    button.dataset.recommendationId = item.id ?? "";
    button.dataset.title = item.title || "";
    button.dataset.description = item.description || "";
    button.dataset.reasoning = item.reasoning || "";
    button.dataset.price = item.approximatePrice ?? "";
    button.dataset.query = item.searchQuery || "";
    button.dataset.ozon = item.ozonSearchUrl || "";
    button.dataset.wb = item.wildberriesSearchUrl || "";
    button.dataset.ym = item.yandexMarketSearchUrl || "";
    button.textContent = "Сохранить";

    actions.appendChild(button);

    wrapper.append(header, priceEl, description, reasoning, links, actions);

    return wrapper;
}

function appendRecommendationCards(data, options = {}) {
    removePlaceholder();

    const message = createAssistantRichMessage();

    if (options.showAssistantMessage && data?.assistantMessage) {
        const assistantParagraph = document.createElement("p");
        assistantParagraph.className = "chat__text";
        assistantParagraph.textContent = data.assistantMessage;
        message.bubble.appendChild(assistantParagraph);
    }

    if (data?.recipientSummary) {
        const summaryParagraph = document.createElement("p");
        summaryParagraph.className = "chat__text";
        summaryParagraph.textContent = data.recipientSummary;
        message.bubble.appendChild(summaryParagraph);
    }

    if (Array.isArray(data?.recommendations) && data.recommendations.length > 0) {
        data.recommendations.forEach((item) => {
            message.bubble.appendChild(createRecommendationCard(item));
        });
    } else {
        const emptyParagraph = document.createElement("p");
        emptyParagraph.className = "chat__text";
        emptyParagraph.textContent = "Не удалось получить рекомендации.";
        message.bubble.appendChild(emptyParagraph);
    }

    scrollChatToBottom();
}

function appendAssistantRecommendations(data) {
    appendRecommendationCards(data, { showAssistantMessage: true });
}

function normalizeSavedRecommendation(item) {
    return {
        id: item.id,
        title: item.title,
        description: item.description,
        reasoning: item.reasoning,
        approximatePrice: item.approximatePrice,
        searchQuery: item.searchQuery,
        rankPosition: item.rankPosition,
        ozonSearchUrl: item.ozonSearchUrl,
        wildberriesSearchUrl: item.wildberriesSearchUrl,
        yandexMarketSearchUrl: item.yandexMarketSearchUrl
    };
}

function renderConversationList() {
    if (!conversationList) return;

    conversationList.innerHTML = "";

    if (!Array.isArray(conversations) || conversations.length === 0) {
        const empty = document.createElement("p");
        empty.className = "chat-sidebar__empty";
        empty.textContent = "Пока нет чатов";
        conversationList.appendChild(empty);
        return;
    }

    conversations.forEach((conversation) => {
        const button = document.createElement("button");
        button.type = "button";
        button.className = "chat-sidebar__item";
        if (conversation.id === currentConversationId) {
            button.classList.add("chat-sidebar__item--active");
        }

        button.textContent = conversation.title?.trim() || `Чат #${conversation.id}`;
        button.dataset.conversationId = String(conversation.id);

        button.addEventListener("click", async () => {
            await openConversation(conversation.id);
        });

        conversationList.appendChild(button);
    });
}

function renderMessagesPage(pageResponse, recommendationBundles = []) {
    clearChatWindow();

    if (!pageResponse?.content || pageResponse.content.length === 0) {
        if (chatWindow) {
            const placeholder = document.createElement("p");
            placeholder.className = "chat__placeholder";
            placeholder.textContent = "ИИ готов помочь — задайте вопрос!";
            chatWindow.appendChild(placeholder);
        }
        return;
    }

    let bundleIndex = 0;

    pageResponse.content.forEach((message) => {
        const normalizedRole =
            message.role?.toLowerCase() === "user" ? "user" : "assistant";
        createMessage(normalizedRole, message.content || "");

        if (normalizedRole === "assistant" && recommendationBundles[bundleIndex]) {
            appendRecommendationCards(recommendationBundles[bundleIndex]);
            bundleIndex += 1;
        }
    });

    while (recommendationBundles[bundleIndex]) {
        appendRecommendationCards(recommendationBundles[bundleIndex]);
        bundleIndex += 1;
    }

    scrollChatToBottom(false);
}

async function apiFetch(url, options = {}) {
    const response = await fetch(url, {
        headers: {
            "Content-Type": "application/json",
            ...(options.headers || {})
        },
        ...options
    });

    if (!response.ok) {
        throw new Error(`HTTP ${response.status}`);
    }

    if (response.status === 204) {
        return null;
    }

    return await response.json();
}

async function getConversations(page = 0, size = 50) {
    return apiFetch(`/api/v1/conversations?page=${page}&size=${size}`, {
        method: "GET"
    });
}

async function createConversation(title = null) {
    return apiFetch("/api/v1/conversations", {
        method: "POST",
        body: JSON.stringify({ title })
    });
}

async function getMessages(conversationId, page = 0, size = 100) {
    return apiFetch(`/api/v1/conversations/${conversationId}/messages?page=${page}&size=${size}`, {
        method: "GET"
    });
}

async function sendPrompt(conversationId, prompt) {
    return apiFetch(`/api/v1/chat/${conversationId}/message`, {
        method: "POST",
        body: JSON.stringify({ prompt })
    });
}

async function getGiftSearchRequests(conversationId, page = 0, size = 100) {
    return apiFetch(`/api/v1/conversations/${conversationId}/gift-search-requests?page=${page}&size=${size}`, {
        method: "GET"
    });
}

async function getRecommendations(searchRequestId, page = 0, size = 20) {
    return apiFetch(`/api/v1/gift-search-requests/${searchRequestId}/recommendations?page=${page}&size=${size}`, {
        method: "GET"
    });
}

async function loadRecommendationBundles(conversationId) {
    const searchRequestsPage = await getGiftSearchRequests(conversationId);
    const searchRequests = Array.isArray(searchRequestsPage?.content)
        ? [...searchRequestsPage.content].reverse()
        : [];

    const bundles = await Promise.all(
        searchRequests.map(async (searchRequest) => {
            const recommendationsPage = await getRecommendations(searchRequest.id);
            const recommendations = Array.isArray(recommendationsPage?.content)
                ? recommendationsPage.content.map(normalizeSavedRecommendation)
                : [];

            if (recommendations.length === 0) {
                return null;
            }

            return {
                recipientSummary: searchRequest.recipientDescription,
                recommendations
            };
        })
    );

    return bundles.filter(Boolean);
}

async function saveGift(button) {
    const recommendationId = button.dataset.recommendationId
        ? Number(button.dataset.recommendationId)
        : null;

    const customUrl =
        button.dataset.ozon ||
        button.dataset.wb ||
        button.dataset.ym ||
        null;

    const payload = {
        recommendationId,
        customTitle: button.dataset.title || "",
        customUrl,
        notes: button.dataset.reasoning || ""
    };

    return apiFetch("/api/v1/wishlist-items", {
        method: "POST",
        body: JSON.stringify(payload)
    });
}

async function loadConversationList() {
    const pageData = await getConversations();
    conversations = Array.isArray(pageData?.content) ? pageData.content : [];
    renderConversationList();

    if (!currentConversationId && conversations.length > 0) {
        await openConversation(conversations[0].id);
    }
}

async function openConversation(conversationId) {
    currentConversationId = conversationId;
    renderConversationList();

    const [messagesPage, recommendationBundles] = await Promise.all([
        getMessages(conversationId),
        loadRecommendationBundles(conversationId)
    ]);

    renderMessagesPage(messagesPage, recommendationBundles);
}

async function handleNewConversation() {
    const created = await createConversation("Новый чат");
    conversations.unshift(created);
    await openConversation(created.id);
    renderConversationList();
}

if (newConversationBtn) {
    newConversationBtn.addEventListener("click", async () => {
        try {
            await handleNewConversation();
        } catch (error) {
            alert(`Не удалось создать чат: ${error.message}`);
        }
    });
}

if (chatForm) {
    chatForm.addEventListener("submit", async (event) => {
        event.preventDefault();

        const prompt = chatInput?.value?.trim();
        if (!prompt) return;

        try {
            if (!currentConversationId) {
                await handleNewConversation();
            }

            appendUserMessage(prompt);
            chatInput.value = "";
            chatInput.focus();

            if (chatSubmit) {
                chatSubmit.disabled = true;
                chatSubmit.classList.add("button--disabled");
            }

            appendLoadingMessage();

            const data = await sendPrompt(currentConversationId, prompt);

            removeLoadingMessage();
            appendAssistantRecommendations(data);

            if (conversations.length > 0) {
                const current = conversations.find((c) => c.id === currentConversationId);
                if (current && (!current.title || current.title === "Новый чат")) {
                    current.title = prompt.slice(0, 40);
                    renderConversationList();
                }
            }
        } catch (error) {
            removeLoadingMessage();
            appendAssistantMessage(`Ошибка: ${error.message}`);
        } finally {
            if (chatSubmit) {
                chatSubmit.disabled = false;
                chatSubmit.classList.remove("button--disabled");
            }
        }
    });
}

document.addEventListener("click", async (event) => {
    const button = event.target.closest("[data-save-gift]");
    if (!button) return;

    const originalText = button.textContent;

    button.disabled = true;
    button.textContent = "Сохраняем...";

    try {
        await saveGift(button);
        button.textContent = "Сохранено";
        button.classList.add("gift-recommendation__save-button--saved");
    } catch (error) {
        button.disabled = false;
        button.textContent = originalText;
        showToast(`Не удалось сохранить подарок: ${error.message}`, "error");
    }
});

(async function initChat() {
    if (!chatWindow) return;

    try {
        await loadConversationList();
    } catch (error) {
        appendAssistantMessage(`Ошибка загрузки чатов: ${error.message}`);
    }

    initWishlistToastSse();
})();
