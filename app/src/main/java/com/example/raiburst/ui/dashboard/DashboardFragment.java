package com.example.raiburst.ui.dashboard;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.raiburst.databinding.FragmentDashboardBinding;
import com.example.raiburst.ui.home.BalanceChecker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private FirebaseAuth auth;
    private DatabaseReference transactionsRef;
    private LinearLayout transactionsList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        // Fetch balance asynchronously
        fetchBalance();

        auth = FirebaseAuth.getInstance();
        transactionsList = binding.transactionsList;

        // Load transactions for the current user
        loadUserTransactions();
        return root;
    }

    private void loadUserTransactions() {
        String userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;

        if (userId == null) {
            Toast.makeText(getContext(), "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        // Reference to the "transactions" node in Firebase
        transactionsRef = FirebaseDatabase.getInstance().getReference("transactions");

        // Query to fetch transactions made by the current user
        transactionsRef.orderByChild("userId").equalTo(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot transactionSnapshot : snapshot.getChildren()) {
                                String message = transactionSnapshot.child("message").getValue(String.class);
                                if (message != null) {
                                    addTransactionRecord(message);
                                }
                            }
                        } else {
                            Toast.makeText(getContext(), "No transactions found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Failed to load transactions: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addTransactionRecord(String message) {
        // Create a new EditText for the transaction record
        EditText transactionRecord = new EditText(getContext());
        transactionRecord.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        transactionRecord.setText(message);
        transactionRecord.setPadding(12, 12, 12, 12);
        transactionRecord.setTextColor(getResources().getColor(android.R.color.white));
        transactionRecord.setHintTextColor(getResources().getColor(android.R.color.darker_gray));
        transactionRecord.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        transactionRecord.setEnabled(false); // Make it read-only

        // Add the EditText to the transactions list
        transactionsList.addView(transactionRecord);
    }

    private void fetchBalance() {
        new FetchBalanceTask().execute();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private class FetchBalanceTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            BalanceChecker balanceChecker = new BalanceChecker();
            return balanceChecker.getUserBalance();
        }

        @Override
        protected void onPostExecute(String balance) {
            if (balance != null) {
                binding.balanceAmount.setText("Balance: $" + balance);
            } else {
                binding.balanceAmount.setText("Failed to retrieve balance.");
            }
        }
    }
}