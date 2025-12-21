# BizzBuy - E-Commerce Platform with Auction System

BizzBuy is a full-stack e-commerce application that combines traditional online shopping with an exciting real-time auction system. Users can browse products, make instant purchases, or participate in competitive bidding to win items at potentially lower prices. Built with Spring Boot and MongoDB, the platform offers a seamless shopping experience with integrated wallet management and real-time notifications.

## Features

- **User Authentication & Profile Management**: Secure registration, login, and profile customization
- **Product Marketplace**: Browse, search, and filter through a wide range of products
- **Shopping Cart**: Add items, manage quantities, and checkout seamlessly
- **Wallet System**: Digital wallet for managing funds, deposits, and transactions
- **Auction System**: Create and participate in time-based auctions with real-time bidding
- **Order Management**: Track your purchase history and order status
- **Seller Dashboard**: Manage your products and auctions in one place
- **Real-time Notifications**: Stay updated on auction status, bids, and orders

## Tech Stack

- **Backend**: Spring Boot, Java
- **Database**: MongoDB
- **Frontend**: HTML, CSS, JavaScript
- **Build Tool**: Maven

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven
- MongoDB Atlas account (or local MongoDB instance)

### Setup Instructions

1. **Clone the repository**
   ```bash
   git clone <your-repo-url>
   cd BizzBuy
   ```

2. **Configure MongoDB Connection**
   
   Open `src/main/resources/application.properties` and replace the placeholder MongoDB URI with your own:
   
   ```properties
   spring.data.mongodb.uri=mongodb+srv://<username>:<password>@<cluster-url>/<database-name>?retryWrites=true&w=majority
   ```

   - Copy the connection string from your cluster and replace `<username>`, `<password>`, and `<database-name>` with your credentials

3. **Build the project**
   ```bash
   mvn clean install
   ```

4. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

5. **Access the application**
   
   Open your browser and navigate to: `http://localhost:8080`

## Team Responsibilities & Architecture

### 1. Authentication & Profile Management (Rohith.M)

Handles all user-related operations including registration, login, and profile management.

**Classes:**
- `AuthController.java`: HTTP endpoints for authentication operations
- `AuthService.java`: Core authentication logic and session management
- `UserService.java`: User profile operations and account management
- `User.java`: User entity model with credentials and profile data
- `BizzBuyApplication.java`: Main application entry point

**Endpoints:**
- `POST /api/auth/register`: Create a new user account
- `POST /api/auth/login`: Authenticate user and create session
- `POST /api/auth/logout`: End current user session
- `GET /api/users/me`: Get current user's profile
- `PUT /api/users/me`: Update current user's profile

---

### 2. Wallet & Transaction Management (Abhinav)

Manages the digital wallet system, fund management, and transaction history.

**Classes:**
- `WalletService.java`: Wallet operations and balance management
- `TransactionService.java`: Transaction history and record management
- `UserController.java`: User-specific endpoints for wallet operations
- `Wallet.java`: Wallet entity model
- `Transaction.java`: Transaction record entity

**Endpoints:**
- `GET /api/users/me/wallet`: Retrieve current wallet balance
- `POST /api/users/me/wallet/add?amount=X`: Add funds to wallet
- `GET /api/users/me/transactions`: View transaction history

---

### 3. Product Management (Raj Vardhan)

Responsible for product catalog, inventory management, and seller stores.

**Classes:**
- `ProductController.java`: Product-related HTTP endpoints
- `ProductService.java`: Product business logic and inventory management
- `Product.java`: Product entity model
- `SellerStore.java`: Seller profile and product collection model

**Endpoints:**
- `POST /api/items/products`: Create a new product listing
- `PUT /api/items/{id}/stock?stock=X`: Update product stock quantity
- `GET /api/items/seller/{sellerId}`: Get all products by a seller
- `GET /api/items`: List all available products
- `GET /api/items/{id}`: Get specific product details
- `GET /api/items/search?keyword=X`: Search products by keyword
- `GET /api/items/filter?min=X&max=Y&sellerId=Z`: Filter products by criteria

---

### 4. Database & Repository Layer (Pranay)

Manages all database operations, repositories, and data persistence layer.

**Classes:**
- `UserRepository.java`: User data access layer
- `ProductRepository.java`: Product data access layer
- `WalletRepository.java`: Wallet data access layer
- `CartRepository.java`: Cart data access layer
- `OrderRepository.java`: Order data access layer
- `TransactionRepository.java`: Transaction data access layer
- `AuctionRepository.java`: Auction data access layer
- `BidRepository.java`: Bid data access layer
- `NotificationRepository.java`: Notification data access layer
- `SellerStoreRepository.java`: Seller store data access layer
- `SequenceGeneratorService.java`: Auto-increment ID generation for MongoDB
- `DatabaseSequence.java`: Database sequence entity for ID generation
- `AppConfig.java`: Application configuration and beans

**Responsibilities:**
- Design and implement MongoDB collections and schemas
- Create repository interfaces with custom queries
- Manage database sequences and auto-increment IDs
- Implement efficient data access patterns
- Handle database configuration and connection pooling

