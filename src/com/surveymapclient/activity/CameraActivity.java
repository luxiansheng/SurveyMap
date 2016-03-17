package com.surveymapclient.activity;

import com.surveymapclient.common.IToast;
import com.surveymapclient.common.ImageTools;
import com.surveymapclient.common.Logger;
import com.surveymapclient.entity.CouplePointLineBean;
import com.surveymapclient.impl.DialogCallBack;
import com.surveymapclient.impl.VibratorCallBack;
import com.surveymapclient.view.CameraBitMapView;
import com.surveymapclient.view.fragment.CameraEditeAndDelDialog;
import com.surveymapclient.view.fragment.EditeAndDelDialog;

import android.R.string;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.Paint.Style;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;
import android.view.LayoutInflater;
import android.view.MotionEvent;
public class CameraActivity extends Activity implements DialogCallBack,VibratorCallBack{
	
    Bitmap bitmap;
	private CameraBitMapView cameraBitMapView;
	public static Bitmap smallBitmap ;
	CouplePointLineBean couple;
	private Button dataMove;
	private Button textView;
    int index;
    int screenWidth;
    int screenHeight;
    private Context mContext = null;
	/**
     * �ֻ�����
     */
    private Vibrator vibrator;
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera);   
		cameraBitMapView = (CameraBitMapView) findViewById(R.id.CameraBitMapView);
		dataMove=(Button) findViewById(R.id.data_move);
		dataMove.setOnTouchListener(mTouchListener);
		textView=(Button) findViewById(R.id.center_move);
		textView.setOnTouchListener(mTouchListener);
		DisplayMetrics dm = getResources().getDisplayMetrics();  
		screenWidth = dm.widthPixels;  
		screenHeight= dm.heightPixels - 50; 
		mContext=this;
        Intent intent=getIntent();
        if(intent!=null)
        {
//            bitmap=intent.getParcelableExtra("bitmap");
//            
//			smallBitmap = ImageTools.zoomBitmap(bitmap, bitmap.getWidth()*5, bitmap.getHeight()*5 );
			Logger.i("ִ��˳��", "smallBitmap="+smallBitmap);
//			String url=intent.getExtras().getString("bitmap");
			Bundle bundle=getIntent().getExtras();
//			String fileName = intent.getStringExtra(
//					TestCameraActivity.KEY_FILENAME);
			
			int where=bundle.getInt("Where");
			if (where==0) {
				String fileName = bundle.getString(TestCameraActivity.KEY_FILENAME);
				Logger.i("ͼƬ��·��","fileName="+ fileName);
				String path=getFileStreamPath(fileName).getAbsolutePath();
				Logger.i("ͼƬ��·��","path="+ path);
				cameraBitMapView.setimagebitmap(path);
			}else if (where==1){
				String fileName = bundle.getString(TestCameraActivity.KEY_FILENAME);
				cameraBitMapView.setimagebitmap(fileName);
			}
        }
        // ��Ч����ϵͳ����
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
	}
	public void DanLianXian(View v){
		if (cameraBitMapView.isDan) {
			cameraBitMapView.isDuo=false;
			cameraBitMapView.isDrag=true;
			cameraBitMapView.isDan=false;
		}else {
//			imageview.ZoomCanvas(1);
			cameraBitMapView.isDuo=false;
			cameraBitMapView.isDrag=false;
			cameraBitMapView.isDan=true;
		}
	}
	public void DuoLianXian(View v){
		if (cameraBitMapView.isDuo) {
			cameraBitMapView.isDuo=false; 
			cameraBitMapView.isDrag=true;
			cameraBitMapView.isDan=false;
		}else {
//			imageview.ZoomCanvas(1);
			cameraBitMapView.isDuo=true;
			cameraBitMapView.isDrag=false;
			cameraBitMapView.isDan=false;
		}
	}
	public void CheHui(View v){
		cameraBitMapView.UnDo();
	}
	public void ZhuSi(View v){
		showEditePopupWindow(v);
	}
	@Override
	public void onVibratorCallBack() {
		// TODO Auto-generated method stub
		/*
         * �𶯵ķ�ʽ
         */
        // vibrator.vibrate(2000);//������
		//��һ��������ָ��һ���𶯵�Ƶ�����顣ÿ����Ϊһ�飬ÿ��ĵ�һ��Ϊ�ȴ�ʱ�䣬�ڶ���Ϊ��ʱ�䡣 
		// ���� [2000,500,100,400],���ȵȴ�2000���룬��500���ٵȴ�100����400 
        // �±��ǿ���ʹ���й��ɵ���   -1����ʾ���ظ� 0��ѭ������
        long[] pattern = {10,50};
        vibrator.vibrate(pattern, -1);
	}
	@SuppressLint("NewApi")
	@Override
	public void onDialogCallBack(CouplePointLineBean couplePoint, int i) {
		// TODO Auto-generated method stub
		this.couple=couplePoint;
		this.index=i;
		Toast.makeText(this, "�̰��¼���������Ϣ x="+couplePoint.getStartPoint().x, Toast.LENGTH_SHORT).show();
		CameraEditeAndDelDialog eadd=CameraEditeAndDelDialog.newIntance();
		FragmentTransaction ft=getFragmentManager().beginTransaction();
		eadd.show(ft, "");
	}
	public void SendData(){
		Intent intent=new Intent();
		Bundle bundle=new Bundle();
		bundle.putString("name", couple.getName());
		bundle.putString("descripte", couple.getDescripte());
		bundle.putDouble("length", couple.getLength());
		bundle.putDouble("angle", couple.getAngle());
		bundle.putFloat("width", couple.getWidth());
		bundle.putInt("color", couple.getColor());
		bundle.putBoolean("style", couple.isFull());
		bundle.putInt("i", 1);
		intent.putExtras(bundle);
		intent.setClass(CameraActivity.this, LineAttributeActivity.class);
		startActivity(intent);
	}
	public void Remove(){
		cameraBitMapView.RemoveIndexLine(index);
	}
	
	public void showEditePopupWindow(View view){
		// һ���Զ���Ĳ��֣���Ϊ��ʾ������
		View contentView=LayoutInflater.from(mContext).inflate(R.layout.popupeditewindow, null);
		Button edittext=(Button) contentView.findViewById(R.id.editetext);
		Button tape=(Button) contentView.findViewById(R.id.tape);
		edittext.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				IToast.show(mContext, "���ֱ༭");
				textView.setVisibility(View.VISIBLE);
				
//				textView.layout(Contants.sreenWidth/2, 400, 400, 500);

//				textView.setOnTouchListener(mTouchListener);
			}
		});
		tape.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				IToast.show(mContext, "¼��");
			}
		});
		final PopupWindow popupWindow=new PopupWindow(contentView, 
				200, 200, true);
