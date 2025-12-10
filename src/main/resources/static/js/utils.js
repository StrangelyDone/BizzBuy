// ========================================
// BizzBuy Utility Functions
// ========================================

// Show toast notification
function showToast(message, type = 'info') {
    const toast = document.createElement('div');
    toast.className = `fixed top-20 right-4 z-50 p-4 rounded-lg shadow-lg max-w-sm ${type === 'success' ? 'bg-green-600' :
        type === 'error' ? 'bg-red-600' :
            type === 'warning' ? 'bg-yellow-600' :
                'bg-blue-600'
        } text-white`;
    toast.textContent = message;

    document.body.appendChild(toast);

    setTimeout(() => {
        toast.remove();
    }, 3000);
}

// Format currency
function formatCurrency(amount) {
    return new Intl.NumberFormat('en-US', {
        style: 'currency',
        currency: 'USD'
    }).format(amount);
}

// Format date in IST timezone
function formatDate(dateString) {
    // Handle null, undefined, or empty values
    if (!dateString || dateString === 'null' || dateString === 'undefined') {
        return 'N/A';
    }

    let date;

    // Handle different date formats
    if (typeof dateString === 'number') {
        // Unix timestamp
        date = new Date(dateString);
    } else if (Array.isArray(dateString)) {
        // MongoDB/Spring sometimes returns dates as arrays [year, month, day, hour, minute, second]
        if (dateString.length >= 3) {
            // Month is 0-indexed in JavaScript Date
            date = new Date(dateString[0], dateString[1] - 1, dateString[2],
                dateString[3] || 0, dateString[4] || 0, dateString[5] || 0);
        } else {
            console.warn('Invalid date array:', dateString);
            return 'Invalid Date';
        }
    } else if (typeof dateString === 'string') {
        // Check if it's LocalDateTime format (no timezone info)
        if (!dateString.endsWith('Z') && !dateString.includes('+') && dateString.includes('T')) {
            // LocalDateTime format - treat as IST
            date = new Date(dateString + '+05:30');
        } else {
            // ISO format with timezone or regular date string
            date = new Date(dateString);
        }
    } else if (dateString instanceof Date) {
        date = dateString;
    } else {
        console.warn('Unknown date format:', typeof dateString, dateString);
        return 'Invalid Date';
    }

    // Check if date is valid
    if (isNaN(date.getTime())) {
        console.warn('Invalid date after parsing:', dateString);
        return 'Invalid Date';
    }

    return date.toLocaleString('en-IN', {
        timeZone: 'Asia/Kolkata',
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit',
        hour12: true
    });
}

// Calculate time remaining
function getTimeRemaining(endTime) {
    if (!endTime) {
        return { total: 0, days: 0, hours: 0, minutes: 0, seconds: 0 };
    }

    // Handle LocalDateTime format from backend
    let endDate;
    if (typeof endTime === 'string' && !endTime.endsWith('Z') && !endTime.includes('+')) {
        // LocalDateTime format - treat as IST
        endDate = new Date(endTime + '+05:30');
    } else {
        endDate = new Date(endTime);
    }

    // Check if date is valid
    if (isNaN(endDate.getTime())) {
        return { total: 0, days: 0, hours: 0, minutes: 0, seconds: 0 };
    }

    const total = endDate.getTime() - Date.now();

    if (total <= 0) {
        return { total: 0, days: 0, hours: 0, minutes: 0, seconds: 0 };
    }

    const seconds = Math.floor((total / 1000) % 60);
    const minutes = Math.floor((total / 1000 / 60) % 60);
    const hours = Math.floor((total / (1000 * 60 * 60)) % 24);
    const days = Math.floor(total / (1000 * 60 * 60 * 24));

    return { total, days, hours, minutes, seconds };
}

// Format time remaining
function formatTimeRemaining(endTime) {
    const time = getTimeRemaining(endTime);

    if (time.total <= 0) {
        return 'Ended';
    }

    if (time.days > 0) {
        return `${time.days}d ${time.hours}h ${time.minutes}m`;
    } else if (time.hours > 0) {
        return `${time.hours}h ${time.minutes}m ${time.seconds}s`;
    } else {
        return `${time.minutes}m ${time.seconds}s`;
    }
}

// Start countdown timer
function startCountdown(elementId, endTime, onEnd) {
    const element = document.getElementById(elementId);
    if (!element) return;

    const updateTimer = () => {
        const timeString = formatTimeRemaining(endTime);
        element.textContent = timeString;

        if (timeString === 'Ended' && onEnd) {
            onEnd();
            return;
        }

        setTimeout(updateTimer, 1000);
    };

    updateTimer();
}

// Show loading spinner
function showLoading() {
    const overlay = document.createElement('div');
    overlay.id = 'loading-overlay';
    overlay.className = 'fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50';
    overlay.innerHTML = `
        <div class="animate-spin rounded-full h-16 w-16 border-t-4 border-b-4 border-blue-600"></div>
    `;
    document.body.appendChild(overlay);
}

// Hide loading spinner
function hideLoading() {
    const overlay = document.getElementById('loading-overlay');
    if (overlay) overlay.remove();
}

// Protect page (require authentication)
function protectPage() {
    if (!isLoggedIn()) {
        window.location.href = '/login.html';
    }
}

// Update navigation based on auth status
function updateNavigation() {
    const authLinks = document.getElementById('auth-links');
    const userLinks = document.getElementById('user-links');

    if (!authLinks || !userLinks) return;

    if (isLoggedIn()) {
        authLinks.classList.add('hidden');
        userLinks.classList.remove('hidden');

        const username = getCurrentUser();
        const usernameEl = document.getElementById('nav-username');
        if (usernameEl) usernameEl.textContent = username;
    } else {
        authLinks.classList.remove('hidden');
        userLinks.classList.add('hidden');
    }
}

// Debounce function for search
function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

// Get query parameter from URL
function getQueryParam(param) {
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get(param);
}

// Validate email
function isValidEmail(email) {
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
}

// Initialize page
document.addEventListener('DOMContentLoaded', () => {
    updateNavigation();
});
