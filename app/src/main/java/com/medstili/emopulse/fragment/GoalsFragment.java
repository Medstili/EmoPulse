package com.medstili.emopulse.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.medstili.emopulse.activities.MainActivity;
import com.medstili.emopulse.databinding.FragmentGoalsBinding;


public class GoalsFragment extends Fragment {
    private  FragmentGoalsBinding binding;
    MainActivity mainActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentGoalsBinding.inflate(inflater, container, false);
        mainActivity = (MainActivity) getActivity();
        assert mainActivity != null;
        mainActivity.hideBottomBarWhileScrollingDown(binding.goalsScrollView);
        return binding.getRoot();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}