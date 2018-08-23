package com.bodytel.util.lib.network.callback

import com.bodytel.remapv2.data.local.audiosample.AudioSampleModel

interface StoreAudioSampleCallback {
    fun onStoreAudioSampleProgress(progress: Double)
    fun onStoreAudioSampleSuccessfully(downloadLink: String)
    fun onStoreAudioSampleReferenceSuccessfully(resultModel: AudioSampleModel)
    fun onStoreAudioSampleFailure(error: String)
}