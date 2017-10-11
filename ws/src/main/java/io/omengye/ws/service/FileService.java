package io.omengye.ws.service;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

import io.omengye.ws.utils.FileUtil;

@Service
public class FileService {
	
	public void saveFile(InputStream in, String fileurl) throws Exception {
		OutputStream out = null;
		try {
			// 加快速度改为java nio copy
			out = new FileOutputStream(fileurl);
			FileUtil.fastCopy(in, out);
		} catch (Exception e) {
			throw e;
		} finally {
			if (in != null) {
				in.close();
			}
			if (out != null) {
				out.close();
			}
		}
	}
}
