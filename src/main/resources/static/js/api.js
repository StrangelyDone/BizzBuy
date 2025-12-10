// ========================================
// BizzBuy API Integration Layer
// ========================================

const API_BASE_URL = 'http://localhost:8080/api';

// Get current user from localStorage
function getCurrentUser() {
    return localStorage.getItem('currentUser');
}

// Set current user in localStorage
function setCurrentUser(username) {
    localStorage.setItem('currentUser', username);
}

// Clear current user (logout)
function logout() {
    localStorage.removeItem('currentUser');
    localStorage.removeItem('userRole');
    window.location.href = '/login.html';
}

// Check if user is logged in
function isLoggedIn() {
    return getCurrentUser() !== null;
}

// Get user role
function getUserRole() {
    return localStorage.getItem('userRole');
}

// Set user role
function setUserRole(role) {
    localStorage.setItem('userRole', role);
}

// Generic API call function
async function apiCall(endpoint, method = 'GET', body = null, requiresAuth = false) {
    const headers = {
        'Content-Type': 'application/json'
    };

    if (requiresAuth) {
        const user = getCurrentUser();
        if (!user) {
            window.location.href = '/login.html';
            throw new Error('Not authenticated');
        }
        headers['X-USER'] = user;
    }

    const config = {
        method,
        headers
    };

    if (body && method !== 'GET') {
        config.body = JSON.stringify(body);
    }

    try {
        const response = await fetch(`${API_BASE_URL}${endpoint}`, config);

        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.error || 'Request failed');
        }

        return await response.json();
    } catch (error) {
        console.error('API Error:', error);
        throw error;
    }
}

// ========================================
// Authentication API
// ========================================

const AuthAPI = {
    async register(userData) {
        return await apiCall('/auth/register', 'POST', userData);
    },

    async login(credentials) {
        const user = await apiCall('/auth/login', 'POST', credentials);
        setCurrentUser(user.username);
        setUserRole(user.roles && user.roles.length > 0 ? user.roles[0] : 'BUYER');
        return user;
    }
};

// ========================================
// Products API
// ========================================

const ProductsAPI = {
    async getAll() {
        return await apiCall('/items');
    },

    async getById(id) {
        return await apiCall(`/items/${id}`);
    },

    async search(keyword) {
        return await apiCall(`/items/search?keyword=${encodeURIComponent(keyword)}`);
    },

    async filter(min, max, sellerId) {
        let query = '/items/filter?';
        if (min) query += `min=${min}&`;
        if (max) query += `max=${max}&`;
        if (sellerId) query += `sellerId=${sellerId}`;
        return await apiCall(query);
    },

    async create(product) {
        return await apiCall('/items/products', 'POST', product, true);
    },

    async updateStock(id, stock) {
        return await apiCall(`/items/${id}/stock?stock=${stock}`, 'PUT', null, true);
    }
};

// ========================================
// Auctions API
// ========================================

const AuctionsAPI = {
    async getActive() {
        return await apiCall('/auctions/active');
    },

    async getById(id) {
        return await apiCall(`/auctions/${id}`);
    },

    async getAll() {
        return await apiCall('/auctions');
    },

    async getBySeller(sellerId) {
        return await apiCall(`/auctions?sellerId=${sellerId}`);
    },

    async create(auction) {
        return await apiCall('/auctions/create', 'POST', auction, true);
    },

    async close(id) {
        return await apiCall(`/auctions/${id}/close`, 'POST', null, true);
    },

    async cancel(id) {
        return await apiCall(`/auctions/${id}/cancel`, 'DELETE', null, true);
    }
};

// ========================================
// Bids API
// ========================================

const BidsAPI = {
    async place(auctionId, amount) {
        return await apiCall('/bids/place', 'POST', { auctionId, amount }, true);
    },

    async getForAuction(auctionId) {
        return await apiCall(`/bids/auction/${auctionId}`);
    },

    async getMyBids() {
        return await apiCall('/bids/my-bids', 'GET', null, true);
    }
};

// ========================================
// Cart API
// ========================================

const CartAPI = {
    async get() {
        return await apiCall('/cart/my-cart', 'GET', null, true);
    },

    async add(productId, quantity) {
        return await apiCall('/cart/add', 'POST', { productId, quantity }, true);
    },

    async update(productId, quantity) {
        return await apiCall('/cart/update', 'PUT', { productId, quantity }, true);
    },

    async remove(productId) {
        return await apiCall(`/cart/remove/${productId}`, 'DELETE', null, true);
    },

    async checkout() {
        return await apiCall('/cart/checkout', 'POST', null, true);
    }
};

// ========================================
// User API
// ========================================

const UserAPI = {
    async getProfile() {
        return await apiCall('/users/me', 'GET', null, true);
    },

    async updateProfile(data) {
        return await apiCall('/users/me', 'PUT', data, true);
    },

    async getWallet() {
        return await apiCall('/users/me/wallet', 'GET', null, true);
    },

    async addFunds(amount) {
        return await apiCall(`/users/me/wallet/add?amount=${amount}`, 'POST', null, true);
    },

    async getOrders() {
        return await apiCall('/users/me/orders', 'GET', null, true);
    },

    async getTransactions() {
        return await apiCall('/users/me/transactions', 'GET', null, true);
    }
};

// ========================================
// Notifications API
// ========================================

const NotificationsAPI = {
    async getAll() {
        return await apiCall('/notifications/my-notifications', 'GET', null, true);
    },

    async getUnread() {
        return await apiCall('/notifications/unread', 'GET', null, true);
    },

    async getUnreadCount() {
        return await apiCall('/notifications/unread-count', 'GET', null, true);
    },

    async markAsRead(id) {
        return await apiCall(`/notifications/${id}/mark-read`, 'PUT', null, true);
    },

    async markAllAsRead() {
        return await apiCall('/notifications/mark-all-read', 'PUT', null, true);
    }
};