---

### 5. Shopping Cart System (Rohith Kumar)

Handles shopping cart operations, item management, and checkout process.

**Classes:**
- `CartController.java`: Cart-related HTTP endpoints
- `CartService.java`: Cart business logic and checkout processing
- `Cart.java`: Cart entity model
- `CartItem.java`: Individual cart item entity

**Endpoints:**
- `GET /api/cart/my-cart`: Get current user's cart
- `POST /api/cart/add`: Add item to cart
- `PUT /api/cart/update`: Update item quantity in cart
- `DELETE /api/cart/remove/{itemId}`: Remove item from cart
- `POST /api/cart/checkout`: Process cart checkout

---

### 6. Orders & Payment Processing (Suhith)

Manages order processing and payment handling for direct purchases.

**Classes:**
- `PaymentController.java`: Payment and purchase endpoints
- `PaymentService.java`: Payment processing logic
- `OrderService.java`: Order creation and management
- `Order.java`: Order entity model

**Endpoints:**
- `POST /api/pay/purchase/{itemId}?quantity=X`: Direct product purchase
- `GET /api/pay/my-history`: View payment history
- `GET /api/users/me/orders`: View order history

---

### 7. Auction & Bidding System (Raj Vardhan, Rohith M, Pranay)

Implements the complete real-time auction system with automated lifecycle management and bidding functionality.

**Classes:**
- `AuctionController.java`: Auction management endpoints
- `AuctionService.java`: Auction business logic
- `AuctionSchedulerService.java`: Automated auction lifecycle management
- `BidController.java`: Bidding operation endpoints
- `BidService.java`: Bid processing and validation
- `NotificationController.java`: Notification endpoints
- `NotificationService.java`: Notification management and delivery
- `Auction.java`: Auction entity model
- `Bid.java`: Bid entity model
- `Notification.java`: Notification entity model

**Endpoints:**
- `POST /api/auctions`: Create a new auction
- `GET /api/auctions`: List all active auctions
- `GET /api/auctions/{id}`: Get auction details
- `POST /api/bids`: Place a bid on an auction
- `GET /api/bids/auction/{auctionId}`: View all bids for an auction
- `GET /api/notifications`: View user notifications

**Responsibilities:**
- Design and implement auction lifecycle (creation, active, ended states)
- Develop real-time bidding logic with validation
- Build automated scheduler for auction start/end times
- Create notification system for auction updates
- Implement bid history tracking and winner determination

---

### 8. Frontend Development (Suhith, Rohith Kumar, Abhinav)

Designed and implemented the complete user interface for the application.

**Files:**
- `index.html`: Landing page and main navigation
- `login.html` & `register.html`: Authentication pages
- `products.html`: Product browsing and search
- `cart.html`: Shopping cart interface
- `auctions.html`: Auction listings
- `auction-detail.html`: Individual auction bidding page
- `create-auction.html`: Auction creation form
- `create-product.html`: Product listing form
- `profile.html`: User profile and wallet management
- `seller-dashboard.html`: Seller product and auction management
- `js/api.js`: API integration and HTTP requests
- `js/utils.js`: Utility functions and helpers

**Responsibilities:**
- Design responsive and intuitive user interfaces
- Implement client-side validation and error handling
- Integrate frontend with backend REST APIs
- Create interactive components for auctions and bidding
- Build dynamic cart and checkout flows
- Develop seller dashboard for product management

---

## Project Structure

```
BizzBuy/
├── src/
│   ├── main/
│   │   ├── java/com/example/BizzBuy/
│   │   │   ├── config/           # Application configuration
│   │   │   ├── controller/       # REST API endpoints
│   │   │   ├── model/            # Entity models
│   │   │   ├── repository/       # Data access layer
│   │   │   └── service/          # Business logic layer
│   │   └── resources/
│   │       ├── application.properties
│   │       └── static/           # Frontend files
│   └── test/                     
└── pom.xml                       
```

## Advanced Features

### Multi-threading Implementation

BizzBuy leverages **Java's multi-threading capabilities** to enhance performance and provide a responsive user experience, particularly for time-sensitive operations like auction management.

**Key Threading Features:**

1. **Auction Scheduler Service**: Uses Spring's `@Scheduled` annotation with thread pools to run background tasks that automatically check and update auction statuses (starting pending auctions and ending active ones) at regular intervals without blocking the main application.

2. **Asynchronous Notifications**: The notification service can process and send notifications asynchronously, allowing the main application flow to continue without waiting for notification delivery to complete.

3. **Concurrent Bid Processing**: Multiple users can place bids simultaneously on different auctions, with thread-safe operations ensuring data consistency and preventing race conditions.


**Implementation Details:**
- Spring Boot's built-in thread pool management handles concurrent requests
- `@Scheduled` tasks run on separate threads for periodic auction checks
- Thread-safe data access through MongoDB's atomic operations and repository pattern

This threading implementation is crucial for the auction system, where timing is critical and multiple users need to interact with auctions simultaneously without performance degradation.