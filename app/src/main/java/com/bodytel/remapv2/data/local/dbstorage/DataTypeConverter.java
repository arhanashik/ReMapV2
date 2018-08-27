package com.bodytel.remapv2.data.local.dbstorage;

import android.arch.persistence.room.TypeConverter;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DataTypeConverter {
    @TypeConverter
    public static List<Integer> stringToList(String data){
        if(data == null || TextUtils.isEmpty(data)){
            return Collections.emptyList();
        }

        List<Integer> dataList = new ArrayList<>();
        for(String str : data.split(",")){
            dataList.add(Integer.valueOf(str));
        }

        return dataList;
    }

    @TypeConverter
    public static String listToString(List<Integer> allData){
        if(allData == null || allData.isEmpty()){
            return "";
        }

        StringBuilder str = new StringBuilder();
        for (int i : allData){
            str.append(",").append(i);
        }

        return str.substring(0, 1);
    }
}
