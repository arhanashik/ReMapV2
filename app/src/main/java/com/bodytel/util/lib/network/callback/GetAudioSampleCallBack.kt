package com.bodytel.util.lib.network.callback

import com.bodytel.remapv2.data.local.audiosample.AudioSampleModel

interface GetAudioSampleCallBack {
    fun onLoadAudioSampleSuccessfully(sampleModelList : List<AudioSampleModel>)
    fun onLoadAudioSampleFailure(error : String)
}