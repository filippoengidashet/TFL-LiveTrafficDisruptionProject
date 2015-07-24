package tae.co.uk.ltd.mvp.model.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import tae.co.uk.ltd.R;
import tae.co.uk.ltd.mvp.model.constants.Constants;
import tae.co.uk.ltd.mvp.model.pojo.Disruption;
import tae.co.uk.ltd.mvp.model.pojo.Point;
import tae.co.uk.ltd.mvp.view.activity.SingleMarkerMapActivity;

public class DisruptionAdapter extends RecyclerView.Adapter<DisruptionAdapter.Holder> implements Filterable {

    private Context mContext;
    private List<Disruption> mDisruptionList = new ArrayList<>();
    private List<Disruption> mList = new ArrayList<>();

    public DisruptionAdapter(Context context, List<Disruption> disruptionList) {
        mContext = context;
        mDisruptionList = disruptionList;
        mList = disruptionList;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View row = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_row, null);
        return new Holder(row);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {

        final Disruption disruption = mDisruptionList.get(position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, SingleMarkerMapActivity.class);
                intent.putExtra(Constants.DISRUPTION_EXTRA, disruption);
                mContext.startActivity(intent);
            }
        });

        holder.mId.setText(disruption.mId);
        holder.mLocation.setText(disruption.mLocation);
        holder.mStatus.setText(disruption.mStatus);
        holder.mSeverity.setText(disruption.mSeverity);
        holder.mFrom.setText(disruption.mStartTime);
        holder.mTo.setText(disruption.mRemarkTime);
        holder.mLastUpdated.setText(disruption.mLastModifiedTime);
        holder.mCategory.setText(disruption.mCategory);
        holder.mSubCategory.setText(disruption.mSubCategory);
        holder.mComments.setText(disruption.mComments);

        Point point = disruption.mPoint;
    }

    @Override
    public int getItemCount() {
        return mDisruptionList.size();
    }

    public static class Holder extends RecyclerView.ViewHolder {

        public TextView mLocation, mId, mStatus, mSeverity, mFrom, mTo, mLastUpdated, mLevelOfInterest, mCategory, mSubCategory, mComments;

        public Holder(View v) {
            super(v);
            mLocation = (TextView) v.findViewById(R.id.location);
            mId = (TextView) v.findViewById(R.id.disruptionId);
            mStatus = (TextView) v.findViewById(R.id.status);
            mSeverity = (TextView) v.findViewById(R.id.severity);
            mFrom = (TextView) v.findViewById(R.id.from);
            mTo = (TextView) v.findViewById(R.id.to);
            mLastUpdated = (TextView) v.findViewById(R.id.lastUpdated);
            mCategory = (TextView) v.findViewById(R.id.category);
            mSubCategory = (TextView) v.findViewById(R.id.subCategory);
            mComments = (TextView) v.findViewById(R.id.comments);
        }
    }

    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults oReturn = new FilterResults();
                final List<Disruption> results = new ArrayList<>();

                if (constraint == null || constraint.length() == 0) {
                    oReturn.values = mList;
                    oReturn.count = mList.size();
                } else {
                    mDisruptionList = mList;
                    oReturn.count = mDisruptionList.size();
                    if (mList != null & mList.size() > 0) {
                        for (final Disruption disruption : mDisruptionList) {
                            if (disruption.mLocation.toLowerCase().contains(constraint.toString())) {
                                results.add(disruption);
                            }
                        }
                    }
                    oReturn.values = results;
                }
                return oReturn;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mDisruptionList = (List<Disruption>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}
