package com.blazingapps.asus.lifesource;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class ChatAdapter extends ArrayAdapter {
    ArrayList<ChatObject> chatObjectArrayList;
    public ChatAdapter(@NonNull Context context, int resource, @NonNull ArrayList objects) {
        super(context, resource, objects);
        this.chatObjectArrayList = objects;
    }

    @Override
    public int getCount() {
        return chatObjectArrayList.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.chatitem, null);

        LinearLayout linearLayout = v.findViewById(R.id.chatitemlayout);
        TextView questionview = v.findViewById(R.id.questiontextbox);
        TextView noreply = v.findViewById(R.id.noreply);
        noreply.setVisibility(View.GONE);

        String question = chatObjectArrayList.get(position).getQuestion();
        String answer="";

        ArrayList<AnswerObject> answerObjects = chatObjectArrayList.get(position).getAnswers();
        for (int j=0; j< answerObjects.size() ;++j){
            String reply = answerObjects.get(j).getAnswer();
            String docId = answerObjects.get(j).getDocId();
            TextView answerTV = new TextView(getContext());
            answerTV.setText(reply);
            TextView docTV = new TextView(getContext());
            docTV.setGravity(Gravity.RIGHT);
            docTV.setText("Answered By,\n"+docId);
            linearLayout.addView(answerTV);
            linearLayout.addView(docTV);

            View view = new View(getContext());
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,1));
            view.setBackgroundColor(getContext().getResources().getColor(R.color.linecolor));

            if (j!=0 || j!=(answerObjects.size()-1)){
                linearLayout.addView(view);
            }
        }

        if (answerObjects.size()<1){
            answer="No Reply";
            noreply.setVisibility(View.VISIBLE);
            noreply.setText(answer);
        }

        questionview.setText(question);

        return v;
    }
}
