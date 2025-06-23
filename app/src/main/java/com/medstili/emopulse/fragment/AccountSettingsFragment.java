package com.medstili.emopulse.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.medstili.emopulse.Components.CustomDialog;
import com.medstili.emopulse.R;
import com.medstili.emopulse.activities.MainActivity;
import com.medstili.emopulse.databinding.FragmentAccountSettingsBinding;
import com.medstili.emopulse.activities.signUp;


public class AccountSettingsFragment extends Fragment {
    FragmentAccountSettingsBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAccountSettingsBinding.inflate(inflater, container, false);
//        binding.deleteBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                CustomDialog customDialog = new CustomDialog(requireActivity());
//                customDialog.showDialog(
//                        "Delete Account",
//                        "Are You Sure You Want To Delete Your Account ?",
//                        "Delete",
//                        v->{
//                            Toast.makeText(requireActivity(),"Account Deleted Successfully", Toast.LENGTH_SHORT).show();
//                            Intent intent =new Intent(requireActivity(), signUp.class);
//                        },
//                        "Cancel",
//                      null
//                );
//                customDialog.title.setTextColor(ContextCompat.getColor(requireActivity(), R.color.danger));
//            }
//        });
//        binding.changePasswordBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
//                navController.navigate(R.id .action_accountSettingsFragment_to_changePasswordFragment);
//            }
//        });
        return binding.getRoot();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}