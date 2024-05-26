package com.delhomme.jobber.SignUser

//import com.google.android.gms.auth.api.signin.GoogleSignInAccount
//import com.google.android.gms.auth.api.signin.GoogleSignInOptions

import android.content.Intent
import android.credentials.CredentialManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.delhomme.jobber.MainActivity
import com.delhomme.jobber.R
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var googleSignInOptions: GoogleSignInOptions
    //private lateinit var googleSignInOptionsExtension: GoogleSignInOptionsExtension
    //private lateinit var googleAuthProvider: GoogleAuthProvider
    //private lateinit var googleAuthCredential: GoogleAuthCredential
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var credentialManager: CredentialManager
    //private lateinit var googleSigniInAccount: GoogleSignInAccount

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        /*
        googleSignInAccount.id
        googleSignInAccount.account
        googleSignInAccount.email
        googleSignInAccount.idToken
        googleSignInOptionsExtension.extensionType
        googleSignInOptionsExtension.impliedScopes
        googleAuthProvider.toString()
        googleAuthCredential.signInMethod
        googleAuthCredential.provider

        googleSignInOptions.account
        googleSignInOptions.extensions
        googleSignInOptions.logSessionId
        googleSignInOptions.isForceCodeForRefreshToken
        googleSignInOptions.isIdTokenRequested
        googleSignInOptions.isServerAuthCodeRequested
        googleSignInOptions.scopeArray
        googleSignInOptions.serverClientId

        googleAuth.signInWithCredential()
        googleAuth.app
        googleAuth.customAuthDomain
        googleAuth.signOut()
        googleAuth.createUserWithEmailAndPassword()
        googleAuth.currentUser
        googleAuth.firebaseAuthSettings
        googleAuth.languageCode
        googleAuth.pendingAuthResult
        googleAuth.tenantId
        googleAuth.addAuthStateListener {  }
        googleAuth.addAuthStateListener()
        googleAuth.addIdTokenListener()
        googleAuth.addIdTokenListener {  }
        googleAuth.applyActionCode()
        googleAuth.checkActionCode()
        googleAuth.confirmPasswordReset()
        googleAuth.initializeRecaptchaConfig()
        googleAuth.revokeAccessToken()
        googleAuth.sendPasswordResetEmail()
        googleAuth.sendPasswordResetEmail()
        googleAuth.signInAnonymously()
        googleAuth.signInWithCustomToken()
        googleAuth.sendSignInLinkToEmail()
        googleAuth.signInWithEmailAndPassword()
        googleAuth.signInWithEmailLink()
        googleAuth.verifyPasswordResetCode()
        googleAuth.signInWithCredential()
        */
        firebaseAuth = FirebaseAuth.getInstance()
        setupGoogleSignInOptions()

        /*
        findViewById<Button>(R.id.btnGoogleSignIn).setOnClickListener {
            signInWithGoogle()
        }*/
    }


    private fun setupGoogleSignInOptions() {
        googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
    }
    /*
    private fun signInWithGoogle() {
        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
            .setServerClientId(getString(R.string.default_web_client_id))
            .setNonce("randomNonce") // Remplacez par votre propre nonce généré de manière sécurisée
            .build()

        val request: GetCredentialRequest = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        // Utilisez Credential Manager pour lancer la requête
        credentialManager.getCredential(request, this).addOnSuccessListener { response ->
            handleSignIn(response)
        }.addOnFailureListener { exception ->
            // Gérer l'échec
            Log.e("LoginActivity", "Erreur lors de la récupération de l'ID token: ", exception)
        }
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    fun handleSignIn(response: GetCredentialResponse) {
        val googleIdTokenCredential = response.credential as? GoogleIdTokenCredential ?: return
        firebaseAuthWithGoogle(googleIdTokenCredential.idToken)
    }*/

    private fun loginWithEmailAndPassword(email: String, password: String) {
        // Cette condition doit être remplacée par la logique d'authentification réelle
        if (email == "paul@delhomme.ovh" && password == "1234") {
            saveUserData(email, password)
            Toast.makeText(this, "Connexion réussie !", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            Toast.makeText(this, "Connexion échouée !", Toast.LENGTH_SHORT).show()
            Log.e("LoginWithEmailAndPassword", "Erreur de connexion")
        }
    }

    private fun saveUserData(email: String, password: String) {
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("email", email)
        editor.putString("password", password)
        editor.apply()
    }

    /*

    private fun getGoogleSignInOptions(): GoogleSignInOptions {
        return GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    startActivity(Intent(this, FragmentDashboard::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Echec de l'authentification", Toast.LENGTH_LONG).show()
                }
            }
    }*/
}

