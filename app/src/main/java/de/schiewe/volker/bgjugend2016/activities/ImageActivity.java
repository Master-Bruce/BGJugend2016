package de.schiewe.volker.bgjugend2016.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import de.schiewe.volker.bgjugend2016.R;
import de.schiewe.volker.bgjugend2016.helper.Util;
import de.schiewe.volker.bgjugend2016.views.TouchImageView;

public class ImageActivity extends AppCompatActivity {
    public static final String IMAGE_NAME = "image_name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        String filename = getIntent().getStringExtra(IMAGE_NAME);
        TouchImageView imageView = (TouchImageView) findViewById(R.id.ivBigImage);
        if (imageView != null) {
            imageView.setImageBitmap(Util.getImage(this,filename));
        }
    }
}
