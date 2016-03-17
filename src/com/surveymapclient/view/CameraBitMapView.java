package com.surveymapclient.view;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.surveymapclient.activity.CameraActivity;
import com.surveymapclient.activity.R;
import com.surveymapclient.common.Contants;
import com.surveymapclient.common.Logger;
import com.surveymapclient.common.ViewContans;
import com.surveymapclient.entity.CouplePointLineBean;
import com.surveymapclient.impl.DialogCallBack;
import com.surveymapclient.impl.VibratorCallBack;
import com.surveymapclient.model.LinesModel;
import com.surveymapclient.model.PolygonModel;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PointF;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.TextureView.SurfaceTextureListener;
import android.view.View;

public class CameraBitMapView extends View {
	
	

	DialogCallBack dialogCallBack;
	VibratorCallBack vibratorCallBack;
//	
	private Canvas  mCanvas;
    private Path    mPath;
    private Paint   mBitmapPaint;// �����Ļ���
    private Bitmap  mBitmap;
//    private Paint   mPaint;// ��ʵ�Ļ���
    private Bitmap background;
    Context mContext;
    private float startDistance;
    
    public static final int NONE = 0;
	public static final int DRAG = 1;
	public static final int ZOOM = 2;
	private int mode = DRAG;
	// ��һ�����µ���ָ�ĵ�   
    private PointF dragPoint = new PointF();
    private float scale=1;
    public int screen_x=0;
	public int screen_y=0;
	//��ֱ��
    private boolean isMovingdraw=false;
    private boolean mIsLongPressed =false;//�����¼�
    private boolean mIsShortPressed=false;
    private boolean isMoveLinedrag=false;
	
	public boolean isDrag=true;
	public boolean isDan=false;
	public boolean isDuo=false;
	
	private LinesModel linesModel;
	private PolygonModel polygonModel;
	
	 private boolean isWriteText=false;
	 private static int m=-1;
	
	//�����߶λ������
    List<CouplePointLineBean> totallist=new ArrayList<CouplePointLineBean>();
    
    public CameraBitMapView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		linesModel=new LinesModel();
		polygonModel=new PolygonModel();
		this.mContext=context;
//		initCanvas();
	}
    
	
	//��ʼ������
    public void initCanvas(){
         
//        mPaint = ViewContans.generatePaint(Color.RED, 5);  
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);    
        //������С 
//        mBitmap = Bitmap.createBitmap(Contants.sreenWidth, Contants.screenHeight, 
//            Bitmap.Config.RGB_565);
        mBitmap = Bitmap.createBitmap(background);  
    	mCanvas = new Canvas(mBitmap);  //����mCanvas���Ķ���������������mBitmap��      
        mCanvas.drawColor(Color.TRANSPARENT);
