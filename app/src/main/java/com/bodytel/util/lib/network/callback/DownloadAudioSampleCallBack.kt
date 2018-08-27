package com.bodytel.util.lib.network.callback

interface DownloadAudioSampleCallBack{
    fun onDownloadAudioSampleSuccessfully(filePath : String, message : String)
    fun onDownloadAudioSampleFailed(error : String)
}