package com.medstili.emopulse.Components;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.material.button.MaterialButton;
import com.medstili.emopulse.R;


public class CustomDialog{

   private final Context context;
   public Dialog dialog;
   public MaterialButton leftBtn, rightBtn;
   public TextView title, message ;
   public ImageView dialogImg ;


    public CustomDialog(Context context) {
        this.context=context;
    }

    public void showDialog(String title, String message,  String rightBtnText, View.OnClickListener rightBtnListener,String leftBtnText, View.OnClickListener leftBtnListener){
//configure the dialog
        this.dialog = new Dialog(context);
        this.dialog.setContentView(R.layout.custom_dialog);
        this.dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        this.dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
// set the dialog views
        this.title=dialog.findViewById(R.id.dialogTitle);
        this.message=dialog.findViewById(R.id.dialogMessage);
        this.leftBtn=dialog.findViewById(R.id.leftBtn);
        this.rightBtn=dialog.findViewById(R.id.rightBtn);
        this.dialogImg = dialog.findViewById(R.id.dialogImage);
// set the dialog text
        this.title.setText(title);
        this.message.setText(message);
        this.leftBtn.setText(leftBtnText);
        this.rightBtn.setText(rightBtnText);

        if(leftBtnText!=null){
            this.leftBtn.setVisibility(View.VISIBLE);
            this.leftBtn.setOnClickListener(v->{
                if(leftBtnListener!=null){
                    leftBtnListener.onClick(v);
                }
                this.dialog.dismiss();
            });
        }
        else{
            this.leftBtn.setVisibility(View.GONE);
        }
//handle right button click listener
        this.rightBtn.setOnClickListener(v->{
            if(rightBtnListener!=null){
                rightBtnListener.onClick(v);
            }
            this.dialog.dismiss();
        });


        this.dialog.show();


    };
}
