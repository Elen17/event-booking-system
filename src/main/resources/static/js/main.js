/**
 * Main JavaScript file for Event Booking System
 */

document.addEventListener('DOMContentLoaded', function() {
    // Enable Bootstrap tooltips
    var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });

    // Enable Bootstrap popovers
    var popoverTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="popover"]'));
    var popoverList = popoverTriggerList.map(function (popoverTriggerEl) {
        return new bootstrap.Popover(popoverTriggerEl);
    });

    // Auto-dismiss alerts after 5 seconds
    var alerts = document.querySelectorAll('.alert-dismissible');
    alerts.forEach(function(alert) {
        setTimeout(function() {
            var bsAlert = new bootstrap.Alert(alert);
            bsAlert.close();
        }, 5000);
    });

    // Password toggle functionality
    var passwordToggles = document.querySelectorAll('.password-toggle');
    passwordToggles.forEach(function(toggle) {
        toggle.addEventListener('click', function() {
            var input = this.previousElementSibling;
            var icon = this.querySelector('i');

            if (input.type === 'password') {
                input.type = 'text';
                icon.classList.remove('fa-eye');
                icon.classList.add('fa-eye-slash');
            } else {
                input.type = 'password';
                icon.classList.remove('fa-eye-slash');
                icon.classList.add('fa-eye');
            }
        });
    });

    // Form validation
    var forms = document.querySelectorAll('.needs-validation');
    Array.prototype.slice.call(forms).forEach(function(form) {
        form.addEventListener('submit', function(event) {
            if (!form.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
            }
            form.classList.add('was-validated');
        }, false);
    });

    // Initialize any datepickers
    var datepickers = document.querySelectorAll('.datepicker');
    if (datepickers.length > 0 && typeof flatpickr !== 'undefined') {
        flatpickr('.datepicker', {
            enableTime: true,
            dateFormat: 'Y-m-d H:i',
            minDate: 'today'
        });
    }
});

// Utility function to show a toast notification
function showToast(message, type = 'success') {
    // You can implement a toast notification system here
    // For now, we'll just log to console
    console.log(`[${type.toUpperCase()}] ${message}`);
}

// Utility function to handle form submissions with fetch
async function submitForm(form, options = {}) {
    const { method = 'POST', onSuccess, onError } = options;

    try {
        const formData = new FormData(form);
        const response = await fetch(form.action, {
            method: method,
            body: formData,
            headers: {
                'Accept': 'application/json'
            }
        });

        const data = await response.json();

        if (response.ok) {
            if (typeof onSuccess === 'function') {
                onSuccess(data);
            }
        } else {
            throw new Error(data.message || 'An error occurred');
        }

        return data;
    } catch (error) {
        console.error('Form submission error:', error);
        if (typeof onError === 'function') {
            onError(error);
        }
        throw error;
    }
}
