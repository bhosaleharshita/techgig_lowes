package com.example.mybot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ai.api.AIListener;
import ai.api.AIServiceException;
import ai.api.RequestExtras;
import ai.api.android.AIConfiguration;
import ai.api.android.AIDataService;
import ai.api.android.AIService;
import ai.api.android.GsonFactory;
import ai.api.model.AIContext;
import ai.api.model.AIError;
import ai.api.model.AIEvent;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import ai.api.model.Metadata;
import ai.api.model.ResponseMessage;
import ai.api.model.Result;
import ai.api.model.Status;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class ChatAppActivity extends AppCompatActivity implements AIListener {

    private Gson gson = GsonFactory.getGson();
    private AIDataService aiDataService;
    AIService aiService;
    ChatAppMsgAdapter chatAppMsgAdapter;
    RecyclerView msgRecyclerView;
    public EditText msgInputText;
    RelativeLayout audiobt;

   static String temp;
    final List<ChatAppMsgDTO> msgDtoList = new ArrayList<ChatAppMsgDTO>();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_app);

        // setTitle("dev2qa.com - Android Chat App Example");


       final LinearLayout leftlayout=(LinearLayout) findViewById(R.id.chat_left_msg_layout);
      final TextView lefttext=(TextView) findViewById(R.id.chat_left_msg_text_view);

    audiobt=findViewById(R.id.audiobt);

        final LanguageConfig config2 = new LanguageConfig("ja", "b4ff2ed344f844a39579644d0754fa07");
        //initService(config2);5
        final AIConfiguration config = new AIConfiguration("b4ff2ed344f844a39579644d0754fa07",
                AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);
        aiDataService = new AIDataService(this, config);



      //  final AIConfiguration configa = new AIConfiguration("3e1fd339992c430c98f30c2ce64400f3",
        //        AIConfiguration.SupportedLanguages.English,
        //        AIConfiguration.RecognitionEngine.System);

        aiService = AIService.getService(this, (AIConfiguration) config);
        aiService.setListener(this);


        // Get RecyclerView object.
        msgRecyclerView= (RecyclerView) findViewById(R.id.chat_recycler_view);

        // Set RecyclerView layout manager.
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        msgRecyclerView.setLayoutManager(linearLayoutManager);

        // Create the initial data list.

        ChatAppMsgDTO msgDto = new ChatAppMsgDTO(ChatAppMsgDTO.MSG_TYPE_RECEIVED, "hello");
        msgDtoList.add(msgDto);

        // Create the data adapter with above data list.
        chatAppMsgAdapter = new ChatAppMsgAdapter(msgDtoList);

        // Set data adapter to RecyclerView.
        msgRecyclerView.setAdapter(chatAppMsgAdapter);

         msgInputText = (EditText) findViewById(R.id.chat_input_msg);

        ImageView msgSendButton = (ImageView) findViewById(R.id.chat_send_msg);


        msgSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String eventString = null;
                final String contextString = null;
                String msgContent = msgInputText.getText().toString();
                if(temp!=null)
                {
                    msgContent=temp;
                    temp=null;
                }
                if (!TextUtils.isEmpty(msgContent)) {
                    // Add a new sent message to the list.
                    new AiTask().execute(msgContent, eventString, contextString);


                    ChatAppMsgDTO msgDto = new ChatAppMsgDTO(ChatAppMsgDTO.MSG_TYPE_SENT, msgContent);
                    msgDtoList.add(msgDto);

                    int newMsgPosition = msgDtoList.size() - 1;

                    // Notify recycler view insert one new data.
                    chatAppMsgAdapter.notifyItemInserted(newMsgPosition);

                    // Scroll RecyclerView to the last message.
                    msgRecyclerView.scrollToPosition(newMsgPosition);

                    // Empty the input edit text box.
                    msgInputText.setText("");
                }
            }
        });




    }




    public static void confirmorder(String st)
    {

        Log.i("in confirm",st);
        temp=st;



    }


    public class AiTask extends AsyncTask<String, Void, AIResponse> {

        private AIError aiError;

        @Override
        protected AIResponse doInBackground(final String... params) {

            final AIRequest request = new AIRequest();
            String query = params[0];
            String event = params[1];
            String context = params[2];

            //Toast.makeText(getApplicationContext(), "in aitask" , Toast.LENGTH_LONG).show();

            if (!TextUtils.isEmpty(query)) {
                request.setQuery(query);
            }

            if (!TextUtils.isEmpty(event)) {
                request.setEvent(new AIEvent(event));
            }

            RequestExtras requestExtras = null;
            if (!TextUtils.isEmpty(context)) {
                final List<AIContext> contexts = Collections.singletonList(new AIContext(context));
                requestExtras = new RequestExtras(contexts, null);
            }

            try {
                return aiDataService.request(request, requestExtras);
            } catch (final AIServiceException e) {
                //Toast.makeText(getApplicationContext(), "in airask error" , Toast.LENGTH_LONG).show();
                aiError = new AIError(e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(final AIResponse response) {
            if (response != null) {
                onResult(response);
            } else {
                //Toast.makeText(getApplicationContext(), "before eonresult()" , Toast.LENGTH_LONG).show();
                onError(aiError);
            }
        }
    }

    public void onResult(final AIResponse response) {
        //runOnUiThread(new Runnable() {

        // @Override
        //public void run() {


        Result result1 = response.getResult();

        if (!TextUtils.isEmpty(result1.getResolvedQuery())) {
            // Add a new sent message to the list.
           // new AiTask().execute(result1.getResolvedQuery(), eventString, contextString);
            String msgContent=result1.getResolvedQuery();

            ChatAppMsgDTO msgDto = new ChatAppMsgDTO(ChatAppMsgDTO.MSG_TYPE_SENT, msgContent);
            msgDtoList.add(msgDto);

            int newMsgPosition = msgDtoList.size() - 1;

            // Notify recycler view insert one new data.
            chatAppMsgAdapter.notifyItemInserted(newMsgPosition);

            // Scroll RecyclerView to the last message.
            msgRecyclerView.scrollToPosition(newMsgPosition);

            // Empty the input edit text box.
            msgInputText.setText("");
        }







        // Variables*/

        Log.i(TAG, "Speech------------------------------:" + response.toString());
        //Toast.makeText(getApplicationContext(), "before gson" , Toast.LENGTH_LONG).show();
        gson.toJson(response);

        //Toast.makeText(getApplicationContext(), "before result" , Toast.LENGTH_LONG).show();

        final ai.api.model.Status status = response.getStatus();
        final Result result = response.getResult();
        String speech = result.getFulfillment().getSpeech();

        //Toast.makeText(getApplicationContext(), "after result" , Toast.LENGTH_LONG).show();

        final Metadata metadata = result.getMetadata();
        final HashMap<String, JsonElement> params = result.getParameters();

        String s = new String();
        s = "";
        int messageCount = result.getFulfillment().getMessages().size();
        if (messageCount > 1) {
            for (int i = 0; i < messageCount; i++) {
                ResponseMessage.ResponseSpeech responseMessage = (ResponseMessage.ResponseSpeech) result
                        .getFulfillment().getMessages().get(i);
                Log.e(TAG, "responseMessage: " + responseMessage.getSpeech());
                ChatAppMsgDTO msgDto = new ChatAppMsgDTO(ChatAppMsgDTO.MSG_TYPE_RECEIVED,responseMessage.getSpeech().get(0) );
                msgDtoList.add(msgDto);
                //Toast.makeText(getApplicationContext(), responseMessage.getSpeech().get(0), Toast.LENGTH_LONG).show();
                //s = s + "\n\n" + responseMessage.getSpeech().get(0);
            }
            //speech = s;
        }

        // Logging
        Log.d(TAG, "onResult");
        Log.i(TAG, "Received success response");
        Log.i(TAG, "Status code: " + status.getCode());
        Log.i(TAG, "Status type: " + status.getErrorType());
        Log.i(TAG, "Resolved query: " + result.getResolvedQuery());
        Log.i(TAG, "Action: " + result.getAction());
        Log.i(TAG, "Speech: " + speech);

        if (metadata != null) {
            Log.i(TAG, "Intent id: " + metadata.getIntentId());
            Log.i(TAG, "Intent name: " + metadata.getIntentName());
        }

        if (params != null && !params.isEmpty()) {
            Log.i(TAG, "Parameters: ");
            for (final Map.Entry<String, JsonElement> entry : params.entrySet()) {
                Log.i(TAG, String.format("%s: %s", entry.getKey(), entry.getValue().toString()));
            }
        }

        //Update view to bot says

        ChatAppMsgDTO msgDto = new ChatAppMsgDTO(ChatAppMsgDTO.MSG_TYPE_RECEIVED, speech);
        msgDtoList.add(msgDto);
        int newMsgPosition = msgDtoList.size() - 1;

        // Notify recycler view insert one new data.
        chatAppMsgAdapter.notifyItemInserted(newMsgPosition);

        // Scroll RecyclerView to the last message.
        msgRecyclerView.scrollToPosition(newMsgPosition);


        //}
        //});
    }

    public void onError(final AIError error) {
        //runOnUiThread(new Runnable() {
        // @Override
        //public void run() {
        Toast.makeText(getApplicationContext(), "error on_error_________", Toast.LENGTH_LONG).show();
        Log.e(TAG, error.toString());
        //}
        //});
    }

    public void buttonClicked(View view) {
        aiService.startListening();
    }

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