//        mPath = new Path();
            
         
    }
    public void setimagebitmap(String path){
//    	this.background=bm;
    	// ͼƬ·��
		DisplayMetrics dm = getResources().getDisplayMetrics();  
		float destWidth = dm.widthPixels;  
		float destHeight= dm.heightPixels ; 
		// ��ȡ����ͼƬ�ߴ�
		BitmapFactory.Options options=new BitmapFactory.Options();
		// ����Ϊtrue��options��Ȼ��Ӧ��ͼƬ��������������Ϊ��ͼƬ�����ڴ�
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);
		
		float srcWidth = options.outWidth;
		float srcHeight = options.outHeight;
		int inSampleSize = 1;
		if (srcHeight > destHeight || srcWidth > destWidth) { // ��ͼƬ����������Ļ����ʱ
			if (srcWidth < srcHeight) {
				inSampleSize = Math.round(srcHeight / destHeight);
			} else {
				inSampleSize = Math.round(srcWidth / destWidth);
			}
		}
		options = new BitmapFactory.Options();
		options.inSampleSize = inSampleSize;
		Bitmap bitmap = BitmapFactory.decodeFile(path, options);
		Matrix matrix = new Matrix();
	    matrix.setRotate(90);
	    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    	this.background=bitmap;
	    dialogCallBack=(DialogCallBack) mContext;
		vibratorCallBack=(VibratorCallBack) mContext;
    	initCanvas();
    }
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
//		canvas.drawBitmap(background, 0, 0, mBitmapPaint);
		// ��ǰ���Ѿ���������ʾ����
		canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);     //��ʾ�ɵĻ���
		canvas.scale(scale, scale,getWidth()/2,getHeight()/2);
		polygonModel.DrawPath(canvas);
		if (isDan&&isMovingdraw) {
			if (isMoveLinedrag) {
				linesModel.MoveDrawLine(canvas);
			}else {
				if (linesModel.LineType==Contants.IS_TWO_LINES) {
	        		linesModel.DrawLines(canvas);;
	        	}else{
	        		linesModel.DrawLine(canvas);
	    		}
			}
        	
		}
		if (isWriteText) {
        	Logger.i("ѡ�е�ֵ", "m="+m);
        	linesModel.LineChangeToText(totallist, canvas, m);
//			linesModel.AddTextOnLine(totallist, canvas, m);
		}
	}
	//�����¼�����ֵ
	float lastx=0;
	float lasty=0;
	long lastDownTime=0;
	boolean Yes=false;
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		float x=event.getX();
		float y=event.getY();
		float rx=event.getRawX();
		float ry=event.getRawY();
		switch (event.getAction()& MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
//			Logger.i("��Ļ����ͼ", "ACTION_DOWN");
			
        	lastx=x;
        	lasty=y;
        	lastDownTime= event.getDownTime();    
			if (isDan) {
				isMovingdraw=true;
				int i=linesModel.PitchOnLine(totallist, x, y);
				if (i>=0) {				
					isMoveLinedrag=true;
					linesModel.MoveLine_down(totallist, i, rx, ry);
					initCanvas();  
            		linesModel.DrawLinesOnBitmap(totallist, mCanvas);
				}else {
					linesModel.Line_camera_touch_down(x, y);
				}
			}else if (isDuo) {
				polygonModel.Continuous_touch_down(x, y);
			}else if (isDrag) {
				mIsShortPressed=false;
				drag_touch_start(rx, ry);
			}
			
			break;
		case MotionEvent.ACTION_POINTER_DOWN: //�����һֻ��ָ������Ļ����������һ����ָ����     // ��ֻ��ָ����
			Logger.i("���ĵ����", "ACTION_POINTER_DOWN");
			zoom_down(event);
        	break;
		case MotionEvent.ACTION_MOVE:
//        	Logger.i("��Ļ����ͼ", "��Ļ:x="+getLeft()+",screem_x="+screen_x+",RowX="+(int)rx+"����ͼ:x="+(int)x+"; ��Ļ:y="+getTop()+",screem_y="+screen_y+",RowY="+(int)ry+",��ͼ:y="+(int)y);
			if (isDan) {
				if (isMoveLinedrag) {        		
					linesModel.MoveLine_move(rx, ry);
				}else {
					if (!mIsLongPressed) {
	                	mIsLongPressed=ViewContans.isLongPressed(lastx, lasty, x, y, lastDownTime, event.getEventTime(), 1000);
	                	Yes=true;
	            	}
	            	if (mIsLongPressed) {        		
	            		if (Yes) {
	            			Yes=false;        			
	            			if (linesModel.ExtendLine(totallist, x, y)) {
								vibratorCallBack.onVibratorCallBack();
							}    		
	                		initCanvas();  
	                		linesModel.DrawLinesOnBitmap(totallist, mCanvas);
						}          		
	            		linesModel.Line_touch_move(x, y);
	    			}else {
	    				linesModel.Line_touch_move(x, y);
					}
				}			
			}else if (isDuo) {
				polygonModel.Continuous_touch_move(x, y);
			}else if (isDrag) {
				if (mode==DRAG) {
	            	drag_touch_move(rx, ry);
				}else if(mode==ZOOM){
					setzoomcanvas(event);
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			
			if (isDan) {			
				isMovingdraw=false;
				if (isMoveLinedrag) {
					isMoveLinedrag=false;
					linesModel.MoveLine_camera_up(mCanvas, rx, ry);
				}else {
					mIsLongPressed=false;
					linesModel.Line_camera_touch_up(x, y, mCanvas);
				}
			}else if (isDuo) {
				polygonModel.Continuous_camera_touch_up(x, y, mCanvas);
			}else {
				mIsShortPressed=ViewContans.isShortPressed(lastx, lasty, x, y, lastDownTime, event.getEventTime(), 200) 	;												      	
	        	if (mIsShortPressed) {
	        		int m=linesModel.PitchOnLine(totallist, x, y);
	        		if (m>=0) {
	    				dialogCallBack.onDialogCallBack(totallist.get(m), m);
					}
				}
			}
			Logger.i("���Ӹ���", "��line�� ��lines="+linesModel.list.size());  
			Logger.i("���Ӹ���", "��polygon�� �� lines="+polygonModel.continuouslist.size());
			for (int i = 0; i < linesModel.list.size(); i++) {
	        	PointF start=linesModel.list.get(i).getStartPoint();
	        	PointF stop=linesModel.list.get(i).getStopPoint();
	        	linesModel.AddLinesParams(totallist, start, stop);	        	
			}
			linesModel.list.clear();
			for (int i = 0; i < polygonModel.continuouslist.size(); i++) {
				PointF start=polygonModel.continuouslist.get(i).getStartPoint();
	        	PointF stop=polygonModel.continuouslist.get(i).getStopPoint(); 
	        	linesModel.AddLinesParams(totallist, start, stop);
	        	
			} 
			polygonModel.continuouslist.clear();
			Logger.i("���Ӹ���", "��total�� �� lines="+totallist.size());
			break;		
		}
		invalidate();
		return true;
	}
	private void drag_touch_start(float rx,float ry){
		dragPoint.set(rx,ry);
        mode=DRAG;
	}
	private void drag_touch_move(float rx,float ry){
		float dx=rx-dragPoint.x;
	    float dy=ry-dragPoint.y;
		if (Math.sqrt(dx*dx+dy*dy)>5f) {
			dragPoint.set(rx,ry);  				
			screen_x+=(int)dx;
			screen_y+=(int)dy;
		    layout(screen_x, screen_y, getWidth()+screen_x, getHeight()+screen_y);
		}
	}
	public boolean SetLineText(int rx,int ry){
		float gx=(float)(-screen_x+rx);
		float gy=(float)(-screen_y+ry-50);
		int i=linesModel.PitchOnLineToText(totallist, gx, gy);
		if (i>=0) {
			m=i;	
			
			return true;
		}else {
			
			return false;
		}		
	}
	public void SetwriteLineText(String text){
//		invalidate();
//		linesModel.AddTextOnLine(totallist, mCanvas, m,text);
	}
	public void LineChangeToText(){
		isWriteText=true;
//		isLineChange=true;
		invalidate();
	}
	public void LineNoChangeToText(){
//		isLineChange=true;
		isWriteText=false;
		invalidate();
	}
	/**
	 * ��������
	 * @param event
	 */
	private void zoom_down(MotionEvent event){
		startDistance = distance(event);//��������ľ���
		if(startDistance > 5f) {  //������ָ֮�����С�������ش���5����Ϊ�Ƕ�㴥��
			mode = ZOOM;
		}
		Logger.i("���ĵ����", "startDistance="+startDistance);
	}
	private void setzoomcanvas(MotionEvent event){
		float distance = distance(event);  //����֮��ľ���
		Logger.i("���ĵ����", "distance="+distance);
		if(distance > 5f) {
			scale = distance / startDistance;	
		}
	}
	/**��������֮��ľ�������**/
	public float distance(MotionEvent event) {
		// TODO Auto-generated method stub
		float eX = getWidth()/2 - event.getRawX();  //����ĵ����� - ǰ�������� 
		float eY = getHeight()/2 - event.getRawY();
		return (float) Math.sqrt(eX * eX + eY * eY);
	}
	/**
	  * �����ĺ���˼����ǽ�������գ�
	  * ������������Path·�����һ���Ƴ�����
	  * ���½�·�����ڻ������档
	  */
	 public void UnDo(){
	     if (totallist!=null&&totallist.size()>0) {
	    	//���ó�ʼ��������������ջ���
	         initCanvas();  
	         linesModel.DeleteLine(totallist, mCanvas);
	         invalidate();
		 }	 
    }
	/**
	  * ɾ��ĳ��
	  */
	 public void RemoveIndexLine(int index){
        if(totallist != null && totallist.size() > 0){
            //���ó�ʼ��������������ջ���
           initCanvas();            
           linesModel.RemoveIndexLine(totallist, mCanvas, index);
           invalidate();// ˢ��
        }
    }
	 //����ͼƬ
	 public void SavaBitmap(){
		 ViewContans.saveBitmap(mBitmap);
	 }	
}