package com.mirza.texttospeechmsg;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Mirza Ahmed Baig on 10/04/19.
 * Avantari Technologies
 * mirza@avantari.org
 */
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    public List<SMSData> data;
    private MessageTranslateListener messageTranslateListener;

    public MessageAdapter(List<SMSData> data, MessageTranslateListener messageTranslateListener) {
        this.data = data;
        this.messageTranslateListener = messageTranslateListener;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.message_item, viewGroup, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder messageViewHolder, int i) {
        messageViewHolder.messageAddress.setText(data.get(i).getAddress());
        messageViewHolder.messageDate.setText(getCurrentTimeString(data.get(i).getTime()));
        messageViewHolder.messageBody.setText(data.get(i).getMsg());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageAddress;
        TextView messageDate;
        TextView messageBody;
        ImageButton speechButton;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageAddress = itemView.findViewById(R.id.sender_adderess);
            messageDate = itemView.findViewById(R.id.date);
            messageBody = itemView.findViewById(R.id.message_body);
            speechButton = itemView.findViewById(R.id.speech_button);

            speechButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    messageTranslateListener.onMessageTranslateRequest(getAdapterPosition());
                }
            });


        }

    }

    private String getCurrentTimeString(Long timestamp) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date netDate = new Date(timestamp);
            return sdf.format(netDate);
        } catch (Exception e) {
            e.toString();
            return "error";
        }
    }
}
