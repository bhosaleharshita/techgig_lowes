package com.example.mybot;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import ai.api.AIServiceException;
import ai.api.android.AIConfiguration;
import ai.api.AIListener;
import ai.api.android.AIService;
import ai.api.model.AIError;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import ai.api.model.ResponseMessage;
import ai.api.model.Result;
import ai.api.AIDataService;
import ai.api.AIListener;

public class MainActivity extends AppCompatActivity implements AIListener {

    private static final String TAG = "";
    AIService aiService;
    TextView t, t2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        t = findViewById(R.id.textView);
        //t2 = findViewById(R.id.textView2);
        //int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        //if (permission != PackageManager.PERMISSION_GRANTED) {
        // makeRequest();
        //}

        final AIConfiguration config = new AIConfiguration("b4ff2ed344f844a39579644d0754fa07",
                AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);

        aiService = AIService.getService(this, (ai.api.android.AIConfiguration) config);
        aiService.setListener(this);
    }
    //protected void makeRequest() {
    //ActivityCompat.requestPermissions(this,
    // new String[]{Manifest.permission.RECORD_AUDIO},101);}

    // Listener
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 101: {
                if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                } else {
                }
                return;
            }
        }
    }

    public void buttonClicked(View view) {
        aiService.startListening();
    }

    @Override
    public void onResult(AIResponse result) {
        Log.d("anu", result.toString());
        Result result1 = result.getResult();
        //String h=AIResponse.getFulfillment().getSpeech();

        String speech = result1.getFulfillment().getSpeech();
        //Log.i(TAG, "Speech: -------------------------------------------------------------------------------------------" );
        //Log.i(TAG, "Speech: " +vi );

        //t.setText(result.toString());
        //t.setText(result1.toString());
        /*t.setText(" Result "+result1.toString()+
                "\n Query " + result1.getResolvedQuery() +
                "\n Parameters: " + result1.getParameters()+
                "\n Metadata: "+result1.getMetadata()+
                "\n Context: "+result1.getContexts()+
                "\n Class "+result1.getClass()+
                "\n Fulfillment context "+result1.getFulfillment().getContextOut()+
                "\n Fulfillment Speech "+result1.getFulfillment().getSpeech()+
                "\n Fulfillment Data "+result1.getFulfillment().getData()+
                "\n Fulfillment msg "+result1.getFulfillment().getMessages().toString()+
                "\n Fulfillment context "+result1.getFulfillment().getMessages()+
                "\n Fulfillment display text "+result1.getFulfillment().getDisplayText()+
                "\n Fulfillment fullfill "+result1.getFulfillment().toString());*/

        String s=new String();
        s="";
        int messageCount = result1.getFulfillment().getMessages().size();
        if (messageCount > 1) {
            for (int i = 0; i < messageCount; i++) {
                ResponseMessage.ResponseSpeech responseMessage = (ResponseMessage.ResponseSpeech) result1.getFulfillment().getMessages().get(i);
                Log.e(TAG, "responseMessage: " + responseMessage.getSpeech());
                Toast.makeText(getApplicationContext(), responseMessage.getSpeech().get(0), Toast.LENGTH_LONG).show();
                s=s+"\n\n"+responseMessage.getSpeech().get(0);
            }
            speech="";
            speech=s;
        }


        t.setText(" Result "+result1.toString()+ "Response: \n"+speech);



    }

    @Override
    public void onError(AIError error) {
    }

    @Override
    public void onAudioLevel(float level) {
    }

    @Override
    public void onListeningStarted() {
    }

    @Override
    public void onListeningCanceled() {
    }

    @Override
    public void onListeningFinished() {
    }

}


    /*


        RecyclerView recyclerView;
        EditText editText;
        RelativeLayout addBtn;
        DatabaseReference ref;
        Boolean flagFab = true;

        private AIService aiService;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},1);

            recyclerView = findViewById(R.id.recyclerView);
            editText = findViewById(R.id.editText);
            addBtn = findViewById(R.id.addBtn);


            final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            linearLayoutManager.setStackFromEnd(true);
            recyclerView.setLayoutManager(linearLayoutManager);

            final Query query=ref.child("chat").limitToLast(50);
            query.keepSynced(true);

            ref = FirebaseDatabase.getInstance().getReference();
            ref.keepSynced(true);

            final AIConfiguration config = new AIConfiguration("b4ff2ed344f844a39579644d0754fa07",
                    AIConfiguration.SupportedLanguages.English,
                    AIConfiguration.RecognitionEngine.System);

            aiService = AIService.getService(this, config);
            aiService.setListener(this);
            final AIDataService aiDataService = new AIDataService(config);
            final AIRequest aiRequest = new AIRequest();

            addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String message = editText.getText().toString().trim();
                    if (!message.equals("")) {
                        ChatMessage chatMessage = new ChatMessage(message, "user");
                        ref.child("chat").push().setValue(chatMessage);
                        aiRequest.setQuery(message);
                        new AsyncTask<AIRequest,Void,AIResponse>(){

                            @Override
                            protected AIResponse doInBackground(AIRequest... aiRequests) {
                                final AIRequest request = aiRequests[0];
                                try {
                                    final AIResponse response = aiDataService.request(aiRequest);
                                    return response;
                                } catch (AIServiceException e) {
                                }
                                return null;
                            }
                            @Override
                            protected void onPostExecute(AIResponse response) {
                                if (response != null) {

                                    Result result = response.getResult();
                                    String reply = result.getFulfillment().getSpeech();
                                    ChatMessage chatMessage = new ChatMessage(reply, "bot");
                                    ref.child("chat").push().setValue(chatMessage);
                                }
                            }
                        }.execute(aiRequest);
                    }
                    else {
                        aiService.startListening();
                    }
                    editText.setText("");
                }
            });



            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    ImageView fab_img = (ImageView)findViewById(R.id.fab_img);
                    Bitmap img = BitmapFactory.decodeResource(getResources(),R.drawable.ic_send_white_24dp);
                    Bitmap img1 = BitmapFactory.decodeResource(getResources(),R.drawable.ic_mic_white_24dp);

                    if (s.toString().trim().length()!=0 && flagFab){
                        ImageViewAnimatedChange(MainActivity.this,fab_img,img);
                        flagFab=false;
                    }
                    else if (s.toString().trim().length()==0){
                        ImageViewAnimatedChange(MainActivity.this,fab_img,img1);
                        flagFab=true;
                    }
                }
                @Override
                public void afterTextChanged(Editable s) {
                }
            });


            FirebaseRecyclerOptions<ChatMessage> options = new FirebaseRecyclerOptions.Builder<ChatMessage>().setQuery(query,ChatMessage.class).build();
            final FirebaseRecyclerAdapter<ChatMessage, chat_rec> adapter = new FirebaseRecyclerAdapter<ChatMessage, chat_rec>(options) {
                @NonNull
                @Override
                public chat_rec onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.msglist,parent,false);
                    return new chat_rec(view);
                }

                @Override
                protected void onBindViewHolder(@NonNull chat_rec holder, int position, ChatMessage model) {
                    if (model.getMsgUser().equals("user")) {
                        holder.rightText.setText(model.getMsgText());
                        holder.rightText.setVisibility(View.VISIBLE);
                        holder.leftText.setVisibility(View.GONE);
                    }
                    else {
                        holder.leftText.setText(model.getMsgText());
                        holder.rightText.setVisibility(View.GONE);
                        holder.leftText.setVisibility(View.VISIBLE);
                    }
                }
            };

            adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onItemRangeInserted(int positionStart, int itemCount) {
                    super.onItemRangeInserted(positionStart, itemCount);
                    int msgCount = adapter.getItemCount();
                    int lastVisiblePosition = linearLayoutManager.findLastCompletelyVisibleItemPosition();

                    if (lastVisiblePosition == -1 ||
                            (positionStart >= (msgCount - 1) &&
                                    lastVisiblePosition == (positionStart - 1))) { recyclerView.scrollToPosition(positionStart);
                    } }
            });
            recyclerView.setAdapter(adapter);

        }//onCreate ends here

    public void ImageViewAnimatedChange(Context c, final ImageView v, final Bitmap new_image) {
            final Animation anim_out = AnimationUtils.loadAnimation(c, R.anim.zoom_out);
            final Animation anim_in  = AnimationUtils.loadAnimation(c, R.anim.zoom_in);
            anim_out.setAnimationListener(new Animation.AnimationListener()
            {
                @Override public void onAnimationStart(Animation animation) {}
                @Override public void onAnimationRepeat(Animation animation) {}
                @Override public void onAnimationEnd(Animation animation)
                {
                    v.setImageBitmap(new_image);
                    anim_in.setAnimationListener(new Animation.AnimationListener() {
                        @Override public void onAnimationStart(Animation animation) {}
                        @Override public void onAnimationRepeat(Animation animation) {}
                        @Override public void onAnimationEnd(Animation animation) {}
                    });
                    v.startAnimation(anim_in);
                }
            });
            v.startAnimation(anim_out);
        }


        @Override
        public void onResult(ai.api.model.AIResponse response) {
            Result result = response.getResult();
            String message = result.getResolvedQuery();
            ChatMessage chatMessage0 = new ChatMessage(message, "user");
            ref.child("chat").push().setValue(chatMessage0);

            String reply = result.getFulfillment().getSpeech();
            ChatMessage chatMessage = new ChatMessage(reply, "bot");
            ref.child("chat").push().setValue(chatMessage);
        }

        @Override
        public void onError(ai.api.model.AIError error) {
        }

        @Override
        public void onAudioLevel(float level) {
        }

        @Override
        public void onListeningStarted() {
        }

        @Override
        public void onListeningCanceled() {
        }

        @Override
        public void onListeningFinished() {
        }
    }



*/

