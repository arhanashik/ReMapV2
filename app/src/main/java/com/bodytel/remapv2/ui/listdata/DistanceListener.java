package com.bodytel.remapv2.ui.listdata;

import com.bodytel.remapv2.data.local.listdata.DistanceModel;

import java.util.List;

public interface DistanceListener {
    void onDistanceFound(List<DistanceModel> distanceModels);
}
