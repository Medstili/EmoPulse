package com.medstili.emopulse.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.medstili.emopulse.activities.MainActivity;
import com.medstili.emopulse.databinding.FragmentFaqsBinding;

public class FaqsFragment extends Fragment {
    private FragmentFaqsBinding binding;
    MainActivity mainActivity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentFaqsBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }
}