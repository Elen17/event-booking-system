--

## ✅ Functional Requirements – Event Booking System

### 1. **User Account Management**

* Users must be able to register with a username, password, and additional profile details.
* Users must be able to log in using their username and password.
* Users must be able to log out securely.
* Users must have a profile page where they can:

  * View their account details.
  * Update personal information (e.g., name, email, etc.).

---

### 2. **Event Browsing and Booking**

* Users must be able to view a list of all available upcoming events.
* Users must be able to book tickets for an upcoming event (up to **2 days in advance**).
* Users must be able to buy tickets for events through a **dummy payment gateway**.

  * Card details must be validated before confirming purchase.
  * If the card is valid, the ticket(s) are marked as bought.
* Users must be able to view their:

  * Booking history.
  * Purchase history.

---

### 3. **Email Notifications**

* Users must receive email notifications when:

  * A **new event** is added.
  * Their **booked event is approaching** (2 days before the event date).
  * Their **booked ticket is canceled automatically** (2 days after booking if not confirmed).

---

### 4. **Admin Functionality**

* Only **admin users** must be allowed to:

  * Add new events.
  * Update event details.

---

### 5. **Security and Access Control**

* All user actions must be authenticated.
* Admin-specific functionality must be protected and accessible only to users with admin privileges.

---
