const loginForm = document.getElementById("loginForm");

const password = document.getElementById("password");

const togglePassword =
    document.getElementById("togglePassword");


// Show / Hide Password

togglePassword.addEventListener("click", function () {

    if (password.type === "password") {

        password.type = "text";

        togglePassword.textContent = "🙈";

    } else {

        password.type = "password";

        togglePassword.textContent = "👁";

    }

});


// Login Form

loginForm.addEventListener("submit", function (event) {

    event.preventDefault();

    const email =
        document.getElementById("email").value.trim();

    const passwordValue =
        password.value.trim();


    if (email === "" || passwordValue === "") {

        alert("Please enter your email and password.");

        return;
    }


    // Temporary frontend test

    alert("Login successful!");


    // Later:
    // Send data to Java Servlet
});