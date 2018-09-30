package com.seeyon.cmp.common.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


//压缩图片类

public class BitmapCompressUtil {
	
	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
	
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;
			// Calculate the largest inSampleSize value that is a power of 2 and
			// keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}
		return inSampleSize;
	}

	/**
	 * 压缩Bitmap到指定大小
	 */
	public static Bitmap decodeSampledBitmapFromResource(String filePath, int reqWidth, int reqHeight) {

		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
	
		options.inJustDecodeBounds = false;

		return BitmapFactory.decodeFile(filePath, options);
		
	}
}
