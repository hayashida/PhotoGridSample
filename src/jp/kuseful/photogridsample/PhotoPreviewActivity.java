package jp.kuseful.photogridsample;

import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.Toast;

public class PhotoPreviewActivity extends Activity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// 起動したらインテントからidという名前の引数を取得
		Intent intent = getIntent();
		long id = intent.getLongExtra("id", -1);
		String name = intent.getStringExtra("name");
		
		// エラーが発生したらメッセージを表示して終了
		if (id < 0) {
			Toast.makeText(this, "Intent getIntExtra Error.", Toast.LENGTH_SHORT).show();
			finish();
		}

		setTitle(name);
		setContentView(R.layout.preview);

		// ファイル用のURIを生成
		Uri imageUri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, String.valueOf(id));
		
		// ImageViewを取得
		ImageView imageview = (ImageView) findViewById(R.id.imageview);
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 4;
		Bitmap resizedBitmap = null;
		
		// コンテントリゾルバを取得
		ContentResolver resolver = getContentResolver();
		try {
			// 生成したURIから画像のInputStreamを開く
			InputStream is = resolver.openInputStream(imageUri);
			resizedBitmap = BitmapFactory.decodeStream(is);
			is.close();
			
			// 画像データをImageViewにセットする
			imageview.setImageBitmap(resizedBitmap);
		} catch (IOException e) {
			Toast.makeText(this, "ImageLoad Error ID:"+id, Toast.LENGTH_SHORT).show();
		}
	}
}
