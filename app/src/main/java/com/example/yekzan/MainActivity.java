package com.example.yekzan;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static android.content.pm.PackageManager.PERMISSION_DENIED;

public class MainActivity extends AppCompatActivity {
    private ImageView firstImage;
    private ImageView secondImage;
    private ImageView thirdImage;
    private ImageView removeFirstImage;
    private ImageView removeSecondImage;
    private ImageView removeThirdImage;
    private List<Uri> selectedImages = new ArrayList<>();
    List<ImageView> imageViews = new ArrayList<>();
    List<ImageView> removeImageViews = new ArrayList<>();
    private ActivityResultLauncher<Void> takePicture = registerForActivityResult(new ActivityResultContracts.TakePicturePreview(), new ActivityResultCallback<Bitmap>() {
        @Override
        public void onActivityResult(Bitmap result) {
            if (result != null) {
                clearSelectedImages();
                firstImage.setVisibility(View.VISIBLE);
                removeFirstImage.setVisibility(View.VISIBLE);
                firstImage.setImageBitmap(result);
            }
        }
    });
    private ActivityResultLauncher<String> takePictureFile = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
        @Override
        public void onActivityResult(Uri result) {
            if (result != null) {
                clearSelectedImages();
                firstImage.setVisibility(View.VISIBLE);
                removeFirstImage.setVisibility(View.VISIBLE);
                firstImage.setImageURI(result);
            }
        }
    });
    private ActivityResultLauncher<String> readExternalStoragePermission =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    selectImageFromGallery();
                } else {
                    Toast.makeText(this, "اجازه دسترسی به گالری داده نشد", Toast.LENGTH_LONG).show();
                }
            });

    public MainActivity() {
       LocaleHelper.init(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpFieldOfQuestion();
        setUpAttachments();
        String questionsString = getString(R.string.questions_count);
        String boldText = "۲ سوال";
        int startIndex = questionsString.indexOf(boldText);
        int endIndex = startIndex + boldText.length();
        SpannableStringBuilder str = new SpannableStringBuilder(questionsString);
        str.setSpan(new StyleSpan(Typeface.BOLD), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        TextView questionsRemaining = findViewById(R.id.questionCounter);
        questionsRemaining.setText(str);
        Button register = findViewById(R.id.registerButton);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.message_confirm);

                Button dialogButton = dialog.findViewById(R.id.btn_dialog);
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                dialog.show();
            }
        });
    }

    private void setUpAttachments() {
        firstImage = findViewById(R.id.firstImage);
        secondImage = findViewById(R.id.secondImage);
        thirdImage = findViewById(R.id.thirdImage);
        imageViews.add(firstImage);
        imageViews.add(secondImage);
        imageViews.add(thirdImage);

        removeSecondImage = findViewById(R.id.removeSecondImage);
        removeFirstImage = findViewById(R.id.removeFirstImage);
        removeThirdImage = findViewById(R.id.removeThirdImage);

        removeImageViews.add(removeFirstImage);
        removeImageViews.add(removeSecondImage);
        removeImageViews.add(removeThirdImage);

        ImageButton attachmentButton = findViewById(R.id.attachmentButton);
        attachmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAttachmentMenu();
            }
        });
        removeFirstImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstImage.setVisibility(View.INVISIBLE);
                removeFirstImage.setVisibility(View.INVISIBLE);
            }
        });
        removeSecondImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                secondImage.setVisibility(View.INVISIBLE);
                removeSecondImage.setVisibility(View.INVISIBLE);
            }
        });
        removeThirdImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thirdImage.setVisibility(View.INVISIBLE);
                removeThirdImage.setVisibility(View.INVISIBLE);
            }
        });
        ImageButton cameraButton = findViewById(R.id.cameraButton);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture.launch(null);
            }
        });

    }

    private void setUpFieldOfQuestion() {
        View bottomSheetContainer = findViewById(R.id.questionBottomSheet);
        bottomSheetContainer.setBackgroundResource(R.drawable.top_rounded_bottom_sheet_background);
        BottomSheetBehavior sheetBehavior = BottomSheetBehavior.from(bottomSheetContainer);
        TextView chooseQuestion = findViewById(R.id.fieldOfQuestionButton);
        ImageButton closeMenuArrow = findViewById(R.id.closeMenuArrow);
        closeMenuArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

            }
        });
        chooseQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

            }
        });
        RadioGroup questionsGroup = findViewById(R.id.questionsGroup);
        questionsGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.nabarvari:
                        chooseQuestion.setText(R.string.nabarvari);
                        break;
                    case R.id.birth:
                        chooseQuestion.setText(R.string.birth);
                        break;
                    case R.id.diseases:
                        chooseQuestion.setText(R.string.diseases);
                        break;
                    case R.id.child:
                        chooseQuestion.setText(R.string.child);
                        break;
                    case R.id.before_marriage:
                        chooseQuestion.setText(R.string.before_marriage);
                        break;
                    case R.id.pregnency:
                        chooseQuestion.setText(R.string.pregnency);
                        break;
                    case R.id.marital:
                        chooseQuestion.setText(R.string.marital);
                        break;
                }

                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

            }
        });
    }

    private void showAttachmentMenu() {
        View attachBottomSheet = findViewById(R.id.attach_menu_container);
        BottomSheetBehavior attachSheetBehavior = BottomSheetBehavior.from(attachBottomSheet);
        attachSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        View chooseFromGallery = findViewById(R.id.chooseFromGallery);
        chooseFromGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PERMISSION_DENIED) {
                    readExternalStoragePermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                } else {
                    attachSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    selectImageFromGallery();
                }
            }
        });

        View chooseFromFile = findViewById(R.id.chooseFromFile);
        chooseFromFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attachSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                takePictureFile.launch("image/*");
            }
        });

        View chooseFromCamera = findViewById(R.id.chooseFromCamera);
        chooseFromCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attachSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                takePicture.launch(null);
            }
        });
    }

    private void clearSelectedImages() {
        selectedImages.clear();
        for (int i = 0; i < imageViews.size(); i++) {
            imageViews.get(i).setVisibility(View.INVISIBLE);
            removeImageViews.get(i).setVisibility(View.INVISIBLE);
        }
    }

    private void selectImageFromGallery() {
        clearSelectedImages();
        View galleryContainer = findViewById(R.id.galleryBottomSheetContainer);
        BottomSheetBehavior gallerySheetBehavior = BottomSheetBehavior.from(galleryContainer);
        gallerySheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        CardView cardView = findViewById(R.id.galleryCardContainer);
        cardView.setBackgroundResource(R.drawable.top_rounded_bottom_sheet_background);
        RecyclerView galleryRecycler = findViewById(R.id.galleryRecycler);
        galleryRecycler.setLayoutManager(new GridLayoutManager(MainActivity.this, 3));
        ArrayList<Uri> images = GalleryHepper.fetchGalleryImages(MainActivity.this);
        galleryRecycler.setAdapter(new GalleryAdapter(images, new GalleryAdapter.OnGalleryItemSelected() {
            @Override
            public void onImageSelected(int position) {
                selectedImages.add(images.get(position));
            }

            @Override
            public void onImageDeSelected(int position) {
                selectedImages.remove(images.get(position));
            }

            @Override
            public void onCameraSelected() {
                gallerySheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                takePicture.launch(null);
            }
        }, () -> selectedImages.size() < 3));

        Button imageSelectionFinished = findViewById(R.id.imagesSelectionFinished);
        imageSelectionFinished.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (int i = 0; i < selectedImages.size(); i++) {
                    ImageView imageView = imageViews.get(i);
                    imageView.setVisibility(View.VISIBLE);
                    removeImageViews.get(i).setVisibility(View.VISIBLE);
                    Picasso.get().load(selectedImages.get(i)).resize(400, 0).into(imageView);
                }

                gallerySheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

            }
        });
    }

}