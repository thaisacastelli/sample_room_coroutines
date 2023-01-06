package br.com.alura.orgs.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import br.com.alura.orgs.database.converter.Converters
import br.com.alura.orgs.database.dao.ProdutoDao
import br.com.alura.orgs.model.Produto

@Database(entities = [Produto::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun produtoDao(): ProdutoDao

    companion object {
        @Volatile /*garante por exemplo que se 2 thread executarem esse cod simultaneamente uma veja a
        instancia que a outra ja criou e nao crie outra novamente, garantindo o singleton, que
        tenha apenas 1 instancia criada, no caso de acesso para o banco de dados*/
        private lateinit var db: AppDatabase

        fun instancia(context: Context): AppDatabase {
            if (::db.isInitialized) return db
            return Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                "orgs.db"
            ).build().also {
                    db = it
                }
        }
    }

    //ou assim tb, funciona igual o de cima, cria um singleton, instancia unica para acesso do banco
//    companion object {
//        @Volatile
//        private var db: AppDatabase? = null
//
//        fun instancia(context: Context): AppDatabase {
//            return db ?: Room.databaseBuilder(
//                context,
//                AppDatabase::class.java,
//                "orgs.db"
//            ).build().also {
//                db = it
//            }
//        }
//    }
}