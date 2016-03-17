package com.surveymapclient.common;

import android.os.Environment;

public class Contants {

	public static final int sreenWidth=720;
	public static final int screenHeight=1280;
	
	public static final int DRAG=1;
	public static final int CONTINU=2;
	public static final int SINGLE=3;
	public static final int RECTANGLE=4;
	public static final int COORDINATE=5;
	public static final int ANGLE=6;
	
	
	public static final int IS_CONTINUES=7;
	public static final int IS_ONE_LINE=8;
	public static final int IS_TWO_LINES=9;
	
	public static final int LINEATTRIBUTEBACK=10;
	
	/**
	 * ��Ŀ¼
	 */
	public static String fish_saying_root = "/com.androidleaf.audiorecord";
	
	/**
	 * ��ȡ��Ƶ����Ŀ¼
	 */
	public static String AUDIO_DIRECTORY = Environment.getExternalStorageDirectory() +
			fish_saying_root + "/audio_record";
	
	/**
	 * ¼����Ƶ��ʼ��׺
	 */
	public static final String PCM_SUFFIX = ".pcm";
	
	/**
	 * ת����aac��Ƶ��׺
	 */
	public static final String AAC_SUFFIX = ".aac";
	
	/**
	 * ת����m4a��Ƶ��׺
	 */
	public static final String M4A_SUFFIX = ".m4a";
	
}
