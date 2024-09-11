package com.example.primeiraaplicacao.roomDB

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Pessoa::class],
    version = 1
)

abstract class DatabasePessoa : RoomDatabase() {
    abstract fun daoPessoa() : DaoPessoa
}