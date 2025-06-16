package dev.netanelbcn.locationnotes.fragmentViews.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import dev.netanelbcn.locationnotes.R;
import dev.netanelbcn.locationnotes.databinding.FragmentLoginBinding;
import dev.netanelbcn.locationnotes.utilities.FBManager;
import dev.netanelbcn.locationnotes.utilities.Validator;
import dev.netanelbcn.locationnotes.views.MainActivity;

public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;
    private FBManager fbManager;
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
        fbManager = FBManager.getInstance();
        validator = Validator.getInstance();
        bindViews();
        setBackgroundImage();
        setupClicks();
        return root;
    }

    private void bindViews() {
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
                Bundle bundle = new Bundle();
                bundle.putString("email", this.LoginTIETMail.getText().toString().trim());
                navController.navigate(R.id.navigation_registration, bundle);
            } else
                navController.navigate(R.id.navigation_registration);
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
            this.LoginMTVAlert.setText("Please fill all fields");
            return false;
        }
        if (!validator.isMailFormatValid(this.LoginTIETMail)) {
            this.LoginMTVAlert.setText("Invalid mail format");
            return false;
        }
        return true;
    }


    private boolean checkNotEmptyLoginFields() {
        return
                validator.isInputBarValueValid(this.LoginTIETMail) && validator.isInputBarValueValid(this.LoginTIETPassword);
    }

    private void login(String mail, String password) {
        fbManager.getmAuth().signInWithEmailAndPassword(mail, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Login successful
                        Intent intent = new Intent(requireContext(), MainActivity.class);
                        startActivity(intent);
                        requireActivity().finish();
                    } else {
                        this.LoginMTVAlert.setText("Invalid user mail or password");
                        Toast.makeText(requireContext(), "Login failed", Toast.LENGTH_LONG).show();
                    }
                });
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
        binding = null;
    }
}
