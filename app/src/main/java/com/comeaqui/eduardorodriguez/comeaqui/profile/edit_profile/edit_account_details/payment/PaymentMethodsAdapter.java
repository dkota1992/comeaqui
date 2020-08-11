package com.comeaqui.eduardorodriguez.comeaqui.profile.edit_profile.edit_account_details.payment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.comeaqui.eduardorodriguez.comeaqui.R;
import com.comeaqui.eduardorodriguez.comeaqui.objects.PaymentMethodObject;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class PaymentMethodsAdapter extends RecyclerView.Adapter<PaymentMethodsAdapter.ViewHolder>{

    private List<PaymentMethodObject> listPaymentMethods;
    private PaymentMethodsActivity activity;
    StorageReference firebaseStorage;

    public PaymentMethodsAdapter(PaymentMethodsActivity c, ArrayList<PaymentMethodObject> paymentObjects) {
        this.activity = c;
        listPaymentMethods = paymentObjects;
    }

    public void addPaymentMethod(PaymentMethodObject m){
        listPaymentMethods.add(m);
        notifyItemInserted(listPaymentMethods.size());
    }

    @Override
    public PaymentMethodsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.payment_method_element, parent, false);
        firebaseStorage = FirebaseStorage.getInstance().getReference();
        return new PaymentMethodsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PaymentMethodsAdapter.ViewHolder holder, int position) {
        holder.mItem = listPaymentMethods.get(position);
        holder.paymentInfo.setText("Ending " + holder.mItem.last4.substring(holder.mItem.last4.length() - 4));
        if (holder.mItem.chosen){
            holder.chosenImage.setVisibility(View.VISIBLE);
        } else {
            holder.chosenImage.setVisibility(View.GONE);
        }
        holder.mView.setOnClickListener(v -> {
            activity.onPaymentMethodClicked(holder.mItem);
        });

        holder.paymentType.setText((holder.mItem.brand == null) ? "Card" : holder.mItem.brand);
        holder.imageView.setImageDrawable(ContextCompat.getDrawable(holder.mView.getContext(), holder.mItem.brandImage));
    }

    @Override
    public int getItemCount() {
        return listPaymentMethods != null ? listPaymentMethods.size(): 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView paymentType;
        public final TextView paymentInfo;
        public final ImageView imageView;
        public final ImageView chosenImage;
        public PaymentMethodObject mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            paymentType = view.findViewById(R.id.payment_type);
            paymentInfo = view.findViewById(R.id.payment_info);
            imageView = view.findViewById(R.id.image);
            chosenImage = view.findViewById(R.id.is_chosen);
        }
    }
}