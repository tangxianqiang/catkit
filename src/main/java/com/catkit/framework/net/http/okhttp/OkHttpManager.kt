package com.catkit.framework.net.http.okhttp

/**
 * The okhttp manager which can implement all the networks by interface.
 */
abstract class OkHttpManager : IGet, IPost, IFile {
    companion object {
        /**
         * If you use okhttp client with default read and write time, [instance] is ok.
         */
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            OkHttpManagerImp()
        }
        /**
         * You can use okhttp client with settable read and write time.
         */
        @Volatile
        private var _instance: OkHttpManagerImp? = null

        fun get_Instance(readTimeOut: Long, writeTimeOut: Long): OkHttpManagerImp {
            if (_instance == null) {
                synchronized(OkHttpManager::class.java) {
                    if (_instance == null) {
                        _instance = OkHttpManagerImp(readTimeOut, writeTimeOut)
                    }
                }
            }
            return _instance!!
        }

    }
}