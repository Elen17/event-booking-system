# Event Booking System Database

## Step 1: Identify All Entities in the System

### Strong (Key) Entities:
1. **Country** - Independent entity representing countries
2. **City** - Independent entity representing cities
3. **Venue** - Independent entity representing event venues
4. **User_Role** - Independent entity defining user roles
5. **Event_Status** - Independent entity defining event statuses
6. **Event_Type** - Independent entity defining event types
7. **Seat_Status** - Independent entity defining seat statuses
8. **App_User** - Independent entity representing system users
9. **Booking_Status** - Independent entity defining booking statuses
10. **Event** - Independent entity representing events
11. **Seat** - Independent entity representing venue seats
12. **Booking** - Independent entity representing seat bookings

### Weak Entities:
13. **User_Password_History** - Weak entity dependent on App_User for password tracking

### Associative Entities:
14. **Event_Seat** - Associative entity linking events to available seats

## Step 2: Identify Relationships Between Entities

### Primary Relationships:

1. **Country ↔ City**: One-to-Many
   - One country can have many cities

2. **Country ↔ App_User**: One-to-Many
   - One country can have many users

3. **City ↔ Venue**: One-to-Many
   - One city can have many venues

4. **User_Role ↔ App_User**: One-to-Many
   - One role can be assigned to many users

5. **Event_Status ↔ Event**: One-to-Many
   - One status can apply to many events

6. **Event_Type ↔ Event**: One-to-Many
   - One type can categorize many events

7. **Venue ↔ Event**: One-to-Many
   - One venue can host many events

8. **Venue ↔ Seat**: One-to-Many
   - One venue can have many seats

9. **Seat_Status ↔ Seat**: One-to-Many
   - One status can apply to many seats

10. **App_User ↔ Booking**: One-to-Many
   - One user can make many bookings

11. **Event ↔ Booking**: One-to-Many
   - One event can have many bookings

12. **Seat ↔ Booking**: One-to-Many
   - One seat can have many bookings (over time)

13. **Booking_Status ↔ Booking**: One-to-Many
   - One status can apply to many bookings

14. **App_User ↔ User_Password_History**: One-to-Many
   - One user can have many password history records

15. **Event ↔ Event_Seat**: One-to-Many
   - One event can be associated with many seats

16. **Seat ↔ Event_Seat**: One-to-Many
   - One seat can be available for many events

17. **Booking ↔ Seat** (Current Booking): One-to-One
   - One booking can be the current active booking for one seat

## Step 3: Identify Cardinality and Ordinality

### Cardinality and Participation:

| Relationship | Entity 1 | Cardinality | Entity 2 | Participation |
|--------------|----------|-------------|----------|---------------|
| Country-City | Country (1) | 1:M | City (M) | Total/Partial |
| Country-App_User | Country (1) | 1:M | App_User (M) | Total/Total |
| City-Venue | City (1) | 1:M | Venue (M) | Total/Total |
| User_Role-App_User | User_Role (1) | 1:M | App_User (M) | Total/Total |
| Event_Status-Event | Event_Status (1) | 1:M | Event (M) | Total/Total |
| Event_Type-Event | Event_Type (1) | 1:M | Event (M) | Total/Total |
| Venue-Event | Venue (1) | 1:M | Event (M) | Total/Total |
| Venue-Seat | Venue (1) | 1:M | Seat (M) | Total/Total |
| Seat_Status-Seat | Seat_Status (1) | 1:M | Seat (M) | Total/Total |
| App_User-Booking | App_User (1) | 1:M | Booking (M) | Total/Total |
| Event-Booking | Event (1) | 1:M | Booking (M) | Total/Total |
| Seat-Booking | Seat (1) | 1:M | Booking (M) | Total/Total |
| Booking_Status-Booking | Booking_Status (1) | 1:M | Booking (M) | Total/Total |
| App_User-User_Password_History | App_User (1) | 1:M | User_Password_History (M) | Total/Total |
| Event-Event_Seat | Event (1) | 1:M | Event_Seat (M) | Total/Partial |
| Seat-Event_Seat | Seat (1) | 1:M | Event_Seat (M) | Total/Partial |
| Booking-Seat (Current) | Booking (1) | 1:1 | Seat (1) | Partial/Partial |

## Step 4: Add Attributes for Entities

### Strong Entities Attributes:

#### **Country**
- **Primary Key**: id (SERIAL)
- **Attributes**: name (VARCHAR(255), NOT NULL, UNIQUE)

#### **City**
- **Primary Key**: id (SERIAL)
- **Attributes**: name (VARCHAR(255), NOT NULL, UNIQUE)
- **Foreign Key**: country_id (INT, NOT NULL)

#### **Venue**
- **Primary Key**: id (SERIAL)
- **Attributes**:
   - name (VARCHAR(255), NOT NULL)
   - address (VARCHAR(255), NOT NULL)
