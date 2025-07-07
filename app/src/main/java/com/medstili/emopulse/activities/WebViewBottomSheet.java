package com.medstili.emopulse.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.medstili.emopulse.R;

public class WebViewBottomSheet extends BottomSheetDialogFragment {

    public static final String ARG_URL="url";

    public static  WebViewBottomSheet newInstance(String url){
        WebViewBottomSheet fragment = new WebViewBottomSheet();
        Bundle args = new Bundle();
        args.putString(ARG_URL, url);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         View view = inflater.inflate(R.layout.bottom_sheet_webview,container, false);


         WebView webview= view.findViewById(R.id.webview);
         String url = getArguments() != null ? getArguments().getString(ARG_URL):null;
          if(url !=null){
              webview.setWebViewClient(new WebViewClient());
              webview.getSettings().setJavaScriptEnabled(true);
              webview.loadUrl(url);
          }
        // adjusting the height of teh bottom sheet view
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            // Get the parent layout params to modify the height of the BottomSheet
            ViewGroup.LayoutParams layoutParams = parent.getLayoutParams();
            if (layoutParams != null) {
                // Set the height of the BottomSheet to 90% of the screen height
                layoutParams.height = (int) (getResources().getDisplayMetrics().heightPixels * 0.9); // 90% of screen height
                parent.setLayoutParams(layoutParams);
            }
        }
        // Set a custom touch listener to handle scrollable content inside the WebView
        webview.setOnTouchListener((v, event) -> {
            // Check if the WebView is at the top and prevent BottomSheet drag
            if (webview.getScrollY() == 0) {
                return false; // Let the BottomSheet handle this touch if WebView is at the top
            }

            // Handle scrollable content inside the WebView (prevent dragging BottomSheet)
            if (event.getAction() == MotionEvent.ACTION_MOVE) {
                return isScrollableArea(event); // If in scrollable area inside WebView, let the WebView handle the scroll
            }
            return false; // Otherwise, allow the BottomSheet to drag
        });

         return view;

    }

    // Method to check if the touch is on a scrollable area inside the WebView
    private boolean isScrollableArea(MotionEvent event) {
        // You can add more refined checks here depending on your WebView's content (e.g., language dropdown)
        return event.getY() > 0; // Ensure it's a scrollable area (modify based on WebView content)
    }
}
