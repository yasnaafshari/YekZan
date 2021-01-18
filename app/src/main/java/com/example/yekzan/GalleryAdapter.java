package com.example.yekzan;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class GalleryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_CAMERA = 0;
    private static final int VIEW_TYPE_IMAGE = 1;
    private OnGalleryItemSelected onGalleryItemSelected;
    private ShouldAllowImageSelection shouldAllowImageSelection;

    public GalleryAdapter(List<Uri> images, OnGalleryItemSelected onGalleryItemSelected, ShouldAllowImageSelection shouldAllowImageSelection)
    {
        this.onGalleryItemSelected = onGalleryItemSelected;
        this.shouldAllowImageSelection = shouldAllowImageSelection;
        this.images = images;
    }

    private List<Uri> images;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType)
        {
            case VIEW_TYPE_CAMERA:
                View cameraItemView = inflater.inflate(R.layout.item_camera, parent, false);
                return new GallerySelectCameraViewHolder(cameraItemView, onGalleryItemSelected);
            case VIEW_TYPE_IMAGE:
                View imageItemView = inflater.inflate(R.layout.item_image, parent, false);
                return new GalleryImageViewHolder(imageItemView, onGalleryItemSelected, shouldAllowImageSelection);
            default:
                throw new IllegalArgumentException("viewtype " + viewType + "not supported");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
    {
        if (getItemViewType(position) == VIEW_TYPE_IMAGE)
        {
            ((GalleryImageViewHolder) holder).bind(images.get(position - 1));
        }
    }

    @Override
    public int getItemCount()
    {
        return images.size() + 1;
    }

    @Override
    public int getItemViewType(int position)
    {
        if (position == 0)
        {
            return VIEW_TYPE_CAMERA;
        }
        else
        {
            return VIEW_TYPE_IMAGE;
        }
    }

    public class GalleryImageViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;
        private ImageView selection;

        public GalleryImageViewHolder(@NonNull View itemView, OnGalleryItemSelected onGalleryItemSelected, ShouldAllowImageSelection shouldAllowImageSelection)
        {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            selection = itemView.findViewById(R.id.selection);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    if (selection.getVisibility() == View.VISIBLE)
                    {
                        selection.setVisibility(View.INVISIBLE);
                        onGalleryItemSelected.onImageDeSelected(getAdapterPosition() - 1);
                    }
                    else
                    {
                        if (!shouldAllowImageSelection.isImageSelectionAllowed())
                        {
                            return;
                        }
                        selection.setVisibility(View.VISIBLE);
                        onGalleryItemSelected.onImageSelected(getAdapterPosition() - 1);
                    }
                }
            });
        }

        public void bind(Uri uri)
        {
            Picasso.get().load(uri).resize(400, 0).into(image);
        }
    }

    public class GallerySelectCameraViewHolder extends RecyclerView.ViewHolder {
        public GallerySelectCameraViewHolder(@NonNull View itemView, OnGalleryItemSelected onGalleryItemSelected)
        {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    onGalleryItemSelected.onCameraSelected();
                }
            });
        }

    }

    public interface OnGalleryItemSelected {
        void onImageSelected(int position);

        void onImageDeSelected(int position);

        void onCameraSelected();
    }

    public interface ShouldAllowImageSelection {
        boolean isImageSelectionAllowed();
    }
}
