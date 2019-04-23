package com.catkit.framework.net.http.okhttp

abstract class OkHttpManager:IGet,IPost{
    companion object {
        val instance by lazy (LazyThreadSafetyMode.SYNCHRONIZED){
            OkHttpManagerImp()
        }
    }
}