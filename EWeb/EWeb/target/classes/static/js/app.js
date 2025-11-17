const apiUrl = "http://localhost:8080/api/items";

function formatPrice(value) {
    if (value == null) return "-";
    return new Intl.NumberFormat(undefined, { style: 'currency', currency: 'USD' }).format(value);
}

function formatDate(value) {
    if (!value) return "-";
    try { return new Date(value).toLocaleString(); } catch { return String(value); }
}

// Load items for dashboard and user page
function loadItems(targetId) {
    fetch(apiUrl)
        .then(res => res.json())
        .then(items => renderItems(targetId, items))
        .catch(() => renderItems(targetId, []));
}

function renderItems(targetId, items) {
    const container = document.getElementById(targetId);
    if (!container) return;
    container.innerHTML = "";
    items.forEach(item => {
        const div = document.createElement("div");
        div.className = "item-card";
        const categoryName = item.category ? (item.category.name || item.category.categoryId) : '-';
        div.innerHTML = `
            <img src="${item.imageUrl || 'https://picsum.photos/400/200'}" alt="${item.title}">
            <h3>${item.title}</h3>
            <p>${item.description || ''}</p>
            <div class="item-meta">
                <span>${categoryName}</span>
                <span>${formatPrice(item.startingPrice)}</span>
            </div>
            <div class="item-meta"><span>Ends</span><span>${formatDate(item.deadline)}</span></div>
            <button class="btn ghost" onclick="bidItem(${item.itemId})">Add Bid</button>
        `;
        container.appendChild(div);
    });
}

// Simple client-side filter
function filterItems(targetId, query) {
    fetch(apiUrl)
        .then(res => res.json())
        .then(items => {
            const q = (query || '').toLowerCase();
            renderItems(targetId, items.filter(i => (i.title || '').toLowerCase().includes(q)));
        });
}

// Add item from seller page aligned to backend model
const addForm = document.getElementById("add-item-form");
if (addForm) {
    addForm.addEventListener("submit", e => {
        e.preventDefault();
        const deadlineVal = document.getElementById("deadline").value;
        const newItem = {
            title: document.getElementById("title").value,
            description: document.getElementById("description").value,
            imageUrl: document.getElementById("imageUrl").value,
            category: {
                categoryId: document.getElementById("categoryId").value,
                name: document.getElementById("categoryName").value
            },
            startingPrice: parseFloat(document.getElementById("startingPrice").value),
            currentHighestBid: 0,
            status: "Active",
            deadline: deadlineVal ? new Date(deadlineVal).toISOString() : null
        };

        fetch(apiUrl, {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify(newItem)
        })
            .then(res => {
                if (!res.ok) throw new Error("Failed");
                return res.json();
            })
            .then(() => {
                alert("Item added successfully!");
                addForm.reset();
                loadItems("seller-items");
            })
            .catch(() => alert("Failed to add item. Check inputs and server logs."));
    });
}

// Dummy bid function
function bidItem(id) {
    alert("Bid button clicked for item ID: " + id);
}

// Load items on page load
document.addEventListener("DOMContentLoaded", () => {
    if (document.getElementById("item-list")) loadItems("item-list");
    if (document.getElementById("user-items")) loadItems("user-items");
    if (document.getElementById("seller-items")) loadItems("seller-items");
});

// Expose for inline calls
window.filterItems = filterItems;
window.bidItem = bidItem;
