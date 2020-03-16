package com.example.mybot;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class ChatAppMsgViewHolder extends RecyclerView.ViewHolder {

    LinearLayout leftMsgLayout;

    LinearLayout rightMsgLayout;

    TextView leftMsgTextView;

    TextView rightMsgTextView;
    EditText ed;
    AlertDialog.Builder builder;

    public ChatAppMsgViewHolder(final View itemView) {
        super(itemView);

        if(itemView!=null) {
            leftMsgLayout = (LinearLayout) itemView.findViewById(R.id.chat_left_msg_layout);
            rightMsgLayout = (LinearLayout) itemView.findViewById(R.id.chat_right_msg_layout);
            leftMsgTextView = (TextView) itemView.findViewById(R.id.chat_left_msg_text_view);
            rightMsgTextView = (TextView) itemView.findViewById(R.id.chat_right_msg_text_view);
            ed=(EditText)itemView.findViewById(R.id.chat_input_msg);
            builder = new AlertDialog.Builder(itemView.getContext());

            leftMsgLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final String responsestring = null;
                    if(leftMsgTextView.getText()!=null) {
                        Toast.makeText(itemView.getContext(),leftMsgTextView.getText() , Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "on click left layout: " + leftMsgTextView.getText());

                        builder.setMessage("Do you want to order this product ?");
                        builder.setCancelable(false);
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                ChatAppActivity.confirmorder((String) leftMsgTextView.getText());

                              //  ed.setText(leftMsgTextView.getText().toString());
                            }
                        });
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //  Action for 'NO' Button
                                dialog.cancel();

                            }
                        });

                        AlertDialog alert = builder.create();
                        //Setting the title manually
                        alert.setTitle("Confirm order..");
                        alert.show();



                    }



                }
            });


        }
    }
}