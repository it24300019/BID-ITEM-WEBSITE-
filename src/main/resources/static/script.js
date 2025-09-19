// eBidPro Complete script.js with Email Change Modal
document.addEventListener('DOMContentLoaded', () => {
    // --- SHARED LOGIC ---
    const user = JSON.parse(localStorage.getItem('user'));
    const currentPage = window.location.pathname.split('/').pop();

    const validateEmail = (email) => {
        const re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
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
        const ageCheck = document.getElementById('ageCheck');
        const termsCheck = document.getElementById('termsCheck');
        const registerButton = document.getElementById('registerButton');
        const openTermsModal = document.getElementById('openTermsModal');
        const agreeTermsButton = document.getElementById('agreeTermsButton');
        const termsModal = document.getElementById('termsModal');
        const termsContent = document.getElementById('termsContent');

        const checkRegistrationReady = () => {
            if (ageCheck && termsCheck && ageCheck.checked && termsCheck.checked) {
                registerButton.disabled = false;
                registerButton.classList.remove('bg-slate-500', 'cursor-not-allowed');
                registerButton.classList.add('bg-blue-600', 'hover:bg-blue-700');
            } else {
                registerButton.disabled = true;
                registerButton.classList.add('bg-slate-500', 'cursor-not-allowed');
                registerButton.classList.remove('bg-blue-600', 'hover:bg-blue-700');
            }
        };

        if (ageCheck) ageCheck.addEventListener('change', checkRegistrationReady);
        if (termsCheck) termsCheck.addEventListener('change', checkRegistrationReady);
        if (openTermsModal && termsModal) {
            openTermsModal.addEventListener('click', () => termsModal.classList.remove('hidden'));
        }
        if (termsContent && agreeTermsButton) {
            termsContent.addEventListener('scroll', () => {
                if (termsContent.scrollHeight - termsContent.scrollTop <= termsContent.clientHeight + 5) {
                    agreeTermsButton.disabled = false;
                    agreeTermsButton.classList.remove('bg-slate-500', 'cursor-not-allowed');
                    agreeTermsButton.classList.add('bg-blue-600', 'hover:bg-blue-700');
                }
            });
            agreeTermsButton.addEventListener('click', () => {
                termsModal.classList.add('hidden');
                termsCheck.checked = true;
                termsCheck.dispatchEvent(new Event('change'));
            });
        }

        if (fullNameInput && fullNameError) {
            fullNameInput.addEventListener('input', () => {
                const hasNumber = /\d/.test(fullNameInput.value);
                fullNameInput.classList.toggle('invalid', hasNumber);
                fullNameError.classList.toggle('hidden', !hasNumber);
            });
        }

        const validatePasswords = () => {
            if (passwordError) passwordError.classList.add('hidden');
            const passValue = passwordInput.value;
            const confirmPassValue = confirmPasswordInput.value;
            if (passValue && confirmPassValue) {
                const doPasswordsMatch = passValue === confirmPassValue;
                passwordInput.classList.toggle('invalid', !doPasswordsMatch);
                confirmPasswordInput.classList.toggle('invalid', !doPasswordsMatch);
                passwordInput.classList.toggle('valid', doPasswordsMatch);
                confirmPasswordInput.classList.toggle('valid', doPasswordsMatch);
                if (!doPasswordsMatch && passwordError) {
                    passwordError.textContent = 'Passwords do not match.';
                    passwordError.classList.remove('hidden');
                }
            } else {
                passwordInput.classList.remove('valid', 'invalid');
                confirmPasswordInput.classList.remove('valid', 'invalid');
            }
        };

        if (passwordInput) passwordInput.addEventListener('input', validatePasswords);
        if (confirmPasswordInput) confirmPasswordInput.addEventListener('input', validatePasswords);

        registerForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const hasNumber = fullNameInput ? /\d/.test(fullNameInput.value) : false;
            const passwordsMatch = passwordInput && confirmPasswordInput ?
                passwordInput.value === confirmPasswordInput.value : true;
            const isEmailValid = emailInput ? validateEmail(emailInput.value) : false;
            const selectedRole = registerForm.querySelector('input[name="role"]:checked');
            const isFormValid = !hasNumber && passwordsMatch && isEmailValid && selectedRole && registerForm.checkValidity();

            if (isFormValid) {
                try {
                    const response = await fetch('/api/users/register', {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/json' },
                        body: JSON.stringify({
                            fullName: fullNameInput.value,
                            email: emailInput.value,
                            password: passwordInput.value,
                            phone: document.getElementById('phone') ? document.getElementById('phone').value || '' : '',
                            role: selectedRole.value
                        })
                    });
                    if (response.ok) {
                        localStorage.setItem('tempUser', JSON.stringify({
                            fullName: fullNameInput.value,
                            email: emailInput.value,
                            phone: document.getElementById('phone') ? document.getElementById('phone').value || '' : '',
                            role: selectedRole.value
                        }));
                        window.location.href = 'success.html';
                    } else {
                        const errorText = await response.text();
                        if (passwordError) {
                            passwordError.textContent = errorText;
                            passwordError.classList.remove('hidden');
                        }
                    }
                } catch (error) {
                    if (passwordError) {
                        passwordError.textContent = 'Registration failed. Please try again.';
                        passwordError.classList.remove('hidden');
                    }
                }
            } else {
                let errorMessage = 'Please fill out all required fields correctly.';
                if (!isEmailValid) { errorMessage = 'Please enter a valid email address.'; }
                else if (!selectedRole) { errorMessage = 'Please select whether you are a buyer or a seller.'; }
                if (passwordError) {
                    passwordError.textContent = errorMessage;
                    passwordError.classList.remove('hidden');
                }
            }
        });
    }

    // --- LOGIN PAGE LOGIC ---
    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const emailInput = document.getElementById('email');
            const passwordInput = document.getElementById('password');
            const loginError = document.getElementById('loginError');

            if (emailInput && passwordInput && validateEmail(emailInput.value) && passwordInput.value) {
                try {
                    const response = await fetch('/api/users/login', {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/json' },
                        body: JSON.stringify({
                            email: emailInput.value,
                            password: passwordInput.value
                        })
                    });
                    if (response.ok) {
                        const userData = await response.json();
                        localStorage.setItem('user', JSON.stringify(userData));
                        localStorage.removeItem('tempUser');
                        if (userData.role === 'seller') {
                            window.location.href = 'seller_dashboard.html';
                        } else {
                            window.location.href = 'buyer_dashboard.html';
                        }
                    } else {
                        if (loginError) {
                            loginError.textContent = 'Invalid email or password.';
                            loginError.classList.remove('hidden');
                        }
                    }
                } catch (error) {
                    if (loginError) {
                        loginError.textContent = 'Login failed. Please try again.';
                        loginError.classList.remove('hidden');
                    }
                }
            } else {
                if (loginError) {
                    loginError.textContent = 'Invalid email format or password cannot be empty.';
                    loginError.classList.remove('hidden');
                }
            }
        });
    }

    // --- DASHBOARD PAGE LOGIC ---
    if (document.getElementById('userNameDisplay')) {
        if (user) {
            document.getElementById('userNameDisplay').textContent = user.fullName;
            const updateFullName = document.getElementById('updateFullName');
            const updateEmail = document.getElementById('updateEmail');
            const updatePhone = document.getElementById('updatePhone');
            if (updateFullName) updateFullName.value = user.fullName;
            if (updateEmail) updateEmail.value = user.email;
            if (updatePhone) updatePhone.value = user.phone || '';
        }

        // Tab functionality
        const tabs = document.querySelectorAll('#tabs button');
        const tabContents = document.querySelectorAll('.tab-content');
        tabs.forEach(tab => {
            tab.addEventListener('click', () => {
                tabs.forEach(t => t.classList.remove('bg-blue-600', 'text-white'));
                tabContents.forEach(c => c.classList.remove('active'));
                tab.classList.add('bg-blue-600', 'text-white');
                const targetTab = document.getElementById(tab.dataset.tab);
                if (targetTab) targetTab.classList.add('active');
            });
        });

        // Profile Update
        const updateProfileForm = document.getElementById('updateProfileForm');
        if (updateProfileForm) {
            updateProfileForm.addEventListener('submit', async (e) => {
                e.preventDefault();
                if (!user || !user.email) {
                    alert('User session expired. Please login again.');
                    window.location.href = 'login.html';
                    return;
                }
                try {
                    const response = await fetch(`/api/users/profile/${encodeURIComponent(user.email)}`, {
                        method: 'PUT',
                        headers: { 'Content-Type': 'application/json' },
                        body: JSON.stringify({
                            fullName: document.getElementById('updateFullName').value,
                            email: user.email,
                            password: "dummy_password",
                            phone: document.getElementById('updatePhone') ? document.getElementById('updatePhone').value || '' : '',
                            role: user.role
                        })
                    });
                    if (response.ok) {
                        const updatedUser = {
                            ...user,
                            fullName: document.getElementById('updateFullName').value,
                            phone: document.getElementById('updatePhone') ? document.getElementById('updatePhone').value || '' : ''
                        };
                        localStorage.setItem('user', JSON.stringify(updatedUser));
                        if (document.getElementById('userNameDisplay')) {
                            document.getElementById('userNameDisplay').textContent = updatedUser.fullName;
                        }
                        const successMsg = document.getElementById('profileUpdateSuccess');
                        if (successMsg) {
                            successMsg.classList.remove('hidden');
                            successMsg.style.opacity = 1;
                            setTimeout(() => {
                                successMsg.style.opacity = 0;
                                setTimeout(() => successMsg.classList.add('hidden'), 500);
                            }, 3000);
                        } else {
                            alert('Profile updated successfully!');
                        }
                    } else {
                        const errorText = await response.text();
                        alert('Profile update failed: ' + errorText);
                    }
                } catch (error) {
                    alert('Profile update failed. Please try again.');
                }
            });
        }

        // Logout
        const logoutButton = document.getElementById('logoutButton');
        if (logoutButton) {
            logoutButton.addEventListener('click', () => {
                localStorage.removeItem('user');
                localStorage.removeItem('tempUser');
                window.location.href = 'login.html';
            });
        }

        // Account Deletion
        const deleteAccountButton = document.getElementById('deleteAccountButton');
        if (deleteAccountButton) {
            deleteAccountButton.addEventListener('click', async () => {
                const isConfirmed = confirm('Are you absolutely sure you want to delete your account? This cannot be undone.');
                if (isConfirmed) {
                    if (!user || !user.email) {
                        alert('User session expired. Please login again.');
                        window.location.href = 'login.html';
                        return;
                    }
                    try {
                        const response = await fetch(`/api/users/profile/${encodeURIComponent(user.email)}`, {
                            method: 'DELETE',
                            headers: { 'Content-Type': 'application/json' },
                        });
                        if (response.ok) {
                            localStorage.removeItem('user');
                            localStorage.removeItem('tempUser');
                            alert('Account deleted successfully from database.');
                            window.location.href = 'index.html';
                        } else {
                            const errorText = await response.text();
                            alert('Account deletion failed: ' + errorText);
                        }
                    } catch (error) {
                        alert('Account deletion failed. Please try again.');
                    }
                }
            });
        }

        // --- EMAIL CHANGE MODAL HANDLING ---
        const emailInput = document.getElementById('updateEmail');
        const emailModal = document.getElementById('emailSupportModal');
        const closeEmailModal = document.getElementById('closeEmailModal');
        const copyEmailBtn = document.getElementById('copyEmailBtn');

        if (emailInput && emailModal) {
            // Make email field readonly and show help cursor
            emailInput.setAttribute('readonly', true);
            emailInput.style.cursor = 'help';
            emailInput.style.backgroundColor = '#374151';
            emailInput.setAttribute('title', 'Click to contact support for email changes');

            // Show modal when trying to focus email field
            emailInput.addEventListener('focus', function(e) {
                e.target.blur();
                emailModal.classList.remove('hidden');
                emailModal.classList.add('flex');
            });

            // Close modal
            if (closeEmailModal) {
                closeEmailModal.addEventListener('click', function() {
                    emailModal.classList.add('hidden');
                    emailModal.classList.remove('flex');
                });
            }

            // Copy support email
            if (copyEmailBtn) {
                copyEmailBtn.addEventListener('click', function() {
                    navigator.clipboard.writeText('support@ebidpro.com').then(function() {
                        copyEmailBtn.textContent = '✅ Copied!';
                        setTimeout(() => {
                            copyEmailBtn.textContent = 'Copy Support Email';
                        }, 2000);
                    }).catch(function() {
                        // Fallback for older browsers
                        const textArea = document.createElement('textarea');
                        textArea.value = 'support@ebidpro.com';
                        document.body.appendChild(textArea);
                        textArea.select();
                        document.execCommand('copy');
                        document.body.removeChild(textArea);
                        copyEmailBtn.textContent = '✅ Copied!';
                        setTimeout(() => {
                            copyEmailBtn.textContent = 'Copy Support Email';
                        }, 2000);
                    });
                });
            }

            // Close modal when clicking outside
            emailModal.addEventListener('click', function(e) {
                if (e.target === emailModal) {
                    emailModal.classList.add('hidden');
                    emailModal.classList.remove('flex');
                }
            });
        }
    }

    // --- CHANGE PASSWORD SECTION ---
    const changePasswordForm = document.getElementById('changePasswordForm');
    if (changePasswordForm) {
        changePasswordForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const oldPassword = document.getElementById('currentPassword').value;
            const newPassword = document.getElementById('newPassword').value;
            const confirmNewPassword = document.getElementById('confirmNewPassword').value;

            let errorDiv = document.getElementById('changePasswordError');
            let successDiv = document.getElementById('changePasswordSuccess');
            if (!errorDiv) {
                errorDiv = document.createElement('div');
                errorDiv.id = 'changePasswordError';
                errorDiv.style.color = 'red';
                changePasswordForm.appendChild(errorDiv);
            }
            if (!successDiv) {
                successDiv = document.createElement('div');
                successDiv.id = 'changePasswordSuccess';
                successDiv.style.color = 'green';
                changePasswordForm.appendChild(successDiv);
            }

            errorDiv.classList.add('hidden');
            successDiv.classList.add('hidden');

            if (newPassword !== confirmNewPassword) {
                errorDiv.textContent = 'New passwords do not match.';
                errorDiv.classList.remove('hidden');
                return;
            }
            if (newPassword.length < 6) {
                errorDiv.textContent = 'Password must be at least 6 characters.';
                errorDiv.classList.remove('hidden');
                return;
            }

            try {
                const user = JSON.parse(localStorage.getItem('user'));
                const response = await fetch(`/api/users/change-password`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({
                        email: user.email,
                        oldPassword,
                        newPassword
                    })
                });

                if (response.ok) {
                    successDiv.textContent = 'Password changed successfully!';
                    successDiv.classList.remove('hidden');
                    changePasswordForm.reset();
                } else {
                    const errorText = await response.text();
                    errorDiv.textContent = errorText || 'Password change failed.';
                    errorDiv.classList.remove('hidden');
                }
            } catch (err) {
                errorDiv.textContent = 'Network error. Please try again.';
                errorDiv.classList.remove('hidden');
            }
        });
    }
});
