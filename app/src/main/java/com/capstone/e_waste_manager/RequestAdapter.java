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

    RequestInterface requestInterface;
    Context context;
    ArrayList<RequestModel> requestModelArrayList;

    public RequestAdapter(RequestInterface learnInterface, Context context, ArrayList<RequestModel> requestModelArrayList) {
        this.requestInterface = requestInterface;
        this.context = context;
        this.requestModelArrayList = requestModelArrayList;
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
        holder.reqName.setText(reqP.reqName);
        holder.reqAddress.setText(reqP.reqAddress);
        holder.reqUserMail.setText(reqP.reqUserMail);
        holder.reqNumber.setText(reqP.reqNumber);
        holder.reqDesc.setText(reqP.reqDesc);
        Picasso.get().load(reqP.reqDTI).into(holder.reqDTI);
        Picasso.get().load(reqP.reqSEC).into(holder.reqSEC);

    }

    @Override
    public int getItemCount() {
        return requestModelArrayList.size();
    }

    public class ReqHolder extends RecyclerView.ViewHolder{

        TextView reqName, reqAddress, reqNumber, reqDesc, reqUserMail;
        ImageView reqDTI, reqSEC;

        public ReqHolder(@NonNull View itemView, RequestInterface requestInterface) {
            super(itemView);
                reqName = itemView.findViewById(R.id.adReqName);
                reqUserMail = itemView.findViewById(R.id.adReqDetail);

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
