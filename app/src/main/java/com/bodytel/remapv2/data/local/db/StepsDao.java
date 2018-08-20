package com.bodytel.remapv2.data.local.db;

/*
 *  ****************************************************************************
 *  * Created by : Md. Azizul Islam on 8/20/2018 at 3:25 PM.
 *  * Email : azizul@w3engineers.com
 *  *
 *  * Purpose:
 *  *
 *  * Last edited by : Md. Azizul Islam on 8/20/2018.
 *  *
 *  * Last Reviewed by : <Reviewer Name> on <mm/dd/yy>
 *  ****************************************************************************
 */

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.bodytel.remapv2.data.local.listdata.StepModel;

import java.util.List;

@Dao
public interface StepsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(StepModel stepModel);

    @Query("SELECT * FROM "+TableName.STEPS)
    List<StepModel> getAllSteps();
}
