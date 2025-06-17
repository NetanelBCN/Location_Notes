package dev.netanelbcn.locationnotes.fragmentViews;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

import dev.netanelbcn.locationnotes.R;
import dev.netanelbcn.locationnotes.databinding.FragmentRegistrationBinding;
import dev.netanelbcn.locationnotes.controllers.DataManager;
import dev.netanelbcn.locationnotes.utilities.Validator;
import dev.netanelbcn.locationnotes.views.MainActivity;

public class RegistrationFragment extends Fragment {


    private DataManager dataManager;
    private Validator validator;
    private TextInputEditText RegistrationTIETMail;
    private TextInputEditText RegistrationTIETPassword;
    private TextInputEditText RegistrationTIETFullName;
    private MaterialButton RegistrationMBReg;
    private ShapeableImageView RegistrationSIVBackground;
    private MaterialTextView RegistrationMTVAlert;
    private MaterialTextView RegistrationMTVGotoLogin;


    private FragmentRegistrationBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRegistrationBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        dataManager = DataManager.getInstance();
        validator = Validator.getInstance();
        findViews();
        setBackgroundImage();
        getMailFromSignIn();
        setupClicks();
        return root;
    }

    private void findViews() {
        this.RegistrationTIETPassword = binding.RegistrationTIETPassword;
        this.RegistrationSIVBackground = binding.RegistrationSIVBackground;
        this.RegistrationMTVAlert = binding.RegistrationMTVAlert;
        this.RegistrationMTVGotoLogin = binding.RegistrationMTVGotoLogin;
        this.RegistrationTIETMail = binding.RegistrationTIETMail;
        this.RegistrationMBReg = binding.RegistrationMBReg;
        this.RegistrationTIETFullName=binding.RegistrationTIETFullName;
    }

    ;

    private void getMailFromSignIn() {
        Bundle args = getArguments();
        if (args != null && args.containsKey("email")) {
            String email = args.getString("email");
            this.RegistrationTIETMail.setText(email);
        }
    }

    private void setupClicks() {
        setupGotoSignInClick();
        setupRegisterClick();
    }

    private void setupRegisterClick() {
        this.RegistrationMBReg.setOnClickListener(v -> {
            if (validateRegArgs()) {
                String mail = this.RegistrationTIETMail.getText().toString().trim();
                String password = this.RegistrationTIETPassword.getText().toString().trim();
                String name = this.RegistrationTIETFullName.getText().toString().trim();
                register(mail, password,name);
            }
        });

    }

    private void register(String mail, String password, String name) {
        dataManager.getFbManager().getmAuth().createUserWithEmailAndPassword(mail, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String uid = dataManager.getFbManager().getmAuth().getCurrentUser().getUid();
                        DataManager.getInstance().setUserId(uid);
                        dataManager.setUserName(name);
                        dataManager.getFBDb().getReference("users")
                                .child(uid)
                                .child("name")
                                .setValue(name);
                        Toast.makeText(requireContext(), "Registration successful!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(requireContext(), MainActivity.class);
                        startActivity(intent);
                        requireActivity().finish();
                    } else {
                        Exception e = task.getException();
                        if (e != null) {
                            String message = e.getMessage();
                            if (e instanceof FirebaseAuthUserCollisionException) {
                                message = "This email is already registered";
                            }
                            Toast.makeText(requireContext(), "Registration failure", Toast.LENGTH_LONG).show();
                            this.RegistrationMTVAlert.setText("Registration failed: " + message);
                        }
                    }
                });
    }

    private boolean validateRegArgs() {
        if (!checkNotEmptyRegFields()) {
            this.RegistrationMTVAlert.setText("Please fill all fields");
            return false;
        }
        if (validator.isMailFormatValid(this.RegistrationTIETMail)) {
            this.RegistrationMTVAlert.setText("Invalid mail format");
            return false;
        }
        return true;
    }

    private boolean checkNotEmptyRegFields() {
        return validator.isInputBarValueValid(this.RegistrationTIETMail) && validator.isInputBarValueValid(this.RegistrationTIETMail)&&validator.isInputBarValueValid(this.RegistrationTIETFullName);
    }

    private void setupGotoSignInClick() {

        this.RegistrationMTVGotoLogin.setOnClickListener(v -> {
            NavController navController = NavHostFragment.findNavController(RegistrationFragment.this);
            navController.navigate(R.id.navigation_login);
        });
    }

    private void setBackgroundImage() {
        Glide.with(this)
                .load(dev.netanelbcn.locationnotes.R.drawable.logging_background)
                .centerCrop()
                .placeholder(dev.netanelbcn.locationnotes.R.drawable.ic_launcher_background)
                .into(this.RegistrationSIVBackground);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}