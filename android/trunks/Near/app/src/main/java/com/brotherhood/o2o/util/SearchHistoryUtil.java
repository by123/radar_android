package com.brotherhood.o2o.util;

import android.text.TextUtils;

import com.brotherhood.o2o.manager.DirManager;
import com.brotherhood.o2o.task.TaskExecutor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SearchHistoryUtil {

    private static final String SEARCH_FOOD_DIR = "history";
    private static final String SEARCH_FOOD_FILENAME = "search_food_key";

    /**
     * 添加搜索记录
     *
     * @param key
     */
    public static void addSearchHistory(final String key) {

        if (key == null) {
            return;
        }
        TaskExecutor.executeTask(new Runnable() {
            @Override
            public void run() {
                List<String> historys = getSearchHistoryForFile();
                if (historys != null && !historys.isEmpty()) {
                    for (String s : historys) {//去除重复
                        if (s.equals(key)) {
                            historys.remove(s);
                            break;
                        }
                    }
                }
                try {
                    File dir = DirManager.getFilesDir(SEARCH_FOOD_DIR);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    File file = new File(dir.getAbsolutePath(), SEARCH_FOOD_FILENAME);
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                    if (historys.size() >= 10) {
                        historys.remove(0);
                    }
                    historys.add(0, key);
                    FileWriter writer = new FileWriter(file, false);
                    for (String s : historys) {
                        writer.write(s + "\r\n");
                    }
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }


    /**
     * 获取搜索记录
     *
     * @return
     */
    public static void getSearchHistory(final OnHistoryResultListener listener) {
        TaskExecutor.executeTask(new Runnable() {
            @Override
            public void run() {
                final List<String> historys = getSearchHistoryForFile();
                TaskExecutor.runTaskOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.result(historys);
                    }
                });
            }
        });
    }

    /**
     * 获取搜索关键字列表
     *
     * @return
     */
    private static List<String> getSearchHistoryForFile() {
        List<String> historys = new ArrayList<>();
        File file = new File(DirManager.getFilesDir(SEARCH_FOOD_DIR), SEARCH_FOOD_FILENAME);
        if (file.exists()) {
            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                String temp;
                while ((temp = bufferedReader.readLine()) != null) {
                    if (!TextUtils.isEmpty(temp)) {
                        historys.add(temp);
                    }
                }
                bufferedReader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return historys;
    }

    public interface OnHistoryResultListener {

        void result(List<String> historyList);

    }

    /**
     * 清空搜索记录
     */
    public static void emptySearchHistoty() {
        File file = new File(DirManager.getFilesDir(SEARCH_FOOD_DIR), SEARCH_FOOD_FILENAME);
        if (file.exists()){
            file.delete();
        }
    }

}