- **Foreign Key**: city_id (INT, NOT NULL)

#### **User_Role**
- **Primary Key**: id (SERIAL)
- **Attributes**: name (VARCHAR(255), NOT NULL, UNIQUE)

#### **Event_Status**
- **Primary Key**: id (SERIAL)
- **Attributes**: name (VARCHAR(255), NOT NULL, UNIQUE)

#### **Event_Type**
- **Primary Key**: id (SERIAL)
- **Attributes**: name (VARCHAR(255), NOT NULL, UNIQUE)

#### **Seat_Status**
- **Primary Key**: id (SERIAL)
- **Attributes**: name (VARCHAR(255), NOT NULL, UNIQUE)

#### **App_User**
- **Primary Key**: id (SERIAL)
- **Attributes**:
   - first_name (VARCHAR(255), NOT NULL)
   - last_name (VARCHAR(255), NOT NULL)
   - email (VARCHAR(255), NOT NULL, UNIQUE)
   - password_hash (VARCHAR(255), NOT NULL)
   - created_at (TIMESTAMP, NOT NULL, DEFAULT CURRENT_TIMESTAMP)
- **Foreign Keys**:
   - role_id (INT, NOT NULL)
   - country_id (INT, NOT NULL)

#### **Booking_Status**
- **Primary Key**: id (SERIAL)
- **Attributes**:
   - name (VARCHAR(50), NOT NULL, UNIQUE)
   - description (TEXT)
   - is_active (BOOLEAN, DEFAULT TRUE)

#### **Event**
- **Primary Key**: id (SERIAL)
- **Attributes**:
   - title (VARCHAR(255), NOT NULL)
   - description (TEXT)
   - event_date (DATE, NOT NULL)
   - start_time (TIME, NOT NULL)
   - end_time (TIME, NOT NULL)
- **Foreign Keys**:
   - status_id (INT, NOT NULL)
   - type_id (INT, NOT NULL)
   - venue_id (INT, NOT NULL)

#### **Seat**
- **Primary Key**: id (SERIAL)
- **Attributes**:
   - section (VARCHAR(50))
   - row_number (INT, NOT NULL)
   - seat_number (INT, NOT NULL)
   - base_price (DECIMAL(10,2), NOT NULL)
   - is_available (BOOLEAN, DEFAULT TRUE)
   - last_booking_update (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP)
   - created_at (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP)
- **Foreign Keys**:
   - venue_id (INT, NOT NULL)
   - status_id (INT, NOT NULL)
   - current_booking_id (INT, NULLABLE)

#### **Booking**
- **Primary Key**: id (SERIAL)
- **Attributes**:
   - booking_reference (VARCHAR(20), UNIQUE, NOT NULL)
   - created_at (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP)
   - expires_at (TIMESTAMP, NULLABLE)
   - confirmed_at (TIMESTAMP, NULLABLE)
   - purchased_at (TIMESTAMP, NULLABLE)
   - cancelled_at (TIMESTAMP, NULLABLE)
   - price (DECIMAL(10,2), NOT NULL)
   - payment_date (TIMESTAMP, NULLABLE)
- **Foreign Keys**:
   - user_id (INT, NOT NULL)
   - event_id (INT, NOT NULL)
   - seat_id (INT, NOT NULL)
   - booking_status_id (INT, NOT NULL)

### Weak Entity Attributes:

#### **User_Password_History**
- **Primary Key**: id (SERIAL)
- **Weak Key**: user_id (partial key)
- **Attributes**:
   - password_hash (VARCHAR(255), NOT NULL)
   - salt (VARCHAR(255), NOT NULL)
   - hash_algorithm (VARCHAR(50), NOT NULL, DEFAULT 'bcrypt')
   - created_at (TIMESTAMP, NOT NULL, DEFAULT CURRENT_TIMESTAMP)
- **Foreign Key**: user_id (INT, NOT NULL)

### Associative Entity Attributes:

#### **Event_Seat**
- **Composite Primary Key**: (event_id, seat_id)
- **Foreign Keys**:
   - event_id (INT, NOT NULL)
   - seat_id (INT, NOT NULL)

## Key Design Features:

### **Derived Attributes**: None explicitly defined, but could include:
- User full name (derived from first_name + last_name)
- Event duration (derived from end_time - start_time)

### **Multivalued Attributes**: None present in this normalized design

### **Business Rules Implemented**:
1. Event end_time must be after start_time
2. Event date must be in the future or current date
3. Seat prices must be positive
4. Row and seat numbers must be positive
5. Booking references are auto-generated with pattern 'BK########'
6. Temporary bookings expire after 2 days
7. Unique constraint on venue seats (venue_id, section, row_number, seat_number)

### **Indexes for Performance**:
- User country lookup
- City country lookup
- Venue city lookup
- Event venue lookup
- Booking user and status lookup
- Event date and status lookup
- Seat availability lookup
- Booking expiration lookup
