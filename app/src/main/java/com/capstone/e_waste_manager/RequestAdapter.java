package com.capstone.e_waste_manager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.ReqHolder> {

    Context context;
    ArrayList<RequestModel> requestModelArrayList;
    RequestInterface requestInterface;

    public RequestAdapter(Context context, ArrayList<RequestModel> requestModelArrayList, RequestInterface requestInterface) {
        this.context = context;
        this.requestModelArrayList = requestModelArrayList;
        this.requestInterface = requestInterface;
    }

    @NonNull
    @Override
    public RequestAdapter.ReqHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.requests_each, parent, false);
        return new ReqHolder(v, requestInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestAdapter.ReqHolder holder, int position) {

        RequestModel reqP = requestModelArrayList.get(position);
        holder.requestName.setText(reqP.reqName);
//        holder.requestAddress.setText(reqP.reqAddress);
        holder.requestUserMail.setText(reqP.reqUserMail);
//        holder.requestNumber.setText(reqP.reqNumber);
//        holder.requestDesc.setText(reqP.reqDesc);
//        Picasso.get().load(reqP.reqDTI).into(holder.requestDTI);
//        Picasso.get().load(reqP.reqSEC).into(holder.requestSEC);

    }

    @Override
    public int getItemCount() {
        return requestModelArrayList.size();
    }

    public class ReqHolder extends RecyclerView.ViewHolder{

        TextView requestName, requestAddress, requestNumber, requestDesc, requestUserMail;
        ImageView requestDTI, requestSEC;

        public ReqHolder(@NonNull View itemView, RequestInterface requestInterface) {
            super(itemView);
                requestName = itemView.findViewById(R.id.adReqName);
                requestUserMail = itemView.findViewById(R.id.adReqDetail);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (requestInterface != null){
                            int pos = getAdapterPosition();
                            if (pos != RecyclerView.NO_POSITION){
                                requestInterface.onItemClick(pos);
                            }
                        }
                    }
                });
        }
    }
}
