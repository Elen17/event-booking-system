## ✅ Non-Functional Requirements – Event Booking System

---

### 1. **Usability & Accessibility**

* The user interface should be intuitive, responsive, and easy to navigate. (only for desktop users for now)
* Forms and messages should provide clear feedback and validation for user actions.
* Users should be able to complete core tasks (like booking or buying a ticket) with no more than 3 steps.

---

### 2. **Reliability & Availability**

* The system should have an uptime of **at least 99.5%** during operational hours.
* In case of unexpected failures (e.g., network or server), the system should fail gracefully and provide user-friendly error messages.
* Automatic retry mechanisms should be used for transient failures (e.g., email sending or dummy payment API).

---

### 3. **Maintainability & Extensibility**

* The system should be modular and follow clean architecture principles (e.g., MVC or layered architecture).
* New features should be easy to add without affecting existing functionality.

---

### 4. **Performance & Scalability**

* The system should respond to user actions (e.g., login, booking) within **2 seconds** under normal conditions.

---

### 5. **Security & Access Control**

* All user sessions should be authenticated using secure mechanisms.
* Passwords must be stored using secure hashing.
* Role-based access control must be enforced:

  * Only admins can create or update events.
  * Normal users must not access admin functionalities.

* Sensitive data (e.g., card info) must **not** be stored.
* Dummy payment validation must simulate secure behavior without exposing real card logic.

---

### 6. **Data Integrity & Consistency**

* Booking and buying operations must be atomic — either fully completed or not done at all (transactional).
* Duplicate bookings must be prevented at both UI and database levels.

---

### 7. **Logging**

* The system must log key actions (e.g., login, ticket booking, payment).
* Errors and exceptions should be recorded with stack traces and timestamps.

---

