# Auction Functionality Implementation Walkthrough

## Overview

I have successfully implemented a complete auction system for the BizzBuy e-commerce platform with the following capabilities:

- **Real-time bidding** on auction items
- **Automated auction lifecycle management** using Java threading (Spring's `@Scheduled`)
- **Automatic status updates** based on start and end times
- **Winner determination** when auctions end
- **Complete REST API** following the SRS specifications

## What Was Implemented

### 1. Model Layer

#### [Auction.java](file:///c:/Users/rohit/Downloads/BizzBuy_final/BizzBuy/src/main/java/com/example/BizzBuy/model/Auction.java)

Created the core auction model with:
- All fields from SRS: `id`, `itemId`, `sellerId`, `currentPrice`, `startingPrice`, `startTime`, `endTime`, `status`, `winnerId`
- `AuctionStatus` enum with values: `SCHEDULED`, `LIVE`, `ENDED`, `CANCELLED`
- MongoDB document mapping with `@Document(collection = "auctions")`
- Lombok annotations for boilerplate code reduction

#### [Bid.java](file:///c:/Users/rohit/Downloads/BizzBuy_final/BizzBuy/src/main/java/com/example/BizzBuy/model/Bid.java)

Created the bid tracking model with:
- Fields: `id`, `auctionId`, `bidderId`, `amount`, `timestamp`
- Automatic timestamp generation on bid creation
- MongoDB document mapping with `@Document(collection = "bids")`

#### [Product.java](file:///c:/Users/rohit/Downloads/BizzBuy_final/BizzBuy/src/main/java/com/example/BizzBuy/model/Product.java#L23-L24) - Modified

Added `isAuction` boolean field to track whether a product is being auctioned.

---

### 2. Repository Layer

#### [AuctionRepository.java](file:///c:/Users/rohit/Downloads/BizzBuy_final/BizzBuy/src/main/java/com/example/BizzBuy/repository/AuctionRepository.java)

MongoDB repository with custom query methods:
- `findByStatus()` - Filter auctions by status
- `findBySellerId()` - Get all auctions by a seller
- `findByItemId()` - Find auction for a specific product
- `findByStartTimeLessThanEqualAndStatus()` - Find auctions ready to start
- `findByEndTimeLessThanEqualAndStatus()` - Find auctions ready to end

#### [BidRepository.java](file:///c:/Users/rohit/Downloads/BizzBuy_final/BizzBuy/src/main/java/com/example/BizzBuy/repository/BidRepository.java)

MongoDB repository with custom query methods:
- `findByAuctionIdOrderByAmountDesc()` - Get all bids for an auction, sorted by amount
- `findByBidderId()` - Get all bids by a user
- `findTopByAuctionIdOrderByAmountDesc()` - Get the highest bid for an auction

---

### 3. Service Layer

#### [AuctionService.java](file:///c:/Users/rohit/Downloads/BizzBuy_final/BizzBuy/src/main/java/com/example/BizzBuy/service/AuctionService.java)

Core business logic with comprehensive validation:

**Key Methods:**
- `createAuction()` - Creates new auction with validation:
  - Verifies product exists and belongs to seller
  - Validates start time is in future
  - Validates end time is after start time
  - Prevents duplicate auctions for same product
  - Marks product as auction item
  
- `getActiveAuctions()` - Returns all LIVE auctions
- `closeAuction()` - Manually close auction (seller only)
- `cancelAuction()` - Cancel auction if no bids placed
- `determineWinner()` - Finds highest bidder and sets as winner
- `findAuctionsToStart()` - Helper for scheduler to find auctions ready to start
- `findAuctionsToEnd()` - Helper for scheduler to find auctions ready to end

#### [BidService.java](file:///c:/Users/rohit/Downloads/BizzBuy_final/BizzBuy/src/main/java/com/example/BizzBuy/service/BidService.java)

Bid management with strict validation:

**Key Methods:**
- `placeBid()` - Place a bid with validation:
  - Auction must be LIVE
  - Bidder cannot be the seller
  - Bid amount must be higher than current price
  - Updates auction's current price
  
- `getBidsForAuction()` - Get all bids for an auction (sorted by amount)
- `getHighestBid()` - Get current highest bid
- `getBidsByUser()` - Get user's bidding history

#### [AuctionSchedulerService.java](file:///c:/Users/rohit/Downloads/BizzBuy_final/BizzBuy/src/main/java/com/example/BizzBuy/service/AuctionSchedulerService.java) ⭐

**Threading Implementation** - This is the core of the automated auction management:

```java
@Scheduled(fixedRate = 30000) // Runs every 30 seconds
public void checkAndStartAuctions() {
    // Finds all SCHEDULED auctions where startTime <= now
    // Updates their status to LIVE
}

@Scheduled(fixedRate = 30000) // Runs every 30 seconds
public void checkAndEndAuctions() {
    // Finds all LIVE auctions where endTime <= now
    // Determines winner
    // Updates status to ENDED
    // Decrements product stock
}
```

**How Threading Works:**
- Uses Spring's `@Scheduled` annotation (no manual thread management needed)
- Runs in background automatically when application starts
- Checks every 30 seconds for auctions to start/end
- Logs all status changes to console
- Handles errors gracefully without crashing

#### [ProductService.java](file:///c:/Users/rohit/Downloads/BizzBuy_final/BizzBuy/src/main/java/com/example/BizzBuy/service/ProductService.java#L152-L154) - Modified

Added `updateProduct()` method to support auction integration.

---

### 4. Controller Layer

#### [AuctionController.java](file:///c:/Users/rohit/Downloads/BizzBuy_final/BizzBuy/src/main/java/com/example/BizzBuy/controller/AuctionController.java)

REST API endpoints at `/api/auctions`:

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/api/auctions/create` | Create new auction | Yes (Seller) |
| GET | `/api/auctions/active` | Get all active auctions | No |
| GET | `/api/auctions/{id}` | Get auction details | No |
| GET | `/api/auctions?sellerId={id}` | Get auctions by seller | No |
| POST | `/api/auctions/{id}/close` | Close auction manually | Yes (Owner) |
| DELETE | `/api/auctions/{id}/cancel` | Cancel auction | Yes (Owner) |

#### [BidController.java](file:///c:/Users/rohit/Downloads/BizzBuy_final/BizzBuy/src/main/java/com/example/BizzBuy/controller/BidController.java)

REST API endpoints at `/api/bids`:

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/api/bids/place` | Place a bid | Yes (Buyer) |
| GET | `/api/bids/auction/{id}` | Get all bids for auction | No |
| GET | `/api/bids/my-bids` | Get current user's bids | Yes |

---

### 5. Configuration

#### [BizzBuyApplication.java](file:///c:/Users/rohit/Downloads/BizzBuy_final/BizzBuy/src/main/java/com/example/BizzBuy/BizzBuyApplication.java#L7) - Modified

Added `@EnableScheduling` annotation to enable Spring's scheduling framework for the auction scheduler.

---

## Key Features Implemented

### ✅ Automated Auction Lifecycle

The auction system automatically manages its lifecycle without manual intervention:

1. **Creation** → Auction starts in `SCHEDULED` status
2. **Auto-Start** → When `startTime` arrives, scheduler changes status to `LIVE`
3. **Bidding** → Users can place bids while status is `LIVE`
4. **Auto-End** → When `endTime` arrives, scheduler:
   - Changes status to `ENDED`
   - Determines winner (highest bidder)
   - Decrements product stock
5. **Manual Close** → Seller can close auction early
6. **Cancellation** → Seller can cancel if no bids placed

### ✅ Real-Time Bidding

- Bids must be higher than current price
- Auction's `currentPrice` updates immediately
- Seller cannot bid on own auction
- Only works on `LIVE` auctions

### ✅ Threading Implementation

Uses Spring's `@Scheduled` annotation:
- **No manual thread creation** - Spring manages threads automatically
- **Fixed-rate execution** - Runs every 30 seconds
- **Concurrent-safe** - Spring handles synchronization
- **Automatic startup** - Starts when application launches
- **Graceful error handling** - Errors don't crash the scheduler

### ✅ Comprehensive Validation

- Time validation (start < end, start > now)
- Ownership validation (only owner can close/cancel)
- Status validation (can only bid on LIVE auctions)
- Duplicate prevention (one auction per product)
- Bid amount validation (must exceed current price)

---

## Testing Instructions

### Prerequisites

1. Ensure MongoDB is running and accessible via the connection string in `application.properties`
2. Start the Spring Boot application:
   ```bash
   cd c:\Users\rohit\Downloads\BizzBuy_final\BizzBuy
   .\mvnw.cmd spring-boot:run
   ```
   Or use your IDE to run `BizzBuyApplication.java`

### Test 1: Create an Auction

**Request:**
```http
POST http://localhost:8080/api/auctions/create
Headers:
  X-USER: <seller-username>
  Content-Type: application/json

Body:
{
  "itemId": 1,
  "startingPrice": 100.0,
  "startTime": "2025-12-08T23:00:00",
  "endTime": "2025-12-08T23:05:00"
}
```

**Expected Response:**
```json
{
  "id": 1,
  "itemId": 1,
  "sellerId": 1,
  "currentPrice": 100.0,
  "startingPrice": 100.0,
  "startTime": "2025-12-08T23:00:00",
  "endTime": "2025-12-08T23:05:00",
  "status": "SCHEDULED",
  "winnerId": null
}
```

### Test 2: Verify Automatic Start (Threading)

1. Create an auction with `startTime` 1 minute in the future
2. Wait for the start time to pass
3. Within 30 seconds, check the auction:

**Request:**
```http
GET http://localhost:8080/api/auctions/{id}
```

**Expected:** Status should change from `SCHEDULED` to `LIVE`

**Console Output:**
```
Started auction ID: 1 at 2025-12-08T23:00:15
```

### Test 3: Place Bids

**Request:**
```http
POST http://localhost:8080/api/bids/place
Headers:
  X-USER: <buyer-username>
  Content-Type: application/json

Body:
{
  "auctionId": 1,
  "amount": 150.0
}
```

**Expected Response:**
```json
{
  "id": 1,
  "auctionId": 1,
  "bidderId": 2,
  "amount": 150.0,
  "timestamp": "2025-12-08T23:01:30"
}
```

### Test 4: Verify Automatic End (Threading)

1. Wait for the auction's `endTime` to pass
2. Within 30 seconds, check the auction again

**Expected:** 
- Status should change to `ENDED`
- `winnerId` should be set to the highest bidder
- Product stock should decrease by 1

**Console Output:**
```
Ended auction ID: 1 at 2025-12-08T23:05:20 Winner ID: 2
```

### Test 5: Get Active Auctions

**Request:**
```http
GET http://localhost:8080/api/auctions/active
```

**Expected:** Returns only auctions with status `LIVE`

### Test 6: Get Bids for Auction

**Request:**
```http
GET http://localhost:8080/api/bids/auction/1
```

**Expected:** Returns all bids sorted by amount (highest first)

### Test 7: Error Cases

Try these to verify validation:

1. **Bid too low:**
   ```json
   {"auctionId": 1, "amount": 50.0}
   ```
   Expected: Error "Bid amount must be higher than current price"

2. **Seller bids on own auction:**
   Use seller's username in X-USER header
   Expected: Error "Seller cannot bid on their own auction"

3. **Bid on non-LIVE auction:**
   Try bidding on SCHEDULED or ENDED auction
   Expected: Error "Auction is not currently active"

---

## File Summary

### Created Files (11 total)

**Models:**
1. `Auction.java` - Core auction model
2. `Bid.java` - Bid tracking model

**Repositories:**
3. `AuctionRepository.java` - Auction data access
4. `BidRepository.java` - Bid data access

**Services:**
5. `AuctionService.java` - Auction business logic
6. `BidService.java` - Bid business logic
7. `AuctionSchedulerService.java` - Threading/automation

**Controllers:**
8. `AuctionController.java` - Auction REST API
9. `BidController.java` - Bid REST API

### Modified Files (3 total)

10. `Product.java` - Added `isAuction` field
11. `ProductService.java` - Added `updateProduct()` method
12. `BizzBuyApplication.java` - Added `@EnableScheduling`

---

## Threading Architecture

```mermaid
graph TD
    A[Spring Boot Application Starts] --> B[@EnableScheduling Activated]
    B --> C[AuctionSchedulerService Initialized]
    C --> D[Background Thread Pool Created]
    D --> E[checkAndStartAuctions - Every 30s]
    D --> F[checkAndEndAuctions - Every 30s]
    
    E --> G{Find SCHEDULED auctions<br/>where startTime <= now}
    G -->|Found| H[Update status to LIVE]
    H --> I[Log to console]
    
    F --> J{Find LIVE auctions<br/>where endTime <= now}
    J -->|Found| K[Determine Winner]
    K --> L[Update status to ENDED]
    L --> M[Decrement Product Stock]
    M --> N[Log to console]
    
    style C fill:#90EE90
    style E fill:#87CEEB
    style F fill:#87CEEB
    style H fill:#FFD700
    style L fill:#FFD700
```

---

## API Endpoint Summary

### Auction Endpoints

```
POST   /api/auctions/create          - Create auction (Seller)
GET    /api/auctions/active          - Get active auctions
GET    /api/auctions/{id}            - Get auction by ID
GET    /api/auctions?sellerId={id}   - Get auctions by seller
POST   /api/auctions/{id}/close      - Close auction (Owner)
DELETE /api/auctions/{id}/cancel     - Cancel auction (Owner)
```

### Bid Endpoints

```
POST   /api/bids/place               - Place bid (Buyer)
GET    /api/bids/auction/{id}        - Get bids for auction
GET    /api/bids/my-bids             - Get user's bids
```

---

## Next Steps

1. **Start the application** and monitor console for scheduler logs
2. **Test the API endpoints** using Postman or similar tool
3. **Verify threading** by creating auctions with near-future start/end times
4. **Check MongoDB** to see the data being stored
5. **Test edge cases** like cancellation, early closure, invalid bids

The implementation is complete and ready for testing! 🎉
