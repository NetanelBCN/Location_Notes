package dev.netanelbcn.locationnotes.fragmentViews;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import dev.netanelbcn.locationnotes.R;
import dev.netanelbcn.locationnotes.databinding.FragmentLoginBinding;
import dev.netanelbcn.locationnotes.controllers.DataManager;
import dev.netanelbcn.locationnotes.utilities.Validator;
import dev.netanelbcn.locationnotes.views.MainActivity;

public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;
    private DataManager dataManager;
    private Validator validator;
    private MaterialTextView LoginMTVGotoReg;
    private MaterialButton LoginMBLogin;
    private MaterialTextView LoginMTVAlert;
    private TextInputEditText LoginTIETMail;
    private TextInputEditText LoginTIETPassword;
    private ShapeableImageView LoginSIVBackground;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        dataManager = DataManager.getInstance();
        validator = Validator.getInstance();
        findViews();
        setBackgroundImage();
        setupClicks();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            dataManager.setUserId(uid);
            this.LoginMTVAlert.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
            this.LoginMTVAlert.setText("Please wait, Auto-login...");

            dataManager.setDataLoadListener(() -> {
                dataManager.getFBDb().getReference("users")
                        .child(uid)
                        .child("name")
                        .get()
                        .addOnSuccessListener(snapshot -> {
                            String name = snapshot.getValue(String.class);
                            if (name != null) {
                                dataManager.setUserName(name);
                            }
                            goToMainActivity();
                        })
                        .addOnFailureListener(e -> {
                            Log.e("AutoLogin", "Failed to load user name: " + e.getMessage());
                            goToMainActivity();
                        });
            });

            dataManager.loadGeneralData();
        }
    }

    private void findViews() {
        this.LoginMTVGotoReg = binding.LoginMTVGotoReg;
        this.LoginMBLogin = binding.LoginMBLogin;
        this.LoginMTVAlert = binding.LoginMTVAlert;
        this.LoginTIETMail = binding.LoginTIETMail;
        this.LoginTIETPassword = binding.LoginTIETPassword;
        this.LoginSIVBackground = binding.LoginSIVBackground;
    }

    private void setupClicks() {
        setupLoginClick();
        setupGotoRegClick();
    }

    private void setupGotoRegClick() {
        this.LoginMTVGotoReg.setOnClickListener(v -> {
            NavController navController = NavHostFragment.findNavController(LoginFragment.this);
            if (this.LoginTIETMail.getText() != null && !this.LoginTIETMail.getText().toString().trim().isEmpty()) {
                moveWithMailToReg(navController);
            } else {
                navController.navigate(R.id.navigation_registration);
            }
        });
    }

    private void moveWithMailToReg(NavController navController) {
        Bundle bundle = new Bundle();
        bundle.putString("email", this.LoginTIETMail.getText().toString().trim());
        navController.navigate(R.id.navigation_registration, bundle);
    }

    private void setupLoginClick() {
        this.LoginMBLogin.setOnClickListener(v -> {
            hideKeyboard();
            if (validateLoginArgs()) {
                String mail = this.LoginTIETMail.getText().toString().trim();
                String password = this.LoginTIETPassword.getText().toString().trim();
                login(mail, password);
            }
        });
    }

    private boolean validateLoginArgs() {
        if (!checkNotEmptyLoginFields()) {
            this.LoginMTVAlert.setTextColor(ContextCompat.getColor(requireContext(), R.color.red));
            this.LoginMTVAlert.setText("Please fill all fields");
            return false;
        }
        if (validator.isMailFormatValid(this.LoginTIETMail)) {
            this.LoginMTVAlert.setTextColor(ContextCompat.getColor(requireContext(), R.color.red));
            this.LoginMTVAlert.setText("Invalid mail format");
            return false;
        }
        return true;
    }

    private boolean checkNotEmptyLoginFields() {
        return validator.isInputBarValueValid(this.LoginTIETMail) &&
                validator.isInputBarValueValid(this.LoginTIETPassword);
    }

    private void login(String mail, String password) {
        this.LoginMBLogin.setEnabled(false);
        this.LoginMTVAlert.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
        this.LoginMTVAlert.setText("Logging in...");

        dataManager.getFbManager().getmAuth().signInWithEmailAndPassword(mail, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String uid = dataManager.getFbManager().getmAuth().getCurrentUser().getUid();
                        DataManager.getInstance().setUserId(uid);
                        this.LoginMTVAlert.setText("Loading notes...");

                        DataManager.getInstance().setDataLoadListener(() -> {
                            this.LoginMTVAlert.setText("Loading user profile...");
                            dataManager.getFBDb().getReference("users")
                                    .child(uid)
                                    .child("name")
                                    .get()
                                    .addOnSuccessListener(snapshot -> {
                                        String name = snapshot.getValue(String.class);
                                        if (name != null) {
                                            dataManager.setUserName(name);
                                        }
                                        goToMainActivity();
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("FB_err", "Failed to load user name: " + e.getMessage());
                                    });
                        });

                        DataManager.getInstance().loadGeneralData();
                    } else {
                        this.LoginMBLogin.setEnabled(true);
                        this.LoginMTVAlert.setTextColor(ContextCompat.getColor(requireContext(), R.color.red));
                        this.LoginMTVAlert.setText("Invalid user mail or password");
                    }
                });
    }

    private void goToMainActivity() {
        Intent intent = new Intent(requireContext(), MainActivity.class);
        startActivity(intent);
        requireActivity().finish();
    }

    private void hideKeyboard() {
        View view = requireActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void setBackgroundImage() {
        Glide.with(this)
                .load(dev.netanelbcn.locationnotes.R.drawable.logging_background)
                .centerCrop()
                .placeholder(dev.netanelbcn.locationnotes.R.drawable.ic_launcher_background)
                .into(this.LoginSIVBackground);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (dataManager != null) {
            dataManager.setDataLoadListener(null);
        }
        binding = null;
    }
}
