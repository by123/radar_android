package com.brotherhood.o2o.chat.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;

public class Utils {

	public static boolean isGIF(byte[] data) {

		if (data == null || data.length < 6) {
			return false;
		}

		String header = new String(data, 0, 6);
		if (header.equals("GIF89a") || header.equals("GIF87a")) {
			return true;
		}

		return false;
	}

	public static boolean isGIF(String filePath) throws IOException {

		FileInputStream fis = null;
		fis = new FileInputStream(filePath);
		byte[] buffer = new byte[6];

		if (fis.read(buffer, 0, 6) == 6) {
			fis.close();
			String header = new String(buffer);
			if (header.equals("GIF89a") || header.equals("GIF87a")) {
				return true;
			}
		} else {
			fis.close();
			return false;
		}

		return false;
	}

	/**
	 * Check whether the specified permission is granted to the current package.
	 * 
	 * @param context
	 * @param permissionName
	 *            The permission.
	 * @return True if granted, false otherwise.
	 */
	public static boolean checkPermission(Context context, String permissionName) {
		PackageManager packageManager = context.getPackageManager();
		String pkgName = context.getPackageName();
		return packageManager.checkPermission(permissionName, pkgName) == PackageManager.PERMISSION_GRANTED;
	}

	/**
	 * Check whether the current SDCard is writable.
	 * 
	 * A lack of permission {@link Manifest.permission#WRITE_EXTERNAL_STORAGE}
	 * or a not mounted SDCard will return false.
	 */
	public static boolean isSdcardWritable(Context context) {
		if (!checkPermission(context,
				Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
			return false;
		}
		String state = Environment.getExternalStorageState();
		return Environment.MEDIA_MOUNTED.equals(state);
	}

	/**
	 * 判断SD card 是否可读
	 */
	public static boolean isSdcardReadable(Context context) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			// 如果是KitKat，先检查是否有read权限，如果没有，则直接返回false
			if (!checkPermission(context,
					Manifest.permission.READ_EXTERNAL_STORAGE)) {
				return false;
			}
		}
		String state = Environment.getExternalStorageState();
		return Environment.MEDIA_MOUNTED.equals(state);
	}

	/**
	 * 读简单的文件
	 * 
	 * @param file
	 *            文件对象
	 * @return 字符
	 */
	public static String readFile(File file) {
		if (!file.exists()) {
			return null;
		}
		ByteArrayOutputStream baos = null;
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			baos = new ByteArrayOutputStream(4096);
			byte[] b = new byte[1024];
			int a = -1;
			while ((a = fis.read(b)) != -1) {
				baos.write(b, 0, a);
			}
			return baos.toString();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != fis) {
				try {
					fis.close();

				} catch (IOException e) {
				}
			}
			if (baos != null) {
				try {
					baos.close();
				} catch (IOException e) {
				}
			}
		}
		return null;
	}

	/**
	 * 读简单的文件
	 * 
	 * @param file
	 *            文件对象
	 * @return 字符
	 */
	public static byte[] readFileToBytes(File file) {
		if (!file.exists()) {
			return null;
		}
		ByteArrayOutputStream baos = null;
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			baos = new ByteArrayOutputStream(4096);
			byte[] b = new byte[1024];
			int a = -1;
			while ((a = fis.read(b)) != -1) {
				baos.write(b, 0, a);
			}
			return baos.toByteArray();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != fis) {
				try {
					fis.close();

				} catch (IOException e) {
				}
			}
			if (baos != null) {
				try {
					baos.close();
				} catch (IOException e) {
				}
			}
		}
		return null;
	}

	/**
	 * Safely resolve the InputStream as a byte array.
	 */
	public static byte[] getInputStreamAsBytes(InputStream is) {
		if (is == null) {
			return null;
		}
		ByteArrayOutputStream baos = null;
		try {
			baos = new ByteArrayOutputStream(1024);
			byte[] buffer = new byte[1024];
			int bytesRead = is.read(buffer);
			while (bytesRead > 0) {
				baos.write(buffer, 0, bytesRead);
				bytesRead = is.read(buffer);
			}
			return baos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (baos != null) {
				try {
					baos.close();
				} catch (IOException e) {
				}
			}
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
		}
		return null;
	}

	public static boolean writeFile(File file, byte[] data, int offset,
			int count) {
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			fos.write(data, offset, count);
			fos.flush();
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != fos) {
				try {
					fos.close();
				} catch (IOException e) {
				}
			}

		}
		return false;
	}

	public static boolean writeFile(File file, byte[] src) {
		return writeFile(file, src, 0, src.length);
	}


}
