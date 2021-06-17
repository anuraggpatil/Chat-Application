package com.example.chatapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.network.ListNetworkRequest;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    String receiverImage, receiverUID, receiverName;
    String senderUID;
    CircleImageView profileImg;
    TextView receivername;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;
    public static String senderImg;
    public static String receiverImg;

    String senderRoom="", receiverRoom="";

    RecyclerView messageAdapter;

    CardView sendBtn;
    EditText chatMsg;

    ArrayList<Messages> messagesArrayList;

    MessageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        receiverImage = getIntent().getStringExtra("ReceiverImage");
        receiverName = getIntent().getStringExtra("name");
        receiverUID = getIntent().getStringExtra("UID");

        profileImg = findViewById(R.id.profile_image);

        Picasso.get().load(receiverImage).into(profileImg);
        receivername = findViewById(R.id.receiverName);
        receivername.setText("" + receiverName);

        sendBtn = findViewById(R.id.sendBtn);
        chatMsg = findViewById(R.id.chatMessage);


        senderUID = firebaseAuth.getUid();
        senderRoom = senderUID + receiverUID;
        receiverRoom = receiverUID + senderUID;

        messageAdapter = findViewById(R.id.messageAdapter);
        messagesArrayList = new ArrayList<Messages>();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        adapter = new MessageAdapter(ChatActivity.this, messagesArrayList);
        messageAdapter.setLayoutManager(linearLayoutManager);

        messageAdapter.setAdapter(adapter);

        DatabaseReference reference = firebaseDatabase.getReference().child("user").child(firebaseAuth.getUid());
        DatabaseReference chatReference = firebaseDatabase.getReference().child("chats").child(senderRoom).child("messages");

        chatReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messagesArrayList.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    Messages messages = dataSnapshot.getValue(Messages.class);
                    messagesArrayList.add(messages);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                senderImg = snapshot.child("imageURI").getValue().toString();
                receiverImg = receiverImage;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String chat = chatMsg.getText().toString();
                chat.trim();
                if(chat.isEmpty()){
                    Toast.makeText(ChatActivity.this, "Please Enter some Message", Toast.LENGTH_SHORT).show();
                    return;
                }
                chatMsg.setText("");
                Date date = new Date();
                Messages m = new Messages(chat, senderUID, date.getTime());


                firebaseDatabase.getReference().
                        child("chats").
                        child(senderRoom).
                        child("messages").
                        push().setValue(m).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                            firebaseDatabase.getReference().
                                    child("chats").
                                    child(receiverRoom).
                                    child("messages").
                                    push().
                                    setValue(m).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                            }
                        });
                    }
                });

            }
        });



    }
}