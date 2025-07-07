package com.medstili.emopulse.Components;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.material.button.MaterialButton;
import com.medstili.emopulse.R;

import java.util.Objects;


public class CustomDialog{

   private final Context context;
   public Dialog dialog;
   public MaterialButton leftBtn, rightBtn;
   public TextView title, message ;
   public ImageView dialogImg ;
   public EditText editTextPassword, editTextEmail;


    public CustomDialog(Context context) {
        this.context=context;
    }

    public void showDialog(String title, String message,  String rightBtnText, View.OnClickListener rightBtnListener,String leftBtnText, View.OnClickListener leftBtnListener){
//configure the dialog
        this.dialog = new Dialog(context);
        this.dialog.setContentView(R.layout.custom_dialog);
        Objects.requireNonNull(this.dialog.getWindow()).setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
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


    }

    public void showPasswordDialog(String Message, String RightBtnText, View.OnClickListener RightBtnListener, String LeftBtnText, View.OnClickListener LeftBtnListener){
        this.dialog = new Dialog(context);
        this.dialog.setContentView(R.layout.re_auth_user_dialog);

        Objects.requireNonNull(this.dialog.getWindow()).setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        this.dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        this.message=dialog.findViewById(R.id.dialogMessage);
        this.leftBtn=dialog.findViewById(R.id.leftBtn);
        this.rightBtn=dialog.findViewById(R.id.rightBtn);
        this.editTextPassword= dialog.findViewById(R.id.editTextPassword);


        this.message.setText(Message);
        this.leftBtn.setText(LeftBtnText);
        this.rightBtn.setText(RightBtnText);


        if(LeftBtnText!=null){

            this.leftBtn.setVisibility(View.VISIBLE);
            this.leftBtn.setOnClickListener(v->{

                if(LeftBtnListener!=null){
                    LeftBtnListener.onClick(v);
                }
                this.dialog.dismiss();
            });
        }
        else{
            this.leftBtn.setVisibility(View.GONE);
            this.leftBtn.setOnClickListener(v-> this.dialog.dismiss());
        }

        this.rightBtn.setOnClickListener(v->{
            if(RightBtnListener!=null){

                RightBtnListener.onClick(v);
            }
            this.dialog.dismiss();
            });

        this.dialog.show();
    }

    public void showEmailDialog( String message, String rightBtnText, View.OnClickListener rightBtnListener, String leftBtnText, View.OnClickListener leftBtnListener) {
        this.dialog = new Dialog(context);
        this.dialog.setContentView(R.layout.email_dialog);

        Objects.requireNonNull(this.dialog.getWindow()).setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        this.message = dialog.findViewById(R.id.dialogMessage);
        this.leftBtn = dialog.findViewById(R.id.leftBtn);
        this.rightBtn = dialog.findViewById(R.id.rightBtn);
        this.editTextEmail = dialog.findViewById(R.id.editTextEmail);

        this.message.setText(message);
        this.leftBtn.setText(leftBtnText);
        this.rightBtn.setText(rightBtnText);
        if (leftBtnText != null) {
            this.leftBtn.setVisibility(View.VISIBLE);
            this.leftBtn.setOnClickListener(v -> {
                if (leftBtnListener != null) {
                    leftBtnListener.onClick(v);
                }
                this.dialog.dismiss();
            });
        } else {
            this.leftBtn.setVisibility(View.GONE);
        }

        this.rightBtn.setOnClickListener(v -> {
            if (rightBtnListener != null) {
                rightBtnListener.onClick(v);
            }
            this.dialog.dismiss();
        });

        this.dialog.show();

    }
}
