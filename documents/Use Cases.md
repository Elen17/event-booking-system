---

## ✅ Use Cases – Event Booking System

---

### **UC-01: User Registration**

**Actor:** Guest (Unregistered User)
**Description:** A user creates a new account.
**Precondition:** User is not logged in.
**Main Flow:**

1. User accesses the registration page.
2. User fills in the registration form (username, password, email, etc.).
3. User submits the form.
4. System validates input and creates a new account.
5. User is redirected to the home page in case of successful registration.

---

### **UC-02: User Login**

**Actor:** Registered User
**Description:** A user logs in to the system.
**Precondition:** User must have an account.
**Main Flow:**

1. User accesses the login page.
2. Enters username and password.
3. System validates credentials.
4. If one of the credentials is non-valid, user is shown an error message.
4. If credentials are valid, User is logged in and redirected to the home page.

---

### **UC-03: View & Update Profile**

**Actor:** Authenticated User
**Description:** User updates their profile information.
**Precondition:** User is logged in.
**Main Flow:**

1. User navigates to the profile page.
2. Edits fields like name, email, password.
3. Submits changes.
4. System saves and confirms the update.

---

### **UC-04: View Events List**

**Actor:** Any User
**Description:** View the list of upcoming events.
**Precondition:** None
**Main Flow:**

1. User opens the event list page.
2. System displays a list of available events with dates, locations, and details.

---

### **UC-05: Book Ticket**

**Actor:** Authenticated User
**Description:** Temporarily reserve a ticket for an event (valid for 2 days).
**Precondition:** User must be logged in.
**Main Flow:**

1. User selects an event.
2. Clicks “Book Ticket”.
3. System creates a temporary reservation valid for 2 days.
4. Booking appears in user history.

---

### **UC-06: Buy Ticket**

**Actor:** Authenticated User
**Description:** User buys a ticket using dummy payment.
**Precondition:** Booking exists OR user buys directly.
**Main Flow:**

1. User selects an event and chooses to buy a ticket.
2. Enters dummy card details.
3. System validates card (simulated).
4. Payment confirmed → ticket marked as “bought”.
5. Purchase appears in user history.

---

### **UC-07: View Booking/Purchase History**

**Actor:** Authenticated User
**Description:** User views their booking and purchase history.
**Precondition:** User is logged in.
**Main Flow:**

1. User navigates to "My Tickets".
2. System shows booked and bought tickets with details.

---

### **UC-08: Receive Notifications**

**Actor:** System
**Description:** System sends notifications related to events and bookings.
**Preconditions:** User has booked or bought a ticket.
**Flows:**

* Notify user when a **new event** is added.
* Notify user **2 days before** a bought event.
* Notify user **2 days after booking** if it wasn’t confirmed.

---

### **UC-09: Add Event (Admin only)**

**Actor:** Admin
**Description:** Admin adds a new event to the system.
**Precondition:** Admin is logged in.
**Main Flow:**

1. Admin navigates to event management.
2. Enters event details.
3. Submits form.
4. System saves and sends notifications to users.

---

### **UC-10: Edit Event (Admin only)**

**Actor:** Admin
**Description:** Admin updates event details.
**Precondition:** Admin is logged in.
**Main Flow:**

1. Admin selects event to edit.
2. Updates details (date, name, capacity, etc.).
3. Submits changes.
4. System updates event info.

---

### **UC-11: Logout**

**Actor:** Authenticated User
**Description:** User logs out of the system.
**Main Flow:**

1. User clicks logout.
2. System clears session and redirects to login or home page.

---
