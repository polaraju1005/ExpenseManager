package com.starkindustries.expensetracker

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.starkindustries.expensetracker.presentation.ui.screens.SignInScreen
import com.starkindustries.expensetracker.presentation.ui.screens.TransactionDetailScreen
import com.starkindustries.expensetracker.presentation.ui.screens.TransactionEntryScreen
import com.starkindustries.expensetracker.presentation.ui.screens.TransactionListScreen
import com.starkindustries.expensetracker.presentation.viewmodel.TransactionViewModel
import com.starkindustries.expensetracker.ui.theme.ExpenseTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val transactionViewModel: TransactionViewModel by viewModels()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val RC_SIGN_IN = 1001
    private var isSignedIn by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // Get it from Firebase console
            .requestEmail().build()

        val googleSignInClient = GoogleSignIn.getClient(this, gso)

        if (auth.currentUser == null) {
            startGoogleSignIn(googleSignInClient)
        } else {
            isSignedIn = true
        }

        setContent {
            ExpenseTheme {
                val navController = rememberNavController()

                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(
                        navController = navController,
                        googleSignInClient = googleSignInClient
                    )
                }
            }
        }
    }

    private fun startGoogleSignIn(googleSignInClient: GoogleSignInClient) {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    @Composable
    fun AppNavigation(navController: NavHostController, googleSignInClient: GoogleSignInClient) {
        NavGraph(navController = navController, googleSignInClient = googleSignInClient)
    }

    @Composable
    fun NavGraph(navController: NavHostController, googleSignInClient: GoogleSignInClient) {
        NavHost(
            navController = navController,
            startDestination = if (isSignedIn) "transaction_list_screen" else "sign_in_screen"
        ) {
            composable("sign_in_screen") {
                SignInScreen(onSignInClicked = { signIn(googleSignInClient, navController) })
            }
            composable("transaction_list_screen") {
                TransactionListScreen(
                    navController = navController,
                    viewModel = transactionViewModel,
                    onTransactionDeleted = { id ->
                        transactionViewModel.deleteTransaction(id)
                    }
                )
            }
            composable("transaction_entry_screen") {
                TransactionEntryScreen(
                    viewModel = transactionViewModel,
                    onTransactionAdded = {
                        navController.popBackStack()
                    },
                    onBackPressed = {
                        navController.popBackStack()
                    }
                )
            }
            composable("transaction_details_screen/{transactionId}") { backStackEntry ->
                val transactionId = backStackEntry.arguments?.getString("transactionId")?.toLong()
                    ?: return@composable
                TransactionDetailScreen(
                    navController = navController,
                    transactionId = transactionId,
                    viewModel = transactionViewModel,
                    onBackPressed = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }

    private fun signIn(googleSignInClient: GoogleSignInClient, navController: NavHostController) {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(
        requestCode: Int, resultCode: Int, data: android.content.Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Toast.makeText(this, "Google sign-in failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential: AuthCredential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                isSignedIn = true
            } else {
                Toast.makeText(this, "Authentication Failed.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
