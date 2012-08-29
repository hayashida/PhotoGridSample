package jp.kuseful.photogridsample;

import java.util.ArrayList;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

public class PhotoGridSampleActivity extends Activity {
	public final static int PREVIEW_PHOTO = 1;
	
	public final static int COLUMN_WIDTH = 200;
	public final static int ROW_HEIGHT = 200;
	
	private GridView mGridview = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        setContentView(R.layout.main);
        
        // グリッドビューを取得
        mGridview = (GridView) findViewById(R.id.gridview);
        
        // WindowManagerを取得
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        int screenWidth = display.getWidth();
        
        // カラム数をGridViewにセットする
        int numColumns = (int) Math.floor(screenWidth / COLUMN_WIDTH);
        mGridview.setNumColumns(numColumns);
        PhotoGalleyAdapter adapter = new PhotoGalleyAdapter(PhotoGridSampleActivity.this);
        
        if (adapter.getCount() == 0) {
        	Toast.makeText(this, "There are no photos in external media.", Toast.LENGTH_SHORT).show();
        	finish();
        }
        
        // グリッドビューにデータをセットする
        mGridview.setAdapter(adapter);
        
        // ウインドウタイトルに画像枚数を表示
        setTitle(getTitle()+"("+String.valueOf(adapter.getCount())+")");
        
        // グリッドをクリックしたら画像のプレビューを表示する
        mGridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(PhotoGridSampleActivity.this, PhotoPreviewActivity.class);
				intent.putExtra("id", parent.getItemIdAtPosition(position));
				intent.putExtra("name", (String) parent.getItemAtPosition(position));
				startActivityForResult(intent, PREVIEW_PHOTO);
			}
		});
    }
    
	private class PhotoGalleyAdapter extends BaseAdapter {
    	
		// コンテントリゾルバから取得した画像情報
    	private ArrayList<Long> mPhotoIds = new ArrayList<Long>();
    	private ArrayList<String> mPhotoNames = new ArrayList<String>();
    	
    	private Context mContext = null;
    	private ContentResolver mResolver = null;
    	
    	/**
    	 * コンストラクタ
    	 * @param context
    	 */
    	public PhotoGalleyAdapter(Context context) {
    		// コンテントリゾルバを取得し、外部メディアの画像を取得する
    		mContext = context;
    		mResolver = context.getContentResolver();
    		Cursor cursor = mResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
    		
    		while (cursor.moveToNext()) {
    			String name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.TITLE));
    			long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media._ID));
    			
    			mPhotoIds.add(id);
    			mPhotoNames.add(name);
    		}
    		
    		cursor.close();
    	}

    	@Override
		public int getCount() {
			return mPhotoIds.size();
		}

		@Override
		public Object getItem(int position) {
			return mPhotoNames.get(position);
		}

		@Override
		public long getItemId(int position) {
			return mPhotoIds.get(position);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView imageview;
			if (convertView == null) {
				imageview = new ImageView(mContext);
			} else {
				imageview = (ImageView) convertView;
			}
			
			// サムネイルのビットマップを取得する
			Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(
															mResolver,
															mPhotoIds.get(position),
															MediaStore.Images.Thumbnails.MICRO_KIND,
															new BitmapFactory.Options());
			
			// ビットマップの画像の大きさを揃える
			Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, COLUMN_WIDTH, ROW_HEIGHT, true);			
			imageview.setImageBitmap(resizedBitmap);
			
			return imageview;
		}
    	
    }
}