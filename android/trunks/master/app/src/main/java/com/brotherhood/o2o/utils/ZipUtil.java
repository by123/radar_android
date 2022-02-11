package com.brotherhood.o2o.utils;

import com.brotherhood.o2o.manager.LogManager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/**
 * Created with Android Studio.
 * <p/>
 * Author:xiaxf
 * <p/>
 * Date:2015/7/22.
 */
public class ZipUtil {

	public static InputStream readZipEntryFile(File gpkFile, String entryFileName) {
		ZipInputStream zis = null;

		try {
			zis = new ZipInputStream(new FileInputStream(gpkFile));
			ZipEntry entry;

			while ((entry = zis.getNextEntry()) != null) {
				String fileName = entry.getName();
				if (!fileName.equals(entryFileName))
					continue;
				return zis;
			}

		} catch (IOException e) {
			LogManager.e(e);
		} finally {
			Utils.closeCloseable(zis);
		}
		return null;
	}


	/**
	 * 获取zip特征�?Crc)
	 * @param filePath
	 * @param entryName
	 * @return
	 */
	public static long getCRCValue(String filePath, String entryName) {
		ZipFile zipFile = null;
		try {
			zipFile = new ZipFile(filePath);
			ZipEntry entry = zipFile.getEntry(entryName);
			return entry.getCrc();
		} catch (IOException e) {
			LogManager.e(e);
		}finally {
			Utils.closeCloseable(zipFile);
		}
		return 0;
	}


	/**
	 * 解压byte数组为对应的字符�?
	 *
	 * @param body
	 * @return
	 */
	public static byte[] decompressZipToByte(byte[] body) {

		if (body == null || body.length == 0) {
			return null;
		}
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ByteArrayInputStream in = new ByteArrayInputStream(body);
			// 判断是否是GZIP格式
			int ss = (body[0] & 0xff) | ((body[1] & 0xff) << 8);
			if (ss == GZIPInputStream.GZIP_MAGIC) {
				GZIPInputStream gunzip = new GZIPInputStream(in);
				byte[] buffer = new byte[256];
				int n;
				while ((n = gunzip.read(buffer)) >= 0) {
					out.write(buffer, 0, n);
				}
				return out.toByteArray();
			} else {
				// 非gzip压缩，直接返回结�?
				return body;
			}
		} catch (Exception e) {
			LogManager.e(e);

		}
		return null;
	}

	/**
	 * 压缩字符串对byte数组
	 *
	 * @param body byte[]
	 * @return
	 */
	public static byte[] compressToByteByZip(byte[] body) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		GZIPOutputStream gzipOut;
		try {
			gzipOut = new GZIPOutputStream(baos);
			gzipOut.write(body);
			gzipOut.close();
			return baos.toByteArray();
		} catch (IOException e) {
			LogManager.e(e);
			return null;
		}

	}

}
