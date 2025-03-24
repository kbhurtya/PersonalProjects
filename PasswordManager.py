import os
import json
from cryptography.fernet import Fernet

# Paths for the key and database files
KEY_FILE = "key.key"
DATA_FILE = "passwords.json"

# Generate and load the encryption key
def generate_key():
    if not os.path.exists(KEY_FILE):
        key = Fernet.generate_key()
        with open(KEY_FILE, 'wb') as key_file:
            key_file.write(key)
    with open(KEY_FILE, 'rb') as key_file:
        return Fernet(key_file.read())

fernet = generate_key()

# Load password data from file
def load_passwords():
    if not os.path.exists(DATA_FILE):
        with open(DATA_FILE, 'w') as data_file:
            json.dump({}, data_file)
    with open(DATA_FILE, 'r') as data_file:
        return json.load(data_file)

# Save password data to file
def save_passwords(passwords):
    with open(DATA_FILE, 'w') as data_file:
        json.dump(passwords, data_file)

# Add a new password
def add_password(website, username, password):
    passwords = load_passwords()
    encrypted_password = fernet.encrypt(password.encode()).decode()
    passwords[website] = {"username": username, "password": encrypted_password}
    save_passwords(passwords)
    print(f"Password for {website} added successfully!")

# Retrieve a password
def retrieve_password(website):
    passwords = load_passwords()
    if website in passwords:
        encrypted_password = passwords[website]["password"]
        decrypted_password = fernet.decrypt(encrypted_password.encode()).decode()
        username = passwords[website]["username"]
        print(f"Website: {website}\nUsername: {username}\nPassword: {decrypted_password}")
    else:
        print(f"No details found for {website}.")

# Main menu
def main():
    while True:
        print("\nPassword Manager")
        print("1. Add a new password")
        print("2. Retrieve a password")
        print("3. Exit")
        choice = input("Enter your choice: ")

        if choice == "1":
            website = input("Enter the website: ")
            username = input("Enter the username: ")
            password = input("Enter the password: ")
            add_password(website, username, password)
        elif choice == "2":
            website = input("Enter the website: ")
            retrieve_password(website)
        elif choice == "3":
            print("Exiting Password Manager. Goodbye!")
            break
        else:
            print("Invalid choice. Please try again.")

if __name__ == "__main__":
    main()


