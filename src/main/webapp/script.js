// This single script file manages interactivity for all pages.
// It checks which page is currently loaded and applies the relevant logic.

document.addEventListener('DOMContentLoaded', () => {

    // --- SHARED LOGIC ---
    const user = JSON.parse(localStorage.getItem('user'));
    const currentPage = window.location.pathname.split('/').pop();

    const validateEmail = (email) => {
        const re = /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
        return re.test(String(email).toLowerCase());
    };

    if (!user && !['login.html', 'register.html', 'index.html', '', 'success.html'].includes(currentPage)) {
        window.location.href = 'login.html';
    }


    // --- REGISTRATION PAGE LOGIC ---
    const registerForm = document.getElementById('registerForm');
    if (registerForm) {
        const fullNameInput = document.getElementById('fullName');
        const emailInput = document.getElementById('email');
        const passwordInput = document.getElementById('password');
        const confirmPasswordInput = document.getElementById('confirmPassword');
        const fullNameError = document.getElementById('fullNameError');
        const passwordError = document.getElementById('passwordError');

        // Modal and Terms elements
        const ageCheck = document.getElementById('ageCheck');
        const termsCheck = document.getElementById('termsCheck');
        const registerButton = document.getElementById('registerButton');
        const openTermsModal = document.getElementById('openTermsModal');
        const agreeTermsButton = document.getElementById('agreeTermsButton');
        const termsModal = document.getElementById('termsModal');
        const termsContent = document.getElementById('termsContent');

        // Function to check if the register button should be enabled
        const checkRegistrationReady = () => {
            if (ageCheck.checked && termsCheck.checked) {
                registerButton.disabled = false;
                registerButton.classList.remove('bg-slate-500', 'cursor-not-allowed');
                registerButton.classList.add('bg-blue-600', 'hover:bg-blue-700');
            } else {
                registerButton.disabled = true;
                registerButton.classList.add('bg-slate-500', 'cursor-not-allowed');
                registerButton.classList.remove('bg-blue-600', 'hover:bg-blue-700');
            }
        };

        // Add event listeners to both checkboxes
        ageCheck.addEventListener('change', checkRegistrationReady);
        termsCheck.addEventListener('change', checkRegistrationReady);

        // Event listeners for the modal
        openTermsModal.addEventListener('click', () => termsModal.classList.remove('hidden'));

        // Logic for scroll-to-agree
        termsContent.addEventListener('scroll', () => {
            // Check if user has scrolled to the bottom (with a 5px tolerance)
            if (termsContent.scrollHeight - termsContent.scrollTop <= termsContent.clientHeight + 5) {
                agreeTermsButton.disabled = false;
                agreeTermsButton.classList.remove('bg-slate-500', 'cursor-not-allowed');
                agreeTermsButton.classList.add('bg-blue-600', 'hover:bg-blue-700');
            }
        });

        agreeTermsButton.addEventListener('click', () => {
            termsModal.classList.add('hidden');
            termsCheck.checked = true;
            // Manually trigger the change event to update the register button state
            termsCheck.dispatchEvent(new Event('change'));
        });

        fullNameInput.addEventListener('input', () => {
            const hasNumber = /\d/.test(fullNameInput.value);
            fullNameInput.classList.toggle('invalid', hasNumber);
            fullNameError.classList.toggle('hidden', !hasNumber);
        });

        const validatePasswords = () => {
            passwordError.classList.add('hidden');
            const passValue = passwordInput.value;
            const confirmPassValue = confirmPasswordInput.value;

            if (passValue && confirmPassValue) {
                const doPasswordsMatch = passValue === confirmPassValue;
                passwordInput.classList.toggle('invalid', !doPasswordsMatch);
                confirmPasswordInput.classList.toggle('invalid', !doPasswordsMatch);
                passwordInput.classList.toggle('valid', doPasswordsMatch);
                confirmPasswordInput.classList.toggle('valid', doPasswordsMatch);

                if (!doPasswordsMatch) {
                    passwordError.textContent = 'Passwords do not match.';
                    passwordError.classList.remove('hidden');
                }
            } else {
                passwordInput.classList.remove('valid', 'invalid');
                confirmPasswordInput.classList.remove('valid', 'invalid');
            }
        };
        passwordInput.addEventListener('input', validatePasswords);
        confirmPasswordInput.addEventListener('input', validatePasswords);

        registerForm.addEventListener('submit', (e) => {
            e.preventDefault();
            const hasNumber = /\d/.test(fullNameInput.value);
            const passwordsMatch = passwordInput.value === confirmPasswordInput.value;
            const isEmailValid = validateEmail(emailInput.value);
            const selectedRole = registerForm.querySelector('input[name="role"]:checked');
            const isFormValid = !hasNumber && passwordsMatch && isEmailValid && selectedRole && registerForm.checkValidity();

            if (isFormValid) {
                const selectedPreferences = Array.from(registerForm.querySelectorAll('input[name="preferences"]:checked')).map(cb => cb.value);
                const newUser = {
                    fullName: fullNameInput.value,
                    email: emailInput.value,
                    phone: document.getElementById('phone').value,
                    role: selectedRole.value,
                    preferences: selectedPreferences
                };
                localStorage.setItem('tempUser', JSON.stringify(newUser));
                window.location.href = 'success.html';
            } else {
                let errorMessage = 'Please fill out all required fields correctly.';
                if (!isEmailValid) { errorMessage = 'Please enter a valid email address.'; }
                else if (!selectedRole) { errorMessage = 'Please select whether you are a buyer or a seller.'; }
                passwordError.textContent = errorMessage;
                passwordError.classList.remove('hidden');
            }
        });
    }


    // --- LOGIN PAGE LOGIC ---
    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', (e) => {
            e.preventDefault();
            const emailInput = document.getElementById('email');
            const passwordInput = document.getElementById('password');
            const loginError = document.getElementById('loginError');

            if (validateEmail(emailInput.value) && passwordInput.value) {
                const tempUser = JSON.parse(localStorage.getItem('tempUser'));
                const userName = tempUser ? tempUser.fullName : "John Doe";
                const userPhone = tempUser ? tempUser.phone : "+1 555-1234";
                const userRole = tempUser ? tempUser.role : "buyer";

                const loggedInUser = {
                    fullName: userName,
                    email: emailInput.value,
                    phone: userPhone,
                    role: userRole
                };
                localStorage.setItem('user', JSON.stringify(loggedInUser));
                localStorage.removeItem('tempUser');

                if (userRole === 'seller') {
                    window.location.href = 'seller_dashboard.html';
                } else {
                    window.location.href = 'buyer_dashboard.html';
                }

            } else {
                loginError.textContent = 'Invalid email format or password cannot be empty.';
                loginError.classList.remove('hidden');
            }
        });
    }


    // --- DASHBOARD PAGE LOGIC (APPLIES TO BOTH) ---
    if (document.getElementById('userNameDisplay')) {
        if (user) {
            document.getElementById('userNameDisplay').textContent = user.fullName;
            const updateFullName = document.getElementById('updateFullName');
            const updateEmail = document.getElementById('updateEmail');
            const updatePhone = document.getElementById('updatePhone');
            if(updateFullName) updateFullName.value = user.fullName;
            if(updateEmail) updateEmail.value = user.email;
            if(updatePhone) updatePhone.value = user.phone;
        }

        const tabs = document.querySelectorAll('#tabs button');
        const tabContents = document.querySelectorAll('.tab-content');
        tabs.forEach(tab => {
            tab.addEventListener('click', () => {
                tabs.forEach(t => t.classList.remove('bg-blue-600', 'text-white'));
                tabContents.forEach(c => c.classList.remove('active'));
                tab.classList.add('bg-blue-600', 'text-white');
                document.getElementById(tab.dataset.tab).classList.add('active');
            });
        });

        const updateProfileForm = document.getElementById('updateProfileForm');
        if(updateProfileForm) {
            updateProfileForm.addEventListener('submit', (e) => {
                e.preventDefault();
                const successMsg = document.getElementById('profileUpdateSuccess');
                successMsg.classList.remove('hidden');
                successMsg.style.opacity = 1;
                setTimeout(() => {
                    successMsg.style.opacity = 0;
                    setTimeout(() => successMsg.classList.add('hidden'), 500);
                }, 3000);
            });
        }

        const logoutButton = document.getElementById('logoutButton');
        if (logoutButton) {
            logoutButton.addEventListener('click', () => {
                localStorage.removeItem('user');
                window.location.href = 'login.html';
            });
        }

        const deleteAccountButton = document.getElementById('deleteAccountButton');
        if(deleteAccountButton) {
            deleteAccountButton.addEventListener('click', () => {
                const isConfirmed = confirm('Are you absolutely sure you want to delete your account? This cannot be undone.');
                if(isConfirmed) {
                    localStorage.removeItem('user');
                    alert('Account deleted successfully.');
                    window.location.href = 'index.html';
                }
            });
        }
    }
});
