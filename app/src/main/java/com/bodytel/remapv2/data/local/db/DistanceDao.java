package com.bodytel.remapv2.data.local.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.bodytel.remapv2.data.local.listdata.DistanceModel;

import java.util.List;

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

@Dao
public interface DistanceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insetDistance(DistanceModel distanceModel);

    @Query("SELECT * FROM "+TableName.DISTANCE)
    List<DistanceModel> getAllDistance();
}
