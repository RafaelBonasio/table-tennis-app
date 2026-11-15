package com.rafaelbonasio.tabletennisapp.di

import org.koin.dsl.module
import org.w3c.dom.Worker

val webModule = module {
    single<SqliteWorkerFactory> {
        SqliteWorkerFactory { SqliteWorker.createWorker() }
    }
}

@OptIn(ExperimentalWasmJsInterop::class)
@JsModule("sqlite-worker")
external object SqliteWorker {
    fun createWorker(): Worker
}