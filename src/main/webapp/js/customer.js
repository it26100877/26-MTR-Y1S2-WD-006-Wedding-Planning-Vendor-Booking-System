// Confirm before deleting a customer
function confirmDelete() {
    return confirm("Are you sure you want to delete this customer?");
}


// Validate customer form
function validateCustomerForm(isEdit) {

    const brideName = document.querySelector("[name='brideName']").value.trim();
    const groomName = document.querySelector("[name='groomName']").value.trim();
    const phoneNumber = document.querySelector("[name='phoneNumber']").value.trim();
    const email = document.querySelector("[name='email']").value.trim();
    const address = document.querySelector("[name='address']").value.trim();
    const weddingDate = document.querySelector("[name='weddingDate']").value;
    const budget = document.querySelector("[name='budget']").value.trim();


    // 1. Required fields (only when adding)
    if (!isEdit) {

        if (!brideName || !groomName || !phoneNumber ||
            !email || !address || !weddingDate || !budget) {

            alert("Please fill in all required fields.");
            return false;
        }
    }


    // 2. Name validation
    const namePattern = /^[\p{L}\p{M} .'-]+$/u;

    if (brideName && !namePattern.test(brideName)) {
        alert("Bride name must contain letters only.");
        return false;
    }

    if (groomName && !namePattern.test(groomName)) {
        alert("Groom name must contain letters only.");
        return false;
    }


    // 3. Phone number validation (exactly 10 digits)
    const phonePattern = /^[0-9]{10}$/;

    if (phoneNumber && !phonePattern.test(phoneNumber)) {

        alert("Phone number must contain exactly 10 digits.");
        return false;
    }


    // 4. Email validation
    const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    if (email && !emailPattern.test(email)) {
        alert("Please enter a valid email address.");
        return false;
    }


    // 5. Address validation
    if (address && address.length < 5) {
        alert("Address must contain at least 5 characters.");
        return false;
    }


    // 6. Wedding date validation
    if (weddingDate) {

        const date = new Date(weddingDate + "T00:00:00");

        if (isNaN(date.getTime())) {
            alert("Please enter a valid wedding date.");
            return false;
        }
    }


    // 7. Budget validation
    if (budget !== "") {

        const budgetValue = Number(budget);

        if (!Number.isFinite(budgetValue) || budgetValue < 0) {
            alert("Budget must be a valid non-negative number.");
            return false;
        }
    }

    return true;
}

