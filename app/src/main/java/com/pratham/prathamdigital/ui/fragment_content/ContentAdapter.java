package com.pratham.prathamdigital.ui.fragment_content;

import android.animation.Animator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.recyclerview.extensions.AsyncListDiffer;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.custom.swipe_reveal_layout.ViewBinderHelper;
import com.pratham.prathamdigital.models.Modal_ContentDetail;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.view_holders.EmptyHolder;
import com.pratham.prathamdigital.view_holders.FileViewHolder;
import com.pratham.prathamdigital.view_holders.FolderViewHolder;

import java.util.List;
import java.util.Objects;

public class ContentAdapter extends RecyclerView.Adapter {

    static final int FOLDER_TYPE = 1;
    static final int FILE_TYPE = 2;
    static final int HEADER_TYPE = 3;
    private final ContentContract.contentClick contentInterface;
    private final AsyncListDiffer<Modal_ContentDetail> mDiffer;
    private final ViewBinderHelper binderHelper = new ViewBinderHelper();

    ContentAdapter(Context context, ContentContract.contentClick contentClick) {
        DiffUtil.ItemCallback<Modal_ContentDetail> diffcallback = new DiffUtil.ItemCallback<Modal_ContentDetail>() {
            @Override
            public boolean areItemsTheSame(@NonNull Modal_ContentDetail detail, @NonNull Modal_ContentDetail t1) {
                return Objects.equals(detail.getNodeid(), t1.getNodeid());
            }

            @Override
            public boolean areContentsTheSame(@NonNull Modal_ContentDetail detail, @NonNull Modal_ContentDetail t1) {
                int result = detail.compareTo(t1);
                return result == 0;
            }
        };
        mDiffer = new AsyncListDiffer<>(this, diffcallback);
        Context context1 = context;
        this.contentInterface = contentClick;
    }

    public void submitList(List<Modal_ContentDetail> data) {
        mDiffer.submitList(data);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return HEADER_TYPE;
        else if (mDiffer.getCurrentList().get(position).getContentType().equalsIgnoreCase(PD_Constant.FOLDER))
            return FOLDER_TYPE;
        else
            return FILE_TYPE;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        switch (viewType) {
            case HEADER_TYPE:
                LayoutInflater header = LayoutInflater.from(parent.getContext());
                v = header.inflate(R.layout.item_content_header, parent, false);
                return new EmptyHolder(v);
            case FOLDER_TYPE:
                LayoutInflater folder = LayoutInflater.from(parent.getContext());
                v = folder.inflate(R.layout.item_content_folder, parent, false);
                return new FolderViewHolder(v, contentInterface);
            case FILE_TYPE:
                LayoutInflater file = LayoutInflater.from(parent.getContext());
                v = file.inflate(R.layout.item_content_file, parent, false);
                return new FileViewHolder(v, contentInterface);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i) {
        Modal_ContentDetail contentDetail = mDiffer.getCurrentList().get(holder.getAdapterPosition());
        if (contentDetail != null) {
            switch (holder.getItemViewType()) {
                case FOLDER_TYPE:
                    FolderViewHolder folderViewHolder = (FolderViewHolder) holder;
                    folderViewHolder.setFolderItem(contentDetail, holder.getAdapterPosition());
                    break;
                case FILE_TYPE:
                    FileViewHolder fileViewHolder = (FileViewHolder) holder;
                    // Use ViewBindHelper to restore and save the open/close state of the SwipeRevealView
                    // put an unique string id as value, can be any string which uniquely define the data
                    binderHelper.bind(fileViewHolder.file_swipe_layout, contentDetail.getNodeid());
                    fileViewHolder.setContentItem(contentDetail, holder.getAdapterPosition());
                    break;
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List payloads) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads);
        } else {
            Modal_ContentDetail contentDetail = (Modal_ContentDetail) payloads.get(0);
            switch (holder.getItemViewType()) {
                case FOLDER_TYPE:
                    //folder type
                    FolderViewHolder folderViewHolder = (FolderViewHolder) holder;
                    folderViewHolder.setFolderItem(contentDetail, holder.getAdapterPosition());
                    break;
                case FILE_TYPE:
                    FileViewHolder fileViewHolder = (FileViewHolder) holder;
                    binderHelper.bind(fileViewHolder.file_swipe_layout, contentDetail.getNodeid());
                    unreveal(fileViewHolder.rl_reveal);
                    fileViewHolder.setContentItem(contentDetail, holder.getAdapterPosition());
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return mDiffer.getCurrentList().size();
    }

    public List<Modal_ContentDetail> getData() {
        return mDiffer.getCurrentList();
    }

    public void reveal(View view) {
        // previously invisible view
        try {
            int centerX = view.getWidth();
            int centerY = view.getHeight();
            int startRadius = 0;
            int endRadius = (int) Math.hypot(view.getWidth(), view.getHeight());
            Animator anim = ViewAnimationUtils.createCircularReveal(view, centerX, centerY, startRadius, endRadius);
            anim.setInterpolator(new AccelerateDecelerateInterpolator());
            anim.setDuration(300);
            view.setVisibility(View.VISIBLE);
            anim.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void unreveal(View view) {
        // previously visible view
        try {
            int centerX = view.getWidth();
            int centerY = view.getHeight();
            int startRadius = 0;
            int endRadius = Math.max(view.getWidth(), view.getHeight());
            Animator anim = ViewAnimationUtils.createCircularReveal(view, centerX, centerY, endRadius, startRadius);
            anim.setInterpolator(new AccelerateDecelerateInterpolator());
            anim.setDuration(300);
            anim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    view.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });
            anim.start();
        } catch (Exception e) {
            e.printStackTrace();
            view.setVisibility(View.GONE);
        }
    }
}
