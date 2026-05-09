package com.example.grama_sanjeevini.constants

/**
 * AppStrings — Single source of truth for all user-facing messages.
 * Never show raw Firebase/exception messages to the user; always map to one of these.
 */
object AppStrings {

    // ── Authentication errors ──────────────────────────────────────────────────
    const val ERR_OTP_INVALID =
        "The code you entered is incorrect. Please check and try again."
    const val ERR_OTP_EXPIRED =
        "The verification code has expired. Please request a new one."
    const val ERR_OTP_SEND_FAILED =
        "We couldn't send the code to this number. Please check the number and try again."
    const val ERR_OTP_TOO_MANY_REQUESTS =
        "Too many attempts. Please wait a few minutes before trying again."

    // ── Login / Registration errors ────────────────────────────────────────────
    const val ERR_LOGIN_NOT_FOUND =
        "No account found with this number. Would you like to create one?"
    const val ERR_REGISTER_ALREADY_EXISTS =
        "An account with this number already exists. Please log in instead."
    const val ERR_REGISTRATION_FAILED =
        "Account creation failed. Please try again."

    // ── Network / Generic errors ───────────────────────────────────────────────
    const val ERR_NETWORK =
        "Network error. Please check your internet connection and try again."
    const val ERR_GENERIC =
        "Something went wrong. Please try again."
    const val ERR_TIMEOUT =
        "The request timed out. Please try again."

    // ── Location errors ────────────────────────────────────────────────────────
    const val ERR_LOCATION_UNAVAILABLE =
        "Unable to determine your location. Please enable GPS and try again."
    const val ERR_LOCATION_PERMISSION =
        "Location permission is needed to find nearby pharmacies."

    // ── Upload / Receipt errors ────────────────────────────────────────────────
    const val ERR_UPLOAD_FAILED =
        "We couldn't upload your prescription. Please try again."
    const val ERR_FILE_TOO_LARGE =
        "The file is too large. Please choose an image under 5 MB."
    const val ERR_NO_FILE_SELECTED =
        "No file selected. Please choose a prescription image."

    // ── Inventory errors ───────────────────────────────────────────────────────
    const val ERR_SAVE_LISTING_FAILED =
        "Failed to save the medicine listing. Please try again."

    // ── Success / Info messages ────────────────────────────────────────────────
    const val INFO_OTP_SENT =
        "A verification code has been sent to your mobile number."
    const val INFO_RECEIPT_UPLOADED =
        "Your prescription has been saved and shared with nearby pharmacies."
    const val INFO_LISTING_SAVED =
        "Medicine listing saved to your inventory."
    const val INFO_RECEIPT_REVIEWED =
        "Prescription marked as reviewed."

    // ── Pharmacist Dashboard ───────────────────────────────────────────────────
    const val ERR_STORE_LOAD_FAILED =
        "Couldn't load your store details. Please restart the app."
    const val ERR_PRESCRIPTIONS_LOAD_FAILED =
        "Couldn't load prescriptions. Pull to refresh."
}
