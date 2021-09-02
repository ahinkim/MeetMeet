package org.techtown.meetmeetapplication;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import org.techtown.meetmeetapp.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static androidx.core.content.ContextCompat.startActivity;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> implements OnNoteItemClickListener{

    ArrayList<NoteItem> items=new ArrayList<NoteItem>();

    OnNoteItemClickListener listener;
    AdapterView.OnItemLongClickListener longListener;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(viewGroup.getContext());
        View itemView=inflater.inflate(R.layout.note_item,viewGroup,false);
        return new ViewHolder(itemView,this);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteAdapter.ViewHolder viewHolder, int position) {
        NoteItem item=items.get(position);
        viewHolder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItem(NoteItem item){
        items.add(item);
    }

    public void setItems(ArrayList<NoteItem> items){
        this.items=items;
    }

    public NoteItem getItem(int position){
        return items.get(position);
    }
    public void setOnItemClickListener(OnNoteItemClickListener listener){
        this.listener=listener;
    }
    @Override
    public void onItemClick(ViewHolder holder, View view, int position) {
        if(listener!=null){
            listener.onItemClick(holder,view,position);
        }
    }

    public void setOnItemLongClickListener(AdapterView.OnItemLongClickListener longListener){
        this.longListener=longListener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView dateTextView;
        TextView contentsTextView;

        public ViewHolder(@NonNull View itemView, final OnNoteItemClickListener listener) {
            super(itemView);
            dateTextView=itemView.findViewById(R.id.dateTextView);
            contentsTextView=itemView.findViewById(R.id.contentsTextView);
            itemView.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View view) {
                    int position=getAdapterPosition();
                    if(listener!=null){
                        listener.onItemClick(ViewHolder.this,view,position);
                    }
                }
            });
        }
        public void setItem(NoteItem item){
            String getDate=item.getCreateDateStr();
            String date=getDate.substring(0,10);
            dateTextView.setText(date);
            //dateTextView.setText(item.getCreateDateStr());
            contentsTextView.setText(item.getContents());
        }
    }
}
