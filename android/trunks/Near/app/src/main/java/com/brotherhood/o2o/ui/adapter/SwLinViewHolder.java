package com.brotherhood.o2o.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.brotherhood.o2o.R;
import com.brotherhood.o2o.listener.SwLinViewHolderClickListener;
import com.brotherhood.o2o.ui.widget.SwLin;

/**
 * Created by laimo.li on 2016/1/18.
 */
public abstract class SwLinViewHolder extends RecyclerView.ViewHolder {

    public SwLin swLinLayout;

    public SwLinViewHolder(View itemView, final SwLinViewHolderClickListener listener) {
        super(itemView);
        swLinLayout = (SwLin) itemView.findViewById(R.id.swLinLayout);
        if (swLinLayout.getChildCount() > 0) {
            View contentView = swLinLayout.getChildAt(0);
            contentView.setTag(swLinLayout);
            contentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SwLin swLinLayout = (SwLin) v.getTag();
                    if (swLinLayout.isShowMenu()) {
                        swLinLayout.showScreen(0);
                        return;
                    }
                    if (listener != null) {
                        listener.onItemClick(getPosition());
                    }
                }
            });
        }
    }

}