//		popupWindow.setTouchable(true);
//		popupWindow.setTouchInterceptor(new OnTouchListener() {
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//
//                Log.i("mengdd", "onTouch : ");
//
//                return false;
//                // �����������true�Ļ���touch�¼���������
//                // ���غ� PopupWindow��onTouchEvent�������ã���������ⲿ�����޷�dismiss
//            }
//
//        });

        // ���������PopupWindow�ı����������ǵ���ⲿ������Back�����޷�dismiss����
        // �Ҿ���������API��һ��bug
        popupWindow.setBackgroundDrawable(getResources().getDrawable(
                R.drawable.meimu));

        // ���úò���֮����show
        popupWindow.showAsDropDown(view);
	}
	
	private OnTouchListener mTouchListener=new OnTouchListener() {
		
		int lastX, lastY;  
		  
        public boolean onTouch(View v, MotionEvent event) {  
            // TODO Auto-generated method stub  
            switch (event.getAction()) {  
            case MotionEvent.ACTION_DOWN:  
                lastX = (int) event.getRawX();  
                lastY = (int) event.getRawY();  
                break;  
            case MotionEvent.ACTION_MOVE:  
                int dx = (int) event.getRawX() - lastX;  
                int dy = (int) event.getRawY() - lastY;  

                int left = v.getLeft() + dx;  
                int top = v.getTop() + dy;  
                int right = v.getRight() + dx;  
                int bottom = v.getBottom() + dy;  

                if (left < 0) {  
                    left = 0;  
                    right = left + v.getWidth();  
                }  

                if (right > screenWidth) {  
                    right = screenWidth;  
                    left = right - v.getWidth();  
                }  

                if (top < 0) {  
                    top = 0;  
                    bottom = top + v.getHeight();  
                }  

                if (bottom > screenHeight) {  
                    bottom = screenHeight;  
                    top = bottom - v.getHeight();  
                }  

                v.layout(left, top, right, bottom);  
                if (cameraBitMapView.SetLineText((int) event.getRawX(), (int) event.getRawY())) {
					Logger.i("�����߶�", "���óɹ��ˣ�");
					cameraBitMapView.LineChangeToText();						
				}else {
					cameraBitMapView.LineNoChangeToText();
				}
                         
                lastX = (int) event.getRawX();  
                lastY = (int) event.getRawY();  

                break;  
            case MotionEvent.ACTION_UP:  
            	if (cameraBitMapView.SetLineText((int) event.getRawX(), (int) event.getRawY())) {
//					Logger.i("�����߶�", "���óɹ��ˣ�");
            		dataMove.setVisibility(View.INVISIBLE);
            		textView.setVisibility(View.INVISIBLE);
            		cameraBitMapView.SetwriteLineText("Text");
            		cameraBitMapView.LineNoChangeToText();
//					v.layout(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
            		
				}
                break;  
            }  
            return false;  
        }  
	};
	@Override
	public void onDestroy() {
		super.onDestroy();	
	}
}
